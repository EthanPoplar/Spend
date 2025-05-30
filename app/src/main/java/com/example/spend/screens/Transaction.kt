package com.example.spend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Transaction(
    val date: String,
    val money: Double,
    val category: String
)

@Composable
fun SimpleChart(transactions: List<Transaction>) {
    val categoryTotals = transactions.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.money } }

    if (categoryTotals.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No transactions to display",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Add some transactions to see the chart",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        return
    }

    val maxAmount = categoryTotals.values.maxOrNull() ?: 1.0
    val colors = listOf(
        Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFF44336),
        Color(0xFFFF9800), Color(0xFF9C27B0)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Spending by Category",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        categoryTotals.entries.forEachIndexed { index, (category, amount) ->
            val percentage = (amount / maxAmount).coerceAtMost(1.0)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    modifier = Modifier.width(80.dp),
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .background(Color.LightGray, RoundedCornerShape(10.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(percentage.toFloat())
                            .background(
                                colors[index % colors.size],
                                RoundedCornerShape(10.dp)
                            )
                    )
                }

                Text(
                    text = "$${String.format("%.0f", amount)}",
                    modifier = Modifier.width(60.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Transaction(
    viewModel: Any, // Your existing ViewModel - replace 'Any' with your actual ViewModel type
    onNavigateBack: () -> Unit,
    onAddTransaction: () -> Unit,
    onViewChart: () -> Unit
) {
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

        // TODO: Replace this with your ViewModel call
        // Example: val allTransactions = viewModel.getAllTransactions()
        // For now, using empty list - you need to connect your ViewModel here
        val allTransactions = emptyList<Transaction>() // REPLACE THIS LINE

        val context = LocalContext.current

        // Chart section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = 4.dp
        ) {
            SimpleChart(allTransactions)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onAddTransaction) {
                Text("Add Transaction")
            }
            Button(onClick = onViewChart) {
                Text("View Chart")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredSpending = allTransactions.filter { transaction ->
            val categoryMatch = selectedCategory == "All" || transaction.category == selectedCategory
            val dateMatch = when (selectedTimeRange) {
                "All Time" -> true
                "Last 7 Days" -> {
                    val itemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(transaction.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    itemDate?.after(calendar.time) ?: false
                }
                "Last 2 Weeks" -> {
                    val itemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(transaction.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -14)
                    itemDate?.after(calendar.time) ?: false
                }
                "Last Month" -> {
                    val itemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(transaction.date)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MONTH, -1)
                    itemDate?.after(calendar.time) ?: false
                }
                else -> true
            }
            categoryMatch && dateMatch
        }

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
                .weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = filteredSpending,
                key = { item -> "${item.date}-${item.category}-${item.money}" }
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
                            text = "$%.2f".format(item.money),
                            modifier = Modifier.weight(0.25f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = {
                                // TODO: Call your ViewModel's delete method
                                // Example: viewModel.deleteTransaction(item)
                                Toast.makeText(
                                    context,
                                    "Delete transaction: ${item.category}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("Return")
        }
    }
}

@Preview
@Composable
fun TransactionPreview() {
    Transaction(
        viewModel = Any(),
        onNavigateBack = {},
        onAddTransaction = {},
        onViewChart = {}
    )
}