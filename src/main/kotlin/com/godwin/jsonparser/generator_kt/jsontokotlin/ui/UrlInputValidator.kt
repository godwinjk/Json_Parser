package com.godwin.jsonparser.generator_kt.jsontokotlin.ui

import com.intellij.openapi.ui.InputValidator
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by Godwin at 2024/12/20 16:58
 *
 */
object UrlInputValidator : InputValidator {
    override fun checkInput(inputString: String): Boolean = try {
        URL(inputString)
        true
    } catch (e: MalformedURLException) {
        false
    }

    override fun canClose(inputString: String): Boolean = true
}
