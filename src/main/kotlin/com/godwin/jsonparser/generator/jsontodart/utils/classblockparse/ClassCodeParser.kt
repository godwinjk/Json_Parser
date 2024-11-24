package com.godwin.jsonparser.generator.jsontodart.utils.classblockparse

import com.godwin.jsonparser.generator.jsontodart.utils.getClassNameFromClassBlockString
import com.godwin.jsonparser.generator.jsontodart.utils.getFileNameFromClassBlockString

/**
 * parser for parse class block string, with this util, we could get the class struct elements
 */
class ClassCodeParser(private val classBlockString: String) {

    fun getDartDataClass(): ParsedDartDataClass {
        return ParsedDartDataClass(getClassAnnotations(), getClassName(), getFileName(), getProperties())
    }

    fun getClassName(): String {
        return getClassNameFromClassBlockString(classBlockString)
    }

    fun getFileName(): String {
        return getFileNameFromClassBlockString(classBlockString)
    }

    fun getClassAnnotations(): List<String> {
        val annotationsBlock = classBlockString.substringBefore("class").trim()
        return annotationsBlock.split("\n").filter { it.contains("@") }.map { it.trim() }
    }

    fun getProperties(): List<ParsedDartDataClass.Property> {
        val propertiesBlock = classBlockString.substringAfter("{").substringBeforeLast("}").trim()

        val lines = propertiesBlock.split("\n")

        val properties = mutableListOf<ParsedDartDataClass.Property>()

        val propertyLinesList = getPropertyLinesList(lines)

        propertyLinesList.forEachIndexed { index, propertyBlockLines ->
            val annotations = getPropertyAnnotations(propertyBlockLines)
            val propertyKeyword = getPropertyKeyword(propertyBlockLines.last())
            val propertyName = getPropertyName(propertyBlockLines.last())
            val isLastLine = index == propertyLinesList.lastIndex
            val propertyType = getPropertyType(propertyBlockLines.last(), isLastLine)
            val propertyValue = getPropertyValue(propertyBlockLines.last(), isLastLine)
            val propertyComment = getPropertyComment(propertyBlockLines.last())
            properties.add(
                ParsedDartDataClass.Property(
                    annotations,
                    propertyKeyword,
                    propertyName,
                    propertyType,
                    propertyValue,
                    propertyComment,
                    isLastLine
                )
            )
        }
        return properties
    }

    private fun getPropertyLinesList(lines: List<String>): List<List<String>> {
        val propertyLinesList = mutableListOf<List<String>>()
        var propertyLines = mutableListOf<String>()
        lines.forEach {
            val deleteCommentLine = it.substringBefore("//").trim()
            if (deleteCommentLine.endsWith(";")) {
                propertyLines.add(it)
                propertyLinesList.add(propertyLines)
                propertyLines = mutableListOf()
            } else {
                propertyLines.add(it)
            }
        }
        return propertyLinesList

    }

    private fun getPropertyAnnotations(lines: List<String>): List<String> {
        return listOf()
    }

    private fun getPropertyKeyword(propertyLine: String): String {
        val stringBeforeLastColonWithoutComment = propertyLine.substringBefore("//").substringBeforeLast(";").trim()
        val keyword = stringBeforeLastColonWithoutComment.split(" ").first()
        return if (keyword == "final") "final" else ""
    }

    private fun getPropertyName(propertyLine: String): String {
        val stringBeforeLastColonWithoutComment = propertyLine.substringBefore("//").substringBeforeLast(";").trim()
        return stringBeforeLastColonWithoutComment.split(" ").last()
    }

    private fun getPropertyType(propertyLine: String, isLastLine: Boolean): String {
        val stringBeforeLastColonWithoutComment = propertyLine.substringBefore("//").substringBeforeLast(";").trim()
        val index = if (getPropertyKeyword(propertyLine) != "") 1 else 0
        return stringBeforeLastColonWithoutComment.split(" ")[index]
    }

    private fun getPropertyValue(propertyLine: String, isLastLine: Boolean): String {
        val deleteCommentPropertyLine = propertyLine.substringBefore("//")
        return if (deleteCommentPropertyLine.contains("=")) {
            val propertyValuePre = deleteCommentPropertyLine.split("=").last()
            if (isLastLine) {
                propertyValuePre.trim()
            } else {
                propertyValuePre.trim().dropLast(1)
            }
        } else {
            ""
        }
    }

    private fun getPropertyComment(propertyLine: String): String {
        return if (propertyLine.contains("//"))
            propertyLine.substringAfter("//").trim()
        else
            ""
    }
}
