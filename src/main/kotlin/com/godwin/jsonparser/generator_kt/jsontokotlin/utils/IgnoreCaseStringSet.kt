package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import java.util.*

/**
 * Created by Godwin on 2024/12/20
 * Description: Set ignore the String Case
 */
class IgnoreCaseStringSet(override val size: Int = 4) : MutableSet<String> {

    private val stringSet = mutableSetOf<String>()

    override fun add(element: String): Boolean {
        return stringSet.add(element.lowercase(Locale.getDefault()))
    }

    override fun addAll(elements: Collection<String>): Boolean {
        return stringSet.addAll(elements.map { it.lowercase(Locale.getDefault()) })
    }

    override fun clear() {
        stringSet.clear()
    }

    override fun contains(element: String): Boolean {
        return stringSet.contains(element.lowercase(Locale.getDefault()))
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return stringSet.containsAll(elements.map { it.lowercase(Locale.getDefault()) })
    }

    override fun isEmpty(): Boolean {
        return stringSet.isEmpty()
    }

    override fun iterator(): MutableIterator<String> {
        return stringSet.iterator()
    }

    override fun remove(element: String): Boolean {
        return stringSet.remove(element.lowercase(Locale.getDefault()))
    }

    override fun removeAll(elements: Collection<String>): Boolean {
        return stringSet.removeAll(elements.map { it.lowercase(Locale.getDefault()) })
    }

    override fun retainAll(elements: Collection<String>): Boolean {
        return stringSet.retainAll(elements.map { it.lowercase(Locale.getDefault()) })
    }

}