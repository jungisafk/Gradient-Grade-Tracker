package com.example.gradientgradetracker.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradientgradetracker.data.model.User
import com.example.gradientgradetracker.data.repository.AuthRepository
import com.example.gradientgradetracker.ui.screens.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
    data class SignUpSuccess(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val authRepository = AuthRepository()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            
            try {
                val result = authRepository.login(email, password)
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = LoginUiState.Success(user)
                    },
                    onFailure = { exception ->
                        _uiState.value = LoginUiState.Error(exception.message ?: "Authentication failed")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                if (password != confirmPassword) {
                    _uiState.value = LoginUiState.Error("Passwords do not match")
                    return@launch
                }

                if (password.length < 6) {
                    _uiState.value = LoginUiState.Error("Password must be at least 6 characters")
                    return@launch
                }

                val result = authRepository.signUp(email, password)
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = LoginUiState.SignUpSuccess("Account created successfully! Please login.")
                    },
                    onFailure = { exception ->
                        _uiState.value = LoginUiState.Error(exception.message ?: "Registration failed")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Initial
    }

    fun signOut() {
        authRepository.signOut()
        resetState()
    }
} 