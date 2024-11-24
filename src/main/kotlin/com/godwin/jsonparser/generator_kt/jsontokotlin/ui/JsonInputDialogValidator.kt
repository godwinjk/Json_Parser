package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.godwin.jsonparser.generator_kt.jsontokotlin.utils.InputFileTpeValidateCallback
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.InputValidator


class JsonInputDialogValidator : InputValidator {
    var jsonInputEditor: Editor? = null
    var inputFileTpeValidateCallback: InputFileTpeValidateCallback? = null
    override fun checkInput(className: String): Boolean {
        if (jsonInputEditor == null || inputFileTpeValidateCallback == null) return false
        return className.isNotBlank() && inputIsValidJson(jsonInputEditor!!.document.text) && inputFileTpeValidateCallback!!.fileTypeSelected()
    }

    override fun canClose(inputString: String): Boolean = true

    private fun inputIsValidJson(string: String) = try {
        val jsonElement = JsonParser.parseString(string)
        (jsonElement.isJsonObject || jsonElement.isJsonArray)
    } catch (e: JsonSyntaxException) {
        false
    }
}