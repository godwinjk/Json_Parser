package com.godwin.jsonparser.generator.jsontodart.utils.classblockparse

import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.utils.getClassesStringList

class NormalClassesCodeParser(private val classesCode: String) {
    fun parse(): List<DartClass> {
        return getClassesStringList(classesCode)
            .map {
                DartClass.fromParsedDartClass(ClassCodeParser(it).getDartDataClass())
            }
    }
}