package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass

/**
 * Class Code Filter
 * Created by Godwin on 2024/12/20.
 */
object ClassCodeFilter {

    /**
     * when not in `innerClassModel` and the class spit with `\n\n` then remove the duplicate class
     */
    fun removeDuplicateClassCode(generateClassesString: String): String {
        return generateClassesString.split("\n\n").distinct().joinToString("\n\n")
    }

    fun buildTypeReference(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        val classNameList = classes.map { it.name }
        /**
         * Build Property Type reference to ParsedKotlinDataClass
         * Only pre class property type could reference behind classes
         */
        classes.forEachIndexed { index, dartClass ->
            dartClass.properties.forEachIndexed { _, property ->
                val indexOfClassName =
                    classNameList.firstIndexAfterSpecificIndex(getRawType(getChildType(property.propertyType)), index)
                if (indexOfClassName != -1) {
                    property.classPropertyTypeRef = classes[indexOfClassName]
                }
            }
        }

        return classes
    }

    fun removeDuplicateClassCode(dartClasses: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        val uniqueList = dartClasses.distinctBy {
            // Convert lists to Sets to remove duplicates and ignore order
            it.properties.map { b -> b.propertyName to b.propertyType }.toSet()
        }
        changeTypesOfRemovedClass(uniqueList)
        return uniqueList
    }

    fun changeTypesOfRemovedClass(dartClasses: List<ParsedDartDataClass>) {
        dartClasses.forEach { dartClass ->
            dartClass.properties.forEach { property ->
                if (property.classPropertyTypeRef != ParsedDartDataClass.NONE) {
                    val actualClass = dartClasses.find { it == property.classPropertyTypeRef }
                    if (actualClass == null) {
                        val similarClass = dartClasses.find { dartClass ->
                            // Convert properties of both classes to Sets of (propertyName, propertyType) pairs for comparison
                            val currentSet = dartClass.properties.map { b -> b.propertyName to b.propertyType }.toSet()
                            val targetSet =
                                property.classPropertyTypeRef.properties.map { b -> b.propertyName to b.propertyType }
                                    .toSet()

                            // Compare sets to determine similarity
                            currentSet == targetSet
                        }

                        if (similarClass != null) {
                            val type = getRawType(getChildType(property.propertyType))
                            val newType = property.propertyType.replace(type, similarClass.name)
                            property.propertyType = newType
                            property.classPropertyTypeRef = similarClass
                        }
                    }
                }
            }
        }
    }

    fun addSubClassImports(dartClasses: List<ParsedDartDataClass>) {
        for (classA in dartClasses) {
            val importStatements = mutableListOf<String>()

            for (property in classA.properties) {
                if (property.classPropertyTypeRef != ParsedDartDataClass.NONE) {
                    for (classB in dartClasses) {
                        if (property.classPropertyTypeRef == classB) {
                            importStatements.add("import '${classB.fileName}.dart';")
                            break // Exit inner loop since a match is found
                        }
                    }
                }
            }
            if (importStatements.isNotEmpty()) {
                classA.importStatement = importStatements.distinct().joinToString(separator = "") { "$it\n" }
            }
        }
    }
}
