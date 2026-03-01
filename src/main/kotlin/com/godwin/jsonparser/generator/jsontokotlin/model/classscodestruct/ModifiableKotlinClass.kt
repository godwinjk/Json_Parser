package com.godwin.jsonparser.generator.jsontokotlin.model.classscodestruct

interface ModifiableKotlinClass : KotlinClass {

    override val modifiable: Boolean
        get() = true


}