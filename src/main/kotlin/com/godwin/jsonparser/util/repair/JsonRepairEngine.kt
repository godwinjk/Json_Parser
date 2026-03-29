package com.godwin.jsonparser.util.repair

import com.godwin.jsonparser.common.exception.InputTooLargeException
import com.godwin.jsonparser.util.JsonUtils
import com.godwin.jsonparser.util.Log
import com.godwin.jsonparser.util.repair.strategy.AiRepairStrategy
import com.godwin.jsonparser.util.repair.strategy.JsonAutoRepairStrategy
import com.godwin.jsonparser.util.repair.strategy.RepairStrategy
import java.util.concurrent.CompletableFuture

object JsonRepairEngine {

    fun repair(
        input: String, strategies: List<RepairStrategy> = listOf(
            JsonAutoRepairStrategy(),
            AiRepairStrategy()
        )
    ): CompletableFuture<String?> {
        if (input.length > 1_000_000) {
            return CompletableFuture.failedFuture(InputTooLargeException("Input is too large"))
        }

        // Chain strategies: run next only if current result is still invalid JSON
        return strategies.fold(CompletableFuture.completedFuture(input)) { chain, strategy ->
            chain.thenCompose { current ->
                val error = JsonUtils.isValidJsonError(current);
                if (error == null) {
                    // Already valid — skip remaining strategies
                    CompletableFuture.completedFuture(current)
                } else {
                    strategy.repair(current, error)
                        .exceptionally { e ->
                            Log.e("${strategy.javaClass.simpleName} failed: ${e.message}")
                            current // fall through with unchanged text on error
                        }
                }
            }
        }.thenApply { result ->
            if (JsonUtils.isValidJson(result)) result else null
        }
    }
}
