package com.runeprofittouch.app.repository

import com.runeprofittouch.app.database.FavoriteDao
import com.runeprofittouch.app.database.FavoriteEntity
import com.runeprofittouch.app.database.ItemAnalysisDao
import com.runeprofittouch.app.database.ItemAnalysisEntity
import com.runeprofittouch.app.database.ItemDao
import com.runeprofittouch.app.database.ItemEntity
import com.runeprofittouch.app.database.PriceDao
import com.runeprofittouch.app.database.PriceEntity
import com.runeprofittouch.app.database.RecipeIngredientDao
import com.runeprofittouch.app.database.RecipeIngredientDetail
import com.runeprofittouch.app.database.ItemRecipeSlotCount
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val priceDao: PriceDao,
    private val itemAnalysisDao: ItemAnalysisDao,
    private val favoriteDao: FavoriteDao
) {
    val items: Flow<List<ItemEntity>> =
        itemDao.observeAll()

    val recipeSlotCounts: Flow<List<ItemRecipeSlotCount>> =
        recipeIngredientDao.observeRecipeSlotCounts()

    val favoriteIds: Flow<List<Int>> =
        favoriteDao.observeFavoriteIds()

    fun observeRecipe(
        itemId: Int
    ): Flow<List<RecipeIngredientDetail>> {
        return recipeIngredientDao
            .observeDetailsByItemId(itemId)
    }

    fun observeLatestResourcePrices(server: String): Flow<List<PriceEntity>> =
        priceDao.observeLatestResourcePrices(server)

    fun observeAnalysis(itemId: Int, server: String): Flow<ItemAnalysisEntity?> =
        itemAnalysisDao.observeByItemId(itemId, server)

    suspend fun saveResourcePrice(resourceId: Int, price: Long, server: String) {
        priceDao.insert(
            PriceEntity(
                subjectType = "RESOURCE",
                subjectId = resourceId,
                server = server,
                lotSize = 1,
                price = price
            )
        )
    }

    suspend fun saveAnalysis(analysis: ItemAnalysisEntity) {
        itemAnalysisDao.upsert(analysis)
    }

    suspend fun addFavorite(itemId: Int) {
        favoriteDao.insert(FavoriteEntity(itemId))
    }

    suspend fun removeFavorite(itemId: Int) {
        favoriteDao.deleteByItemId(itemId)
    }
}
