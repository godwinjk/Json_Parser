package com.godwin.jsonparser.services

import com.godwin.jsonparser.MyBundle

class MyApplicationService {

    init {
        println(MyBundle.message("applicationService"))
        field= this
    }

    companion object{
        private lateinit var field:MyApplicationService
        fun getInstance(): MyApplicationService{
            return field
        }
    }
}
