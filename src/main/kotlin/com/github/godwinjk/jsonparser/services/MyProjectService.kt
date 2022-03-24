package com.github.godwinjk.jsonparser.services

import com.intellij.openapi.project.Project
import com.github.godwinjk.jsonparser.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
