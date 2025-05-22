// app/src/main/kotlin/com/example/spend/screens/FormScreen.kt
package com.example.spend.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spend.model.Transaction
import com.example.spend.viewmodel.TransactionViewModel
import java.util.Calendar

@Composable
fun FormScreen(
    viewModel: TransactionViewModel,
    onReturnHome: () -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("Select Date") }
    var categoryText by remember { mutableStateOf("") }

    // Date picker setup
    val calendar = Calendar.getInstance()
    val picker = DatePickerDialog(
        context,
        { _, year, month, day ->
            dateText = "${day}/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Return button
        Button(onClick = onReturnHome) {
            Text("Return")
        }

        Spacer(Modifier.height(16.dp))

        // Amount input
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Date picker trigger
        Text(
            text = dateText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { picker.show() }
                .padding(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Category free-form input
        OutlinedTextField(
            value = categoryText,
            onValueChange = { categoryText = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Save button
        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull()
                if (amt != null &&
                    dateText != "Select Date" &&
                    categoryText.isNotBlank()
                ) {
                    viewModel.addTransaction(
                        Transaction(dateText, categoryText, amt)
                    )
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                    onReturnHome()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}



