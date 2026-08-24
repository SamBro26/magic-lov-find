package com.example.data.repository

import com.example.data.dao.PropertyDao
import com.example.data.model.PropertyItem
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val dao: PropertyDao) {

    fun getFilteredProperties(
        isOffer: Boolean,
        query: String,
        wilayaFilter: String,
        communeFilter: String,
        categoryFilter: String,
        transactionFilter: String
    ): Flow<List<PropertyItem>> {
        return dao.filterProperties(
            isOffer = isOffer,
            query = query.trim(),
            wilayaFilter = wilayaFilter,
            communeFilter = communeFilter,
            categoryFilter = categoryFilter,
            transactionFilter = transactionFilter
        )
    }

    suspend fun addProperty(property: PropertyItem): Long {
        return dao.insertProperty(property)
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        dao.setFavorite(id, !currentStatus)
    }

    suspend fun deleteProperty(property: PropertyItem) {
        dao.deleteProperty(property)
    }
}
