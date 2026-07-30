package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query(
        """
        SELECT * FROM items
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT id FROM items")
    fun observeAllIds(): Flow<List<Int>>

    @Query(
        """
        SELECT * FROM items
        WHERE id = :itemId
        LIMIT 1
        """
    )
    suspend fun getById(itemId: Int): ItemEntity?

    @Query(
        """
        SELECT * FROM items
        WHERE name LIKE '%' || :search || '%'
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun search(search: String): Flow<List<ItemEntity>>

    @Query(
        """
        SELECT * FROM items
        WHERE profession = :profession COLLATE NOCASE
        ORDER BY requiredProfessionLevel ASC, name COLLATE NOCASE ASC
        """
    )
    fun observeByProfession(
        profession: String
    ): Flow<List<ItemEntity>>

    @Query(
        """
        SELECT * FROM items
        WHERE name LIKE '%' || :search || '%'
        AND (
            :profession = 'Tous'
            OR profession = :profession COLLATE NOCASE
        )
        ORDER BY requiredProfessionLevel ASC, name COLLATE NOCASE ASC
        """
    )
    fun observeFiltered(
        search: String,
        profession: String
    ): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun countItems(): Int

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
