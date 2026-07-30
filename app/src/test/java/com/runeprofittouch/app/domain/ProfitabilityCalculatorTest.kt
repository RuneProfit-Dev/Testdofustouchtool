package com.runeprofittouch.app.domain

import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfitabilityCalculatorTest {

    @Test
    fun manualCraftCostTakesPriorityAndCoefficientAdjustsRunes() {
        val result = ProfitabilityCalculator.calculate(
            detailedCraftCost = 60_000,
            manualCraftCost = 45_000,
            baseRuneValue = 40_000,
            crushingCoefficientPercent = 125.0
        )

        assertEquals(45_000, result.craftCost)
        assertEquals(50_000, result.adjustedRuneValue)
        assertEquals(5_000, result.profit)
        assertEquals(11.11, result.roiPercent, 0.01)
    }

    @Test
    fun detailedCostIsUsedWhenManualValueIsEmpty() {
        val result = ProfitabilityCalculator.calculate(
            detailedCraftCost = 12_500,
            manualCraftCost = 0,
            baseRuneValue = 10_000,
            crushingCoefficientPercent = 100.0
        )

        assertEquals(12_500, result.craftCost)
        assertEquals(-2_500, result.profit)
    }

    @Test
    fun freshnessUsesRequestedDayBoundaries() {
        val now = 2_000_000_000_000L
        assertEquals(
            PriceFreshness.FRESH,
            ProfitabilityCalculator.freshness(now - TimeUnit.DAYS.toMillis(3), now)
        )
        assertEquals(
            PriceFreshness.AGING,
            ProfitabilityCalculator.freshness(now - TimeUnit.DAYS.toMillis(4), now)
        )
        assertEquals(
            PriceFreshness.AGING,
            ProfitabilityCalculator.freshness(now - TimeUnit.DAYS.toMillis(6), now)
        )
        assertEquals(
            PriceFreshness.STALE,
            ProfitabilityCalculator.freshness(now - TimeUnit.DAYS.toMillis(7), now)
        )
    }
}
