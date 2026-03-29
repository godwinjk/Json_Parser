package com.godwin.jsonparser.util.repair.strategy

import com.godwin.jsonautorepair.JsonAutoRepair
import java.util.concurrent.CompletableFuture

class JsonAutoRepairStrategy : RepairStrategy() {
    private companion object {
        const val MAX_ITERATIONS = 5
    }

    override fun repair(input: String, error: String?): CompletableFuture<String> {
        return CompletableFuture.supplyAsync {
            JsonAutoRepair(MAX_ITERATIONS).repair(input).output
        }
    }
}
