package wu.seal.jsontodart.utils

import com.intellij.openapi.diagnostic.LoggerRt
import wu.seal.jsontodart.test.TestConfig
import java.io.File
import java.util.logging.Logger

/**
 * Created by Seal.Wu on 2018/3/12.
 */
object LogUtil {

    fun i(info: String) {
        if (TestConfig.isTestModel) {
            Logger.getLogger(PLUGIN_NAME).info(info)
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).info(info)
        }
    }

    fun w(warning: String) {
        if (TestConfig.isTestModel) {
            Logger.getLogger(PLUGIN_NAME).warning(warning)
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).warn(warning)
        }
    }

    fun e(message: String, e: Throwable) {
        if (TestConfig.isTestModel) {
            e.printStackTrace()
        } else {
            LoggerRt.getInstance(PLUGIN_NAME).error(message,e)
        }
    }
}