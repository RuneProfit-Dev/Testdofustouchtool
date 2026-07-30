package com.runeprofittouch.app.database

import androidx.room.Entity

@Entity(
    tableName = "item_stats",
    primaryKeys = ["itemId", "name"]
)
data class ItemStatEntity(
    val itemId: Int,
    val name: String,
    val minimum: Int,
    val maximum: Int
)
