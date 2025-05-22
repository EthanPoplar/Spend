// app/src/main/kotlin/com/example/spend/network/OpenAIParser.kt
package com.example.spend.network


import android.util.Log
import com.example.spend.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


@Serializable
data class GPTTxn(
    val date: String,
    val description: String,
    val amount: Double
)

object OpenAIParser {
    private val client = OkHttpClient()
    private val json   = Json { ignoreUnknownKeys = true }

    /**
     * Sends raw OCR text to OpenAI in a chat-completion request,
     * logs the outgoing JSON (so you can debug size/contents),
     * and returns a list of GPTTxn parsed from the response.
     */
    suspend fun parse(rawText: String): List<GPTTxn> = withContext(Dispatchers.IO) {
        // 1️⃣ Log the key so you can confirm it's being injected correctly.
        Log.d("OPENAI_KEY", BuildConfig.OPENAI_API_KEY)

        // 2️⃣ Build the ChatCompletion payload
        val payloadJson = buildJsonObject {
            put("model", JsonPrimitive("gpt-4o"))
            putJsonArray("messages") {
                // 🔒 Locked‐down system prompt
                addJsonObject {
                    put("role", "system")
                    put(
                        "content",
                        """
                You are a bank‐statement parser. 
                Output **exactly** and only a JSON array of transaction objects—no markdown fences, no commentary:

                [
                  {
                    "date": "YYYY-MM-DD",
                    "description": "...",
                    "amount": -123.45
                  },
                  ...
                ]

                • date: ISO format (YYYY-MM-DD)  
                • description: merchant or description text  
                • amount: signed number (negative for debits, positive for credits)  

                Discard any UI chrome, menus, headers, footers—return nothing else.
                """.trimIndent()
                    )
                }
                // Your OCR dump goes here
                addJsonObject {
                    put("role", "user")
                    put("content", rawText)
                }
            }
            put("temperature", JsonPrimitive(0))
        }

        // 3️⃣ Debug-log the full outgoing JSON and its size
        val payloadString = payloadJson.toString()
        Log.d("OpenAI_REQUEST", payloadString)
        Log.d("OpenAI_REQUEST", "Length = ${payloadString.length} chars")

        // 4️⃣ Build and execute the HTTP request
        val body = payloadString
            .toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(body)
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw Exception("OpenAI error ${resp.code}")
            }
            val respText = resp.body?.string() ?: throw Exception("Empty response")

            // 5️⃣ Extract the JSON array from choices[0].message.content
            val root = json.parseToJsonElement(respText).jsonObject
            val content = root["choices"]!!
                .jsonArray[0].jsonObject["message"]!!
                .jsonObject["content"]!!.jsonPrimitive.content

            // 6️⃣ Decode that JSON array into our data class list
            return@withContext json.decodeFromString(content)
        }
    }
    /**
     * A simple connectivity test: send “Hello” and return whatever GPT replies.
     */
    suspend fun ping(): String = withContext(Dispatchers.IO) {
        // Build a minimal ChatCompletion request
        val payload = buildJsonObject {
            put("model", JsonPrimitive("gpt-3.5-turbo"))
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "system")
                    put("content", "You are a helpful assistant.")
                }
                addJsonObject {
                    put("role", "user")
                    put("content", "Hello!")
                }
            }
            put("temperature", JsonPrimitive(0.7))
        }

        // Log the request so you can inspect it
        val asString = payload.toString()
        Log.d("PingRequest", asString)
        Log.d("PingRequest", "Length = ${asString.length} chars")

        // Build & execute
        val body = asString.toRequestBody("application/json".toMediaType())
        val req = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .post(body)
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw Exception("Ping failed: HTTP ${resp.code}")
            }
            // extract the assistant’s reply
            val root = json.parseToJsonElement(resp.body!!.string()).jsonObject
            return@withContext root["choices"]!!
                .jsonArray[0].jsonObject["message"]!!
                .jsonObject["content"]!!.jsonPrimitive.content
        }
    }

}


