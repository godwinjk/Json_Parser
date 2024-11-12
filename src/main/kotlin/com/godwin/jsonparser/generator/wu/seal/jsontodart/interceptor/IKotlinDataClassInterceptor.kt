package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.classscodestruct.KotlinDataClass

/**
 * Interceptor for code transform
 */
interface IKotlinDataClassInterceptor {

    /**
     * intercept the kotlindataclass and modify the class,the function will return a new  Kotlin Data Class Object
     * warn: the new returned object  is a new object ,not the original
     */
    fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass

}