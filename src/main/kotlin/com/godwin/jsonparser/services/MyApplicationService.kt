package com.godwin.jsonparser.services

import com.godwin.jsonparser.MyBundle
import com.intellij.openapi.components.Service

@Service(Service.Level.APP)
class MyApplicationService {
    companion object{
        private var field:MyApplicationService? =null
        fun getInstance(): MyApplicationService{
            if(field == null) field = MyApplicationService()
            return field!!
        }
    }
}
