package com.example.trifhop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Wishlist Manager untuk menyimpan produk favorit
 * Menggunakan SharedPreferences untuk persistensi data
 */
class WishlistManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("trifhop_wishlist", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_WISHLIST_ITEMS = "wishlist_items"
        private var instance: WishlistManager? = null
        
        fun getInstance(context: Context): WishlistManager {
            if (instance == null) {
                instance = WishlistManager(context.applicationContext)
            }
            return instance!!
        }
    }
    
    /**
     * Mengambil semua product ID di wishlist
     */
    fun getWishlistProductIds(): List<Int> {
        val json = prefs.getString(KEY_WISHLIST_ITEMS, "[]")
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    /**
     * Menambahkan produk ke wishlist
     */
    fun addToWishlist(productId: Int): Boolean {
        val items = getWishlistProductIds().toMutableList()
        
        if (!items.contains(productId)) {
            items.add(productId)
            saveWishlist(items)
            return true
        }
        
        return false
    }
    
    /**
     * Menghapus produk dari wishlist
     */
    fun removeFromWishlist(productId: Int) {
        val items = getWishlistProductIds().toMutableList()
        items.remove(productId)
        saveWishlist(items)
    }
    
    /**
     * Mengecek apakah produk ada di wishlist
     */
    fun isInWishlist(productId: Int): Boolean {
        return getWishlistProductIds().contains(productId)
    }
    
    /**
     * Menghitung jumlah item di wishlist
     */
    fun getWishlistCount(): Int {
        return getWishlistProductIds().size
    }
    
    /**
     * Menghapus semua item dari wishlist
     */
    fun clearWishlist() {
        prefs.edit().remove(KEY_WISHLIST_ITEMS).apply()
    }
    
    /**
     * Toggle wishlist status (add jika belum ada, remove jika sudah ada)
     */
    fun toggleWishlist(productId: Int): Boolean {
        return if (isInWishlist(productId)) {
            removeFromWishlist(productId)
            false
        } else {
            addToWishlist(productId)
            true
        }
    }
    
    /**
     * Menyimpan wishlist ke SharedPreferences
     */
    private fun saveWishlist(items: List<Int>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_WISHLIST_ITEMS, json).apply()
    }
}
