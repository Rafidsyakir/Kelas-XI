package com.example.trifhop.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("role")
    val role: String = "customer",
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("address")
    val address: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    @SerializedName("id_token")
    val idToken: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val address: String? = null
)

// Updated LoginResponse untuk match dengan backend baru
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: LoginData
)

data class LoginData(
    @SerializedName("user")
    val user: User,
    
    @SerializedName("token")
    val token: String
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean = true,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: T? = null
)

data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,
    
    @SerializedName("error")
    val error: String? = null
)

data class UserProfile(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("address")
    val address: String? = null
)
