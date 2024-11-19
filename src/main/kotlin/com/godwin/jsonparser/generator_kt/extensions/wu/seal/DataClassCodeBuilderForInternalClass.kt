package com.godwin.jsonparser.generator_kt.extensions.wu.seal

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder.IKotlinDataClassCodeBuilder
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass

/**
 * kotlin class code generator with internal modifier before class
 *
 * Created by Seal on 2020/7/7 21:40.
 */
class DataClassCodeBuilderForInternalClass(private val kotlinDataClassCodeBuilder: IKotlinDataClassCodeBuilder) :
    BaseDataClassCodeBuilder(kotlinDataClassCodeBuilder) {

    override fun DataClass.genClassName(): String {
        val originClassName = kotlinDataClassCodeBuilder.run { genClassName() }
        return "internal $originClassName"
    }
}
