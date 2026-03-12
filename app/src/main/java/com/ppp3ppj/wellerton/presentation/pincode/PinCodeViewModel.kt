package com.ppp3ppj.wellerton.presentation.pincode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ppp3ppj.wellerton.data.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PinCodeUiState {
    data object Loading : PinCodeUiState()
    data class Create(val pin: String = "", val confirm: String = "", val error: String? = null) : PinCodeUiState()
    data class Verify(val pin: String = "", val error: String? = null) : PinCodeUiState()
    data object Success : PinCodeUiState()
}

@HiltViewModel
class PinCodeViewModel @Inject constructor(
    private val pinRepository: PinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PinCodeUiState>(PinCodeUiState.Loading)
    val uiState: StateFlow<PinCodeUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = if (pinRepository.isPinSet()) {
                PinCodeUiState.Verify()
            } else {
                PinCodeUiState.Create()
            }
        }
    }

    fun onPinEntered(digit: String) {
        when (val state = _uiState.value) {
            is PinCodeUiState.Create -> {
                if (state.confirm.isEmpty()) {
                    val newPin = (state.pin + digit).take(4)
                    _uiState.update { state.copy(pin = newPin, error = null) }
                    if (newPin.length == 4) onPinComplete()
                } else {
                    val newConfirm = (state.confirm + digit).take(4)
                    _uiState.update { state.copy(confirm = newConfirm, error = null) }
                    if (newConfirm.length == 4) onConfirmComplete()
                }
            }
            is PinCodeUiState.Verify -> {
                val newPin = (state.pin + digit).take(4)
                _uiState.update { state.copy(pin = newPin, error = null) }
                if (newPin.length == 4) onVerifyComplete()
            }
            else -> Unit
        }
    }

    fun onDelete() {
        when (val state = _uiState.value) {
            is PinCodeUiState.Create -> {
                if (state.confirm.isNotEmpty()) {
                    _uiState.update { state.copy(confirm = state.confirm.dropLast(1)) }
                } else {
                    _uiState.update { state.copy(pin = state.pin.dropLast(1)) }
                }
            }
            is PinCodeUiState.Verify -> {
                _uiState.update { state.copy(pin = state.pin.dropLast(1)) }
            }
            else -> Unit
        }
    }

    private fun onPinComplete() {
        val state = _uiState.value as? PinCodeUiState.Create ?: return
        _uiState.update { state.copy(confirm = "") }
    }

    private fun onConfirmComplete() {
        val state = _uiState.value as? PinCodeUiState.Create ?: return
        if (state.pin == state.confirm) {
            viewModelScope.launch {
                pinRepository.savePin(state.pin)
                _uiState.value = PinCodeUiState.Success
            }
        } else {
            _uiState.update { state.copy(pin = "", confirm = "", error = "PINs do not match") }
        }
    }

    private fun onVerifyComplete() {
        val state = _uiState.value as? PinCodeUiState.Verify ?: return
        viewModelScope.launch {
            if (pinRepository.verifyPin(state.pin)) {
                _uiState.value = PinCodeUiState.Success
            } else {
                _uiState.update { state.copy(pin = "", error = "Incorrect PIN") }
            }
        }
    }
}
