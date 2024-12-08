package com.godwin.jsonparser.generator.jsontodart

import com.google.gson.JsonElement

/**
 * Target Json Element Maker form a String or an Element for generating Code
 * Created by Godwin on 2024/12/20.
 */
interface ITargetJsonElement {
    /**
     * get expected jsonElement for generating kotlin code
     */
    fun getTargetJsonElementForGeneratingCode(): JsonElement
}
