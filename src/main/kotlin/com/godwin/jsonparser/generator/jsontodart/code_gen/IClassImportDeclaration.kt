package com.godwin.jsonparser.generator.jsontodart.code_gen

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

/**
 *  annotation and so on class import declaration writer
 * Created by Godwin on 2024/12/20.
 */
interface IClassImportDeclaration {
    fun insertImportClassCode(project: Project?, editFile: Document, className: String)
}