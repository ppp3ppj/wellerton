package com.ppp3ppj.wellerton.presentation.healthlog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ppp3ppj.wellerton.data.local.entity.HealthLogActivityType
import com.ppp3ppj.wellerton.data.local.entity.HealthLogStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthLogFormScreen(
    isEditMode: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: HealthLogFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Edit Health Log" else "Add Health Log")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val timePickerState = rememberTimePickerState(
                initialHour = uiState.draftTimeHour,
                initialMinute = uiState.draftTimeMinute,
                is24Hour = true
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Activity type picker
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Type", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HealthLogActivityType.entries.forEach { type ->
                            val selected = uiState.draftType == type
                            Surface(
                                onClick = { viewModel.onTypeChange(type) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                shadowElevation = if (selected) 4.dp else 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = type.emoji,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = type.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                TimePicker(state = timePickerState)

                OutlinedTextField(
                    value = uiState.draftActivity,
                    onValueChange = viewModel::onActivityChange,
                    label = { Text("Activity") },
                    placeholder = { Text("e.g. Wakeup, Drink water, Toilet") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("How was it?", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val goodSelected = uiState.draftStatus == HealthLogStatus.GOOD
                        val notGoodSelected = uiState.draftStatus == HealthLogStatus.NOT_GOOD
                        Button(
                            onClick = {
                                viewModel.onStatusChange(
                                    if (goodSelected) HealthLogStatus.UNRATED else HealthLogStatus.GOOD
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (goodSelected) Color(0xFF4CAF50)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (goodSelected) Color.White
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✓ Good")
                        }
                        Button(
                            onClick = {
                                viewModel.onStatusChange(
                                    if (notGoodSelected) HealthLogStatus.UNRATED else HealthLogStatus.NOT_GOOD
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (notGoodSelected) Color(0xFFF44336)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (notGoodSelected) Color.White
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("✗ Not Good")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.onSave(timePickerState.hour, timePickerState.minute) },
                    enabled = uiState.draftActivity.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}
