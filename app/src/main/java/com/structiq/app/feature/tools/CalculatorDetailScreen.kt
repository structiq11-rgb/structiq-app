package com.structiq.app.feature.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.AmberWarning
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.core.designsystem.theme.EmeraldSuccess
import com.structiq.app.core.model.CalculationCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorDetailScreen(
    calcType: String,
    viewModel: ToolsViewModel,
    onBackClick: () -> Unit
) {
    var input1 by remember { mutableStateOf("10.0") }
    var input2 by remember { mutableStateOf("5.0") }
    var input3 by remember { mutableStateOf("0.3") }

    val isRebar = calcType.equals("rebar", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isRebar) "Steel Rebar Weight Calculator" else "Concrete Mix & Volume Calculator") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Safety Disclaimer Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AmberWarning.copy(alpha = 0.15f))
                    .border(1.dp, AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning)
                    Text(
                        text = "Engineering Notice: Estimates are for tendering & planning. Must be verified against approved drawings & structural codes.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "Calculation Inputs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (isRebar) {
                OutlinedTextField(
                    value = input1,
                    onValueChange = { input1 = it },
                    label = { Text("Bar Diameter (mm) (e.g. 8, 10, 12, 16, 20, 25, 32)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = input2,
                    onValueChange = { input2 = it },
                    label = { Text("Total Length Required (Meters)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            } else {
                OutlinedTextField(
                    value = input1,
                    onValueChange = { input1 = it },
                    label = { Text("Length (Meters)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = input2,
                    onValueChange = { input2 = it },
                    label = { Text("Width (Meters)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = input3,
                    onValueChange = { input3 = it },
                    label = { Text("Thickness / Depth (Meters)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calculated Output Card
            EngineeringCard {
                Text(
                    text = "Calculated Output",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CivilOrange
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isRebar) {
                    val dia = input1.toDoubleOrNull() ?: 16.0
                    val len = input2.toDoubleOrNull() ?: 100.0
                    val res = viewModel.calculateRebar(dia, len)

                    Text(text = "Formula: Unit Weight (kg/m) = D² / 162", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Weight per meter: ${"%.3f".format(res.weightPerMeterKg)} kg/m", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "• Total Steel Weight: ${"%.2f".format(res.totalWeightKg)} kg (${"%.3f".format(res.totalWeightTons)} Metric Tons)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldSuccess)
                    Text(text = "• Standard 12m Bars Required: ~${res.totalBars12m} Bars", style = MaterialTheme.typography.bodyLarge)
                } else {
                    val l = input1.toDoubleOrNull() ?: 10.0
                    val w = input2.toDoubleOrNull() ?: 5.0
                    val t = input3.toDoubleOrNull() ?: 0.3
                    val res = viewModel.calculateConcrete(l, w, t)

                    Text(text = "Formula: Dry Vol = Wet Vol × 1.54 | Ratio 1:2:4 (M20)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Wet Concrete Volume: ${"%.2f".format(res.wetVolumeM3)} m³", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "• Dry Volume Factor: ${"%.2f".format(res.dryVolumeM3)} m³", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "• Cement Required: ${"%.1f".format(res.cementBags)} Bags (50kg)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldSuccess)
                    Text(text = "• Sand Volume: ${"%.2f".format(res.sandM3)} m³", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "• Aggregate Volume: ${"%.2f".format(res.aggregateM3)} m³", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "• Est. Water Required: ${"%.0f".format(res.waterLiters)} Liters", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (isRebar) {
                        viewModel.saveCalculation("Rebar Y${input1} Weight", CalculationCategory.REINFORCEMENT, "D²/162", "${input2}m = ${input1}mm")
                    } else {
                        viewModel.saveCalculation("Concrete Slab ${input1}x${input2}m", CalculationCategory.CONCRETE, "V = L*W*T * 1.54", "${input1}x${input2}x${input3}m")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivilOrange)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Project Calculations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
