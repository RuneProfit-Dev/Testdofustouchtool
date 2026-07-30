package com.runeprofittouch.app.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ItemEntity::class,
        ResourceEntity::class,
        RecipeIngredientEntity::class,
        PriceEntity::class,
        ItemAnalysisEntity::class,
        ItemStatEntity::class,
        FavoriteEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun resourceDao(): ResourceDao

    abstract fun recipeIngredientDao(): RecipeIngredientDao

    abstract fun priceDao(): PriceDao

    abstract fun itemAnalysisDao(): ItemAnalysisDao

    abstract fun itemStatDao(): ItemStatDao

    abstract fun favoriteDao(): FavoriteDao
}
