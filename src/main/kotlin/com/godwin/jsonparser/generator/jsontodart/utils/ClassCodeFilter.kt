package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass

/**
 * Class Code Filter
 * Created by Godwin on 2024/12/20.
 */
object ClassCodeFilter {

    /**
     * when not in `innerClassModel` and the class split with `\n\n` then remove the duplicate class
     */
    fun removeDuplicateClassCode(generateClassesString: String): String {
        return generateClassesString.split("\n\n").distinct().joinToString("\n\n")
    }

    fun buildTypeReference(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        val classNameList = classes.map { it.name }
        /**
         * Build Property Type reference to ParsedDartDataClass
         * Only pre class property type could reference behind classes
         */
        classes.forEachIndexed { index, dartClass ->
            dartClass.properties.forEach { property ->
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
                    if (dartClasses.none { it == property.classPropertyTypeRef }) {
                        val similarClass = dartClasses.find { candidate ->
                            val currentSet = candidate.properties.map { it.propertyName to it.propertyType }.toSet()
                            val targetSet = property.classPropertyTypeRef.properties.map { it.propertyName to it.propertyType }.toSet()
                            currentSet == targetSet
                        }

                        similarClass?.let {
                            val type = getRawType(getChildType(property.propertyType))
                            property.propertyType = property.propertyType.replace(type, it.name)
                            property.classPropertyTypeRef = it
                        }
                    }
                }
            }
        }
    }

    fun addSubClassImports(dartClasses: List<ParsedDartDataClass>) {
        dartClasses.forEach { classA ->
            val importStatements = classA.properties
                .filter { it.classPropertyTypeRef != ParsedDartDataClass.NONE }
                .mapNotNull { property ->
                    dartClasses.find { it == property.classPropertyTypeRef }?.let { classB ->
                        "import '${classB.fileName}.dart';"
                    }
                }
                .distinct()

            if (importStatements.isNotEmpty()) {
                classA.importStatement = importStatements.joinToString(separator = "\n") + "\n"
            }
        }
    }
}
