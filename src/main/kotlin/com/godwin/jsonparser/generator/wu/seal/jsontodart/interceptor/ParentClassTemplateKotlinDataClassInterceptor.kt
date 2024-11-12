package wu.seal.jsontodart.interceptor

import wu.seal.jsontodart.ConfigManager
import wu.seal.jsontodart.classscodestruct.KotlinDataClass

class ParentClassTemplateKotlinDataClassInterceptor : IKotlinDataClassInterceptor {

    override fun intercept(kotlinDataClass: KotlinDataClass): KotlinDataClass {

        val parentClassTemplateSimple = ConfigManager.parenClassTemplate.substringAfterLast(".")
        return kotlinDataClass.copy(parentClassTemplate = parentClassTemplateSimple)
    }


}