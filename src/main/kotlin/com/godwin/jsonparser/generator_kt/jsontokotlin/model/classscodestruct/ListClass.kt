package com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.builder.KotlinListClassCodeBuilder

/**
 * ListClass present the class is a class which extends List
 * Created by Godwin on 2024/12/20
 */
data class ListClass(
    override val name: String,
    override val generic: KotlinClass,
    val nullableElements: Boolean,
    override val referencedClasses: List<KotlinClass> = listOf(generic),
    override val modifiable: Boolean = true,
    override val codeBuilder: KotlinListClassCodeBuilder = KotlinListClassCodeBuilder.DEFAULT
) : UnModifiableGenericClass() {

    override fun getOnlyCurrentCode(): String {
        return codeBuilder.getOnlyCurrentCode(this)
    }

    override fun replaceReferencedClasses(replaceRule: Map<KotlinClass, KotlinClass>): ListClass {
        if (replaceRule.isEmpty()) return this
        return copy(generic = replaceRule.values.toList()[0], referencedClasses = replaceRule.values.toList())
    }

    override fun rename(newName: String) = copy(name = newName)

    override fun getCode(): String {
        return codeBuilder.getCode(this)
    }

    override fun <T : KotlinClass> applyInterceptors(enabledKotlinClassInterceptors: List<IKotlinClassInterceptor<T>>): KotlinClass {
        val newGenerics = generic.applyInterceptors(enabledKotlinClassInterceptors)
        val newImportedClasses = referencedClasses.map { it.applyInterceptors(enabledKotlinClassInterceptors) }
        return copy(generic = newGenerics, referencedClasses = newImportedClasses)
    }
}

