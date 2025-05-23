package com.example.spend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Button
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

data class SpendingScreen(
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
            expanded = timeRangeExpanded ,
            onExpandedChange = { timeRangeExpanded  = !timeRangeExpanded  },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedTimeRange,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeRangeExpanded )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = timeRangeExpanded ,
                onDismissRequest = { timeRangeExpanded  = false }
            ) {
                categories.forEach { range ->
                    DropdownMenuItem(
                        text = { Text(text = range) },
                        onClick = {
                            selectedCategory = range
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dummy chart placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Chart Placeholder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        var spendingScreen by remember { mutableStateOf(generateSpendingList()) }
        val context = LocalContext.current
        val currentDate = remember { Calendar.getInstance() }

        val filteredSpending = spendingScreen.filter { spendingItem ->
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

        LazyColumn (modifier = Modifier.fillMaxWidth().height(300.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(spendingScreen) { item ->
                Box (modifier = Modifier
                    .fillMaxWidth().height(50.dp)
                    .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ){
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.date,
                            modifier = Modifier.weight(0.25f).padding(start = 16.dp)
                        )
                        Text(
                            text = item.category,
                            modifier = Modifier.weight(0.4f).padding(start = 16.dp)
                        )
                        Text(
                            text = "$%.2f".format(item.money),
                            modifier = Modifier.weight(0.25f)
                        )
                        IconButton(
                            onClick = {
                                spendingScreen = spendingScreen.filter { it != item }
                                Toast.makeText(
                                    context,
                                    "Delete successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(0.1f).padding(end = 16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Return Button
        Button(onClick = {
            navController?.navigate("home") {
                popUpTo("home"){ inclusive = true }
            }
        }
        ) {
            Text("Return")
        }
    }
}

private fun generateSpendingList(): List<SpendingScreen>{
    return List(120) { index ->
        val day = (index / 4) + 1
        val exactDay = day.toString().padStart(2, '0')
        val date = "2025-05-$exactDay"

        val money = (Math.random() * 400 + 1.23)

        val categories = listOf("Business", "Education", "Entertainment", "Groceries", "Bills")
        val category = categories[(Math.random() * categories.size).toInt()]

        SpendingScreen(date, money, category)
    }
}

@Preview()
@Composable
fun SpendingScreenPreview() {
    SpendingScreen()

}