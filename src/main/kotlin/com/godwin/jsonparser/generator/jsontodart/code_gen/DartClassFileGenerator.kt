package com.godwin.jsonparser.generator.jsontodart.code_gen

import com.godwin.jsonparser.generator.common.filetype.DartFileType
import com.godwin.jsonparser.generator.common.util.CodeStyleUtils
import com.godwin.jsonparser.generator.jsontodart.interceptor.InterceptorManager
import com.godwin.jsonparser.generator.jsontodart.interceptor.clazz.IDartClassInterceptor
import com.godwin.jsonparser.generator.jsontodart.specs.elements.getDefaultValue
import com.godwin.jsonparser.generator.jsontodart.utils.*
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ClassCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.NormalClassesCodeParser
import com.godwin.jsonparser.generator.jsontodart.utils.classblockparse.ParsedDartDataClass
import com.godwin.jsonparser.generator.jsontokotlin.extensions.chen.biao.KeepAnnotationSupport.append
import com.godwin.jsonparser.generator.jsontokotlin.model.DartConfigManager
import com.godwin.jsonparser.generator.jsontokotlin.utils.toDartDocMultilineComment
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

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
        if (project == null) {
            showNotify("Something went wrong", project)
            return
        }
        var generatedFilesClasses = generateDartClasses(removeDuplicateClassCode, directory)
        generatedFilesClasses = ClassCodeFilter.removeDuplicateClassCode(generatedFilesClasses)
        ClassCodeFilter.addSubClassImports(generatedFilesClasses)

        val notificationText = if (DartConfigManager.isInnerClassModel) {
            generateSingleDartFile(
                generatedFilesClasses,
                packageDeclare,
                project,
                psiFileFactory,
                directory,
                jsonString
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
                    jsonString
                )
            }
            "${generatedFilesClasses.size} Dart class generated successful"
        }

        val notifyMessage = buildString {
            append(notificationText)
            if (renamedClassNames.isNotEmpty()) {
                append("\nThese class names has been auto renamed to new names:\n")
                append(renamedClassNames.joinToString("\n") { "${it.first} -> ${it.second}" })
            }
        }
        showNotify(notifyMessage, project)
    }

    private fun generateDartClasses(
        removeDuplicateClassCode: String,
        directory: PsiDirectory
    ): List<ParsedDartDataClass> {
        val classes = getClassesStringList(removeDuplicateClassCode).map { ClassCodeParser(it).getDartDataClass() }
        val buildRefClasses = ClassCodeFilter.buildTypeReference(classes)
        val newClassNames = getNoneConflictClassNames(buildRefClasses, directory)
        val newKotlinClasses = updateClassNames(buildRefClasses, newClassNames)
        return synchronizedPropertyTypeWithTypeRef(newKotlinClasses)
    }

    private fun updateClassNames(
        dataClasses: List<ParsedDartDataClass>,
        newClassNames: List<String>
    ): List<ParsedDartDataClass> {
        val newKotlinClasses = dataClasses.toMutableList()
        newKotlinClasses.forEachIndexed { index, kotlinDataClass ->
            val newClassName = newClassNames[index]
            if (newClassName != kotlinDataClass.fileName) {
                renamedClassNames.add(Pair(kotlinDataClass.fileName, newClassName))
                val newClass = kotlinDataClass.copy(fileName = newClassName)
                newKotlinClasses[index] = newClass
                updateTypeRef(dataClasses, kotlinDataClass, newClass)
            }
        }
        return newKotlinClasses
    }

    private fun getNoneConflictClassNames(
        buildRefClasses: List<ParsedDartDataClass>,
        directory: PsiDirectory
    ): List<String> {
        val result = mutableListOf<String>()
        buildRefClasses.forEach {
            var newClassName = changeFileNameIfDirectoryHasSameName(it.fileName, directory)
            newClassName = changeClassNameIfCurrentListContains(result, newClassName)
            result.add(newClassName)
        }
        return result
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


    private fun generateDartClassFile(
        parsedClass: ParsedDartDataClass,
        isFirstClass: Boolean,
        packageDeclare: String,
        project: Project,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String
    ) {
        val jsonComment = if (isFirstClass && DartConfigManager.isAppendOriginalJson) {
            jsonString.toDartDocMultilineComment().append("\n")
        } else ""

        val dartFileContent = preProcessDartClass(parsedClass.fileName, packageDeclare, parsedClass)
        val fileContent = jsonComment.append(dartFileContent).trim()

        createAndAddDartFile("${parsedClass.fileName}.dart", fileContent, project, psiFileFactory, directory)
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
            if (parsedClass.importStatement.isNotBlank() && !DartConfigManager.isInnerClassModel) {
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
        project: Project,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory,
        jsonString: String
    ) {
        val fileName = classes[0].fileName
        val jsonComment = if (DartConfigManager.isAppendOriginalJson) {
            jsonString.toDartDocMultilineComment()
        } else ""

        val fileContent = buildString {
            append(jsonComment)
            if (packageDeclare.isNotEmpty()) {
                append("\n").append(packageDeclare).append("\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration(fileName).trim()
            if (importClassDeclaration.isNotBlank()) {
                append("\n").append(importClassDeclaration).append("\n")
            }
            classes.forEach { item ->
                val classCode = if (interceptors.isNotEmpty()) {
                    NormalClassesCodeParser(item.toString()).parse()[0].applyInterceptors(interceptors).getCode()
                } else {
                    item.toString()
                }
                append("\n").append(classCode)
            }
        }.trim()

        createAndAddDartFile("$fileName.dart", fileContent, project, psiFileFactory, directory)
    }

    private fun createAndAddDartFile(
        fileName: String,
        fileContent: String,
        project: Project,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory
    ) {
        val file = psiFileFactory.createFileFromText(
            fileName,
            DartFileType(),
            fileContent,
            LocalTime.now().toEpochSecond(LocalDate.now(ZoneOffset.UTC), ZoneOffset.UTC),
            true
        )

        executeCouldRollBackAction(project) {
            val addedFile = directory.add(file) as PsiFile
            CodeStyleUtils.reFormat(addedFile, project)
        }
    }

    private fun changeFileNameIfDirectoryHasSameName(fileName: String, directory: PsiDirectory): String {
        var newFileName = fileName
        val existingFileNames = directory.files
            .filter { it.name.endsWith(".dart") }
            .map { it.name.dropLast(5).lowercase() }
        while (existingFileNames.contains(newFileName.lowercase())) {
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