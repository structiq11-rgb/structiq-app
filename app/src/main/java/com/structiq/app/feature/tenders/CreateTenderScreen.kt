package com.structiq.app.feature.tenders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.core.model.ContractType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTenderScreen(
    viewModel: TendersViewModel,
    onBackClick: () -> Unit,
    onTenderCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var tenderNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var estValueText by remember { mutableStateOf("1500000") }
    var closingDaysText by remember { mutableStateOf("14") }
    var selectedContractType by remember { mutableStateOf(ContractType.MEASURED_BOQ) }
    var notes by remember { mutableStateOf("") }

    var expandedContractDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Tender Workspace", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tender Title (e.g., Highway Overpass Bridge Works)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = client,
                onValueChange = { client = it },
                label = { Text("Client / Procuring Entity (e.g., Highway Authority)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tenderNumber,
                    onValueChange = { tenderNumber = it },
                    label = { Text("Tender No.") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = closingDaysText,
                    onValueChange = { closingDaysText = it.filter { c -> c.isDigit() } },
                    label = { Text("Days to Close") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Project Location / Region") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = estValueText,
                onValueChange = { estValueText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Estimated Budget / Value ($)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            // Contract Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedContractDropdown,
                onExpandedChange = { expandedContractDropdown = !expandedContractDropdown }
            ) {
                OutlinedTextField(
                    value = selectedContractType.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Contract Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedContractDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedContractDropdown,
                    onDismissRequest = { expandedContractDropdown = false }
                ) {
                    ContractType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.label) },
                            onClick = {
                                selectedContractType = type
                                expandedContractDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Key Requirements & Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.createTender(
                        title = title,
                        client = client,
                        tenderNum = tenderNumber,
                        location = location,
                        contractType = selectedContractType,
                        estValueUsd = estValueText.toDoubleOrNull() ?: 0.0,
                        closingDaysFromNow = closingDaysText.toIntOrNull() ?: 14,
                        notes = notes,
                        onComplete = onTenderCreated
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivilOrange)
            ) {
                Text("Create Tender Workspace", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
