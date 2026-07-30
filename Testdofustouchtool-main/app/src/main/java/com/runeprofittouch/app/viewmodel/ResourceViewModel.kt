package com.runeprofittouch.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.runeprofittouch.app.database.PriceDao
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.ResourceEntity
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.domain.PriceFreshness
import com.runeprofittouch.app.domain.ProfitabilityCalculator
import com.runeprofittouch.app.repository.ResourceRepository
import java.text.Normalizer
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CatalogPriceSort(val label: String) {
    TO_UPDATE("À actualiser en premier"),
    NAME("Nom A–Z"),
    OLDEST("Plus anciens en premier"),
    NEWEST("Plus récents en premier"),
    PRICE_ASC("Prix croissant"),
    PRICE_DESC("Prix décroissant")
}

@OptIn(ExperimentalCoroutinesApi::class)
class ResourceViewModel(
    repository: ResourceRepository,
    private val priceDao: PriceDao,
    private val subjectType: String,
    usedResourceIds: Flow<List<Int>>,
    equipmentItemIds: Flow<List<Int>>
) : ViewModel() {

    companion object {
        private const val SAVE_DELAY_MS = 600L

        private val EQUIPMENT_TYPES = setOf(
            "amulette",
            "anneau",
            "baguette",
            "baton",
            "bottes",
            "bouclier",
            "cape",
            "ceinture",
            "chapeau",
            "coiffe",
            "dague",
            "dofus",
            "epee",
            "familier",
            "hache",
            "marteau",
            "montilier",
            "pelle",
            "trophee"
        )
    }

    val searchText = MutableStateFlow("")
    val sortMode = MutableStateFlow(CatalogPriceSort.TO_UPDATE)
    val priceDrafts = MutableStateFlow<Map<Int, String>>(emptyMap())
    private val saveJobs = mutableMapOf<Int, Job>()

    private val catalog = combine(
        repository.resources,
        usedResourceIds,
        equipmentItemIds
    ) { resources, usedIds, itemIds ->
        if (subjectType == "RUNE") {
            resources.filter { it.resourceType.contains("rune", ignoreCase = true) }
        } else {
            val usedIdSet = usedIds.toSet()
            val equipmentIdSet = itemIds.toSet()
            resources.filter {
                val normalizedType = normalizeForSearch(it.resourceType)
                it.id in usedIdSet &&
                    it.id !in equipmentIdSet &&
                    normalizedType !in EQUIPMENT_TYPES &&
                    !it.resourceType.contains("rune", ignoreCase = true)
            }
        }
    }

    val latestPrices = ServerStore.selectedServer
        .flatMapLatest { server ->
            priceDao.observeLatestPrices(subjectType, server)
        }
        .map { prices -> prices.associateBy(PriceEntity::subjectId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val resources = combine(
        catalog,
        searchText,
        latestPrices,
        sortMode
    ) { resources, query, prices, sort ->
        val normalizedQuery = normalizeForSearch(query)
        val filtered = resources.filter {
            normalizeForSearch(it.name).contains(normalizedQuery)
        }
        when (sort) {
            CatalogPriceSort.TO_UPDATE -> filtered.sortedWith(
                compareBy<ResourceEntity> {
                    freshnessRank(prices[it.id])
                }.thenBy {
                    prices[it.id]?.recordedAt ?: Long.MIN_VALUE
                }.thenBy {
                    normalizeForSearch(it.name)
                }
            )
            CatalogPriceSort.NAME -> filtered.sortedBy {
                normalizeForSearch(it.name)
            }
            CatalogPriceSort.OLDEST -> filtered.sortedWith(
                compareBy<ResourceEntity> {
                    prices[it.id]?.recordedAt ?: Long.MIN_VALUE
                }.thenBy {
                    normalizeForSearch(it.name)
                }
            )
            CatalogPriceSort.NEWEST -> filtered.sortedWith(
                compareByDescending<ResourceEntity> {
                    prices[it.id]?.recordedAt ?: Long.MIN_VALUE
                }.thenBy {
                    normalizeForSearch(it.name)
                }
            )
            CatalogPriceSort.PRICE_ASC -> filtered.sortedWith(
                compareBy<ResourceEntity> {
                    prices[it.id]?.price ?: Long.MIN_VALUE
                }.thenBy {
                    normalizeForSearch(it.name)
                }
            )
            CatalogPriceSort.PRICE_DESC -> filtered.sortedWith(
                compareByDescending<ResourceEntity> {
                    prices[it.id]?.price ?: Long.MIN_VALUE
                }.thenBy {
                    normalizeForSearch(it.name)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun updateSearchText(value: String) {
        searchText.value = value
    }

    fun updateSortMode(value: CatalogPriceSort) {
        sortMode.value = value
    }

    fun updatePrice(resourceId: Int, value: String) {
        val sanitized = value.filter(Char::isDigit).take(12)
        priceDrafts.value = priceDrafts.value + (resourceId to sanitized)
        saveJobs.remove(resourceId)?.cancel()
        saveJobs[resourceId] = viewModelScope.launch {
            delay(SAVE_DELAY_MS)
            sanitized.toLongOrNull()?.let { price ->
                priceDao.insert(
                    PriceEntity(
                        subjectType = subjectType,
                        subjectId = resourceId,
                        server = ServerStore.selectedServer.value,
                        lotSize = 1,
                        price = price
                    )
                )
            }
        }
    }

    private fun normalizeForSearch(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
            .trim()

    private fun freshnessRank(price: PriceEntity?): Int {
        if (price == null) return 0
        return when (ProfitabilityCalculator.freshness(price.recordedAt)) {
            PriceFreshness.STALE -> 1
            PriceFreshness.AGING -> 2
            PriceFreshness.FRESH -> 3
        }
    }
}

class ResourceViewModelFactory(
    private val repository: ResourceRepository,
    private val priceDao: PriceDao,
    private val subjectType: String,
    private val usedResourceIds: Flow<List<Int>>,
    private val equipmentItemIds: Flow<List<Int>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResourceViewModel::class.java)) {
            return ResourceViewModel(
                repository,
                priceDao,
                subjectType,
                usedResourceIds,
                equipmentItemIds
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
