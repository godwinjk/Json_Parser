package com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.ListClass


/**
 * kotlin list code generator
 *
 * Created by Nstd on 2020/6/30 15:59.
 */
class KotlinListClassCodeBuilder : ICodeBuilder<ListClass> {

    override fun getCode(clazz: ListClass): String {
        clazz.run {
            val elementType = if (nullableElements) generic.name + "?" else generic.name
            return if (generic.modifiable.not()) {
                getOnlyCurrentCode()
            } else {
                """
            class $name : ArrayList<$elementType>(){
${referencedClasses.filter { it.modifiable }.joinToString("\n\n") { it.getCode().prependIndent("            $indent") }}
            }
        """.trimIndent()
            }
        }
    }

    override fun getOnlyCurrentCode(clazz: ListClass): String {
        clazz.run {
            val elementType = if (nullableElements) generic.name + "?" else generic.name
            return """
            class $name : ArrayList<$elementType>()
        """.trimIndent()
        }
    }
    companion object{
        val DEFAULT = KotlinListClassCodeBuilder()
    }
}