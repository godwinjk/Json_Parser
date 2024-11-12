package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.classscodestruct.KotlinDataClass

/**
 * Interceptor that apply the `isCommentOff` config enable condition
 */
object CommentOffInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val newProperty = kotlinDataClass.properties.map {
            it.copy(comment = "")
        }

        return kotlinDataClass.copy(properties = newProperty)
    }
}