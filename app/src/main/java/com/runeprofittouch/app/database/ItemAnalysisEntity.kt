package com.runeprofittouch.app.database

import androidx.room.Entity

@Entity(
    tableName = "item_analyses",
    primaryKeys = ["itemId", "server"]
)
data class ItemAnalysisEntity(
    val itemId: Int,
    val server: String,
    val manualCraftCost: Long = 0L,
    val baseRuneValue: Long = 0L,
    val crushingCoefficientPercent: Double = 100.0,
    val updatedAt: Long = System.currentTimeMillis()
)
