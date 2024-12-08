package com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder.ICodeBuilder

/**
 * Created by Godwin on 2024/12/20
 * Kotlin class which could not be modified the code content also with generic type
 */
abstract class UnModifiableGenericClass : GenericKotlinClass {
    override val modifiable: Boolean = false
    override fun getCode(): String =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override fun getOnlyCurrentCode(): String =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override fun rename(newName: String): KotlinClass =
        throw UnsupportedOperationException("Dont support this function called on unModifiable Class")

    override val codeBuilder: ICodeBuilder<*> = ICodeBuilder.EMPTY
}