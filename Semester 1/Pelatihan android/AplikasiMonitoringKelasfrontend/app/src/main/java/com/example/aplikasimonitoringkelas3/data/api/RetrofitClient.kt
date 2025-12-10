package com.example.aplikasimonitoringkelas3.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // ===================== PILIH SALAH SATU BASE_URL =====================
    // 
    // OPSI 1: Jika menggunakan EMULATOR Android Studio
    // 10.0.2.2 adalah IP khusus untuk emulator mengakses localhost komputer
    // private const val BASE_URL = "http://10.0.2.2:8000/api/"
    //
    // OPSI 2: Jika menggunakan DEVICE FISIK + ADB USB (RECOMMENDED)
    // Jalankan: adb reverse tcp:8000 tcp:8000
    // private const val BASE_URL = "http://localhost:8000/api/"
    //
    // OPSI 3: Jika menggunakan WIFI - Device fisik di jaringan yang sama (AKTIF SEKARANG)
    // IP komputer Anda: 192.168.40.26
    private const val BASE_URL = "http://192.168.40.26:8000/api/"
    //
    // OPSI 4: Jika menggunakan HOTSPOT ANDROID (perlu ngrok atau port forward router)
    // Masalah: Android hotspot tidak bisa akses client device langsung
    //
    // =====================================================================
    
    @Volatile
    private var authToken: String? = null
    
    fun setAuthToken(token: String?) {
        authToken = token
        android.util.Log.d("RetrofitClient", "Token set: ${token?.take(20)}...")
    }
    
    fun getAuthToken(): String? = authToken
    
    fun clearToken() {
        authToken = null
        android.util.Log.d("RetrofitClient", "Token cleared")
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // Add authorization header if token exists
        val token = authToken
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            android.util.Log.d("RetrofitClient", "Adding token to request: ${originalRequest.url}")
        } else {
            android.util.Log.d("RetrofitClient", "No token for request: ${originalRequest.url}")
        }
        
        // Add accept header
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")
        
        chain.proceed(requestBuilder.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
