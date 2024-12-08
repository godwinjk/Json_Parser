package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator.jsontodart.filetype.GenFileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

object ModuleChecker {

    fun detectProjectType(project: Project): GenFileType {
        val isFlutter = isFlutterProject(project)

        // Check for Kotlin project (look for Kotlin files or build.gradle)
        val isKotlin = isKotlinProject(project)

        return when {
            isFlutter -> GenFileType.Dart
            isKotlin -> GenFileType.Kotlin
            else -> GenFileType.UnInitialized
        }
    }
}

fun isFlutterProject(project: Project): Boolean {
    // Check for the presence of 'pubspec.yaml' file in the project
    val flutterFile = FilenameIndex.getVirtualFilesByName("pubspec.yaml", GlobalSearchScope.allScope(project))
    return flutterFile.isNotEmpty()
}

fun isKotlinProject(project: Project): Boolean {
    // Check for Kotlin files or 'build.gradle' files
    val kotlinFiles = FileTypeIndex.getFiles(
        FileTypeManager.getInstance().getFileTypeByExtension("kt"),
        GlobalSearchScope.allScope(project)
    )
    val gradleFiles = FilenameIndex.getVirtualFilesByName("build.gradle", GlobalSearchScope.allScope(project)) +
            FilenameIndex.getVirtualFilesByName("build.gradle.kts", GlobalSearchScope.allScope(project))

    return kotlinFiles.isNotEmpty() || gradleFiles.isNotEmpty()
}