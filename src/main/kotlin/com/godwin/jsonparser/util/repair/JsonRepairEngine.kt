package com.godwin.jsonparser.util.repair

import com.godwin.jsonparser.common.exception.InputTooLargeException
import com.godwin.jsonparser.util.Log
import com.intellij.openapi.project.Project

object JsonRepairEngine {

    private val strategies = listOf(
        PsiRepairStrategy(),
        JacksonRepairStrategy()
    )

    fun repair(project: Project, input: String): String {
        if (input.length > 1_000_000) {
            throw InputTooLargeException("Input is too large")
        }

        var text = input
        strategies.forEach { strategy ->
            try {
                text = strategy.repair(project, text)
            } catch (e: Exception) {
                Log.e("${strategy.javaClass} Failed: ${e.message}")
            }
        }
        return text
    }
}
