package com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct

interface ModifiableKotlinClass : KotlinClass {

    override val modifiable: Boolean
        get() = true


}