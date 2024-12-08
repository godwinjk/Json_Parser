package com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder.ICodeBuilder

/**
 * Created by Godwin on 2024/12/20
 * Kotlin class which could not be modified the code content without generic type
 */
abstract class UnModifiableNoGenericClass : NoGenericKotlinClass {
    override val modifiable: Boolean = false
    override val referencedClasses: List<KotlinClass> = listOf()
    override val codeBuilder: ICodeBuilder<*> = ICodeBuilder.EMPTY
    override fun getCode() =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override fun getOnlyCurrentCode() =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override fun rename(newName: String) =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override fun replaceReferencedClasses(replaceRule: Map<KotlinClass, KotlinClass>): KotlinClass =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")
}