package com.runeprofittouch.app.database

data class RecipeIngredientDetail(
    val resourceId: Int,
    val resourceName: String,
    val quantity: Int,
    val resourceType: String,
    val resourceLevel: Int,
    val resourceImageUrl: String
)
