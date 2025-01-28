package com.plb.conference.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.plb.conference.domain.MailVerificationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

class LoginViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private var _email = MutableStateFlow("") //TODO : approfondir private var val observer
    val email = _email.asStateFlow()

    private var _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun tryLogin() {
        val newEmail = _email.value
        val newPassword = _password.value

        _uiState.value = LoginUiState(isSuccess = true)

        if(!MailVerificationUseCase().isMailOK(newEmail)){
            _uiState.value = LoginUiState(error = "Invalid email")
        } else {
            _uiState.value = LoginUiState(error = null)
        }

        if(newPassword.length < 6){
            _uiState.value = LoginUiState(error = "Password must be at least 6 characters")
        }else {
            _uiState.value = LoginUiState(error = null)
        }

        _uiState.value = LoginUiState(isSuccess = true)
    }

}