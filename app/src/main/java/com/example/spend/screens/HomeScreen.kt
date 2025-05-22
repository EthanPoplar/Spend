// app/src/main/kotlin/com/example/spend/screens/HomeScreen.kt
package com.example.spend.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spend.viewmodel.TransactionViewModel

@Composable
fun HomeScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToSpending: () -> Unit,
    viewModel: TransactionViewModel
) {
    val context = LocalContext.current

    // Observe loading state and transaction count
    val isProcessing by viewModel.isProcessing.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    // Launcher for picking an image
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.parseAndAdd(it, context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isProcessing) {
            // Show a spinner while OCR is running
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("Processing bank statement…")
        } else {
            // Once idle, show how many items have been imported
            Text("Imported: ${transactions.size} transactions")
            Spacer(Modifier.height(16.dp))

            // Upload button
            Button(
                onClick = { filePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Bank Statement")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Navigate to the Spending screen
        Button(
            onClick = onNavigateToSpending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Daily Spend")
        }

        Spacer(Modifier.height(16.dp))

        // Navigate to the manual-entry Form screen
        Button(
            onClick = onNavigateToForm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Transaction Manually")
        }
        Button(onClick = {
            viewModel.pingOpenAI(
                onResult = { msg ->
                    Toast.makeText(context, "Got back: $msg", Toast.LENGTH_SHORT).show()
                },
                onError = { err ->
                    Toast.makeText(context, "Ping error: $err", Toast.LENGTH_SHORT).show()
                }
            )
        }) {
            Text("Test Connect to GPT")
        }

    }
}




