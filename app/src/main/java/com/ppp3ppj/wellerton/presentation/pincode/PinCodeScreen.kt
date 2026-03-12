package com.ppp3ppj.wellerton.presentation.pincode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PinCodeScreen(
    onSuccess: (username: String) -> Unit,
    viewModel: PinCodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess(uiState.username)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("Enter PIN", style = MaterialTheme.typography.headlineMedium)

            PinDots(filled = uiState.pin.length, total = 6)

            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            PinPad(onDigit = viewModel::onPinDigit, onDelete = viewModel::onDelete)
        }
    }
}

@Composable
private fun PinDots(filled: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(total) { index ->
            Surface(
                modifier = Modifier.size(14.dp),
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
