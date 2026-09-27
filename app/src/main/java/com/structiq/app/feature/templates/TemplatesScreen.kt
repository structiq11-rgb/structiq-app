package com.structiq.app.feature.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.CivilOrange

data class TemplateItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String
)

val templateList = listOf(
    TemplateItem("method_statement", "Method Statement Builder", "Guided generator for construction sequence, H&S & quality control", "Technical"),
    TemplateItem("company_profile", "Company Profile Builder", "Reusable contractor profile, equipment & experience sheets", "Administrative"),
    TemplateItem("tech_proposal", "Technical Proposal Builder", "Executive summary, approach, quality & work programme", "Technical"),
    TemplateItem("site_report", "Daily Site Diary & Report", "Daily weather, labour count, equipment & site activities log", "Site Operations"),
    TemplateItem("risk_assessment", "Risk Assessment & HSE", "Hazard identification, risk scoring & mitigation controls", "HSE / Safety"),
    TemplateItem("rfi", "Request for Information (RFI)", "Formal clarification document for consultants & engineers", "Site Operations")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onTemplateClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professional Engineering Templates") },
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
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templateList) { template ->
                    EngineeringCard(
                        onClick = { onTemplateClick(template.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CivilOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = CivilOrange)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = template.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
