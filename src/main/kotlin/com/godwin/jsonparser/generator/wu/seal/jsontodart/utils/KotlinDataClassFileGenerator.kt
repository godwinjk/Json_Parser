package wu.seal.jsontodart.utils

import com.godwin.jsonparser.generator.wu.seal.jsontodart.filetype.GenFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import wu.seal.jsontodart.codeelements.getDefaultValue
import wu.seal.jsontodart.filetype.DartFileType
import wu.seal.jsontodart.interceptor.IKotlinDataClassInterceptor
import wu.seal.jsontodart.interceptor.InterceptorManager
import wu.seal.jsontodart.utils.classblockparse.ClassCodeParser
import wu.seal.jsontodart.utils.classblockparse.NormalClassesCodeParser
import wu.seal.jsontodart.utils.classblockparse.ParsedKotlinDataClass
import wu.seal.jsontokotlin.filetype.KotlinFileType

class KotlinDataClassFileGenerator(private val interceptors: List<IKotlinDataClassInterceptor> = InterceptorManager.getEnabledKotlinDataClassInterceptors()) {

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
        fileType: GenFileType
    ) {

        val generatedFilesClasses = generateKotlinClasses(removeDuplicateClassCode, directory)

        generatedFilesClasses.forEach { kotlinDataClass ->
            generateKotlinDataClassFile(
                kotlinDataClass.name,
                packageDeclare,
                kotlinDataClass.toString(),
                project,
                psiFileFactory,
                directory,
                fileType
            )
        }
        val notifyMessage = buildString {
            append("${generatedFilesClasses.size} Kotlin Data Class files generated successful")
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

    private fun generateKotlinClasses(
        removeDuplicateClassCode: String,
        directory: PsiDirectory
    ): List<ParsedKotlinDataClass> {
        val classes =
            getClassesStringList(removeDuplicateClassCode).map { ClassCodeParser(it).getKotlinDataClass() }

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
        dataClasses: List<ParsedKotlinDataClass>,
        newClassNames: List<String>
    ): List<ParsedKotlinDataClass> {

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

    /**
     * None conflict with current directory files and exist class
     */
    private fun getNoneConflictClassNames(
        buildRefClasses: List<ParsedKotlinDataClass>,
        directory: PsiDirectory
    ): List<String> {
        val resolveSameConflictClassesNames = mutableListOf<String>()
        buildRefClasses.forEach {
            val originClassName = it.name
            var newClassName =
                changeKotlinFileNameIfCurrentDirectoryExistTheSameFileNameWithoutSuffix(originClassName, directory)
            newClassName = changeClassNameIfCurrentListContains(resolveSameConflictClassesNames, newClassName)
            resolveSameConflictClassesNames.add(newClassName)
        }

        return resolveSameConflictClassesNames
    }

    fun updateTypeRef(
        classes: List<ParsedKotlinDataClass>,
        originDataClass: ParsedKotlinDataClass,
        newKotlinDataClass: ParsedKotlinDataClass
    ) {
        classes.forEach {
            it.properties.forEach { p ->
                if (p.kotlinDataClassPropertyTypeRef == originDataClass) {
                    p.kotlinDataClassPropertyTypeRef = newKotlinDataClass
                }
            }
        }
    }

    fun synchronizedPropertyTypeWithTypeRef(unSynchronizedTypeClasses: List<ParsedKotlinDataClass>): List<ParsedKotlinDataClass> {
        return unSynchronizedTypeClasses.map { dataClass ->
            val newProperties = dataClass.properties.map {
                if (it.kotlinDataClassPropertyTypeRef != ParsedKotlinDataClass.NONE) {
                    val rawPropertyReferenceType = getRawType(getChildType(it.propertyType))
                    val tobeReplaceNewType =
                        it.propertyType.replace(rawPropertyReferenceType, it.kotlinDataClassPropertyTypeRef.name)
                    if (it.propertyValue.isNotBlank()) {
                        it.copy(propertyType = tobeReplaceNewType, propertyValue = getDefaultValue(tobeReplaceNewType))
                    } else
                        it.copy(propertyType = tobeReplaceNewType)
                } else {
                    it
                }
            }
            dataClass.copy(properties = newProperties)
        }
    }

    fun buildTypeReference(classes: List<ParsedKotlinDataClass>): List<ParsedKotlinDataClass> {
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

    private fun generateKotlinDataClassFile(
        fileName: String,
        packageDeclare: String,
        classCodeContent: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory, genFileType: GenFileType
    ) {
        val classCode = if (interceptors.isNotEmpty()) {
            NormalClassesCodeParser(classCodeContent).parse()[0].applyInterceptors(interceptors).getCode()
        } else {
            classCodeContent
        }
        val kotlinFileContent = buildString {
            if (packageDeclare.isNotEmpty()) {
                append(packageDeclare)
                append("\n\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration()
            if (importClassDeclaration.isNotBlank()) {
                append(importClassDeclaration)
                append("\n\n")
            }
            append(classCode)
        }
        val fileType = if (genFileType == GenFileType.kotlin) KotlinFileType() else DartFileType()
        val fileExtn =  if (genFileType == GenFileType.kotlin) ".kt" else ".dart"
        executeCouldRollBackAction(project) {
            val file = psiFileFactory.createFileFromText("$fileName$fileExtn", fileType, kotlinFileContent)
            directory.add(file)
        }
    }

    private fun changeKotlinFileNameIfCurrentDirectoryExistTheSameFileNameWithoutSuffix(
        fileName: String,
        directory: PsiDirectory
    ): String {
        var newFileName = fileName
        val kotlinFileSuffix = ".kt"
        val fileNamesInLowerCaseWithoutSuffix =
            directory.files.filter { it.name.endsWith(kotlinFileSuffix) }
                .map { it.name.dropLast(kotlinFileSuffix.length).toLowerCase() }
        while (fileNamesInLowerCaseWithoutSuffix.contains(newFileName.toLowerCase())) {
            newFileName += "X"
        }
        return newFileName
    }

    private fun changeClassNameIfCurrentListContains(classesNames: List<String>, className: String): String {
        var newClassName = className

        val fileNamesInLowerCase = classesNames.map { it.toLowerCase() }
        while (fileNamesInLowerCase.contains(newClassName.toLowerCase())) {
            newClassName += "X"
        }
        return newClassName
    }
}
