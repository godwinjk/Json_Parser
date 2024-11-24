package com.godwin.jsonparser.generator.jsontodart.utils.classblockparse

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.classscodestruct.Property
import com.godwin.jsonparser.generator.jsontodart.utils.getClassesStringList

/**
 * parser which to parse the code generate in nested class model and no comment and no annotation, means pure.
 */
@Deprecated("Please use #NestedClassModelClassesCodeParser")
class PureNestedClassModelClassesCodeParser(private val classesCode: String) {

    fun parse(): DartClass {

        if (classesCode.contains("//") || classesCode.contains("@")) {
            throw IllegalArgumentException("Can't support this classes code for it has comment or annotations $classesCode")
        }

        return if (classesCode.contains("{").not()) {
            parsedToKotlinDataClass(classesCode)
        } else {
            val trimedClassesCode = classesCode.trim()
            val tobeParsedCode = trimedClassesCode.substringBefore("{")
            val tobeParsedNestedClassesCode = trimedClassesCode.substringAfter("{").substringBeforeLast("}")
            val parentClass = parsedToKotlinDataClass(tobeParsedCode)
            val subClasses = getClassesStringList(tobeParsedNestedClassesCode).map { parsedToKotlinDataClass(it) }
            parentClass.copy(nestedClasses = subClasses)
        }


    }

    private fun parsedToKotlinDataClass(classCode: String): DartClass {
        val tobeParsedCode = classCode.trim()
        val parsedKotlinDataClass = ClassCodeParser(tobeParsedCode).getDartDataClass()
        return toKotlinDataClass(parsedKotlinDataClass)
    }

    private fun toKotlinDataClass(parsedDartDataClass: ParsedDartDataClass): DartClass {
        val properties = parsedDartDataClass.properties.map {
            Property(
                annotations = listOf(),
                keyword = it.keyword,
                originName = it.propertyName,
                name = it.propertyName,
                type = it.propertyType,
                value = it.propertyValue,
                comment = it.propertyComment,
                isLast = it.isLastProperty,
                originJsonValue = ""
            )
        }
        return DartClass(annotations = listOf(), name = parsedDartDataClass.name, properties = properties)
    }

}
