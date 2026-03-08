package com.godwin.jsonparser.util.analytics

import com.godwin.jsonparser.services.JsonPersistence
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.PermanentInstallationID
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object AnalyticsService {
    private const val WORKER_URL = "https://jsonparser.godwinjoseph-k.workers.dev/"

    private val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    val userId = PermanentInstallationID.get()
    val ideVersion: String? = ApplicationInfo.getInstance().versionName
    private val sessionId = UUID.randomUUID().toString()

    fun track(eventName: String) {
        if (!JsonPersistence.getInstance().analyticsEnabled) return

        // Create the JSON payload
        val json = """
            {
                "event_name": "$eventName",
                "ide_version": "$ideVersion",
                "user_id": "$userId",
                "session_id": "$sessionId"
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(WORKER_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build()

        // Send asynchronously to avoid blocking the UI thread
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept { response ->
                if (response.statusCode() != 202) {
                    println("Analytics Error: ${response.statusCode()}")
                }
            }
    }
}