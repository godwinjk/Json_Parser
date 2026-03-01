package com.godwin.jsonparser.generator.jsontodart.code_gen

import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.specs.clazz.DartClass
import com.godwin.jsonparser.generator.jsontodart.specs.elements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ClassCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass

class DartDataClassMaker(private val rootClassName: String, private val json: String) {

    private val renamedClassNames = mutableListOf<Pair<String, String>>()

    fun makeDartDataClassCode(): String {
        val interceptors = InterceptorManager.getEnabledDartDataClassInterceptors()
        val dartDataClasses = makeDartDataClasses()
        val interceptedDataClasses = dartDataClasses.map { it.applyInterceptors(interceptors) }
        return interceptedDataClasses.joinToString("\n\n") { it.getCode() }
    }

    private fun makeDartDataClasses(): List<DartClass> {
        val code = ClassCodeFilter.removeDuplicateClassCode(DartCodeMaker(rootClassName, json).makeDartClassData())
        var parsedClasses = parseAndBuildClasses(code)
        parsedClasses = ClassCodeFilter.buildTypeReference(parsedClasses)
        parsedClasses = ClassCodeFilter.removeDuplicateClassCode(parsedClasses)

        showNotify(buildNotificationMessage(parsedClasses.size), null)
        return parsedClasses.map { DartClass.fromParsedDartClass(it) }
    }

    private fun parseAndBuildClasses(code: String): List<ParsedDartDataClass> {
        val classes = getClassesStringList(code).map { ClassCodeParser(it).getDartDataClass() }
        val buildRefClasses = buildTypeReference(classes)
        val newClassNames = getNoneConflictClassNames(buildRefClasses)
        val updatedClasses = updateClassNames(buildRefClasses, newClassNames)
        return synchronizedPropertyTypeWithTypeRef(updatedClasses)
    }

    private fun buildNotificationMessage(classCount: Int) = buildString {
        append("$classCount Dart Classes generated successful")
        if (renamedClassNames.isNotEmpty()) {
            append("\nThese class names has been auto renamed to new names:\n")
            append(renamedClassNames.joinToString("\n") { "${it.first} -> ${it.second}" })
        }
    }

    private fun getNoneConflictClassNames(classes: List<ParsedDartDataClass>): List<String> {
        val result = mutableListOf<String>()
        classes.forEach {
            result.add(changeClassNameIfCurrentListContains(result, it.name))
        }
        return result
    }

    private fun updateClassNames(
        dataClasses: List<ParsedDartDataClass>,
        newClassNames: List<String>
    ): List<ParsedDartDataClass> {
        val newClasses = dataClasses.toMutableList()
        newClasses.forEachIndexed { index, kotlinDataClass ->
            val newClassName = newClassNames[index]
            if (newClassName != kotlinDataClass.name) {
                renamedClassNames.add(Pair(kotlinDataClass.name, newClassName))
                val newKotlinDataClass = kotlinDataClass.copy(name = newClassName)
                newClasses[index] = newKotlinDataClass
                updateTypeRef(dataClasses, kotlinDataClass, newKotlinDataClass)
            }
        }
        return newClasses
    }

    private fun updateTypeRef(
        classes: List<ParsedDartDataClass>,
        originDataClass: ParsedDartDataClass,
        newKotlinDataClass: ParsedDartDataClass
    ) {
        classes.forEach { dartClass ->
            dartClass.properties.forEach { property ->
                if (property.classPropertyTypeRef == originDataClass) {
                    property.classPropertyTypeRef = newKotlinDataClass
                }
            }
        }
    }

    private fun synchronizedPropertyTypeWithTypeRef(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        return classes.map { dataClass ->
            val newProperties = dataClass.properties.map { property ->
                if (property.classPropertyTypeRef != ParsedDartDataClass.NONE) {
                    val rawPropertyReferenceType = getRawType(getChildType(property.propertyType))
                    val newType =
                        property.propertyType.replace(rawPropertyReferenceType, property.classPropertyTypeRef.name)
                    if (property.propertyValue.isNotBlank()) {
                        property.copy(propertyType = newType, propertyValue = getDefaultValue(newType))
                    } else {
                        property.copy(propertyType = newType)
                    }
                } else {
                    property
                }
            }
            dataClass.copy(properties = newProperties)
        }
    }

    private fun buildTypeReference(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        val notBeenReferencedClass = classes.drop(1).toMutableList()
        val classNameList = notBeenReferencedClass.map { it.name }.toMutableList()
        classes.forEach { buildClassTypeReference(it, classNameList, notBeenReferencedClass) }
        return classes
    }

    private fun buildClassTypeReference(
        tobeBuildTypeReferenceClass: ParsedDartDataClass,
        classNameList: MutableList<String>,
        notBeenReferencedClass: MutableList<ParsedDartDataClass>
    ) {
        tobeBuildTypeReferenceClass.properties.forEach { property ->
            val indexOfClassName = classNameList.indexOf(getRawType(getChildType(property.propertyType)))
            if (indexOfClassName != -1) {
                val referencedClass = notBeenReferencedClass[indexOfClassName]
                notBeenReferencedClass.removeAt(indexOfClassName)
                classNameList.removeAt(indexOfClassName)
                property.classPropertyTypeRef = referencedClass
                buildClassTypeReference(referencedClass, classNameList, notBeenReferencedClass)
            }
        }
    }

    private fun changeClassNameIfCurrentListContains(classesNames: List<String>, className: String): String {
        var newClassName = className
        while (classesNames.contains(newClassName)) {
            newClassName += "X"
        }
        return newClassName
    }
}
