package com.example.trifhop.utils

/**
 * Sealed class untuk handle berbagai state dari API call
 * - Success: Data berhasil didapat
 * - Error: Terjadi error
 * - Loading: Sedang loading
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}
