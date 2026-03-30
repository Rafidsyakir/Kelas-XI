package com.example.trifhop.data.repository

import com.example.trifhop.data.local.SharedPrefsManager
import com.example.trifhop.data.model.*
import com.example.trifhop.data.network.ApiService
import com.example.trifhop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository untuk handle autentikasi
 * - Login
 * - Register
 * - Logout
 */
class AuthRepository(
    private val apiService: ApiService,
    private val prefsManager: SharedPrefsManager
) {
    
    /**
     * Register new user
     */
    suspend fun register(name: String, email: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(name, email, password)
                val response = apiService.register(request)
                
                // Check if response is successful
                if (response.success) {
                    // Save token dan user data
                    prefsManager.saveAuthToken(response.data.token)
                    prefsManager.saveUser(response.data.user)
                    Resource.Success(response)
                } else {
                    Resource.Error(response.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Registration failed")
            }
        }
    }
    
    /**
     * Login user
     */
    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)
                
                // Check if response is successful
                if (response.success) {
                    // Save token dan user data
                    prefsManager.saveAuthToken(response.data.token)
                    prefsManager.saveUser(response.data.user)
                    Resource.Success(response)
                } else {
                    Resource.Error(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Login failed")
            }
        }
    }
    
    /**
     * Logout user
     */
    suspend fun logout(): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = prefsManager.getAuthTokenWithBearer()
                if (token != null) {
                    apiService.logout(token)
                }
                
                // Clear semua data lokal
                prefsManager.clearAll()
                
                Resource.Success(Unit)
            } catch (e: Exception) {
                // Tetap clear data lokal meskipun API call gagal
                prefsManager.clearAll()
                Resource.Success(Unit)
            }
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return prefsManager.isLoggedIn()
    }
    
    /**
     * Get current user
     */
    fun getCurrentUser(): User? {
        return prefsManager.getUser()
    }
}
