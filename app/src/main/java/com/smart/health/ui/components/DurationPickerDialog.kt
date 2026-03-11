package com.smart.health.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DurationPickerDialog(
    title: String,
    currentDurationMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    predefinedDurations: List<Int> = listOf(1, 3, 5, 10, 15, 20, 25, 30, 45, 60)
) {
    var selectedDuration by remember { mutableStateOf(currentDurationMinutes) }
    var showCustomInput by remember { mutableStateOf(false) }
    var customDurationText by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Select Duration",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Predefined durations grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    predefinedDurations.chunked(3).forEach { rowDurations ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowDurations.forEach { duration ->
                                DurationButton(
                                    duration = duration,
                                    isSelected = selectedDuration == duration && !showCustomInput,
                                    onClick = {
                                        selectedDuration = duration
                                        showCustomInput = false
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space if row is not complete
                            repeat(3 - rowDurations.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Custom duration option
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomInput = !showCustomInput }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Custom Duration",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        RadioButton(
                            selected = showCustomInput,
                            onClick = { showCustomInput = !showCustomInput }
                        )
                    }
                }
                
                if (showCustomInput) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customDurationText,
                        onValueChange = { 
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                customDurationText = it
                                it.toIntOrNull()?.let { minutes ->
                                    if (minutes in 1..999) {
                                        selectedDuration = minutes
                                    }
                                }
                            }
                        },
                        label = { Text("Minutes") },
                        placeholder = { Text("Enter minutes (1-999)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { 
                            val finalDuration = if (showCustomInput) {
                                customDurationText.toIntOrNull() ?: selectedDuration
                            } else {
                                selectedDuration
                            }
                            onConfirm(finalDuration)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedDuration > 0
                    ) {
                        Text("Set ${selectedDuration}min")
                    }
                }
            }
        }
    }
}

@Composable
private fun DurationButton(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (isSelected) {
        ButtonDefaults.buttonColors()
    } else {
        ButtonDefaults.outlinedButtonColors()
    }
    
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(50.dp),
            colors = colors
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "min",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(50.dp),
            colors = colors
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "min",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
