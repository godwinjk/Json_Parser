package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.ConfigManager


/**
 * insert parent class declaration code
 */
class ParentClassClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {

        val parentClassImportDeclaration = "import ${ConfigManager.parenClassTemplate.substringBeforeLast("(").trim()}"

        return "$originClassImportDeclaration\n$parentClassImportDeclaration".trim()
    }
}