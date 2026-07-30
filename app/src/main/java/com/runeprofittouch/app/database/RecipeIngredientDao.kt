package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeIngredientDao {

    @Query("SELECT DISTINCT resourceId FROM recipe_ingredients")
    fun observeUsedResourceIds(): Flow<List<Int>>

    @Query(
        """
        SELECT itemId, COUNT(*) AS slotCount
        FROM recipe_ingredients
        GROUP BY itemId
        """
    )
    fun observeRecipeSlotCounts(): Flow<List<ItemRecipeSlotCount>>

    @Query(
        """
        SELECT * FROM recipe_ingredients
        WHERE itemId = :itemId
        """
    )
    fun observeByItemId(
        itemId: Int
    ): Flow<List<RecipeIngredientEntity>>

    @Query("SELECT * FROM recipe_ingredients")
    fun observeAll(): Flow<List<RecipeIngredientEntity>>

    @Query(
        """
        SELECT
            r.id AS resourceId,
            r.name AS resourceName,
            ri.quantity AS quantity,
            r.resourceType AS resourceType,
            r.resourceLevel AS resourceLevel,
            r.imageUrl AS resourceImageUrl
        FROM recipe_ingredients AS ri
        INNER JOIN resources AS r
            ON r.id = ri.resourceId
        WHERE ri.itemId = :itemId
        ORDER BY r.name COLLATE NOCASE ASC
        """
    )
    fun observeDetailsByItemId(
        itemId: Int
    ): Flow<List<RecipeIngredientDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(
        ingredients: List<RecipeIngredientEntity>
    )

    @Query(
        """
        DELETE FROM recipe_ingredients
        WHERE itemId = :itemId
        """
    )
    suspend fun deleteByItemId(
        itemId: Int
    )
}
