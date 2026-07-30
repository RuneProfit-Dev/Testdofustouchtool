package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemAnalysisDao {

    @Query(
        """
        SELECT * FROM item_analyses
        WHERE itemId = :itemId AND server = :server
        LIMIT 1
        """
    )
    fun observeByItemId(itemId: Int, server: String): Flow<ItemAnalysisEntity?>

    @Query("SELECT * FROM item_analyses WHERE server = :server")
    fun observeByServer(server: String): Flow<List<ItemAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(analysis: ItemAnalysisEntity)
}
