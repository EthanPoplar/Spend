// app/src/main/kotlin/com/example/spend/viewmodel/TransactionViewModel.kt
package com.example.spend.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.model.Transaction
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionViewModel : ViewModel() {

    // 1️⃣ Loading state
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    // 2️⃣ The live list of transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    /**
     * OCR + parse the bank-statement image (off main thread), then append all found items.
     */
    fun parseAndAdd(uri: Uri, context: Context) = viewModelScope.launch {
        _isProcessing.value = true
        try {
            // Load & down-sample the image on IO
            val image = withContext(Dispatchers.IO) {
                InputImage.fromFilePath(context, uri)
            }

            // Run on-device OCR and suspend until done
            val visionText = TextRecognition
                .getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .await()  // requires kotlinx-coroutines-play-services
            // —— LOG RAW OUTPUT HERE ——
            Log.d("OCR_RAW", visionText.text)
            // Parse the raw text into domain objects
            val items = parseBankStatement(visionText.text)

            // Append them to our current list
            _transactions.value = _transactions.value + items

        } catch (e: Exception) {
            // You can log or expose an error state here if you want
            e.printStackTrace()
        } finally {
            _isProcessing.value = false
        }
    }

    /**
     * Parses lines of a mobile-style bank statement into Transaction(date, description, amount).
     * Expects headers like "Thursday 22 May 2025" and entries like "- $7.99" or "+ $20.00".
     */
    private fun parseBankStatement(raw: String): List<Transaction> {
        val allLines = raw.lines().map { it.trim() }

        // 1. Drop everything starting at the first nav‐menu or footer keyword
        val footers = setOf("Accounts","Transfer & pay","Cards","More","Move Money","Support")
        val lines = allLines.takeWhile { line ->
            footers.none { line.startsWith(it) }
        }

        // 2. Regexes for headers & amounts
        val headerRegex = Regex("""(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)\s+\d{1,2}\s+\w+\s+\d{4}""")
        val amountRegex = Regex("""([+-])\s*\$?([\d,]+\.\d{2})(?:\s*[A-Za-z]{3})?""")

        // 3. Gather (index → normalized date) for every header line
        val dateMap = lines.mapIndexedNotNull { idx, line ->
            headerRegex.find(line)?.value?.let { rawDate ->
                // legacy API—works on all Android versions
                val inFmt  = SimpleDateFormat("EEEE d MMMM yyyy", Locale.ENGLISH)
                val outFmt = SimpleDateFormat("d/M/yyyy",      Locale.ENGLISH)
                inFmt.parse(rawDate)?.let { dt -> idx to outFmt.format(dt) }
            }
        }

        // helper: “what’s the date at or just before this line?”
        fun dateFor(i: Int) = dateMap.lastOrNull { it.first < i }?.second

        val result = mutableListOf<Transaction>()
        // 4. Scan for amounts & build transactions
        lines.mapIndexedNotNull { idx, line ->
            amountRegex.find(line)?.let { m ->
                val (sign, num) = m.destructured
                num.replace(",", "").toDoubleOrNull()?.let { amt ->
                    idx to if (sign=="-") -amt else amt
                }
            }
        }.forEach { (amtIdx, signedAmt) ->
            // 5. Look upwards for a “real” description
            val desc = (amtIdx - 1 downTo 0).map { lines[it] }
                .firstOrNull { cand ->
                    cand.length >= 3
                            && cand.any { it.isLetter() }        // must contain at least one letter
                            && !headerRegex.matches(cand)        // not a date header
                            && amountRegex.find(cand) == null    // not an amount line
                } ?: return@forEach

            // 6. Find the date
            val date = dateFor(amtIdx) ?: return@forEach

            result += Transaction(date, desc, signedAmt)
        }

        return result
    }



    /** Manually add a single entry from FormScreen */
    fun addTransaction(txn: Transaction) {
        _transactions.value = _transactions.value + txn
    }

    /** Remove a single entry when user taps Delete */
    fun removeTransaction(txn: Transaction) {
        _transactions.value = _transactions.value.filter { it != txn }
    }
}

