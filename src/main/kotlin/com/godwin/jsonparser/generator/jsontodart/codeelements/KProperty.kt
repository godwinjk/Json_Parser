package com.godwin.jsonparser.generator.jsontodart.codeelements

import com.godwin.jsonparser.generator.jsontodart.supporter.NoneSupporter

/**
 *
 * Created by Godwin on 2024/12/20.
 */

interface IProperty {
    /**
     *
     */
    fun getPropertyStringBlock(): String

    fun getPropertyComment(): String

}

class KProperty(
    private val rawPropertyName: String,
    private val propertyType: String,
    private val propertyValue: String
) : IProperty {

    override fun getPropertyStringBlock(): String {

        val blockBuilder = StringBuilder()

        blockBuilder.append(NoneSupporter.getNoneLibSupporterProperty(rawPropertyName, propertyType))

        return blockBuilder.toString()
    }

    override fun getPropertyComment(): String = propertyValue
}
