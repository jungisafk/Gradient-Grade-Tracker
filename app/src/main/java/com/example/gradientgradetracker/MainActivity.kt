package com.example.gradientgradetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface       
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gradientgradetracker.ui.components.ErrorDialog
import com.example.gradientgradetracker.ui.components.LoadingIndicator
import com.example.gradientgradetracker.ui.components.SuccessDialog
import com.example.gradientgradetracker.ui.screens.HomeScreen
import com.example.gradientgradetracker.ui.screens.LoginScreen
import com.example.gradientgradetracker.ui.screens.SignUpScreen
import com.example.gradientgradetracker.ui.screens.UserRole
import com.example.gradientgradetracker.ui.theme.GradientGradeTrackerTheme
import com.example.gradientgradetracker.ui.viewmodels.LoginUiState
import com.example.gradientgradetracker.ui.viewmodels.LoginViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private var showSignUp by mutableStateOf(false)
    private var showHome by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            GradientGradeTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    private fun MainScreen() {
        val uiState by viewModel.uiState.collectAsState()
        
        when {
            showHome -> {
                HomeScreen()
            }
            uiState is LoginUiState.Initial -> {
                if (showSignUp) {
                    SignUpScreen(
                        onSignUpClick = { email, password, confirmPassword ->
                            viewModel.signUp(email, password, confirmPassword)
                        },
                        onBackToLoginClick = {
                            showSignUp = false
                        }
                    )
                } else {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            viewModel.login(email, password)
                        },
                        onSignUpClick = {
                            showSignUp = true
                        }
                    )
                }
            }
            uiState is LoginUiState.Loading -> {
                LoadingIndicator()
            }
            uiState is LoginUiState.Success -> {
                showHome = true
                // Optionally, reset state or pass user info to HomeScreen
            }
            uiState is LoginUiState.Error -> {
                val errorMessage = (uiState as LoginUiState.Error).message
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = {
                        viewModel.resetState()
                    }
                )
            }
            uiState is LoginUiState.SignUpSuccess -> {
                val message = (uiState as LoginUiState.SignUpSuccess).message
                SuccessDialog(
                    message = message,
                    onDismiss = {
                        showSignUp = false
                        viewModel.resetState()
                    }
                )
            }
        }
    }
}