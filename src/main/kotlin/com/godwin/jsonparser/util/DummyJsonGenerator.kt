package com.godwin.jsonparser.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import net.datafaker.Faker

object DummyJsonGenerator {

    private val faker = Faker()
    private val mapper = ObjectMapper()

    fun generateObject(properties: Int, depth: Int): String {
        val node = buildObject(properties, depth)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node)
    }

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
                // last property becomes a nested object when depth allows
                node.set<ObjectNode>(key, buildObject(properties, depth - 1))
            } else {
                putFakeValue(node, key, i)
            }
        }
        return node
    }

    private fun putFakeValue(node: ObjectNode, key: String, index: Int) {
        when (index % 8) {
            0 -> node.put(key, faker.name().fullName())
            1 -> node.put(key, faker.internet().emailAddress())
            2 -> node.put(key, faker.phoneNumber().phoneNumber())
            3 -> node.put(key, faker.address().fullAddress())
            4 -> node.put(key, faker.number().numberBetween(1, 1000))
            5 -> node.put(key, faker.number().randomDouble(2, 1, 10000))
            6 -> node.put(key, faker.bool().bool())
            7 -> node.put(key, faker.lorem().sentence())
        }
    }

    private fun fieldName(index: Int): String {
        return when (index % 8) {
            0 -> "name"
            1 -> "email"
            2 -> "phone"
            3 -> "address"
            4 -> "id"
            5 -> "amount"
            6 -> "active"
            7 -> "description"
            else -> "field$index"
        }.let { base -> if (index >= 8) "${base}_$index" else base }
    }
}
