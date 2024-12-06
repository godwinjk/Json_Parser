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

        var generatedFilesClasses = generateDartClasses(removeDuplicateClassCode, directory)
        //Remove any duplicate finally before generating actual file
        generatedFilesClasses = ClassCodeFilter.removeDuplicateClassCode(generatedFilesClasses)

        //Adding import statement for subclass
        ClassCodeFilter.addSubClassImports(generatedFilesClasses)

        val notificationText = if (DartConfigManager.isInnerClassModel) {
            generateSingleDartFile(
                generatedFilesClasses, packageDeclare, project, psiFileFactory, directory, jsonString
            )
            "1 Dart class generated successfully"
        } else {
            generatedFilesClasses.forEachIndexed { index, dartClass ->
                generateDartClassFile(
                    dartClass,
                    index == 0,
                    packageDeclare,
                    project,
                    psiFileFactory,
                    directory,
                    jsonString,
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
                if (p.classPropertyTypeRef == originDataClass) {
                    p.classPropertyTypeRef = newKotlinDataClass
                }
            }
        }
    }

    fun synchronizedPropertyTypeWithTypeRef(unSynchronizedTypeClasses: List<ParsedDartDataClass>): List<ParsedDartDataClass> {
        return unSynchronizedTypeClasses.map { dataClass ->
            val newProperties = dataClass.properties.map {
                if (it.classPropertyTypeRef != ParsedDartDataClass.NONE) {
                    val rawPropertyReferenceType = getRawType(getChildType(it.propertyType))
                    val tobeReplaceNewType =
                        it.propertyType.replace(rawPropertyReferenceType, it.classPropertyTypeRef.name)
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

    private fun generateDartClassFile(
        parsedClass: ParsedDartDataClass,
        isFirstClass: Boolean,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String,
    ) {
        val jsonComment = if (isFirstClass && DartConfigManager.isAppendOriginalJson) {
            jsonString.toDartDocMultilineComment().append("\n")
        } else ""

        val dartFileContent = preProcessDartClass(parsedClass.fileName, packageDeclare, parsedClass)
        val fileContent = jsonComment.append(dartFileContent).trim()

        executeCouldRollBackAction(project) {
            val file = psiFileFactory.createFileFromText("${parsedClass.fileName}.dart", DartFileType(), fileContent)
            directory.add(file)
        }
    }

    private fun preProcessDartClass(
        fileName: String,
        packageDeclare: String,
        parsedClass: ParsedDartDataClass,
    ): String {
        val classCode = if (interceptors.isNotEmpty()) {
            NormalClassesCodeParser(parsedClass.toString()).parse()[0].applyInterceptors(interceptors).getCode()
        } else {
            parsedClass.toString()
        }
        val dartFileContent = buildString {
            if (packageDeclare.isNotEmpty()) {
                append(packageDeclare)
                append("\n")
            }
            if (parsedClass.importStatement.isNotBlank()) {
                append(parsedClass.importStatement.trim())
                append("\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration(fileName).trim()
            if (importClassDeclaration.isNotBlank()) {
                append(importClassDeclaration)
                append("\n")
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
            "$value\n${
                preProcessDartClass(
                    fileName, packageDeclare, item
                )
            }"
        }.trim()

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
