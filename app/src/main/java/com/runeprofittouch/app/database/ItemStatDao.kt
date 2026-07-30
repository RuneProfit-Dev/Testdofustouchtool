package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemStatDao {

    @Query("SELECT * FROM item_stats WHERE itemId = :itemId ORDER BY name")
    fun observeByItemId(itemId: Int): Flow<List<ItemStatEntity>>

    @Query("SELECT * FROM item_stats")
    fun observeAll(): Flow<List<ItemStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stats: List<ItemStatEntity>)

    @Query("SELECT COUNT(*) FROM item_stats")
    suspend fun count(): Int
}
