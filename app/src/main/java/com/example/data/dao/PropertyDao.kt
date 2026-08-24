package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PropertyItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Query("SELECT * FROM properties WHERE isOffer = :isOffer ORDER BY createdAt DESC")
    fun getPropertiesByTab(isOffer: Boolean): Flow<List<PropertyItem>>

    @Query("""
        SELECT * FROM properties 
        WHERE isOffer = :isOffer
        AND (:query = '' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR commune LIKE '%' || :query || '%' OR wilaya LIKE '%' || :query || '%')
        AND (:wilayaFilter = '' OR wilaya = :wilayaFilter OR wilaya LIKE '%' || :wilayaFilter || '%')
        AND (:communeFilter = '' OR commune = :communeFilter)
        AND (:categoryFilter = '' OR category = :categoryFilter)
        AND (:transactionFilter = '' OR transactionType = :transactionFilter)
        ORDER BY createdAt DESC
    """)
    fun filterProperties(
        isOffer: Boolean,
        query: String,
        wilayaFilter: String,
        communeFilter: String,
        categoryFilter: String,
        transactionFilter: String
    ): Flow<List<PropertyItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyItem>)

    @Query("UPDATE properties SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteProperty(property: PropertyItem)

    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getCount(): Int
}
