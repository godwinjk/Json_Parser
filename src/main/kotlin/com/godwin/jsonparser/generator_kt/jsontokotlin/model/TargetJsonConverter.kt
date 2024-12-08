package com.godwin.jsonparser.generator_kt.jsontokotlin.model

/**
 *
 * Created by Godwin on 2024/12/20
 */
/**
 * This means which Json convert library you are using in you project
 */
enum class TargetJsonConverter {
    None, NoneWithCamelCase, Gson, FastJson, Jackson, MoShi, LoganSquare, Custom, MoshiCodeGen, Serializable, DartPackage
}
