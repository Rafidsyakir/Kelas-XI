package com.example.trifhop.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.trifhop.data.model.User
import com.google.gson.Gson

/**
 * UserPreferences untuk menyimpan data user yang login
 */
class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val KEY_USER = "user_data"
        private const val KEY_TOKEN = "auth_token"
    }
    
    /**
     * Simpan data user yang login
     */
    fun saveUser(user: User, token: String) {
        val userJson = gson.toJson(user)
        prefs.edit().apply {
            putString(KEY_USER, userJson)
            putString(KEY_TOKEN, token)
            apply()
        }
    }
    
    /**
     * Ambil data user yang login
     */
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Ambil token
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null && getUser() != null
    }
    
    /**
     * Clear data user (logout)
     */
    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
