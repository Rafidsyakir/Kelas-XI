package com.example.trifhop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trifhop.data.model.Category
import com.example.trifhop.data.model.Product
import com.example.trifhop.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MainViewModel - Single Source of Truth untuk data aplikasi
 * Memegang state products dan categories dari Laravel Backend
 */
class MainViewModel : ViewModel() {
    
    // Private mutable state
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    // Public immutable state
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        // Auto-fetch data saat ViewModel dibuat (saat aplikasi dibuka)
        fetchAllData()
    }
    
    /**
     * Fetch semua data (products + categories) dari API
     */
    private fun fetchAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Fetch categories
                val categoriesResult = RetrofitClient.apiService.getCategories()
                if (categoriesResult.isSuccessful && categoriesResult.body() != null) {
                    _categories.value = categoriesResult.body()?.data ?: emptyList()
                }
                
                // Fetch products
                val productsResult = RetrofitClient.apiService.getProducts()
                if (productsResult.isSuccessful && productsResult.body() != null) {
                    _products.value = productsResult.body()?.data ?: emptyList()
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh data (untuk pull-to-refresh)
     */
    fun refresh() {
        fetchAllData()
    }
    
    /**
     * Cari produk berdasarkan ID
     */
    fun getProductById(id: Int): Product? {
        return _products.value.find { it.id == id }
    }
    
    /**
     * Filter produk berdasarkan kategori
     */
    fun getProductsByCategory(categoryId: Int): List<Product> {
        return _products.value.filter { it.categoryId == categoryId }
    }
    
    /**
     * Search produk berdasarkan nama
     */
    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return _products.value
        return _products.value.filter { 
            it.name.contains(query, ignoreCase = true)
        }
    }
}
