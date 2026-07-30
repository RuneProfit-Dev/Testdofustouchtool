package com.runeprofittouch.app.domain

import com.runeprofittouch.app.database.ItemStatEntity
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.ResourceEntity
import java.text.Normalizer
import java.util.Locale

data class RuneEstimate(
    val characteristic: String,
    val runeName: String,
    val averageStat: Double,
    val estimatedQuantity: Double,
    val unitPrice: Long,
    val estimatedValue: Long
)

object RuneEstimator {

    private data class Rule(
        val statAliases: List<String>,
        val runeName: String,
        val pointsPerRune: Double
    )

    private val rules = listOf(
        Rule(listOf("force"), "Rune Fo", 1.0),
        Rule(listOf("intelligence"), "Rune Ine", 1.0),
        Rule(listOf("agilite"), "Rune Age", 1.0),
        Rule(listOf("chance"), "Rune Cha", 1.0),
        Rule(listOf("sagesse"), "Rune Sa", 1.0),
        Rule(listOf("vitalite", "vie"), "Rune Vi", 3.0),
        Rule(listOf("initiative"), "Rune Ini", 10.0),
        Rule(listOf("pods"), "Rune Pod", 10.0),
        Rule(listOf("pa"), "Rune Ga Pa", 1.0),
        Rule(listOf("pm"), "Rune Ga Pme", 1.0),
        Rule(listOf("po", "portee"), "Rune Po", 1.0),
        Rule(listOf("prospection"), "Rune Prospe", 1.0),
        Rule(listOf("dommages", "dommage"), "Rune Do", 1.0),
        Rule(listOf("dommages air"), "Rune Do Air", 1.0),
        Rule(listOf("dommages eau"), "Rune Do Eau", 1.0),
        Rule(listOf("dommages feu"), "Rune Do Feu", 1.0),
        Rule(listOf("dommages neutre"), "Rune Do Neutre", 1.0),
        Rule(listOf("dommages terre"), "Rune Do Terre", 1.0),
        Rule(listOf("dommages critiques"), "Rune Do Cri", 1.0),
        Rule(listOf("dommages poussee"), "Rune Do Pou", 1.0),
        Rule(listOf("dommages pieges"), "Rune Pi", 1.0),
        Rule(listOf("puissance (pieges)"), "Rune Pi Per", 1.0),
        Rule(listOf("soins", "soin"), "Rune So", 1.0),
        Rule(listOf("coups critiques", "critique", "cc"), "Rune Cri", 1.0),
        Rule(listOf("invocations", "invocation"), "Rune Invo", 1.0),
        Rule(listOf("puissance"), "Rune Pui", 1.0),
        Rule(listOf("tacle"), "Rune Tac", 1.0),
        Rule(listOf("fuite"), "Rune Fui", 1.0),
        Rule(listOf("retrait pa"), "Rune Ret Pa", 1.0),
        Rule(listOf("retrait pm"), "Rune Ret Pme", 1.0),
        Rule(listOf("esquive pa"), "Rune Ré Pa", 1.0),
        Rule(listOf("esquive pm"), "Rune Ré Pme", 1.0),
        Rule(listOf("resistance critiques"), "Rune Ré Cri", 1.0),
        Rule(listOf("resistance poussee"), "Rune Ré Pou", 1.0),
        Rule(listOf("resistance air"), "Rune Ré Air", 1.0),
        Rule(listOf("resistance eau"), "Rune Ré Eau", 1.0),
        Rule(listOf("resistance feu"), "Rune Ré Feu", 1.0),
        Rule(listOf("resistance neutre"), "Rune Ré Neutre", 1.0),
        Rule(listOf("resistance terre"), "Rune Ré Terre", 1.0),
        Rule(listOf("% resistance air"), "Rune Ré Per Air", 1.0),
        Rule(listOf("% resistance eau"), "Rune Ré Per Eau", 1.0),
        Rule(listOf("% resistance feu"), "Rune Ré Per Feu", 1.0),
        Rule(listOf("% resistance neutre"), "Rune Ré Per Neutre", 1.0),
        Rule(listOf("% resistance terre"), "Rune Ré Per Terre", 1.0),
        Rule(listOf("renvoie dommages", "renvoie  dommages"), "Rune Do Ren", 1.0)
    )

    fun availableRuneNames(): List<String> =
        rules.map { it.runeName }.distinct().sortedWith(
            compareBy<String> {
                when (normalize(it)) {
                    "rune ga pa" -> 0
                    "rune ga pme" -> 1
                    "rune po" -> 2
                    else -> 3
                }
            }.thenBy(::normalize)
        )

    fun producedRuneNames(stats: List<ItemStatEntity>): Set<String> =
        stats.mapNotNull { stat ->
            val normalizedStat = normalize(stat.name)
            rules.firstOrNull { candidate ->
                candidate.statAliases.any { normalize(it) == normalizedStat }
            }?.runeName
        }.toSet()

    fun estimate(
        stats: List<ItemStatEntity>,
        runes: List<ResourceEntity>,
        runePrices: Map<Int, PriceEntity>,
        coefficientPercent: Double
    ): List<RuneEstimate> {
        val runesByName = runes.associateBy { normalize(it.name) }
        val coefficient = coefficientPercent.coerceAtLeast(0.0) / 100.0

        return stats.mapNotNull { stat ->
            val normalizedStat = normalize(stat.name)
            val rule = rules.firstOrNull { candidate ->
                candidate.statAliases.any { normalize(it) == normalizedStat }
            } ?: return@mapNotNull null
            val rune = runesByName[normalize(rule.runeName)] ?: return@mapNotNull null
            val average = (stat.minimum + stat.maximum) / 2.0
            val quantity = average / rule.pointsPerRune * coefficient
            val price = runePrices[rune.id]?.price ?: 0L

            RuneEstimate(
                characteristic = stat.name,
                runeName = rune.name,
                averageStat = average,
                estimatedQuantity = quantity,
                unitPrice = price,
                estimatedValue = (quantity * price).toLong()
            )
        }.filter { it.estimatedQuantity > 0.0 }
    }

    private fun normalize(value: String): String {
        val repaired =
            if ('Ã' in value || 'Â' in value) {
                String(value.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            } else {
                value
            }
        return Normalizer.normalize(repaired, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
            .trim()
    }
}
