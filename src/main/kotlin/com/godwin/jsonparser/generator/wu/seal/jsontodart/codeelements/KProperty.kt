package wu.seal.jsontodart.codeelements

import wu.seal.jsontodart.supporter.NoneSupporter

/**
 *
 * Created by Seal.Wu on 2017/9/18.
 */

interface IProperty {
    /**
     *
     */
    fun getPropertyStringBlock(): String

    fun getPropertyComment(): String

}

class KProperty(private val rawPropertyName: String, private val propertyType: String, private val propertyValue: String) : IProperty {

    override fun getPropertyStringBlock(): String {

        val blockBuilder = StringBuilder()

        blockBuilder.append(NoneSupporter.getNoneLibSupporterProperty(rawPropertyName, propertyType))

        return blockBuilder.toString()
    }

    override fun getPropertyComment(): String = propertyValue
}
