package com.godwin.jsonparser.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.random.Random

object DummyJsonGenerator {

    private val mapper = ObjectMapper()
    private val rng = Random.Default

    private val firstNames = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry", "Iris", "Jack")
    private val lastNames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Moore")
    private val domains = listOf("gmail.com", "yahoo.com", "outlook.com", "example.com", "mail.com")
    private val streets = listOf("Main St", "Oak Ave", "Maple Dr", "Cedar Ln", "Pine Rd", "Elm Blvd", "Park Way")
    private val cities = listOf("New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio")
    private val words = listOf("lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "sed", "do")

    fun generateObject(properties: Int, depth: Int): String =
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(buildObject(properties, depth))

    fun generateArray(properties: Int, depth: Int, arraySize: Int): String {
        val array: ArrayNode = mapper.createArrayNode()
        repeat(arraySize) { array.add(buildObject(properties, depth)) }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array)
    }

    private fun buildObject(properties: Int, depth: Int): ObjectNode {
        val node: ObjectNode = mapper.createObjectNode()
        repeat(properties) { i ->
            val key = fieldName(i)
            if (depth > 1 && i == properties - 1) {
                node.set<ObjectNode>(key, buildObject(properties, depth - 1))
            } else {
                putFakeValue(node, key, i)
            }
        }
        return node
    }

    private fun putFakeValue(node: ObjectNode, key: String, index: Int) {
        when (index % 8) {
            0 -> node.put(key, "${firstNames.random(rng)} ${lastNames.random(rng)}")
            1 -> node.put(key, "${firstNames.random(rng).lowercase()}.${lastNames.random(rng).lowercase()}@${domains.random(rng)}")
            2 -> node.put(key, "+1-${rng.nextInt(200, 999)}-${rng.nextInt(100, 999)}-${rng.nextInt(1000, 9999)}")
            3 -> node.put(key, "${rng.nextInt(1, 9999)} ${streets.random(rng)}, ${cities.random(rng)}")
            4 -> node.put(key, rng.nextInt(1, 1000))
            5 -> node.put(key, (rng.nextDouble() * 9999 + 1).toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble())
            6 -> node.put(key, rng.nextBoolean())
            7 -> node.put(key, (1..rng.nextInt(5, 10)).joinToString(" ") { words.random(rng) } + ".")
        }
    }

    private fun fieldName(index: Int): String {
        val base = when (index % 8) {
            0 -> "name"; 1 -> "email"; 2 -> "phone"; 3 -> "address"
            4 -> "id"; 5 -> "amount"; 6 -> "active"; 7 -> "description"
            else -> "field$index"
        }
        return if (index >= 8) "${base}_$index" else base
    }
}
