package com.godwin.jsonparser.util.repair

import com.intellij.openapi.project.Project

abstract class RepairStrategy {
    abstract fun repair(project: Project, input: String): String
}
