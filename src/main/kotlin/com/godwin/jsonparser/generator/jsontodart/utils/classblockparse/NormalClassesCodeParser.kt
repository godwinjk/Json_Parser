package com.godwin.jsonparser.generator.jsontodart.utils.classblockparse

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.KotlinDataClass
import com.godwin.jsonparser.generator.jsontodart.utils.getClassesStringList

class NormalClassesCodeParser(private val classesCode: String) {
    fun parse(): List<KotlinDataClass> {
        return getClassesStringList(classesCode)
            .map {
                KotlinDataClass.fromParsedKotlinDataClass(ClassCodeParser(it).getKotlinDataClass())
            }
    }
}