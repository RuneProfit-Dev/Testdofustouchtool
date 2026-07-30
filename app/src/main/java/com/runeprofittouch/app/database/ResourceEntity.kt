package com.runeprofittouch.app.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    indices = [
        Index(value = ["name"]),
        Index(value = ["resourceType"])
    ]
)
data class ResourceEntity(
    @PrimaryKey
    val id: Int,

    val name: String,

    val resourceType: String,

    val resourceLevel: Int,

    val imageUrl: String = ""
)
