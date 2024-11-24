package com.godwin.jsonparser.generator_kt.extensions.nstd

import com.godwin.jsonparser.generator_kt.extensions.Extension
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.DataClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jCheckBox
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jHorizontalLinearLayout
import com.godwin.jsonparser.generator_kt.jsontokotlin.ui.jLabel
import javax.swing.JPanel

/**
 * Extension support replace constructor parameters by member variables
 *
 * default:
 *
 *     data class Foo(
 *         @SerializedName("a")
 *         val a: Int = 0 // 1
 *     )
 *
 *
 * after enable this:
 *
 *     data class Foo {
 *         @SerializedName("a")
 *         val a: Int = 0 // 1
 *     }
 *
 * Created by Nstd on 2020/6/29 17:45.
 */
object ReplaceConstructorParametersByMemberVariablesSupport : Extension() {

    const val configKey = "nstd.replace_constructor_parameters_by_member_variables"

    override fun createUI(): JPanel {

        return jHorizontalLinearLayout {
            jCheckBox(
                "",
                getConfig(configKey).toBoolean(),
                { isSelected -> setConfig(configKey, isSelected.toString()) }
            )
            jLabel(
                "Replace constructor parameters by member variables",
            )
            fillSpace()
        }
    }

    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (getConfig(configKey).toBoolean())
            if (kotlinClass is DataClass) {
                return kotlinClass.copy(codeBuilder = DataClassCodeBuilderForNoConstructorMemberFields(kotlinClass.codeBuilder))
            }
        return kotlinClass
    }
}