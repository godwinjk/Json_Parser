package com.godwin.jsonparser.util.repair.strategy

import com.godwin.jsonparser.services.JsonPersistence
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CompletableFuture

class AiRepairStrategy : RepairStrategy() {
    private val workerUrl = "https://jsonrepair.godwinjoseph-k.workers.dev/"

    override fun repair(input: String, error: String?): CompletableFuture<String> {
        if (!JsonPersistence.getInstance().repairUsingAi) {
            return CompletableFuture.completedFuture(input)
        }
        return sendRequest(input, error)
    }

    private fun sendRequest(input: String, error: String?): CompletableFuture<String> {
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build()

        val requestBody = com.google.gson.JsonObject().apply {
            addProperty("json", input)
            addProperty("error", error ?: "")
        }.toString()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(workerUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val result = CompletableFuture<String>()
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .whenComplete { response, error ->
                when {
                    error != null -> result.complete(input)
                    response.statusCode() == 200 -> {
                        // Response body: {"success": true, "repaired": "..."}
                        val repaired = extractRepaired(response.body()) ?: input
                        result.complete(repaired)
                    }

                    else -> result.complete(input)
                }
            }
        return result
    }

    private fun extractRepaired(body: String): String? {
        return try {
            com.google.gson.JsonParser.parseString(body)
                .asJsonObject
                .get("repaired")
                ?.asString
        } catch (e: Exception) {
            null
        }
    }
}
