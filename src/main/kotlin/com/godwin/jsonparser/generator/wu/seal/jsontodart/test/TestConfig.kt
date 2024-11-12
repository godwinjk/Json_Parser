package wu.seal.jsontodart.test

import wu.seal.jsontodart.DefaultValueStrategy
import wu.seal.jsontodart.TargetJsonConverter

/**
 *
 * Created by Seal.Wu on 2018/2/7.
 */
/**
 * config for test unit
 */
object TestConfig {
    /**
     * If it is in test model
     */
    var isTestModel = false
    var isCommentOff = false
    var isOrderByAlphabetical = false
    var isPropertiesFinal = false
    var targetJsonConvertLib = TargetJsonConverter.None
    var defaultValueStrategy = DefaultValueStrategy.AvoidNull

    var customPropertyAnnotationFormatString = "@Optional\n@SerialName(\"%s\")"
    var customAnnotaionImportClassString = "import kotlinx.serialization.SerialName\n" +
            "import kotlinx.serialization.Serializable" + "\n" + "import kotlinx.serialization.Optional"

    var customClassAnnotationFormatString = "@Serializable"

    var indent: Int = 4

    var enableMapType: Boolean = true

    var enableMinimalAnnotation = false

    var parenClassTemplate = ""

    var isKeywordPropertyValid = true

    var extensionsConfig = ""
}
