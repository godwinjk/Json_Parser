package com.godwin.jsonparser.util.repair.strategy

import com.godwin.jsonautorepair.JsonAutoRepair
import com.intellij.openapi.project.Project

class JsonAutoRepairStrategy : RepairStrategy() {
    private companion object {
        const val MAX_ITERATIONS = 5
    }

    override fun repair(project: Project, input: String): String {
        return JsonAutoRepair(MAX_ITERATIONS).repair(input).output
    }
}