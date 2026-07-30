package com.runeprofittouch.app.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    indices = [
        Index(value = ["name"]),
        Index(value = ["itemType"]),
        Index(value = ["profession"])
    ]
)
data class ItemEntity(
    @PrimaryKey
    val id: Int,

    val name: String,

    val itemType: String,

    val itemLevel: Int,

    val profession: String,

    val requiredProfessionLevel: Int,

    val imageUrl: String = ""
)
