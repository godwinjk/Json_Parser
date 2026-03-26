package com.godwin.jsonparser.util.repair

import com.godwin.jsonparser.common.exception.InputTooLargeException
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.Log
import com.godwin.jsonparser.util.repair.strategy.JsonAutoRepairStrategy
import com.godwin.jsonparser.util.repair.strategy.PsiRepairStrategy
import com.intellij.openapi.project.Project

object JsonRepairEngine {

    private val strategies = listOf(
        JsonAutoRepairStrategy(),
        PsiRepairStrategy()
    )

    fun repair(project: Project, input: String): String? {
        return null;
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
        val result = JsonUtils.isValidJson(text)

        return if (result) text else null
    }
}
