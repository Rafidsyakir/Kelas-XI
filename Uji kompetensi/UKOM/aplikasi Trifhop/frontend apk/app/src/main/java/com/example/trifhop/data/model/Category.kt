package com.example.trifhop.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model Category - Sesuai dengan API response Laravel
 */
data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    
    @SerializedName("icon_url")
    val iconUrl: String? = null,
    
    @SerializedName("available_products_count")
    val availableProductsCount: Int? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    
    // Relasi products (optional, untuk kategori detail dengan produknya)
    val products: List<Product>? = null
) {
    /**
     * Helper function untuk mendapatkan URL icon kategori lengkap
     */
    fun getFullIconUrl(baseUrl: String): String {
        return if (iconUrl.isNullOrEmpty()) {
            "" 
        } else if (iconUrl.startsWith("http")) {
            iconUrl
        } else {
            "${baseUrl.removeSuffix("/")}/${iconUrl.removePrefix("/")}"
        }
    }
}
