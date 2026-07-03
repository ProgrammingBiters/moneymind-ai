package com.moneymind.ai.presentation.smsimport

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.presentation.smsimport.components.SmsCandidateRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsImportScreen(
    onBack: () -> Unit,
    viewModel: SmsImportViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.scan()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import from SMS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!hasPermission) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Read your bank & UPI SMS", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "MoneyMind scans your SMS inbox on-device to find bank and UPI messages and " +
                                "turn them into transaction entries automatically. Nothing is uploaded anywhere — " +
                                "this all happens locally, and every result is reviewed by you before it's saved.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.READ_SMS) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant SMS Access")
                        }
                    }
                }
                return@Scaffold
            }

            if (state.importedCount != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Imported ${state.importedCount} transaction(s).",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = viewModel::dismissImportedBanner) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            if (!state.hasScanned) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan your inbox to find recent bank and UPI messages from the last 90 days.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(onClick = viewModel::scan, enabled = !state.isScanning) {
                        Text(if (state.isScanning) "Scanning…" else "Scan SMS Inbox")
                    }
                }
            } else {
                if (state.candidates.isEmpty()) {
                    Text(
                        text = "No recognizable bank or UPI transactions found in the last 90 days.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Import into which ledger?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        Ledger.entries.forEachIndexed { index, ledger ->
                            SegmentedButton(
                                selected = state.targetLedger == ledger,
                                onClick = { viewModel.setTargetLedger(ledger) },
                                shape = SegmentedButtonDefaults.itemShape(index, Ledger.entries.size)
                            ) { Text(ledger.displayName) }
                        }
                    }

                    SelectAllRow(onSelectAll = { viewModel.selectAll(true) }, onSelectNone = { viewModel.selectAll(false) })

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.candidates, key = { it.id }) { candidate ->
                            SmsCandidateRow(candidate = candidate, onToggle = { viewModel.toggleSelected(candidate.id) })
                        }
                    }

                    Button(
                        onClick = viewModel::importSelected,
                        enabled = !state.isImporting && state.candidates.any { it.selected },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isImporting) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        }
                        Text("Import ${state.candidates.count { it.selected }} Selected")
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectAllRow(onSelectAll: () -> Unit, onSelectNone: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(onClick = onSelectAll) { Text("Select All") }
        TextButton(onClick = onSelectNone) { Text("Select None") }
    }
}
