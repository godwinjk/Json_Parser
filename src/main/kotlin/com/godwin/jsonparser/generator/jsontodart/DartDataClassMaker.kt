package com.godwin.jsonparser.generator.jsontodart


import com.godwin.jsonparser.generator.jsontodart.classscodestruct.DartClass
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ClassCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass

class DartDataClassMaker(private val rootClassName: String, private val json: String) {

    private val renamedClassNames = mutableListOf<Pair<String, String>>()

    fun makeDartDataClasses(): List<DartClass> {

        val codeMaker = DartCodeMaker(rootClassName, json)

        //code string after removing duplicate class structure code
        val code = ClassCodeFilter.removeDuplicateClassCode(codeMaker.makeDartClassData())

        // create the list of non duplicate classes
        val parsedClasses: List<ParsedDartDataClass> =
            makeDartDataClasses(code)

        return parsedClasses.map {
            DartClass.fromParsedDartClass(it)
        }
    }

    // method to make in class data classes for JsonToKotlinClass, renames same class name to different
    private fun makeDartDataClasses(
        removeDuplicateClassCode: String
    ): List<ParsedDartDataClass> {

        val classes =
            generateClassesWithNonConflictNames(removeDuplicateClassCode = removeDuplicateClassCode)

        val notifyMessage = buildString {
            append("${classes.size} Dart Classes generated successful")
            if (renamedClassNames.isNotEmpty()) {
                append("\n")
                append(
                    "These class names has been auto renamed to new names:\n ${
                        renamedClassNames.map { it.first + " -> " + it.second }.toList()
                    }"
                )
            }
        }
        showNotify(notifyMessage, null)


        return classes
    }


    /**
     * generates Dart classes without having any class name conflicts
     */
    fun generateClassesWithNonConflictNames(removeDuplicateClassCode: String): List<ParsedDartDataClass> {
        val classes =
            getClassesStringList(removeDuplicateClassCode).map { ClassCodeParser(it).getDartDataClass() }

        /**
         * Build Property Type reference to ParsedKotlinDataClass
         * Only pre class property type could reference behind classes
         */
        val buildRefClasses = buildTypeReference(classes)

        val newClassNames = getNoneConflictClassNames(buildRefClasses)

        val newClass = updateClassNames(buildRefClasses, newClassNames)


        return synchronizedPropertyTypeWithTypeRef(newClass)

    }


    /**
     * gets the list of non conflicting class names by appending extra character
     */
    fun getNoneConflictClassNames(
        buildRefClasses: List<ParsedDartDataClass>
    ): List<String> {

        val resolveSameConflictClassesNames = mutableListOf<String>()
        buildRefClasses.forEach {
            val originClassName = it.name
            val newClassName = changeClassNameIfCurrentListContains(resolveSameConflictClassesNames, originClassName)
            resolveSameConflictClassesNames.add(newClassName)
        }

        return resolveSameConflictClassesNames
    }


    /**
     * updates the class names with renamed class names
     */
    fun updateClassNames(
        dataClasses: List<ParsedDartDataClass>,
        newClassNames: List<String>
    ): List<ParsedDartDataClass> {

        val newKotlinClasses = dataClasses.toMutableList()

        newKotlinClasses.forEachIndexed { index, kotlinDataClass ->

            val newClassName = newClassNames[index]
            val originClassName = kotlinDataClass.name

            if (newClassName != originClassName) {
                renamedClassNames.add(Pair(originClassName, newClassName))
                val newKotlinDataClass = kotlinDataClass.copy(name = newClassName)
                newKotlinClasses[index] = newKotlinDataClass
                updateTypeRef(dataClasses, kotlinDataClass, newKotlinDataClass)
            }
        }

        return newKotlinClasses
    }

    private fun updateTypeRef(
        classes: List<ParsedDartDataClass>,
        originDataClass: ParsedDartDataClass,
        newKotlinDataClass: ParsedDartDataClass
    ) {
        classes.forEach {
            it.properties.forEach { p ->
                if (p.classPropertyTypeRef == originDataClass) {
                    p.classPropertyTypeRef = newKotlinDataClass
                }
            }
        }
    }

    /**
     * Returns list of Classes with synchronized property based on renamed classes
     */
    fun synchronizedPropertyTypeWithTypeRef(unSynchronizedTypeClasses: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        return unSynchronizedTypeClasses.map { dataClass: ParsedDartDataClass ->

            val newProperties = dataClass.properties.map { property ->
                if (property.classPropertyTypeRef != ParsedDartDataClass.NONE) {
                    val rawPropertyReferenceType = getRawType(getChildType(property.propertyType))
                    val tobeReplaceNewType =
                        property.propertyType.replace(
                            rawPropertyReferenceType,
                            property.classPropertyTypeRef.name
                        )
                    if (property.propertyValue.isNotBlank()) {
                        property.copy(
                            propertyType = tobeReplaceNewType,
                            propertyValue = getDefaultValue(tobeReplaceNewType)
                        )
                    } else
                        property.copy(propertyType = tobeReplaceNewType)
                } else {
                    property
                }
            }
            dataClass.copy(properties = newProperties)
        }
    }

    /**
     * builds the reference for each property in the data classes
     */
    fun buildTypeReference(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {

        val notBeenReferencedClass = mutableListOf<ParsedDartDataClass>().apply {
            addAll(classes)
            removeAt(0)
        }

        val classNameList = notBeenReferencedClass.map { it.name }.toMutableList()

        classes.forEach {
            buildClassTypeReference(it, classNameList, notBeenReferencedClass)
        }

        return classes
    }

    private fun buildClassTypeReference(
        tobeBuildTypeReferenceClass: ParsedDartDataClass,
        classNameList: MutableList<String>,
        notBeenReferencedClass: MutableList<ParsedDartDataClass>
    ) {
        tobeBuildTypeReferenceClass.properties.forEach { property ->
            val indexOfClassName =
                classNameList.indexOf(getRawType(getChildType(property.propertyType)))
            if (indexOfClassName != -1) {
                val referencedClass = notBeenReferencedClass[indexOfClassName]
                notBeenReferencedClass.remove(referencedClass)
                classNameList.removeAt(indexOfClassName)
                notBeenReferencedClass.remove(referencedClass)
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
