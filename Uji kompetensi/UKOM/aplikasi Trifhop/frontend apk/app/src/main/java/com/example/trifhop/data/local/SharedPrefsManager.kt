package com.example.trifhop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.trifhop.data.model.User
import com.google.gson.Gson

/**
 * SharedPreferences Manager untuk menyimpan data lokal
 * - Auth token
 * - User data
 * - Login state
 */
class SharedPrefsManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "trifhop_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        @Volatile
        private var INSTANCE: SharedPrefsManager? = null
        
        fun getInstance(context: Context): SharedPrefsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefsManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    
    // ==================== AUTH TOKEN ====================
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun getAuthTokenWithBearer(): String? {
        val token = getAuthToken()
        return if (token != null) "Bearer $token" else null
    }
    
    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }
    
    // ==================== USER DATA ====================
    
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER_DATA, userJson).apply()
        setLoggedIn(true)
    }
    
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }
    
    fun clearUser() {
        prefs.edit().remove(KEY_USER_DATA).apply()
    }
    
    // ==================== LOGIN STATE ====================
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAuthToken() != null
    }
    
    // ==================== LOGOUT ====================
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
