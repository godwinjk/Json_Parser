package com.godwin.jsonparser.generator_kt.extensions.nstd

import com.godwin.jsonparser.generator_kt.extensions.wu.seal.BaseDataClassCodeBuilder
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder.IKotlinDataClassCodeBuilder
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.addIndent
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.getCommentCode
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.toAnnotationComments

/**
 * kotlin class code generator
 *
 * Created by Nstd on 2020/6/29 15:40.
 */
class DataClassCodeBuilderForNoConstructorMemberFields(private val kotlinDataClassCodeBuilder: IKotlinDataClassCodeBuilder) :
        BaseDataClassCodeBuilder(kotlinDataClassCodeBuilder) {

    override fun DataClass.genBody(): String {
        val delegateBody = kotlinDataClassCodeBuilder.run { genBody() }
        val noConstructorMemberFields = genNoConstructorProperties()
        return buildString {
            if (delegateBody.isEmpty()) {
                append(noConstructorMemberFields)
            } else{
                appendLine(noConstructorMemberFields)
                append(delegateBody)
            }
        }
    }


    override fun DataClass.genPrimaryConstructorProperties(): String {
        return ""
    }

    private fun DataClass.genNoConstructorProperties(): String {
        return buildString {
            properties.filterNot { excludedProperties.contains(it.name) }.forEachIndexed { index, property ->
                val addIndentCode = property.getCode().addIndent(indent)
                val commentCode = getCommentCode(property.comment)
                if (fromJsonSchema && commentCode.isNotBlank()) {
                    append(commentCode.toAnnotationComments(indent))
                }
                append(addIndentCode)
                if (!fromJsonSchema && commentCode.isNotBlank()) append(" // ").append(commentCode)
                if (index != properties.size - 1) append("\n")
            }
        }
    }
}
