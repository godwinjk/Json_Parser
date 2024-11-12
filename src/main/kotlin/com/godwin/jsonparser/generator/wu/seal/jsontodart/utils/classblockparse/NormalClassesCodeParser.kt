package wu.seal.jsontodart.utils.classblockparse

import wu.seal.jsontodart.classscodestruct.KotlinDataClass
import wu.seal.jsontodart.utils.getClassesStringList

class NormalClassesCodeParser(private val classesCode: String) {
    fun parse(): List<KotlinDataClass> {
        return getClassesStringList(classesCode)
            .map {
                KotlinDataClass.fromParsedKotlinDataClass(ClassCodeParser(it).getKotlinDataClass())
            }
    }
}