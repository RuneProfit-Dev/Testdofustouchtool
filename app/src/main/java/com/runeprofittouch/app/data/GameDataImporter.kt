package com.runeprofittouch.app.data

import android.content.Context
import androidx.room.withTransaction
import com.runeprofittouch.app.database.AppDatabase
import com.runeprofittouch.app.database.ItemEntity
import com.runeprofittouch.app.database.ItemStatEntity
import com.runeprofittouch.app.database.RecipeIngredientEntity
import com.runeprofittouch.app.database.ResourceEntity
import org.json.JSONObject

class GameDataImporter(
    private val context: Context,
    private val database: AppDatabase
) {

    suspend fun importFromAssets(
        fileName: String = "runetouch.json"
    ) {
        val jsonText = context.assets
            .open(fileName)
            .bufferedReader()
            .use { reader ->
                reader.readText()
            }

        val root = JSONObject(jsonText)

        val items = parseItems(root)
        val resources = parseResources(root)
        val recipes = parseRecipes(root)
        val stats = parseItemStats(root)

        database.withTransaction {
            database.resourceDao().insertAll(resources)
            database.itemDao().insertAll(items)
            database.recipeIngredientDao().insertAll(recipes)
            database.itemStatDao().insertAll(stats)
        }
    }

    private fun parseItemStats(root: JSONObject): List<ItemStatEntity> {
        val jsonItems = root.optJSONArray("items") ?: return emptyList()
        return buildList {
            for (itemIndex in 0 until jsonItems.length()) {
                val item = jsonItems.getJSONObject(itemIndex)
                val itemId = item.getInt("id")
                val stats = item.optJSONArray("stats") ?: continue
                for (statIndex in 0 until stats.length()) {
                    val stat = stats.getJSONObject(statIndex)
                    add(
                        ItemStatEntity(
                            itemId = itemId,
                            name = stat.getString("name"),
                            minimum = stat.optInt("minimum"),
                            maximum = stat.optInt("maximum")
                        )
                    )
                }
            }
        }
    }

    private fun parseItems(
        root: JSONObject
    ): List<ItemEntity> {
        val jsonItems = root.optJSONArray("items")
            ?: return emptyList()

        return buildList {
            for (index in 0 until jsonItems.length()) {
                val jsonItem = jsonItems.getJSONObject(index)

                add(
                    ItemEntity(
                        id = jsonItem.getInt("id"),
                        name = jsonItem.getString("name"),
                        itemType = jsonItem.optString("itemType"),
                        itemLevel = jsonItem.optInt("itemLevel"),
                        profession = jsonItem.optString("profession"),
                        requiredProfessionLevel = jsonItem.optInt(
                            "requiredProfessionLevel"
                        ),
                        imageUrl = jsonItem.optString("imageUrl")
                    )
                )
            }
        }
    }

    private fun parseResources(
        root: JSONObject
    ): List<ResourceEntity> {
        val jsonResources = root.optJSONArray("resources")
            ?: return emptyList()

        return buildList {
            for (index in 0 until jsonResources.length()) {
                val jsonResource = jsonResources.getJSONObject(index)

                add(
                    ResourceEntity(
                        id = jsonResource.getInt("id"),
                        name = jsonResource.getString("name"),
                        resourceType = jsonResource.optString(
                            "resourceType"
                        ),
                        resourceLevel = jsonResource.optInt(
                            "resourceLevel"
                        ),
                        imageUrl = jsonResource.optString("imageUrl")
                    )
                )
            }
        }
    }

    private fun parseRecipes(
        root: JSONObject
    ): List<RecipeIngredientEntity> {
        val jsonRecipes = root.optJSONArray("recipes")
            ?: return emptyList()

        return buildList {
            for (index in 0 until jsonRecipes.length()) {
                val jsonRecipe = jsonRecipes.getJSONObject(index)

                add(
                    RecipeIngredientEntity(
                        itemId = jsonRecipe.getInt("itemId"),
                        resourceId = jsonRecipe.getInt("resourceId"),
                        quantity = jsonRecipe.getInt("quantity")
                    )
                )
            }
        }
    }
}
