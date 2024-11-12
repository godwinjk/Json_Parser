package wu.seal.jsontodart.interceptor.annotations.custom

import wu.seal.jsontodart.ConfigManager
import wu.seal.jsontodart.interceptor.IImportClassDeclarationInterceptor

class AddCustomAnnotationClassImportDeclarationInterceptor : IImportClassDeclarationInterceptor {

    override fun intercept(originClassImportDeclaration: String): String {


        val propertyAnnotationImportClassString = ConfigManager.customAnnotationClassImportdeclarationString


        return originClassImportDeclaration.append(propertyAnnotationImportClassString)
    }
}
