package com.example.spend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class SpendingScreen(
    val date: String,
    val money: Double,
    val category: String
)

@Composable
fun SpendingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Return Button
        Button(onClick = { /* Return placeholder */ }) {
            Text("Return")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Spending Overview")

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

        LazyColumn (modifier = Modifier.fillMaxWidth()
            .height(300.dp), contentPadding = PaddingValues(8.dp),
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
    }
}

private fun generateSpendingList(): List<SpendingScreen>{
    return List(120) { index ->
        val day = (index / 4) + 1
        val exactDay = day.toString().padStart(2, '0')
        val date = "2025-05-$exactDay"

        val money = (Math.random() * 400 + 1.23)

        val categories = listOf("Business", "Education", "Entertainment", "Groceries")
        val category = categories[(Math.random() * categories.size).toInt()]

        SpendingScreen(date, money, category)
    }
}

@Preview()
@Composable
fun SpendingScreenPreview() {
    SpendingScreen()
}