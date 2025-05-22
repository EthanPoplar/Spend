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
import java.time.format.DateTimeFormatter
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
    // Read theme color *outside* of Canvas (so it isn't a Composable call inside drawScope)
    val barColor = MaterialTheme.colors.primary

    // State for the toggle
    var chartPeriod by remember { mutableStateOf(ChartPeriod.WEEKLY) }

    // Precompute grouping & averages
    val fmt = DateTimeFormatter.ofPattern("d/M/yyyy")
    val groups by remember(transactions, chartPeriod) {
        mutableStateOf(
            transactions
                .groupBy { txn ->
                    val date = LocalDate.parse(txn.date, fmt)
                    when (chartPeriod) {
                        ChartPeriod.WEEKLY  -> "${date.get(IsoFields.WEEK_BASED_YEAR)}-W${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}"
                        ChartPeriod.MONTHLY -> "${date.monthValue}/${date.year}"
                    }
                }
                .map { (label, list) ->
                    val sum  = list.sumOf { it.amount }
                    val days = when (chartPeriod) {
                        ChartPeriod.WEEKLY  -> 7
                        ChartPeriod.MONTHLY -> YearMonth.of(
                            LocalDate.parse(list.first().date, fmt).year,
                            LocalDate.parse(list.first().date, fmt).monthValue
                        ).lengthOfMonth()
                    }
                    PeriodData(label, sum / days)
                }
                .sortedBy { it.label }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Back button
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
        Spacer(Modifier.height(16.dp))

        // Period toggle
        Row {
            ChartPeriod.values().forEach { period ->
                OutlinedButton(
                    onClick = { chartPeriod = period },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(period.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // The actual bar chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (groups.isEmpty()) return@Canvas

            val maxVal = groups.maxOf { it.avgAmount }.toFloat()
            val barWidth = size.width / groups.size

            groups.forEachIndexed { i, (label, avg) ->
                val h = (avg.toFloat() / maxVal) * size.height
                drawRect(
                    color   = barColor,
                    topLeft = Offset(i * barWidth, size.height - h),
                    size    = Size(barWidth * 0.8f, h)
                )
                // if you later want to draw the 'label', you'll need to use
                // drawContext.canvas.nativeCanvas.drawText(...)
            }
        }
    }
}


