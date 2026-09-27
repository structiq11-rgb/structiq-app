package com.structiq.app.feature.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.theme.CivilOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodStatementBuilderScreen(
    onBackClick: () -> Unit
) {
    var scopeOfWorks by remember { mutableStateOf("Laying of 450mm Ductile Iron pipe along A104 corridor including trench excavation, bedding, pipe jointing and hydrostatic testing.") }
    var resources by remember { mutableStateOf("Site Agent (1), Quality Engineer (1), Pipe Fitters (4), Skilled Labour (8), Excavator 20-Ton (2), Plate Compactor (2).") }
    var methodology by remember { mutableStateOf("1. Trench excavation to depth 1.5m with 45-deg side slope.\n2. 150mm granular bed placement.\n3. Lowering pipe sections using 20T excavator with webbing slings.\n4. Spigot & socket jointing with rubber gasket.\n5. Hydrostatic testing to 12 Bar for 4 hours.") }
    var healthAndSafety by remember { mutableStateOf("Mandatory PPE (Hard hat, high-vis, steel toe boots). Trench shoring for depths >1.5m. Gas detection in deep manholes.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Method Statement Builder") },
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
            Text(
                text = "Construct Technical Method Statement",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = scopeOfWorks,
                onValueChange = { scopeOfWorks = it },
                label = { Text("1. Scope of Works & Objectives") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = resources,
                onValueChange = { resources = it },
                label = { Text("2. Personnel & Equipment Resources") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = methodology,
                onValueChange = { methodology = it },
                label = { Text("3. Work Methodology & Sequence of Activities") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = healthAndSafety,
                onValueChange = { healthAndSafety = it },
                label = { Text("4. Health, Safety & Environmental Controls") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = { onBackClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivilOrange)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Save Method Statement", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
