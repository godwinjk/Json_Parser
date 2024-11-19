package com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct

import com.godwin.jsonparser.generator.jsontodart.utils.LogUtil
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor


/**
 * Created by Seal.Wu on 2019-11-23
 *  present a list class type like List<Any>
 */
data class GenericListClass(override val generic: KotlinClass, val nullableElements: Boolean) : UnModifiableGenericClass() {
    override val name: String
        get() = if (nullableElements) "List<${generic.name}?>" else "List<${generic.name}>"

    override val hasGeneric: Boolean = true

    override fun replaceReferencedClasses(replaceRule: Map<KotlinClass, KotlinClass>): GenericListClass {
        return if (generic is GenericListClass) {
            copy(generic = generic.replaceReferencedClasses(replaceRule))
        } else {
            val replacement = replaceRule[generic]
            if (replacement == null) {
                LogUtil.i("Can't find replacement for ${generic.name}")
                this
            } else {
                copy(generic = replacement)
            }
        }
    }

    override fun <T : KotlinClass> applyInterceptors(enabledKotlinClassInterceptors: List<IKotlinClassInterceptor<T>>): KotlinClass {
        return copy(generic = generic.applyInterceptors(enabledKotlinClassInterceptors))
    }

}