// app/src/main/kotlin/com/example/spend/screens/ChartScreen.kt
package com.example.spend.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.spend.model.Transaction
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import android.graphics.Paint

private enum class ChartPeriod { WEEKLY, MONTHLY }
private data class PeriodData(val label: String, val avgAmount: Double)

@Composable
fun ChartScreen(
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit
) {
    val barColor = MaterialTheme.colors.primary

    var chartPeriod by remember { mutableStateOf(ChartPeriod.WEEKLY) }

    // **Use ISO_LOCAL_DATE for "YYYY-MM-DD"**
    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val groups by remember(transactions, chartPeriod) {
        mutableStateOf(
            transactions
                .groupBy { txn ->
                    val date = LocalDate.parse(txn.date, fmt)
                    when (chartPeriod) {
                        ChartPeriod.WEEKLY  ->
                            "${date.get(IsoFields.WEEK_BASED_YEAR)}-W${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)}"
                        ChartPeriod.MONTHLY ->
                            "${date.monthValue}/${date.year}"
                    }
                }
                .map { (label, list) ->
                    val sum  = list.sumOf { it.amount }
                    val days = when (chartPeriod) {
                        ChartPeriod.WEEKLY -> 7
                        ChartPeriod.MONTHLY -> {
                            // still parse the first date in ISO
                            val firstDate = LocalDate.parse(list.first().date, fmt)
                            YearMonth.of(firstDate.year, firstDate.monthValue).lengthOfMonth()
                        }
                    }
                    PeriodData(label, sum / days)
                }
                .sortedBy { it.label }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
        Spacer(Modifier.height(16.dp))
        Row {
            ChartPeriod.values().forEach { period ->
                val text = period.name.lowercase().replaceFirstChar { it.uppercase() }
                OutlinedButton(
                    onClick = { chartPeriod = period },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            if (groups.isEmpty()) return@Canvas

            val maxVal = groups.maxOf { it.avgAmount }.toFloat()
            val tickCount = 5

            val paint = Paint().apply {
                isAntiAlias = true
                textSize = 32f
                color = androidx.compose.ui.graphics.Color.Black.toArgb()
            }
            val yAxisX = 60f
            val bottomMargin = 60f

            // Y‐axis
            drawLine(
                color = androidx.compose.ui.graphics.Color.Gray,
                start = Offset(yAxisX, 0f),
                end   = Offset(yAxisX, size.height - bottomMargin),
                strokeWidth = 2f
            )
            for (i in 0..tickCount) {
                val y = (size.height - bottomMargin) * (1 - i.toFloat() / tickCount)
                drawLine(
                    color = androidx.compose.ui.graphics.Color.Gray,
                    start = Offset(yAxisX - 8f, y),
                    end   = Offset(yAxisX, y),
                    strokeWidth = 2f
                )
                val value = maxVal * i / tickCount
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(value),
                    0f,
                    y + paint.textSize / 2f,
                    paint
                )
            }

            // X‐axis
            drawLine(
                color = androidx.compose.ui.graphics.Color.Gray,
                start = Offset(yAxisX, size.height - bottomMargin),
                end   = Offset(size.width, size.height - bottomMargin),
                strokeWidth = 2f
            )

            // Bars & labels
            val chartWidth = size.width - yAxisX
            val barWidth   = chartWidth / groups.size
            groups.forEachIndexed { idx, pd ->
                val x0 = yAxisX + idx * barWidth
                val h  = (pd.avgAmount.toFloat() / maxVal) * (size.height - bottomMargin)
                drawRect(
                    color   = barColor,
                    topLeft = Offset(x0 + barWidth * 0.1f, size.height - bottomMargin - h),
                    size    = Size(barWidth * 0.8f, h)
                )
                drawContext.canvas.nativeCanvas.drawText(
                    pd.label,
                    x0 + barWidth * 0.1f,
                    size.height - bottomMargin + paint.textSize + 4f,
                    paint
                )
            }
        }
    }
}




