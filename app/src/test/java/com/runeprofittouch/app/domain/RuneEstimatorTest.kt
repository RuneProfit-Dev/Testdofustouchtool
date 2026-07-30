package com.runeprofittouch.app.domain

import com.runeprofittouch.app.database.ItemStatEntity
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.ResourceEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RuneEstimatorTest {

    @Test
    fun paStatIsExposedAsGaPaForSearchFiltering() {
        val produced = RuneEstimator.producedRuneNames(
            listOf(ItemStatEntity(1, "PA", 1, 1))
        )

        assertEquals(setOf("Rune Ga Pa"), produced)
        assertEquals("Rune Ga Pa", RuneEstimator.availableRuneNames().first())
    }

    @Test
    fun coefficientAndRunePriceAreAppliedToAverageJet() {
        val stat = ItemStatEntity(1, "Force", 10, 20)
        val rune = ResourceEntity(1519, "Rune Fo", "Rune de forgemagie", 10)
        val price = PriceEntity(
            subjectType = "RUNE",
            subjectId = 1519,
            server = "Tiliwan",
            price = 100
        )

        val estimate = RuneEstimator.estimate(
            stats = listOf(stat),
            runes = listOf(rune),
            runePrices = mapOf(1519 to price),
            coefficientPercent = 200.0
        ).single()

        assertEquals(30.0, estimate.estimatedQuantity, 0.001)
        assertEquals(3_000, estimate.estimatedValue)
    }

    @Test
    fun elementalResistanceAndPaDodgeMapToTheirRunes() {
        val stats = listOf(
            ItemStatEntity(1, "% Résistance Air", 4, 6),
            ItemStatEntity(1, "Esquive PA", 2, 2)
        )
        val runes = listOf(
            ResourceEntity(1, "Rune Ré Per Air", "Rune de forgemagie", 10),
            ResourceEntity(2, "Rune Ré Pa", "Rune de forgemagie", 10)
        )

        val estimates = RuneEstimator.estimate(
            stats = stats,
            runes = runes,
            runePrices = emptyMap(),
            coefficientPercent = 100.0
        )

        assertEquals("Rune Ré Per Air", estimates[0].runeName)
        assertEquals(5.0, estimates[0].estimatedQuantity, 0.001)
        assertEquals("Rune Ré Pa", estimates[1].runeName)
        assertEquals(2.0, estimates[1].estimatedQuantity, 0.001)
    }
}
