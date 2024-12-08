package com.godwin.jsonparser.generator.jsontodart.codeelements

import java.util.*

/**
 * make name to be camel case
 * Created by Godwin on 2024/12/20.
 */

interface IPropertyNameMaker {

    /**
     * make legal property name from a input raw string
     */
    fun makePropertyName(rawString: String): String


    /**
     * make legal property name from a input raw string
     */
    fun makePropertyName(rawString: String, needTransformToLegalName: Boolean): String


}

object KPropertyName : KName(), IPropertyNameMaker {
    override fun getName(rawName: String): String {
        return makePropertyName(rawName, true)
    }

    override fun makePropertyName(rawString: String): String {
        return rawString
    }

    override fun makePropertyName(rawString: String, needTransformToLegalName: Boolean): String {
        return if (needTransformToLegalName) {
            val camelCaseLegalName = makeLowerCamelCaseLegalNameOrEmptyName(rawString)
            if (camelCaseLegalName.isEmpty()) makeLowerCamelCaseLegalNameOrEmptyName("x-$rawString") else camelCaseLegalName
        } else {
            rawString
        }
    }

    /**
     * get the none empty legal came case name
     */
    fun makeLowerCamelCaseLegalName(rawNameString: String): String {
        return makePropertyName(rawNameString, true)
    }

    /**
     * this function may return empty string when the raw string is only make of number and illegal character
     */
    fun makeLowerCamelCaseLegalNameOrEmptyName(rawString: String): String {
        /**
         * keep nameSeparator character
         */
        val pattern = illegalCharacter.toMutableList().apply { removeAll(nameSeparator) }.toRegex()

        val temp = rawString.replace(pattern, "").let {

            return@let removeStartNumberAndIllegalCharacter(it)

        }

        val lowerCamelCaseName = toLowerCamelCase(temp)

        return toBeLegalName(lowerCamelCaseName)
    }


    /**
     * this function can remove the rest white space
     */
    private fun toLowerCamelCase(temp: String): String {
        val stringBuilder = StringBuilder()
        temp.split(nameSeparator.toRegex()).forEach {
            if (it.isNotBlank()) {
                stringBuilder.append(it.substring(0, 1).uppercase(Locale.getDefault()).plus(it.substring(1)))
            }
        }

        val camelCaseName = stringBuilder.toString()
        return if (camelCaseName.isNotEmpty()) {
            camelCaseName.substring(0, 1).lowercase(Locale.getDefault()).plus(camelCaseName.substring(1))
        } else {
            camelCaseName
        }
    }
}
