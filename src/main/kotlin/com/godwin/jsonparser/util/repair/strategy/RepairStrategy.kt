package com.godwin.jsonparser.util.repair.strategy

import java.util.concurrent.CompletableFuture

abstract class RepairStrategy {
    abstract fun repair( input: String,error: String?): CompletableFuture<String>
}
