package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isOffer: Boolean, // true = "عقارات معروضة", false = "طلبات البحث"
    val title: String,
    val description: String,
    val priceDzd: Long, // Price in DZD
    val priceDisplayCentimes: String, // e.g., "450 مليون" or "4.5 مليون/شهر"
    val wilaya: String,
    val commune: String,
    val category: String, // "شقة", "فيلا", "أرض", "محل تجاري", "دوبلكس", "استوديو"
    val transactionType: String, // "للبيع", "للكراء", "كراء يومي", "تبادل"
    val rooms: String = "F3",
    val areaSqMeters: Int = 100,
    val phone: String = "0550 00 00 00",
    val publisherName: String = "مستخدم دبرلي",
    val publisherType: String = "خاص", // "مالك", "وكالة عقارية", "باحث"
    val featuresList: String = "ماء,كهرباء,غاز",
    val isFavorite: Boolean = false,
    val rating: Float = 4.8f,
    val reviewsCount: Int = 18,
    val createdAt: Long = System.currentTimeMillis(),
    val imageUrl: String? = null
)
