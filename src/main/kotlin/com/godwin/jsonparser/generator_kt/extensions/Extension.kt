package com.godwin.jsonparser.generator_kt.extensions

import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IImportClassDeclarationInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.interceptor.IKotlinClassInterceptor
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.KotlinConfigManager
import com.godwin.jsonparser.generator_kt.jsontokotlin.model.classscodestruct.KotlinClass
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.swing.JPanel

/**
 * Extension which represent a function extending the current functions of JsonToKotlinClass plugin
 * It must have a UI item to be insert into the JBList JPanel of Extensions Tab
 */
abstract class Extension : IImportClassDeclarationInterceptor, IKotlinClassInterceptor<KotlinClass> {

    private val gson = Gson()

    /**
     * Create a user interface item for inserting into JBList
     */
    abstract fun createUI(): JPanel


    /**
     * set every config inside the extension
     * to keep the key primary, we could define it like : wu.seal.xxx using domain before the real config
     */
    protected fun setConfig(key: String, value: String) {
        val configs = gson.fromJson(KotlinConfigManager.extensionsConfig, JsonObject::class.java) ?: JsonObject()
        configs.addProperty(key, value)
        KotlinConfigManager.extensionsConfig = gson.toJson(configs)
    }

    /**
     * get every config inside the extension
     * to keep the key primary, we could define it like : wu.seal.xxx  using domain before the real config
     */
    protected fun getConfig(key: String): String {
        val configs = gson.fromJson(KotlinConfigManager.extensionsConfig, JsonObject::class.java) ?: JsonObject()
        return configs[key]?.asString ?: ""
    }

    override fun intercept(originClassImportDeclaration: String): String {
        return originClassImportDeclaration
    }

    @Deprecated("Please use val testHelper")
    @JvmName("getTestHelperForJVM")
    fun getTestHelper() = TestHelper(this)

    val testHelper = TestHelper(this)

    /**
     * Test helper for test config settings
     */
    class TestHelper(private val extension: Extension) {

        fun setConfig(key: String, value: String) {
            extension.setConfig(key, value)
        }

        fun getConfig(key: String): String {
            return extension.getConfig(key)
        }
    }

    /**
     * val configKey = "xxConfig"
     *
     * val configBooleanValue = configKey.booleanConfig()
     */
    protected val String.booleanConfigValue: Boolean
        get() = getConfig(this).toBoolean()
}
