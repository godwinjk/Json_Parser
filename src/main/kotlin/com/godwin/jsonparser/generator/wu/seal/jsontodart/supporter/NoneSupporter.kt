package wu.seal.jsontodart.supporter

import wu.seal.jsontodart.utils.getIndent

/**
 *
 * Created by Seal.Wu on 2017/9/18.
 */

interface INoneLibSupporter {
    /**
     * create property String block
     */
    fun getNoneLibSupporterProperty(rawPropertyName: String, propertyType: String): String


    fun getNoneLibSupporterClassName(rawClassName: String):String

}


object NoneSupporter : INoneLibSupporter {


    override fun getNoneLibSupporterClassName(rawClassName: String):String {
        return ""
    }


    override fun getNoneLibSupporterProperty(rawPropertyName: String, propertyType: String): String {

        val blockBuilder = StringBuilder()
        blockBuilder.append(getIndent())
        blockBuilder.append(propertyType)
        blockBuilder.append(" ")
        blockBuilder.append(rawPropertyName.decapitalize())
        blockBuilder.append(";")
        return blockBuilder.toString()
    }

}

