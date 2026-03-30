package com.example.trifhop.data.model

import com.google.gson.annotations.SerializedName

// Helper data classes
data class Measurements(
    val length: Int, // in cm
    val width: Int   // in cm
)

data class Seller(
    val name: String,
    val rating: Double,
    val totalSales: Int,
    val profileImage: String
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

// Enums
enum class OrderStatus {
    PENDING,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

enum class ProductCondition(val displayName: String, val rating: String) {
    NEW("New", "10/10"),
    LIKE_NEW("Like New", "9/10"),
    GOOD("Good Condition", "8/10"),
    WORN("Worn", "6/10"),
    USED("Used", "5/10")
}
