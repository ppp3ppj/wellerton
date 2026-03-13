package com.ppp3ppj.wellerton.presentation.pincode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ppp3ppj.wellerton.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinCodeUiState(
    val pin: String = "",
    val error: String? = null,
    val loggedInUsername: String? = null
)

@HiltViewModel
class PinCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinCodeUiState())
    val uiState: StateFlow<PinCodeUiState> = _uiState

    fun onPinDigit(digit: String) {
        val newPin = (_uiState.value.pin + digit).take(6)
        _uiState.update { it.copy(pin = newPin, error = null) }
        if (newPin.length == 6) verify(newPin)
    }

    fun onDelete() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1)) }
    }

    private fun verify(pin: String) {
        viewModelScope.launch {
            val username = userRepository.findUserByPin(pin)
            if (username != null) {
                _uiState.update { it.copy(loggedInUsername = username) }
            } else {
                _uiState.update { it.copy(pin = "", error = "Incorrect PIN") }
            }
        }
    }
}
