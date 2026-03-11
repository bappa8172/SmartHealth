package com.smart.health.ui.routine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smart.health.data.model.BreakType
import com.smart.health.data.model.WellnessRoutine
import com.smart.health.viewmodel.WellnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    navController: NavController,
    viewModel: WellnessViewModel
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedBreakType by remember { mutableStateOf(BreakType.STRETCHING) }
    var duration by remember { mutableStateOf("5") }
    var expanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create Your Wellness Routine",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Routine Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Break Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedBreakType.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Break Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    BreakType.values().forEach { breakType ->
                        DropdownMenuItem(
                            text = {
                                Text(breakType.name.lowercase().replaceFirstChar { it.uppercase() })
                            },
                            onClick = {
                                selectedBreakType = breakType
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Duration Field
            OutlinedTextField(
                value = duration,
                onValueChange = { 
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        duration = it
                    }
                },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Create Button
            Button(
                onClick = {
                    if (name.isNotBlank() && duration.isNotBlank()) {
                        val routine = WellnessRoutine(
                            name = name,
                            description = description,
                            breakType = selectedBreakType,
                            durationMinutes = duration.toIntOrNull() ?: 5
                        )
                        viewModel.addRoutine(routine)
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank() && duration.isNotBlank()
            ) {
                Text(
                    text = "Create Routine",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
