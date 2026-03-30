package com.example.trifhop.data.model

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.Locale

/**
 * Model Product - Sesuai dengan API response Laravel
 */
data class Product(
    val id: Int,
    val name: String,
    val price: Double, // Harga dalam Rupiah (DECIMAL di database)
    val description: String,
    val condition: String,
    val size: String,
    val status: String,
    val stock: Int,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("category_id")
    val categoryId: Int,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    // Relasi (optional, akan diisi jika include category)
    val category: Category? = null
) {
    /**
     * Helper function untuk mendapatkan URL gambar lengkap
     */
    fun getFullImageUrl(baseUrl: String): String {
        return if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "${baseUrl.removeSuffix("/")}/${imageUrl.removePrefix("/")}"
        }
    }
    
    /**
     * Helper function untuk format harga ke Rupiah
     */
    fun getFormattedPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
        return "Rp ${formatter.format(price.toLong())}"
    }
    
    /**
     * Check if product is available
     */
    fun isAvailable(): Boolean {
        return status == "available" && stock > 0
    }
}
