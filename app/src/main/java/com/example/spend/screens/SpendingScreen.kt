package com.example.spend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

data class SpendingItem(
    val date: String,
    val money: Double,
    val category: String
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpendingScreen(navController: NavController? = null) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Business", "Education", "Entertainment", "Groceries", "Bills")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Spending Overview",
            modifier = Modifier,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Filter by Category",
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Filter by Time Range",
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        var selectedTimeRange by remember { mutableStateOf("All Time") }
        val timeRanges = listOf("All Time", "Last 7 Days", "Last 2 Weeks", "Last Month")
        var timeRangeExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = timeRangeExpanded,
            onExpandedChange = { timeRangeExpanded = !timeRangeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedTimeRange,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeRangeExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = timeRangeExpanded,
                onDismissRequest = { timeRangeExpanded = false }
            ) {
                timeRanges.forEach { range ->
                    DropdownMenuItem(
                        text = { Text(text = range) },
                        onClick = {
                            selectedTimeRange = range
                            timeRangeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Improved chart placeholder with spending summary
        var spendingItems by remember { mutableStateOf(generateSpendingList()) }
        val totalSpent = spendingItems.sumOf { it.money }
        val averageSpent = if (spendingItems.isNotEmpty()) totalSpent / spendingItems.size else 0.0

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Spending Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total: $${String.format("%.2f", totalSpent)}",
                    fontSize = 16.sp
                )
                Text(
                    text = "Average: $${String.format("%.2f", averageSpent)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Transactions: ${spendingItems.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📊 Chart Placeholder",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        val currentDate = remember { Calendar.getInstance() }

        val filteredSpending = spendingItems.filter { spendingItem ->
            val categoryMatch =
                selectedCategory == "All" || spendingItem.category == selectedCategory
            val dateMatch = when (selectedTimeRange) {
                "All Time" -> true
                "Last 7 Days" -> {
                    val itemDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(spendingItem.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    itemDate?.after(calendar.time) ?: false
                }
                "Last 2 Weeks" -> {
                    val itemDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(spendingItem.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -14)
                    itemDate?.after(calendar.time) ?: false
                }
                "Last Month" -> {
                    val itemDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(spendingItem.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MONTH, -1)
                    itemDate?.after(calendar.time) ?: false
                }
                else -> true
            }
            categoryMatch && dateMatch
        }

        // Show filtered count
        Text(
            text = "Showing ${filteredSpending.size} transactions",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Use weight instead of fixed height
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = filteredSpending,
                key = { item -> "${item.date}-${item.category}-${item.money}" } // Add key for performance
            ) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.date,
                            modifier = Modifier.weight(0.25f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = item.category,
                            modifier = Modifier.weight(0.4f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$${String.format("%.2f", item.money)}",
                            modifier = Modifier.weight(0.25f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = {
                                spendingItems = spendingItems.filter { it != item }
                                Toast.makeText(
                                    context,
                                    "Transaction deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete transaction",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Return Button
        Button(
            onClick = {
                navController?.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Text("Return")
        }
    }
}

// Fixed data generation function
private fun generateSpendingList(): List<SpendingItem> {
    val items = mutableListOf<SpendingItem>()
    val categories = listOf("Business", "Education", "Entertainment", "Groceries", "Bills")
    val calendar = Calendar.getInstance()

    // Generate transactions for the last 30 days
    repeat(50) { index -> // Reduced from 120 to 50 for better performance
        calendar.set(2025, Calendar.MAY, 1) // Start from May 1st
        calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(30)) // Add random days within May

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        val money = Random.nextDouble(5.0, 500.0) // Random amount between $5-$500
        val category = categories.random()

        items.add(SpendingItem(date, money, category))
    }

    // Sort by date (newest first)
    return items.sortedByDescending { it.date }
}

@Preview
@Composable
fun SpendingScreenPreview() {
    SpendingScreen()
}