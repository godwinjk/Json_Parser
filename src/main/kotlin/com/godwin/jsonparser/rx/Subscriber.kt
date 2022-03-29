package com.godwin.jsonparser.rx

import java.util.*

object Subscriber {

    private val list = LinkedList<Publisher>()

    fun add(p: Publisher) {
        if (!list.contains(p)) {
            list.add(p)
        }
    }

    fun remove(p: Publisher) {
        list.remove(p)
    }

    fun publishMessage(message: String) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            iterator.next().onMessage(message)
        }
    }
}