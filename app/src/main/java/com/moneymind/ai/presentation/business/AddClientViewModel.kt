package com.moneymind.ai.presentation.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneymind.ai.data.local.entity.ClientEntity
import com.moneymind.ai.domain.usecase.AddClientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddClientUiState(
    val name: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val notes: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val addClientUseCase: AddClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddClientUiState())
    val uiState: StateFlow<AddClientUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun onCompanyChange(value: String) {
        _uiState.value = _uiState.value.copy(company = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(notes = value)
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Enter a client name")
            return
        }

        viewModelScope.launch {
            addClientUseCase(
                ClientEntity(
                    name = state.name.trim(),
                    company = state.company.trim().ifBlank { null },
                    email = state.email.trim().ifBlank { null },
                    phone = state.phone.trim().ifBlank { null },
                    notes = state.notes.trim().ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
