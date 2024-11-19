package com.godwin.jsonparser.generator.jsontodart.utils

/**
 * Class Code Filter
 * Created by Seal.Wu on 2018/4/18.
 */
object ClassCodeFilter {

    /**
     * when not in `innerClassModel` and the class spit with `\n\n` then remove the duplicate class
     */
    fun removeDuplicateClassCode(generateClassesString: String): String {
        return generateClassesString.split("\n\n\n\n").distinct().joinToString("\n\n")
    }
}
