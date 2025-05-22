// app/src/main/kotlin/com/example/spend/screens/ChartScreen.kt
package com.example.spend.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.spend.model.Transaction
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.IsoFields

private enum class ChartPeriod { WEEKLY, MONTHLY }
private data class PeriodData(val label: String, val avgAmount: Double)

/**
 * A standalone screen to display a bar-chart of average daily spend,
 * with a weekly/monthly toggle.
 */
@Composable
fun ChartScreen(
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit
) {
    val barColor = MaterialTheme.colors.primary
    var chartPeriod by remember { mutableStateOf(ChartPeriod.WEEKLY) }

    val groups by remember(transactions, chartPeriod) {
        mutableStateOf(
            transactions
                .groupBy { txn ->
                    val date = LocalDate.parse(txn.date) // ISO "YYYY-MM-DD"
                    when (chartPeriod) {
                        ChartPeriod.WEEKLY  ->
                            "${date.get(IsoFields.WEEK_BASED_YEAR)}-W${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}"
                        ChartPeriod.MONTHLY ->
                            "${date.monthValue}/${date.year}"
                    }
                }
                .map { (label, list) ->
                    val sum = list.sumOf { it.amount }
                    val days = when (chartPeriod) {
                        ChartPeriod.WEEKLY -> 7
                        ChartPeriod.MONTHLY -> {
                            // parse the first date in that bucket
                            val firstDate = LocalDate.parse(list.first().date)
                            YearMonth.of(firstDate.year, firstDate.monthValue)
                                .lengthOfMonth()
                        }
                    }
                    PeriodData(label, sum / days)
                }
                .sortedBy { it.label }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
        Spacer(Modifier.height(16.dp))

        Row {
            ChartPeriod.values().forEach { period ->
                OutlinedButton(
                    onClick = { chartPeriod = period },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        period.name
                            .lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (groups.isEmpty()) return@Canvas

            val maxVal = groups.maxOf { it.avgAmount }.toFloat()
            val barWidth = size.width / groups.size

            groups.forEachIndexed { i, (_, avg) ->
                val height = (avg.toFloat() / maxVal) * size.height
                drawRect(
                    color   = barColor,
                    topLeft = Offset(i * barWidth, size.height - height),
                    size    = Size(barWidth * 0.8f, height)
                )
            }
        }
    }
}



