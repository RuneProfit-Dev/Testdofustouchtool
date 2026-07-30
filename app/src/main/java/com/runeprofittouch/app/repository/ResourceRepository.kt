package com.runeprofittouch.app.repository

import com.runeprofittouch.app.database.ResourceDao
import com.runeprofittouch.app.database.ResourceEntity
import kotlinx.coroutines.flow.Flow

class ResourceRepository(
    private val resourceDao: ResourceDao
) {

    val resources: Flow<List<ResourceEntity>> =
        resourceDao.observeAll()

    fun searchResources(searchText: String): Flow<List<ResourceEntity>> {
        return resourceDao.search(searchText)
    }

    suspend fun getResourceById(resourceId: Int): ResourceEntity? {
        return resourceDao.getById(resourceId)
    }

    suspend fun insert(resource: ResourceEntity) {
        resourceDao.insert(resource)
    }

    suspend fun insertAll(resources: List<ResourceEntity>) {
        resourceDao.insertAll(resources)
    }

    suspend fun update(resource: ResourceEntity) {
        resourceDao.update(resource)
    }

    suspend fun delete(resource: ResourceEntity) {
        resourceDao.delete(resource)
    }

    suspend fun deleteAll() {
        resourceDao.deleteAll()
    }
}