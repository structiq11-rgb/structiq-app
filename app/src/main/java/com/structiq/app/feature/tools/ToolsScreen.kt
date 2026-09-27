package com.structiq.app.feature.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.core.designsystem.theme.ElectricBlue
import com.structiq.app.core.designsystem.theme.EmeraldSuccess

data class CalculatorItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: String
)

val calculatorList = listOf(
    CalculatorItem("concrete", "Concrete Mix & Volume", "Estimate concrete volume, cement bags, sand & aggregate", Icons.Default.SquareFoot, "Concrete"),
    CalculatorItem("rebar", "Steel Rebar Weight", "Calculate bar tonnage using D²/162 formula", Icons.Default.GridOn, "Reinforcement"),
    CalculatorItem("bricks", "Brick / Blockwork", "Count wall bricks, mortar volume & sand required", Icons.Default.ViewQuilt, "Masonry"),
    CalculatorItem("earthwork", "Excavation & Backfill", "Trench excavation, compaction & swelling factor", Icons.Default.Terrain, "Earthworks"),
    CalculatorItem("slope", "Gradient & Slope", "Calculate % slope, fall & vertical rise ratio", Icons.Default.TrendingUp, "Roads"),
    CalculatorItem("pipe", "Pipe Flow & Capacity", "Water flow velocity & tank volume in liters/m³", Icons.Default.WaterDrop, "Hydraulics")
)

@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel,
    onCalculatorClick: (String) -> Unit
) {
    val savedCalcs by viewModel.savedCalculations.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StructIQHeader(
            title = "Civil Engineering Toolbox",
            subtitle = "Precise site calculators, quantities & material estimates"
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Calculators & Quantity Surveying",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(calculatorList.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { calc ->
                        EngineeringCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCalculatorClick(calc.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CivilOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(calc.icon, contentDescription = null, tint = CivilOrange)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = calc.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = calc.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (savedCalcs.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recent Saved Calculations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(savedCalcs.take(3)) { calc ->
                    item {
                        EngineeringCard {
                            Text(
                                text = calc.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = calc.resultFormatted,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}
