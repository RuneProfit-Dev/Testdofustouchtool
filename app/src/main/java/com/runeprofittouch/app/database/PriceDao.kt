package com.runeprofittouch.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {

    @Query(
        """
        SELECT p.*
        FROM prices AS p
        WHERE p.subjectType = :subjectType
          AND p.server = :server
          AND p.lotSize = 1
          AND p.recordedAt = (
              SELECT MAX(candidate.recordedAt)
              FROM prices AS candidate
              WHERE candidate.subjectType = p.subjectType
                AND candidate.subjectId = p.subjectId
                AND candidate.server = p.server
                AND candidate.lotSize = p.lotSize
          )
        """
    )
    fun observeLatestPrices(
        subjectType: String,
        server: String
    ): Flow<List<PriceEntity>>

    @Query(
        """
        SELECT p.*
        FROM prices AS p
        WHERE p.subjectType = 'RESOURCE'
          AND p.server = :server
          AND p.lotSize = 1
          AND p.recordedAt = (
              SELECT MAX(candidate.recordedAt)
              FROM prices AS candidate
              WHERE candidate.subjectType = p.subjectType
                AND candidate.subjectId = p.subjectId
                AND candidate.server = p.server
                AND candidate.lotSize = p.lotSize
          )
        """
    )
    fun observeLatestResourcePrices(server: String): Flow<List<PriceEntity>>

    @Query(
        """
        SELECT *
        FROM prices
        WHERE subjectType = :subjectType
          AND subjectId = :subjectId
          AND server = :server
        ORDER BY recordedAt DESC
        """
    )
    fun observePriceHistory(
        subjectType: String,
        subjectId: Int,
        server: String
    ): Flow<List<PriceEntity>>

    @Query(
        """
        SELECT *
        FROM prices
        WHERE subjectType = :subjectType
          AND subjectId = :subjectId
          AND server = :server
          AND lotSize = :lotSize
        ORDER BY recordedAt DESC
        LIMIT 1
        """
    )
    fun observeLatestPrice(
        subjectType: String,
        subjectId: Int,
        server: String,
        lotSize: Int
    ): Flow<PriceEntity?>

    @Query(
        """
        SELECT *
        FROM prices
        WHERE subjectType = :subjectType
          AND subjectId = :subjectId
          AND server = :server
          AND lotSize = :lotSize
        ORDER BY recordedAt DESC
        LIMIT 1
        """
    )
    suspend fun getLatestPrice(
        subjectType: String,
        subjectId: Int,
        server: String,
        lotSize: Int
    ): PriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: PriceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<PriceEntity>)

    @Update
    suspend fun update(price: PriceEntity)

    @Query("DELETE FROM prices WHERE id = :priceId")
    suspend fun deleteById(priceId: Long)

    @Query(
        """
        DELETE FROM prices
        WHERE subjectType = :subjectType
          AND subjectId = :subjectId
          AND server = :server
        """
    )
    suspend fun deleteHistory(
        subjectType: String,
        subjectId: Int,
        server: String
    )

    @Query("DELETE FROM prices")
    suspend fun deleteAll()
}
