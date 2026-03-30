package com.example.trifhop.data.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client Singleton
 * Setup koneksi ke Laravel Backend
 */
object RetrofitClient {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = GsonBuilder()
        .setLenient()
        // Handle decimals that may come as strings from PHP (e.g. "50000.00")
        .registerTypeAdapter(Double::class.javaObjectType, JsonDeserializer { json, _, _ ->
            try {
                if (json.isJsonPrimitive) {
                    val p = json.asJsonPrimitive
                    if (p.isNumber) p.asDouble else p.asString.toDoubleOrNull() ?: 0.0
                } else 0.0
            } catch (e: Exception) { 0.0 }
        })
        .create()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    /**
     * Instance ApiService yang siap digunakan
     */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
