package com.runeprofittouch.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.runeprofittouch.app.data.Professions
import com.runeprofittouch.app.data.ServerStore
import com.runeprofittouch.app.database.DatabaseProvider
import com.runeprofittouch.app.database.ItemAnalysisEntity
import com.runeprofittouch.app.database.ItemEntity
import com.runeprofittouch.app.database.ItemStatEntity
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.RecipeIngredientDetail
import com.runeprofittouch.app.database.ResourceEntity
import com.runeprofittouch.app.domain.RuneEstimator
import com.runeprofittouch.app.repository.ItemRepository
import java.text.Normalizer
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PriceViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SAVE_DELAY_MS = 600L
    }

    private val database = DatabaseProvider.getDatabase(application)
    private val repository = ItemRepository(
        itemDao = database.itemDao(),
        recipeIngredientDao = database.recipeIngredientDao(),
        priceDao = database.priceDao(),
        itemAnalysisDao = database.itemAnalysisDao(),
        favoriteDao = database.favoriteDao()
    )

    val searchText = MutableStateFlow("")
    val selectedProfessions = MutableStateFlow<Set<String>>(emptySet())
    val selectedRecipeSlots = MutableStateFlow((2..8).toSet())
    val selectedRuneFilters = MutableStateFlow<Set<String>>(emptySet())
    val runeFilterOptions = RuneEstimator.availableRuneNames()
    val sortMode = MutableStateFlow(ItemSortMode.PROFIT_DESCENDING)
    val isFiltering = MutableStateFlow(false)
    private var filteringJob: Job? = null
    private val selectedItemId = MutableStateFlow<Int?>(null)
    val selectedItem = MutableStateFlow<ItemEntity?>(null)

    val manualCraftCostText = MutableStateFlow("")
    val crushingCoefficientText = MutableStateFlow("100")
    val resourcePriceDrafts = MutableStateFlow<Map<Int, String>>(emptyMap())

    private var analysisSaveJob: Job? = null
    private val resourceSaveJobs = mutableMapOf<Int, Job>()

    private val searchFilters = combine(
        searchText,
        selectedProfessions,
        selectedRecipeSlots,
        selectedRuneFilters
    ) { search, professions, selectedSlots, selectedRunes ->
        SearchFilters(search, professions, selectedSlots, selectedRunes)
    }

    val filteredItems = combine(
        repository.items,
        repository.recipeSlotCounts,
        database.itemStatDao().observeAll(),
        searchFilters
    ) { items, recipeSlotCounts, allStats, filters ->
        val normalizedSearch = normalizeForSearch(filters.search)
        val slotCountByItemId = recipeSlotCounts.associate { it.itemId to it.slotCount }
        val statsByItemId = allStats.groupBy(ItemStatEntity::itemId)
        items.filter { item ->
            normalizeForSearch(item.name).contains(normalizedSearch) &&
                (filters.professions.isEmpty() ||
                    filters.professions.any { selected -> item.profession.equals(selected, ignoreCase = true) }) &&
                slotCountByItemId[item.id] in filters.selectedSlots &&
                (
                    filters.selectedRunes.isEmpty() ||
                        RuneEstimator.producedRuneNames(
                            statsByItemId[item.id].orEmpty()
                        ).containsAll(filters.selectedRunes)
                )
        }
    }
        .conflate()
        .flowOn(Dispatchers.Default)
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val favoriteIds = repository.favoriteIds
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    val selectedRecipe = selectedItemId
        .flatMapLatest { itemId ->
            if (itemId == null) flowOf(emptyList())
            else repository.observeRecipe(itemId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList<RecipeIngredientDetail>()
        )

    val selectedStats = selectedItemId
        .flatMapLatest { itemId ->
            if (itemId == null) flowOf(emptyList())
            else database.itemStatDao().observeByItemId(itemId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList<ItemStatEntity>()
        )

    val latestResourcePrices = repository
        .let {
            ServerStore.selectedServer.flatMapLatest { server ->
                repository.observeLatestResourcePrices(server)
            }
        }
        .map { prices -> prices.associateBy(PriceEntity::subjectId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val latestRunePrices = ServerStore.selectedServer
        .flatMapLatest { server ->
            database.priceDao().observeLatestPrices("RUNE", server)
        }
        .map { prices -> prices.associateBy(PriceEntity::subjectId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val runes = database.resourceDao().observeAll()
        .map { resources ->
            resources.filter { it.resourceType.contains("rune", ignoreCase = true) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList<ResourceEntity>()
        )

    private val profitabilityInputs = combine(
        repository.items,
        ServerStore.selectedServer.flatMapLatest { server ->
            database.itemAnalysisDao().observeByServer(server)
        },
        database.recipeIngredientDao().observeAll(),
        database.itemStatDao().observeAll()
    ) { items, analyses, recipes, stats ->
        ProfitabilityInputs(items, analyses, recipes, stats)
    }

    private val marketInputs = combine(
        latestResourcePrices,
        latestRunePrices,
        runes
    ) { resourcePrices, currentRunePrices, currentRunes ->
        Triple(resourcePrices, currentRunePrices, currentRunes)
    }

    val profitByItemId = combine(
        profitabilityInputs,
        marketInputs
    ) { inputs, (resourcePrices, currentRunePrices, currentRunes) ->
        val analysesByItem = inputs.analyses.associateBy { it.itemId }
        val recipesByItem = inputs.recipes.groupBy { it.itemId }
        val statsByItem = inputs.stats.groupBy { it.itemId }

        inputs.items.associate { item ->
            val analysis = analysesByItem[item.id]
            val detailedCost = recipesByItem[item.id].orEmpty().sumOf { ingredient ->
                (resourcePrices[ingredient.resourceId]?.price ?: 0L) * ingredient.quantity
            }
            val effectiveCost =
                analysis?.manualCraftCost?.takeIf { it > 0L } ?: detailedCost
            val runeValue = RuneEstimator.estimate(
                stats = statsByItem[item.id].orEmpty(),
                runes = currentRunes,
                runePrices = currentRunePrices,
                coefficientPercent = analysis?.crushingCoefficientPercent ?: 100.0
            ).sumOf { it.estimatedValue }

            item.id to (runeValue - effectiveCost)
        }
    }
        .conflate()
        .flowOn(Dispatchers.Default)
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    val sortedItems = combine(
        filteredItems,
        sortMode,
        profitByItemId,
        repository.recipeSlotCounts,
        favoriteIds
    ) { items, selectedSort, profits, recipeSlotCounts, favorites ->
        val ingredientCountByItemId = recipeSlotCounts.associate { it.itemId to it.slotCount }
        when (selectedSort) {
            ItemSortMode.PROFIT_DESCENDING -> items.sortedWith(
                compareByDescending<ItemEntity> { profits[it.id] ?: Long.MIN_VALUE }
                    .thenBy { normalizeForSearch(it.name) }
            )
            ItemSortMode.NAME -> items.sortedBy { normalizeForSearch(it.name) }
            ItemSortMode.INGREDIENT_COUNT_ASCENDING -> items.sortedWith(
                compareBy<ItemEntity> { ingredientCountByItemId[it.id] ?: Int.MAX_VALUE }
                    .thenBy { normalizeForSearch(it.name) }
            )
            ItemSortMode.FAVORITES_FIRST -> items.sortedWith(
                compareByDescending<ItemEntity> { it.id in favorites }
                    .thenByDescending { profits[it.id] ?: Long.MIN_VALUE }
                    .thenBy { normalizeForSearch(it.name) }
            )
            ItemSortMode.LEVEL_ASCENDING -> items.sortedWith(
                compareBy<ItemEntity> { it.itemLevel }
                    .thenBy { normalizeForSearch(it.name) }
            )
            ItemSortMode.LEVEL_DESCENDING -> items.sortedWith(
                compareByDescending<ItemEntity> { it.itemLevel }
                    .thenBy { normalizeForSearch(it.name) }
            )
        }
    }
        .conflate()
        .flowOn(Dispatchers.Default)
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val selectedAnalysis = combine(
        selectedItemId,
        ServerStore.selectedServer
    ) { itemId, server -> itemId to server }
        .flatMapLatest { (itemId, server) ->
            if (itemId == null) flowOf(null)
            else repository.observeAnalysis(itemId, server)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private fun showFilteringIndicator() {
        filteringJob?.cancel()
        isFiltering.value = true
        filteringJob = viewModelScope.launch {
            delay(220L)
            isFiltering.value = false
        }
    }

    fun updateSearchText(value: String) {
        searchText.value = value
        showFilteringIndicator()
    }

    fun toggleProfession(value: String) {
        showFilteringIndicator()
        selectedProfessions.value =
            if (value in selectedProfessions.value) {
                selectedProfessions.value - value
            } else {
                selectedProfessions.value + value
            }
    }

    fun clearProfessions() {
        showFilteringIndicator()
        selectedProfessions.value = emptySet()
    }

    fun updateSortMode(value: ItemSortMode) {
        showFilteringIndicator()
        sortMode.value = value
    }

    fun toggleRecipeSlot(slotCount: Int) {
        showFilteringIndicator()
        selectedRecipeSlots.value =
            if (slotCount in selectedRecipeSlots.value) {
                selectedRecipeSlots.value - slotCount
            } else {
                selectedRecipeSlots.value + slotCount
            }
    }

    fun toggleRuneFilter(runeName: String) {
        showFilteringIndicator()
        selectedRuneFilters.value =
            if (runeName in selectedRuneFilters.value) {
                selectedRuneFilters.value - runeName
            } else {
                selectedRuneFilters.value + runeName
            }
    }

    fun clearRuneFilters() {
        showFilteringIndicator()
        selectedRuneFilters.value = emptySet()
    }

    fun toggleFavorite(itemId: Int) {
        viewModelScope.launch {
            if (itemId in favoriteIds.value) {
                repository.removeFavorite(itemId)
            } else {
                repository.addFavorite(itemId)
            }
        }
    }

    fun selectItem(item: ItemEntity) {
        selectedItem.value = item
        selectedItemId.value = item.id
        resourcePriceDrafts.value = emptyMap()
        viewModelScope.launch {
            val saved = repository.observeAnalysis(
                item.id,
                ServerStore.selectedServer.value
            ).first()
            manualCraftCostText.value = saved?.manualCraftCost?.takeIf { it > 0 }?.toString().orEmpty()
            crushingCoefficientText.value =
                saved?.crushingCoefficientPercent?.formatEditable() ?: "100"
        }
    }

    fun closeRecipe() {
        analysisSaveJob?.cancel()
        resourceSaveJobs.values.forEach(Job::cancel)
        selectedItem.value = null
        selectedItemId.value = null
    }

    fun updateManualCraftCost(value: String) {
        manualCraftCostText.value = sanitizeInteger(value)
        scheduleAnalysisSave()
    }

    fun updateCrushingCoefficient(value: String) {
        crushingCoefficientText.value = sanitizeDecimal(value)
        scheduleAnalysisSave()
    }

    fun updateResourcePrice(resourceId: Int, value: String) {
        val sanitized = sanitizeInteger(value)
        resourcePriceDrafts.value = resourcePriceDrafts.value + (resourceId to sanitized)
        resourceSaveJobs.remove(resourceId)?.cancel()
        resourceSaveJobs[resourceId] = viewModelScope.launch {
            delay(SAVE_DELAY_MS)
            sanitized.toLongOrNull()?.let { price ->
                repository.saveResourcePrice(
                    resourceId,
                    price,
                    ServerStore.selectedServer.value
                )
            }
        }
    }

    private fun scheduleAnalysisSave() {
        val itemId = selectedItemId.value ?: return
        analysisSaveJob?.cancel()
        analysisSaveJob = viewModelScope.launch {
            delay(SAVE_DELAY_MS)
            repository.saveAnalysis(
                ItemAnalysisEntity(
                    itemId = itemId,
                    server = ServerStore.selectedServer.value,
                    manualCraftCost = manualCraftCostText.value.toLongOrNull() ?: 0L,
                    baseRuneValue = 0L,
                    crushingCoefficientPercent =
                        crushingCoefficientText.value.replace(',', '.').toDoubleOrNull() ?: 100.0
                )
            )
        }
    }

    private fun normalizeForSearch(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
            .trim()

    private fun sanitizeInteger(value: String): String =
        value.filter(Char::isDigit).take(12)

    private fun sanitizeDecimal(value: String): String {
        var separatorSeen = false
        return value.filter { character ->
            when {
                character.isDigit() -> true
                (character == '.' || character == ',') && !separatorSeen -> {
                    separatorSeen = true
                    true
                }
                else -> false
            }
        }.take(7)
    }

    private fun Double.formatEditable(): String =
        if (this % 1.0 == 0.0) toLong().toString() else toString()
}

enum class ItemSortMode(val label: String) {
    PROFIT_DESCENDING("Rentable (par profit)"),
    NAME("Nom A–Z"),
    INGREDIENT_COUNT_ASCENDING("Nombre d’ingrédients (croissant)"),
    FAVORITES_FIRST("Favoris"),
    LEVEL_ASCENDING("Niveau croissant"),
    LEVEL_DESCENDING("Niveau décroissant")
}

private data class ProfitabilityInputs(
    val items: List<ItemEntity>,
    val analyses: List<ItemAnalysisEntity>,
    val recipes: List<com.runeprofittouch.app.database.RecipeIngredientEntity>,
    val stats: List<ItemStatEntity>
)

private data class SearchFilters(
    val search: String,
    val professions: Set<String>,
    val selectedSlots: Set<Int>,
    val selectedRunes: Set<String>
)
