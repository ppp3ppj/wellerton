package com.ppp3ppj.wellerton.presentation.pincode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PinCodeScreen(
    onSuccess: () -> Unit,
    viewModel: PinCodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is PinCodeUiState.Success) onSuccess()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is PinCodeUiState.Loading -> CircularProgressIndicator()
            is PinCodeUiState.Create -> PinEntryContent(
                title = if (state.confirm.isEmpty()) "Create PIN" else "Confirm PIN",
                pinLength = if (state.confirm.isEmpty()) state.pin.length else state.confirm.length,
                error = state.error,
                onDigit = viewModel::onPinEntered,
                onDelete = viewModel::onDelete
            )
            is PinCodeUiState.Verify -> PinEntryContent(
                title = "Enter PIN",
                pinLength = state.pin.length,
                error = state.error,
                onDigit = viewModel::onPinEntered,
                onDelete = viewModel::onDelete
            )
            is PinCodeUiState.Success -> Unit
        }
    }
}

@Composable
private fun PinEntryContent(
    title: String,
    pinLength: Int,
    error: String?,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)

        PinDots(filled = pinLength)

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        PinPad(onDigit = onDigit, onDelete = onDelete)
    }
}

@Composable
private fun PinDots(filled: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(4) { index ->
            Surface(
                modifier = Modifier.size(16.dp),
                shape = MaterialTheme.shapes.small,
                color = if (index < filled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant
            ) {}
        }
    }
}

@Composable
private fun PinPad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    when {
                        key.isEmpty() -> Spacer(Modifier.size(80.dp))
                        key == "⌫" -> OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.size(80.dp)
                        ) { Text(key, textAlign = TextAlign.Center) }
                        else -> FilledTonalButton(
                            onClick = { onDigit(key) },
                            modifier = Modifier.size(80.dp)
                        ) { Text(key, style = MaterialTheme.typography.titleLarge) }
                    }
                }
            }
        }
    }
}
