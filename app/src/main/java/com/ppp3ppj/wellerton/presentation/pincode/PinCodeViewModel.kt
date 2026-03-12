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
    val username: String = "",
    val pin: String = "",
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PinCodeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinCodeUiState())
    val uiState: StateFlow<PinCodeUiState> = _uiState

    init {
        viewModelScope.launch {
            val name = userRepository.getCurrentUsername() ?: ""
            _uiState.update { it.copy(username = name) }
        }
    }

    fun onPinDigit(digit: String) {
        val state = _uiState.value
        val newPin = (state.pin + digit).take(6)
        _uiState.update { it.copy(pin = newPin, error = null) }
        if (newPin.length == 6) verify()
    }

    fun onDelete() {
        _uiState.update { it.copy(pin = it.pin.dropLast(1)) }
    }

    private fun verify() {
        val state = _uiState.value
        viewModelScope.launch {
            if (userRepository.verifyPin(state.username, state.pin)) {
                _uiState.update { it.copy(isSuccess = true) }
            } else {
                _uiState.update { it.copy(pin = "", error = "Incorrect PIN") }
            }
        }
    }
}
