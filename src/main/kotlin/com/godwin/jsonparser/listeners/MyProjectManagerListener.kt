package com.godwin.jsonparser.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.godwin.jsonparser.services.ProjectService

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<ProjectService>()
    }


    override fun projectClosed(project: Project) {
        super.projectClosed(project)
    }

    override fun projectClosing(project: Project) {
        super.projectClosing(project)
    }

    override fun projectClosingBeforeSave(project: Project) {
        super.projectClosingBeforeSave(project)
    }
}
