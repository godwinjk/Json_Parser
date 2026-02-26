package com.godwin.jsonparser.ui.tabs

class TabIdCollection {
    fun getId(): Int {
        for (i in 0 until MAX_COUNT) {
            if (i !in list) return i
        }
        return MAX_COUNT
    }

    fun removeId() {}

    companion object {
        private val list = mutableListOf<Int>()
        private const val MAX_COUNT = 11
    }
}
