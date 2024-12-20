package com.godwin.jsonparser.generator_kt.jsontokotlin.utils

import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager


/**
 * Class Code Filter
 * Created by Godwin on 2024/12/20
 */
object ClassCodeFilter {

    /**
     * when not in `innerClassModel` and the class spit with `\n\n` then remove the duplicate class
     */
    fun removeDuplicateClassCode(generateClassesString: String): String {
        return if (KotlinConfigManager.isInnerClassModel.not()) {
            generateClassesString.split("\n\n").distinct().joinToString("\n\n")
        } else {
            generateClassesString
        }
    }
}
