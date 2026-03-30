package com.example.trifhop.data.network

/**
 * Konfigurasi API Base URL
 * 
 * PENTING! Ganti IP_ADDRESS_ANDA dengan IP komputer yang menjalankan Laravel:
 * - Untuk Android Emulator: http://10.0.2.2:8000
 * - Untuk Real Device: http://192.168.X.X:8000 (IP laptop di jaringan WiFi yang sama)
 * 
 * Cara cek IP:
 * Windows: ipconfig
 * Mac/Linux: ifconfig
 */
object ApiConfig {
    // BASE_URL - SUDAH DI-SET KE IP LAPTOP ANDA
    // IP Laptop: 192.168.0.101
    // Untuk Emulator: gunakan 10.0.2.2
    // Untuk Real Device: gunakan IP laptop (192.168.0.101)
    
    const val BASE_URL = "http://192.168.0.101:8000/" // ← IP Laptop Anda (UPDATED)
    // const val BASE_URL = "http://10.0.2.2:8000/" // ← Uncomment untuk emulator
    
    val API_BASE_URL = "${BASE_URL}api/"
    
    // Untuk helper getFullImageUrl
    fun getBaseUrl(): String = BASE_URL
}
