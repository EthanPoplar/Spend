// app/src/main/kotlin/com/example/spend/viewmodel/TransactionViewModel.kt
// app/src/main/kotlin/com/example/spend/viewmodel/TransactionViewModel.kt
package com.example.spend.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spend.model.Transaction
import com.example.spend.network.OpenAIParser            // ← new import
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionViewModel : ViewModel() {

    // 1️⃣ Loading state
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    // 2️⃣ The live list of transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    /**
     * OCR the image, then hand off the raw text to OpenAIParser for JSON→Transaction parsing.
     * Same name & signature as before, so you don’t need to touch any callers.
     */
    fun parseAndAdd(uri: Uri, context: Context) = viewModelScope.launch {
        _isProcessing.value = true
        try {
            // 1️⃣ Load & down-sample the image off the main thread
            val image = withContext(Dispatchers.IO) {
                InputImage.fromFilePath(context, uri)
            }

            // 2️⃣ Run on-device OCR and await result
            val visionText = TextRecognition
                .getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .await()

            // Optional: log raw text for debugging
            Log.d("OCR_RAW", visionText.text)

            // 3️⃣ Use GPT to parse raw OCR into structured data
            val gptTxns = withContext(Dispatchers.IO) {
                OpenAIParser.parse(visionText.text)
            }


            // 4️⃣ Map to your Transaction model and append
            val newList = _transactions.value + gptTxns.map {
                Transaction(it.date, it.description, it.amount)
            }
            _transactions.value = newList

        } catch (e: Exception) {
            Log.e("TxnViewModel", "Failed to parse via LLM", e)
        } finally {
            _isProcessing.value = false
        }
    }

    /** Manually add a single entry from FormScreen */
    fun addTransaction(txn: Transaction) {
        _transactions.value = _transactions.value + txn
    }

    /** Remove a single entry when user taps Delete */
    fun removeTransaction(txn: Transaction) {
        _transactions.value = _transactions.value.filter { it != txn }
    }
    fun pingOpenAI(onResult: (String)->Unit, onError: (String)->Unit) = viewModelScope.launch {
        try {
            val reply = OpenAIParser.ping()
            onResult(reply)
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
        }
    }

}


