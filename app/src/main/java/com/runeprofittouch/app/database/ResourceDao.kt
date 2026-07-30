package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {

    @Query("SELECT * FROM resources ORDER BY name ASC")
    fun observeAll(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :resourceId LIMIT 1")
    suspend fun getById(resourceId: Int): ResourceEntity?

    @Query(
        """
        SELECT *
        FROM resources
        WHERE name LIKE '%' || :searchText || '%'
        ORDER BY name ASC
        """
    )
    fun search(searchText: String): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resource: ResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(resources: List<ResourceEntity>)

    @Update
    suspend fun update(resource: ResourceEntity)

    @Delete
    suspend fun delete(resource: ResourceEntity)

    @Query("DELETE FROM resources")
    suspend fun deleteAll()
}