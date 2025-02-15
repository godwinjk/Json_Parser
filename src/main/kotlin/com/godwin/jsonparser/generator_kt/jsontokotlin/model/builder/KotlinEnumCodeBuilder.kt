package com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.EnumClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.constToLiteral
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.getIndent
import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.toAnnotationComments


/**
 * kotlin enum code generator
 *
 * Created by Nstd on 2020/6/30 14:52.
 */
class KotlinEnumCodeBuilder : ICodeBuilder<EnumClass> {

    private fun EnumClass.generateValues(): List<String> {
        val list = mutableListOf<String>()
        for (i in enum.indices) {
            val constantValue: Any = when (generic) {
                KotlinClass.INT -> (enum[i] as Double).toInt()
                KotlinClass.DOUBLE -> enum[i] as Double
                else -> enum[i].toString()
            }
            val extensionEnumName = xEnumNames?.get(i)
            val constantName = when {
                extensionEnumName != null -> extensionEnumName
                constantValue is Int -> "_$constantValue"
                // Not `.` allowed in variable names
                constantValue is Double -> "_${constantValue.toInt()}"
                else -> constantValue.toString()
            }
            val finalValue = "${constantName}(${constToLiteral(constantValue)})" + if (i != enum.size - 1) "," else ";"
            list.add(finalValue)
        }
        return list
    }

    override fun getCode(clazz: EnumClass): String {
        clazz.run {
            val indent = getIndent()
            return StringBuilder().append(comments.toAnnotationComments())
                .append("enum class $name(val value: ${generic.name}) {\n")
                .append(generateValues().joinToString("\n\n") { "$indent$it" })
                .append("\n}").toString()
        }
    }

    override fun getOnlyCurrentCode(clazz: EnumClass): String {
        return getCode(clazz)
    }

    companion object{
        val DEFAULT = KotlinEnumCodeBuilder()
    }
}