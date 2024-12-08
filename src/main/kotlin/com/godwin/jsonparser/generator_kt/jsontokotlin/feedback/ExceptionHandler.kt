package com.godwin.jsonparser.generator_kt.jsontokotlin.feedback

import com.godwin.jsonparser.common.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.ui.Messages
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * Created by Godwin on 2024/12/20
 */

/**
 * handler the exception
 */

val prettyPrintGson: Gson = GsonBuilder().setPrettyPrinting().create()

fun getUncaughtExceptionHandler(jsonString: String, callBack: () -> Unit): Thread.UncaughtExceptionHandler =
    Thread.UncaughtExceptionHandler { _, e ->
        val logBuilder = StringBuilder()
        logBuilder.append("\n\n")
        logBuilder.append("PluginVersion:$PLUGIN_VERSION\n")
        logBuilder.append("user: $UUID").append("\n")
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss E", Locale.CHINA).format(Date())
        logBuilder.append("createTime: $time").append("\n")

        logBuilder.appendLine().append(getConfigInfo()).appendLine()
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter, true)
        e.printStackTrace(printWriter)
        var cause = e.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        logBuilder.append(stringWriter.toString())

        logBuilder.append("Error Json String:\n")
        logBuilder.append(jsonString)
        Logger.i(logBuilder.toString())
//        Thread {
//            sendExceptionLog(logBuilder.toString())
//        }.start()

        callBack.invoke()
    }

/**
 * get the config info of this plugin settings
 */
fun getConfigInfo(): String {
    return prettyPrintGson.toJson(ConfigInfo())
}

fun dealWithException(jsonString: String, e: Throwable) {
    var jsonString1 = jsonString
    val yes = Messages.showYesNoDialog(
        "Some thing execute wrong.\nAgree with publishing your JSON text to help us to solve the problem?",
        "Oops",
        Messages.getQuestionIcon()
    )
    if (yes != Messages.YES) {
        jsonString1 = "User keep private about JSON text"
    }
    getUncaughtExceptionHandler(jsonString1) {
        Messages.showErrorDialog(
            "Oops! It looks like the Json Parser ran into a little hiccup. No worries though! You can give it another go after updating to the latest version, or if the issue persists, feel free to toss it into the issue tracker\nhttps://github.com/godwinjk/Json_Parser\n and we'll tackle it together!",
            "OOps!! Fatal Error"
        )
    }.uncaughtException(Thread.currentThread(), e)
}