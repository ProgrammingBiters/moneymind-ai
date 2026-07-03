package com.moneymind.ai.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A 3x4 numeric keypad for PIN entry, with a backspace key and an optional
 * biometric shortcut key in the bottom-left slot.
 */
@Composable
fun NumericKeypad(
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onBiometricClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { digit ->
                    KeypadKey(label = digit.toString()) { onDigit(digit) }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (onBiometricClick != null) {
                IconButton(onClick = onBiometricClick, modifier = Modifier.size(64.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Use biometric unlock"
                    )
                }
            } else {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(64.dp))
            }
            KeypadKey(label = "0") { onDigit(0) }
            IconButton(onClick = onBackspace, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete digit"
                )
            }
        }
    }
}

@Composable
private fun KeypadKey(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.size(64.dp)) {
        Text(text = label, style = MaterialTheme.typography.headlineMedium)
    }
}
