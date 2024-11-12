package wu.seal.jsontodart

import com.intellij.openapi.components.ApplicationComponent
import wu.seal.jsontodart.utils.LogUtil
import wu.seal.jsontodart.utils.PLUGIN_VERSION

/**
 *
 * Created by Seal.wu on 2017/8/21.
 */

class JsonToKotlinApplication : ApplicationComponent {

    override fun initComponent() {
        LogUtil.i("init JSON To Kotlin Class version ==$PLUGIN_VERSION")
    }

    override fun disposeComponent() {}

    override fun getComponentName(): String {
        return "wu.seal.jsontodart.JsonToKotlinApplication"
    }
}
