package com.example.trifhop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.trifhop.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Cart Manager untuk menyimpan produk di keranjang
 * Menggunakan SharedPreferences untuk persistensi data
 */
class CartManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("trifhop_cart", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_CART_ITEMS = "cart_items"
        private var instance: CartManager? = null
        
        fun getInstance(context: Context): CartManager {
            if (instance == null) {
                instance = CartManager(context.applicationContext)
            }
            return instance!!
        }
    }
    
    /**
     * Data class untuk cart item dengan quantity
     */
    data class CartItem(
        val product: Product,
        var quantity: Int = 1
    ) {
        fun getTotalPrice(): Double = product.price * quantity
    }
    
    /**
     * Mengambil semua item di cart
     * 🔧 Validasi: Pastikan semua produk memiliki ID valid
     */
    fun getCartItems(): List<CartItem> {
        val json = prefs.getString(KEY_CART_ITEMS, "[]")
        val type = object : TypeToken<List<CartItem>>() {}.type
        val items = gson.fromJson<List<CartItem>>(json, type) ?: return emptyList()
        
        // 🔧 Filter out items dengan product ID invalid atau product null
        val validItems = items.filter { cartItem ->
            cartItem.product != null && 
            cartItem.product.id > 0 && 
            cartItem.product.price > 0 &&
            cartItem.product.stock >= 0 &&
            cartItem.quantity > 0
        }
        
        // Jika ada item invalid, hapus dan simpan kembali
        if (validItems.size < items.size) {
            android.util.Log.w("CartManager", "Found ${items.size - validItems.size} invalid items in cart, removing...")
            saveCart(validItems)
        }
        
        return validItems
    }
    
    /**
     * Menambahkan produk ke cart
     */
    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        val items = getCartItems().toMutableList()
        val existingItem = items.find { it.product.id == product.id }
        
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            items.add(CartItem(product, quantity))
        }
        
        saveCart(items)
        return true
    }
    
    /**
     * Menghapus produk dari cart
     */
    fun removeFromCart(productId: Int) {
        val items = getCartItems().toMutableList()
        items.removeAll { it.product.id == productId }
        saveCart(items)
    }
    
    /**
     * Update quantity produk di cart
     */
    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        
        val items = getCartItems().toMutableList()
        items.find { it.product.id == productId }?.quantity = quantity
        saveCart(items)
    }
    
    /**
     * Mengosongkan cart
     */
    fun clearCart() {
        prefs.edit().remove(KEY_CART_ITEMS).apply()
    }
    
    /**
     * Mendapatkan total item di cart
     */
    fun getCartCount(): Int {
        return getCartItems().sumOf { it.quantity }
    }
    
    /**
     * Mendapatkan total harga semua item
     */
    fun getTotalPrice(): Double {
        return getCartItems().sumOf { it.getTotalPrice() }
    }
    
    /**
     * Menyimpan cart ke SharedPreferences
     */
    private fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_CART_ITEMS, json).apply()
    }
    
    /**
     * 🔧 Validasi cart sebelum checkout
     * Memastikan semua produk valid dan dapat dipesan
     * Returns: Pair<isValid, errorMessage>
     */
    fun validateCart(): Pair<Boolean, String?> {
        val items = getCartItems()
        
        if (items.isEmpty()) {
            return Pair(false, "Keranjang belanja kosong")
        }
        
        for (item in items) {
            // Check product ID
            if (item.product.id <= 0) {
                return Pair(false, "❌ Produk '${item.product.name}' memiliki ID tidak valid")
            }
            
            // Check stock
            if (item.product.stock <= 0) {
                return Pair(false, "❌ Produk '${item.product.name}' tidak memiliki stok")
            }
            
            // Check quantity vs stock
            if (item.quantity > item.product.stock) {
                return Pair(false, "❌ Produk '${item.product.name}' hanya tersedia ${item.product.stock} item(s), tapi Anda meminta ${item.quantity}")
            }
            
            // Check price
            if (item.product.price <= 0) {
                return Pair(false, "❌ Produk '${item.product.name}' memiliki harga tidak valid")
            }
            
            // Check status
            if (item.product.status != "available") {
                return Pair(false, "❌ Produk '${item.product.name}' tidak tersedia (status: ${item.product.status})")
            }
        }
        
        return Pair(true, null)
    }
    
    /**
     * Remove invalid products from cart and return count removed
     */
    fun cleanupInvalidProducts(): Int {
        val items = getCartItems().toMutableList()
        val validItems = items.filter { cartItem ->
            cartItem.product != null && 
            cartItem.product.id > 0 && 
            cartItem.product.price > 0 &&
            cartItem.product.stock > 0 &&
            cartItem.product.status == "available" &&
            cartItem.quantity > 0 &&
            cartItem.quantity <= cartItem.product.stock
        }
        
        val removedCount = items.size - validItems.size
        if (removedCount > 0) {
            saveCart(validItems)
            android.util.Log.w("CartManager", "Removed $removedCount invalid product(s) from cart")
        }
        
        return removedCount
    }
}
