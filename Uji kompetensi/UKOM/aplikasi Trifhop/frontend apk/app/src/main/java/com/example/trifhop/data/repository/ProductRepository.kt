package com.example.trifhop.data.repository

import com.example.trifhop.data.network.ApiService
import com.example.trifhop.data.model.Category
import com.example.trifhop.data.model.Product

class ProductRepository(private val apiService: ApiService) {
    
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Failed to load products"))
                }
            } else {
                Result.failure(Exception("Failed to load products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Failed to load categories"))
                }
            } else {
                Result.failure(Exception("Failed to load categories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
