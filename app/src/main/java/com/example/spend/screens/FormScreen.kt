package com.example.spend.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.runtime.Composable
import android.widget.Toast

@Composable
fun FormScreen(onReturnHome: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    val categories = listOf("Groceries", "Entertainment", "Bills", "Other")
    var dateText by remember { mutableStateOf("Select Date") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> dateText = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Return Button
        Button(onClick = { onReturnHome() }) {
            Text("Return")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Money")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = dateText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Category") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(onClick = {
                        selectedCategory = category
                        expanded = false
                    }) {
                        Text(text = category)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { 
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && selectedCategory != "Select Category" && dataTest != "Select Date") {
                    val message = "Date: $dataText\nAmount: $amountValue\nCategory: $selectedCategory"
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeTest(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

