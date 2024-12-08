package com.godwin.jsonparser.generator_kt.jsontokotlin.feedback

import java.io.File
import java.util.*

/**
 *
 * Created by Godwin on 2024/12/20
 */

interface IPersistCache {

    fun saveExceptionInfo(exceptionInfo: String)

    fun readAllCachedExceptionInfo(): String

    fun deleteAllExceptionInfoCache()

    fun saveActionInfo(actionInfo: String)

    fun readAllCachedActionInfo(): String

    fun deleteAllActionInfo()
}

object PersistCache : IPersistCache {

    private val rootDir = "${DefaultCacheDirProvider().get()}/.jsontokotlin"
    private val exceptionDirPath = "$rootDir/exceptionLog"

    private val actionInfoPath = "$rootDir/actionInfo"

    private val exceptionDir = File(exceptionDirPath)
    private val actionDir = File(actionInfoPath)

    init {

        if (!exceptionDir.exists()) {
            exceptionDir.mkdirs()
        }

        if (!actionDir.exists()) {
            actionDir.mkdirs()
        }
    }

    override fun saveExceptionInfo(exceptionInfo: String) {
        File(exceptionDir, Date().time.toString()).writeText(exceptionInfo)
    }

    override fun readAllCachedExceptionInfo(): String {
        val builder = StringBuilder()
        exceptionDir.listFiles()?.forEach {
            if (it.exists()) {
                builder.append(it.readText()).append("#")
            }
        }
        return builder.toString()
    }

    override fun deleteAllExceptionInfoCache() {
        exceptionDir.listFiles()?.forEach { it.delete() }
    }

    override fun saveActionInfo(actionInfo: String) {
        File(actionDir, Date().time.toString()).writeText(actionInfo)
    }

    override fun readAllCachedActionInfo(): String {
        val builder = StringBuilder()
        actionDir.listFiles()?.forEach {
            if (it.exists()) {
                builder.append(it.readText()).append("#")
            }
        }
        return builder.toString()
    }

    override fun deleteAllActionInfo() {
        actionDir.listFiles()?.forEach { it.delete() }
    }

}
