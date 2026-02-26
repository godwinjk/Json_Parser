package com.godwin.jsonparser.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object JsonDownloader {

    fun getData(inputString: String): String {
        return try {
            val urlWithHeaders = getUrlWithHeaders(inputString)
            val urlContent = urlWithHeaders["url_god"]?.replace("\r\n", "\n") ?: return ""
            val connection = URL(urlContent).openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000

                urlWithHeaders.forEach { (key, value) ->
                    if (key != "url_god") {
                        Log.i("Key: $key, Value: $value")
                        addRequestProperty(key, value)
                    }
                }
            }

            connection.inputStream.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(e.toString())
            ""
        }
    }

    private fun getUrlWithHeaders(input: String): Map<String, String> {
        val lines = input.split("\n")
        val map = mutableMapOf("url_god" to lines[0])

        lines.drop(1).forEach { line ->
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                map[parts[0].trim()] = parts[1].trim()
            } else {
                Log.i("Invalid format in line: $line")
            }
        }

        return map
    }
}
