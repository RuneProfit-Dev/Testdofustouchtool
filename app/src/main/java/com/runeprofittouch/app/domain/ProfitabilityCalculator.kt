package com.runeprofittouch.app.domain

import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

enum class PriceFreshness {
    FRESH,
    AGING,
    STALE
}

data class ProfitabilityResult(
    val craftCost: Long,
    val adjustedRuneValue: Long,
    val profit: Long,
    val roiPercent: Double
)

object ProfitabilityCalculator {

    fun calculate(
        detailedCraftCost: Long,
        manualCraftCost: Long,
        baseRuneValue: Long,
        crushingCoefficientPercent: Double
    ): ProfitabilityResult {
        val craftCost = manualCraftCost.takeIf { it > 0 } ?: detailedCraftCost
        val adjustedRuneValue =
            (baseRuneValue * crushingCoefficientPercent.coerceAtLeast(0.0) / 100.0).roundToLong()
        val profit = adjustedRuneValue - craftCost
        val roi = if (craftCost > 0) profit.toDouble() / craftCost * 100.0 else 0.0

        return ProfitabilityResult(craftCost, adjustedRuneValue, profit, roi)
    }

    fun freshness(
        recordedAt: Long,
        now: Long = System.currentTimeMillis()
    ): PriceFreshness {
        val ageDays = TimeUnit.MILLISECONDS.toDays((now - recordedAt).coerceAtLeast(0L))
        return when (ageDays) {
            in 0..3 -> PriceFreshness.FRESH
            in 4..6 -> PriceFreshness.AGING
            else -> PriceFreshness.STALE
        }
    }
}
