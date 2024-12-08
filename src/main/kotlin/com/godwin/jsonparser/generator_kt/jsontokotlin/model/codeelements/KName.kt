package com.godwin.jsonparser.generator_kt.jsontokotlin.model.codeelements

/**
 * Name class
 * Created by Godwin on 2024/12/20
 */


interface IKName {

    fun getName(rawName: String): String

}

abstract class KName : IKName {

    private val suffix = "X"

    protected val illegalNameList = listOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "val",
        "var",
        "when",
        "while"
    )


    protected val illegalCharacter = listOf(
        "\\+",
        "\\-",
        "\\*",
        "/",
        "%",
        "=",
        "&",
        "\\|",
        "!",
        "\\[",
        "\\]",
        "\\{",
        "\\}",
        "\\(",
        "\\)",
        "\\\\",
        "\"",
        "_",
        ",",
        ":",
        "\\?",
        "\\>",
        "\\<",
        "@",
        ";",
        "'",
        "\\`",
        "\\~",
        "\\$",
        "\\^",
        "#",
        "\\",
        "/",
        " ",
        "\t",
        "\n"
    )


    protected val nameSeparator = listOf(" ", "_", "\\-", ":", "\\.")


    /**
     * remove the start number or whiteSpace characters in this string
     */
    protected fun removeStartNumberAndIllegalCharacter(it: String): String {

        val numberAndIllegalCharacters = listOf(*illegalCharacter.toTypedArray(), "\\d")

        val firstNumberAndIllegalCharactersRegex = "^(${numberAndIllegalCharacters.toRegex()})+".toRegex()

        return it.trim().replaceFirst(firstNumberAndIllegalCharactersRegex, "")

    }

    protected fun toBeLegalName(name: String): String {
        val tempName = name.replace(illegalCharacter.toRegex(), "")

        return if (tempName in illegalNameList) {
            tempName + suffix
        } else {
            tempName
        }
    }

    /**
     * array string into regex match patten that could match any element of the array
     */
    protected fun Iterable<String>.toRegex() = joinToString(separator = "|").toRegex()


}
