package com.godwin.jsonparser.generator.jsontodart.utils

import com.godwin.jsonparser.generator.extensions.ClassNameSuffixSupport.append
import com.godwin.jsonparser.generator.jsontodart.codeelements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.interceptor.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ClassCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.NormalClassesCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.toDartDocMultilineComment
import com.godwin.jsonparser.generatorjsontodart.filetype.DartFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

class DartClassFileGenerator(private val interceptors: List<IDartClassInterceptor> = InterceptorManager.getEnabledDartDataClassInterceptors()) {

    /**
     * record the renamed class name when generate multiple files
     */
    private val renamedClassNames = mutableListOf<Pair<String, String>>()

    fun generateMultipleDataClassFiles(
        removeDuplicateClassCode: String,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String
    ) {

        val generatedFilesClasses = generateDartClasses(removeDuplicateClassCode, directory)

        val notificationText = if (DartConfigManager.isInnerClassModel) {
            generateSingleDartFile(
                generatedFilesClasses, packageDeclare, project, psiFileFactory, directory, jsonString
            )
            "1 Dart class generated successfully"
        } else {
            generatedFilesClasses.forEachIndexed { index, dartClass ->
                generateDartClassFile(
                    index == 0,
                    dartClass.fileName,
                    packageDeclare,
                    dartClass.toString(),
                    project,
                    psiFileFactory,
                    directory,
                    jsonString
                )
            }
            "${generatedFilesClasses.size} Dart class generated successful"
        }
        val notifyMessage = buildString {
            append(notificationText)
            if (renamedClassNames.isNotEmpty()) {
                append("\n")
                append(
                    "These class names has been auto renamed to new names:\n ${
                        renamedClassNames.map { it.first + " -> " + it.second }.toList()
                    }"
                )
            }
        }
        showNotify(notifyMessage, project)

    }

    private fun generateDartClasses(
        removeDuplicateClassCode: String, directory: PsiDirectory
    ): List<ParsedDartDataClass> {
        val classes = getClassesStringList(removeDuplicateClassCode).map { ClassCodeParser(it).getDartDataClass() }

        /**
         * Build Property Type reference to ParsedKotlinDataClass
         * Only pre class property type could reference behind classes
         */
        val buildRefClasses = buildTypeReference(classes)

        val newClassNames = getNoneConflictClassNames(buildRefClasses, directory)

        val newKotlinClasses = updateClassNames(buildRefClasses, newClassNames)


        return synchronizedPropertyTypeWithTypeRef(newKotlinClasses)

    }

    fun updateClassNames(
        dataClasses: List<ParsedDartDataClass>, newClassNames: List<String>
    ): List<ParsedDartDataClass> {

        val newKotlinClasses = dataClasses.toMutableList()

        newKotlinClasses.forEachIndexed { index, kotlinDataClass ->

            val newClassName = newClassNames[index]
            val originClassName = kotlinDataClass.fileName

            if (newClassName != originClassName) {
                renamedClassNames.add(Pair(originClassName, newClassName))
                val newClass = kotlinDataClass.copy(fileName = newClassName)
                newKotlinClasses[index] = newClass
                updateTypeRef(dataClasses, kotlinDataClass, newClass)
            }
        }

        return newKotlinClasses
    }

    /**
     * None conflict with current directory files and exist class
     */
    private fun getNoneConflictClassNames(
        buildRefClasses: List<ParsedDartDataClass>, directory: PsiDirectory
    ): List<String> {
        val resolveSameConflictClassesNames = mutableListOf<String>()
        buildRefClasses.forEach {
            val originClassName = it.fileName
            var newClassName =
                changeKotlinFileNameIfCurrentDirectoryExistTheSameFileNameWithoutSuffix(originClassName, directory)
            newClassName = changeClassNameIfCurrentListContains(resolveSameConflictClassesNames, newClassName)
            resolveSameConflictClassesNames.add(newClassName)
        }

        return resolveSameConflictClassesNames
    }

    fun updateTypeRef(
        classes: List<ParsedDartDataClass>,
        originDataClass: ParsedDartDataClass,
        newKotlinDataClass: ParsedDartDataClass
    ) {
        classes.forEach {
            it.properties.forEach { p ->
                if (p.kotlinDataClassPropertyTypeRef == originDataClass) {
                    p.kotlinDataClassPropertyTypeRef = newKotlinDataClass
                }
            }
        }
    }

    fun synchronizedPropertyTypeWithTypeRef(unSynchronizedTypeClasses: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        return unSynchronizedTypeClasses.map { dataClass ->
            val newProperties = dataClass.properties.map {
                if (it.kotlinDataClassPropertyTypeRef != ParsedDartDataClass.NONE) {
                    val rawPropertyReferenceType = getRawType(getChildType(it.propertyType))
                    val tobeReplaceNewType =
                        it.propertyType.replace(rawPropertyReferenceType, it.kotlinDataClassPropertyTypeRef.name)
                    if (it.propertyValue.isNotBlank()) {
                        it.copy(propertyType = tobeReplaceNewType, propertyValue = getDefaultValue(tobeReplaceNewType))
                    } else it.copy(propertyType = tobeReplaceNewType)
                } else {
                    it
                }
            }
            dataClass.copy(properties = newProperties)
        }
    }

    fun buildTypeReference(classes: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        val classNameList = classes.map { it.name }

        /**
         * Build Property Type reference to ParsedKotlinDataClass
         * Only pre class property type could reference behind classes
         */
        classes.forEachIndexed { index, kotlinDataClass ->
            kotlinDataClass.properties.forEachIndexed { _, property ->
                val indexOfClassName =
                    classNameList.firstIndexAfterSpecificIndex(getRawType(getChildType(property.propertyType)), index)
                if (indexOfClassName != -1) {
                    property.kotlinDataClassPropertyTypeRef = classes[indexOfClassName]
                }
            }
        }

        return classes
    }

    private fun generateDartClassFile(
        isFirstClass: Boolean,
        fileName: String,
        packageDeclare: String,
        classCodeContent: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String
    ) {
        val jsonComment = if (isFirstClass && DartConfigManager.isAppendOriginalJson) {
            jsonString.toDartDocMultilineComment().append("\n\n")
        } else ""

        val dartFileContent = preProcessDartClass(fileName, packageDeclare, classCodeContent)
        val fileContent = jsonComment.append(dartFileContent)

        executeCouldRollBackAction(project) {
            val file = psiFileFactory.createFileFromText("$fileName.dart", DartFileType(), fileContent)
            directory.add(file)
        }
    }

    private fun preProcessDartClass(
        fileName: String,
        packageDeclare: String,
        classCodeContent: String,
    ): String {
        val classCode = if (interceptors.isNotEmpty()) {
            NormalClassesCodeParser(classCodeContent).parse()[0].applyInterceptors(interceptors).getCode()
        } else {
            classCodeContent
        }
        val dartFileContent = buildString {
            if (packageDeclare.isNotEmpty()) {
                append(packageDeclare)
                append("\n\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration(fileName)
            if (importClassDeclaration.isNotBlank()) {
                append(importClassDeclaration)
                append("\n\n")
            }
            append(classCode)
        }
        return dartFileContent
    }


    private fun generateSingleDartFile(
        classes: List<ParsedDartDataClass>,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String
    ) {
        val fileName = classes[0].fileName
        val jsonComment = if (DartConfigManager.isAppendOriginalJson) {
            jsonString.toDartDocMultilineComment()
        } else ""
        val fileContent = classes.fold(jsonComment) { value, item ->
            "$value\n\n${
                preProcessDartClass(
                    item.fileName, packageDeclare, item.toString()
                )
            }"
        }
        executeCouldRollBackAction(project) {
            val file = psiFileFactory.createFileFromText("$fileName.dart", DartFileType(), fileContent)
            directory.add(file)
        }
    }

    private fun changeKotlinFileNameIfCurrentDirectoryExistTheSameFileNameWithoutSuffix(
        fileName: String, directory: PsiDirectory
    ): String {
        var newFileName = fileName
        val dartFileSuffix = ".dart"
        val fileNamesInLowerCaseWithoutSuffix = directory.files.filter { it.name.endsWith(dartFileSuffix) }
            .map { it.name.dropLast(dartFileSuffix.length).lowercase() }
        while (fileNamesInLowerCaseWithoutSuffix.contains(newFileName.lowercase())) {
            newFileName += "_x"
        }
        return newFileName
    }

    private fun changeClassNameIfCurrentListContains(classesNames: List<String>, className: String): String {
        var newClassName = className

        val fileNamesInLowerCase = classesNames.map { it.lowercase() }
        while (fileNamesInLowerCase.contains(newClassName.lowercase())) {
            newClassName += "_x"
        }
        return newClassName
    }
}
