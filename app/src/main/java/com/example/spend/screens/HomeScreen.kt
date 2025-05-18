package com.example.spend.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToSpending: () -> Unit
) {
    var resultText by remember { mutableStateOf("") }

    // File picker launcher for bank statement (placeholder functionality)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val parsedData = parseBankStatementWithAI(it)
                resultText = "AI Parsed: $parsedData"
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Return Button
        Button(onClick = { /* Return placeholder */ }) {
            Text("Return")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Home Screen")

        // Display parsed result if any
        if (resultText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = resultText)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                filePickerLauncher.launch("*/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Bank Statement")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToForm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Form")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToSpending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Spending")
        }
    }
}

/**
 * Placeholder function to simulate AI processing on a bank statement.
 */
fun parseBankStatementWithAI(uri: Uri): String {
    return "Transactions extracted from $uri"
}
