package com.moneymind.ai.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneymind.ai.core.security.BiometricAuthManager
import com.moneymind.ai.presentation.components.NumericKeypad
import com.moneymind.ai.presentation.components.PinDots

@Composable
fun PinEntryScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    biometricAuthManager: BiometricAuthManager,
    onUnlocked: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(state.unlocked) {
        if (state.unlocked) onUnlocked()
    }

    LaunchedEffect(Unit) {
        if (activity != null && biometricAuthManager.isAvailable(activity)) {
            biometricAuthManager.authenticate(
                activity = activity,
                onSuccess = { viewModel.onBiometricSuccess() },
                onFailure = { /* user falls back to PIN keypad below */ }
            )
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome back", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Enter your PIN to unlock",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            PinDots(length = 6, filledCount = state.enteredPin.length)

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 40.dp))

            NumericKeypad(
                onDigit = { viewModel.onEntryDigit(it) },
                onBackspace = { viewModel.onEntryBackspace() },
                onBiometricClick = {
                    if (activity != null) {
                        biometricAuthManager.authenticate(
                            activity = activity,
                            onSuccess = { viewModel.onBiometricSuccess() },
                            onFailure = { }
                        )
                    }
                }
            )
        }
    }
}
