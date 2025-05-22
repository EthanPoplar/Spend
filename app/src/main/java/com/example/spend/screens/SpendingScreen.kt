// app/src/main/kotlin/com/example/spend/screens/SpendingScreen.kt
package com.example.spend.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spend.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class SortOption(val label: String) {
    DATE_ASC("Date ↑"),
    DATE_DESC("Date ↓"),
    AMOUNT_ASC("Amount ↑"),
    AMOUNT_DESC("Amount ↓")
}

@Composable
fun SpendingScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    onAddTransaction: () -> Unit,
    onViewChart: () -> Unit
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    // --- Sort menu state ---
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(SortOption.DATE_ASC) }

    // --- Compute sorted list whenever underlying data or sort choice changes ---
    val sortedTransactions = remember(transactions, selectedSort) {
        // helper to parse "d/M/yyyy"
        fun parseDate(s: String): LocalDate {
            val fmt = DateTimeFormatter.ofPattern("d/M/yyyy")
            return LocalDate.parse(s, fmt)
        }

        when (selectedSort) {
            SortOption.DATE_ASC   -> transactions.sortedBy { parseDate(it.date) }
            SortOption.DATE_DESC  -> transactions.sortedByDescending { parseDate(it.date) }
            SortOption.AMOUNT_ASC -> transactions.sortedBy { it.amount }
            SortOption.AMOUNT_DESC-> transactions.sortedByDescending { it.amount }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Top row: Return, Add & Sort ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onNavigateBack) {
                Text("Return")
            }
            Button(onClick = onAddTransaction) {
                Text("Add")
            }
            Button(onClick = onViewChart)    {
                Text("View Chart")
            }
            Box {
                Button(onClick = { sortMenuExpanded = true }) {
                    Text(selectedSort.label)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort options")
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedSort = option
                            sortMenuExpanded = false
                        }) {
                            Text(option.label)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))


        Spacer(Modifier.height(16.dp))



        Spacer(Modifier.height(16.dp))

        // --- Transaction list ---
        if (sortedTransactions.isEmpty()) {
            Text("No transactions yet.", style = MaterialTheme.typography.body1)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedTransactions) { txn ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(txn.date, style = MaterialTheme.typography.body1)
                            Text(txn.description, style = MaterialTheme.typography.body2)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$${"%.2f".format(txn.amount)}",
                                style = MaterialTheme.typography.body1
                            )
                            IconButton(onClick = {
                                viewModel.removeTransaction(txn)
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingScreenPreview() {
    val dummyVm = TransactionViewModel()
    SpendingScreen(
        viewModel         = dummyVm,
        onNavigateBack    = {},
        onAddTransaction  = {},
        onViewChart       = {}
    )
}

