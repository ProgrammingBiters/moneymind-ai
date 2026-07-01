package com.moneymind.ai.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.core.security.AppLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PIN_LENGTH = 6

data class AuthUiState(
    val isPinSet: Boolean = false,
    val enteredPin: String = "",
    val confirmPin: String = "",
    val isConfirmStep: Boolean = false,
    val error: String? = null,
    val unlocked: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isPinSet = appLockManager.isPinSet()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun shouldRequireUnlock(): Boolean = appLockManager.shouldRequireUnlock()

    // --- PIN setup (first launch) ---

    fun onSetupDigit(digit: Int) {
        val state = _uiState.value
        if (!state.isConfirmStep) {
            if (state.enteredPin.length >= PIN_LENGTH) return
            val updated = state.enteredPin + digit
            _uiState.value = state.copy(enteredPin = updated, error = null)
            if (updated.length == PIN_LENGTH) {
                _uiState.value = _uiState.value.copy(isConfirmStep = true)
            }
        } else {
            if (state.confirmPin.length >= PIN_LENGTH) return
            val updated = state.confirmPin + digit
            _uiState.value = state.copy(confirmPin = updated, error = null)
            if (updated.length == PIN_LENGTH) {
                finishSetup()
            }
        }
    }

    fun onSetupBackspace() {
        val state = _uiState.value
        if (state.isConfirmStep) {
            _uiState.value = state.copy(confirmPin = state.confirmPin.dropLast(1))
        } else {
            _uiState.value = state.copy(enteredPin = state.enteredPin.dropLast(1))
        }
    }

    private fun finishSetup() {
        val state = _uiState.value
        if (state.enteredPin != state.confirmPin) {
            _uiState.value = state.copy(
                confirmPin = "",
                error = "PINs don't match. Try again."
            )
            return
        }
        viewModelScope.launch {
            appLockManager.setPin(state.enteredPin)
            _uiState.value = state.copy(isPinSet = true, unlocked = true)
        }
    }

    // --- PIN entry (returning user) ---

    fun onEntryDigit(digit: Int) {
        val state = _uiState.value
        if (state.enteredPin.length >= PIN_LENGTH) return
        val updated = state.enteredPin + digit
        _uiState.value = state.copy(enteredPin = updated, error = null)
        if (updated.length == PIN_LENGTH) {
            verifyEnteredPin(updated)
        }
    }

    fun onEntryBackspace() {
        _uiState.value = _uiState.value.copy(enteredPin = _uiState.value.enteredPin.dropLast(1))
    }

    private fun verifyEnteredPin(pin: String) {
        viewModelScope.launch {
            val valid = appLockManager.verifyPin(pin)
            _uiState.value = if (valid) {
                _uiState.value.copy(unlocked = true)
            } else {
                _uiState.value.copy(enteredPin = "", error = "Incorrect PIN")
            }
        }
    }

    fun onBiometricSuccess() {
        appLockManager.recordUnlock()
        _uiState.value = _uiState.value.copy(unlocked = true)
    }
}
