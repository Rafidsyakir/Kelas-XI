package com.example.trifhop.data.repository

import com.example.trifhop.data.network.ApiService
import com.example.trifhop.data.model.Order
import com.example.trifhop.data.model.OrderProduct
import com.example.trifhop.data.model.OrderCreateResult
import com.example.trifhop.data.model.ApiResponse
import com.example.trifhop.data.model.CheckoutRequest
import com.example.trifhop.data.network.CheckoutData
import org.json.JSONObject
import org.json.JSONException
import com.google.gson.JsonParseException

/**
 * Repository untuk mengelola Order data
 */
class OrderRepository(private val apiService: ApiService) {
    
    /**
     * Ambil semua order dari user yang login (current + past)
     */
    suspend fun getUserOrders(token: String): Result<List<Order>> {
        return try {
            val allOrders = mutableListOf<Order>()
            
            // Get current orders
            val currentResponse = apiService.getCurrentOrders("Bearer $token")
            if (currentResponse.isSuccessful && currentResponse.body()?.success == true) {
                val currentOrders = currentResponse.body()?.data ?: emptyList()
                allOrders.addAll(currentOrders)
            }
            
            // Get past orders
            val pastResponse = apiService.getPastOrders("Bearer $token")
            if (pastResponse.isSuccessful && pastResponse.body()?.success == true) {
                val pastOrders = pastResponse.body()?.data ?: emptyList()
                allOrders.addAll(pastOrders)
            }
            
            Result.success(allOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Ambil detail order berdasarkan ID
     */
    suspend fun getOrderById(token: String, orderId: Int): Result<Order> {
        return try {
            val response = apiService.getOrderById(
                token = "Bearer $token",
                orderId = orderId
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data
                if (order != null) {
                    Result.success(order)
                } else {
                    Result.failure(Exception("Order not found"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "Failed to get order"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Buat order baru — returns OrderCreateResult
     * 
     * STEP 1: Validate input
     * STEP 2: Send to /api/orders endpoint
     * STEP 3: Parse response and extract order data
     * STEP 4: Return Order with ID (needed for checkout invoice)
     */
    suspend fun createOrder(
        token: String,
        customerName: String,
        customerPhone: String,
        shippingAddress: String,
        products: List<OrderProduct>,
        shippingFee: Int = 10000
    ): Result<OrderCreateResult> {
        return try {
            // === Build request payload ===
            val request = com.example.trifhop.data.model.CreateOrderRequest(
                products = products,
                customerName = customerName,
                customerPhone = customerPhone,
                shippingAddress = shippingAddress,
                shippingFee = shippingFee
            )
            
            // === Debug log before sending ===
            android.util.Log.d("OrderRepository", """
                Creating order with:
                - Token: ${token.take(20)}...
                - Name: $customerName
                - Phone: $customerPhone
                - Address: $shippingAddress
                - Products: ${products.size} item(s)
                - Shipping Fee: $shippingFee
            """.trimIndent())
            
            // === Send request to backend ===
            val response = apiService.createOrder(
                token = "Bearer $token",
                request = request
            )
            
            // === Handle success response ===
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()?.data
                if (order != null) {
                    android.util.Log.i("OrderRepository", "✅ Order created successfully: ID=${order.id}, Invoice=${order.invoiceCode}")
                    Result.success(
                        OrderCreateResult(
                            order = order
                        )
                    )
                } else {
                    android.util.Log.w("OrderRepository", "Order data is null in response")
                    Result.failure(Exception("Order data is null"))
                }
            } else {
                // === Handle error response ===
                val errorBody = response.errorBody()?.string() ?: ""
                val statusCode = response.code()
                
                android.util.Log.e("OrderRepository", """
                    ❌ Order creation failed:
                    HTTP Status: $statusCode
                    Error Body: $errorBody
                    Response Body: ${response.body()}
                """.trimIndent())
                
                // Extract error details for user display (parse JSON response)
                val detailedError = parseErrorResponse(statusCode, errorBody)
                Result.failure(Exception(detailedError))
            }
        } catch (e: retrofit2.HttpException) {
            // === Handle HttpException (network-level HTTP errors) ===
            val errorBody = e.response()?.errorBody()?.string() ?: ""
            val statusCode = e.code()
            
            android.util.Log.e("OrderRepository", """
                ❌ HttpException during order creation:
                Status: $statusCode
                Message: ${e.message}
                Body: $errorBody
            """.trimIndent())
            
            val detailedError = parseErrorResponse(statusCode, errorBody)
            Result.failure(Exception(detailedError))
        } catch (e: java.io.IOException) {
            // === Handle network errors (no internet, timeout, etc) ===
            android.util.Log.e("OrderRepository", "❌ Network error: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message ?: "Unable to connect to server"}"))
        } catch (e: Exception) {
            // === Handle unexpected errors ===
            android.util.Log.e("OrderRepository", "❌ Unexpected error: ${e.message}", e)
            Result.failure(Exception("Unexpected error: ${e.message ?: "Unknown error occurred"}"))
        }
    }
    
    /**
     * Extract meaningful error details from order creation response
     * Priority-based extraction: error → message → validation errors → generic
     */
    private fun parseErrorResponse(statusCode: Int, errorBody: String): String {
        // === Try to parse JSON response ===
        return try {
            // Trim and check empty
            val body = errorBody.trim()
            if (body.isEmpty()) {
                return when (statusCode) {
                    401 -> "Sesi login habis. Silakan login ulang."
                    402, 403 -> "Anda tidak memiliki akses ke resource ini."
                    404 -> "Resource tidak ditemukan."
                    422 -> "Data input tidak valid. Silakan periksa kembali."
                    429 -> "Terlalu banyak request. Coba lagi dalam beberapa saat."
                    500, 502, 503 -> "Terjadi kesalahan di server. Silakan coba lagi."
                    else -> "Terjadi kesalahan (HTTP $statusCode). Silakan coba lagi."
                }
            }
            
            // Parse JSON
            val json = JSONObject(body)
            
            // === Priority 1: Check "error" field (specific error from backend) ===
            if (json.has("error") && !json.isNull("error")) {
                val errorMsg = json.getString("error")
                if (errorMsg.isNotEmpty()) {
                    android.util.Log.d("ParseError", "✅ Found specific error: $errorMsg")
                    return errorMsg
                }
            }
            
            // === Priority 2: Check "message" field ===
            if (json.has("message") && !json.isNull("message")) {
                val message = json.getString("message")
                if (message.isNotEmpty() && message != "Failed") {
                    android.util.Log.d("ParseError", "✅ Found message: $message")
                    return message
                }
            }
            
            // === Priority 3: Check "errors" object (validation errors) ===
            if (json.has("errors") && !json.isNull("errors")) {
                val errors = json.getJSONObject("errors")
                val errorsList = mutableListOf<String>()
                
                val keys = errors.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = errors.get(key)
                    
                    when (value) {
                        is String -> {
                            if (value.isNotEmpty()) {
                                errorsList.add("• $key: $value")
                            }
                        }
                        is JSONObject -> {
                            try {
                                // Handle nested error objects
                                errorsList.add("• $key: ${value.toString()}")
                            } catch (e: Exception) {
                                // Ignore parsing errors
                            }
                        }
                        else -> {
                            // Try to convert to string
                            val strValue = value.toString()
                            if (strValue.isNotEmpty() && strValue != "null") {
                                errorsList.add("• $key: $strValue")
                            }
                        }
                    }
                }
                
                if (errorsList.isNotEmpty()) {
                    val validationErrors = errorsList.joinToString("\n")
                    android.util.Log.d("ParseError", "✅ Found validation errors:\n$validationErrors")
                    return "Validasi gagal:\n$validationErrors"
                }
            }
            
            // === Priority 4: Check "data" field (sometimes contains error info) ===
            if (json.has("data") && !json.isNull("data")) {
                val data = json.get("data")
                if (data is String && data.isNotEmpty()) {
                    android.util.Log.d("ParseError", "✅ Found error in data field: $data")
                    return data
                }
            }
            
            // === Default: Return status code specific error ===
            android.util.Log.w("ParseError", "Could not extract specific error, using status code fallback")
            when (statusCode) {
                401 -> "Sesi login habis. Silakan login ulang."
                402, 403 -> "Anda tidak memiliki akses ke resource ini."
                404 -> "Resource tidak ditemukan."
                422 -> "Data input tidak valid. Silakan periksa kembali."
                429 -> "Terlalu banyak request. Coba lagi dalam beberapa saat."
                500, 502, 503 -> "Terjadi kesalahan di server. Silakan coba lagi."
                else -> "Terjadi kesalahan (HTTP $statusCode). Silakan coba lagi."
            }
            
        } catch (e: JSONException) {
            // === JSON parsing failed - try to extract plain text ===
            android.util.Log.w("ParseError", "Failed to parse JSON: ${e.message}")
            try {
                // Try to extract error text from plain text response
                if (errorBody.contains("stock", ignoreCase = true)) {
                    return "Stok produk tidak mencukupi"
                }
                if (errorBody.contains("not found", ignoreCase = true)) {
                    return "Produk tidak ditemukan"
                }
                if (errorBody.contains("validation", ignoreCase = true)) {
                    return "Data input tidak valid"
                }
                if (errorBody.contains("unauthorized", ignoreCase = true)) {
                    return "Anda tidak memiliki akses"
                }
                
                // Return what we can extract
                val lines = errorBody.split("\n", "\r").filter { it.isNotEmpty() }
                if (lines.isNotEmpty()) {
                    return lines.first().take(200) // Limit to 200 chars
                }
            } catch (e: Exception) {
                // Ignore
            }
            
            // === Final fallback ===
            when (statusCode) {
                401 -> "Sesi login habis. Silakan login ulang."
                402, 403 -> "Anda tidak memiliki akses ke resource ini."
                404 -> "Resource tidak ditemukan."
                422 -> "Data input tidak valid. Silakan periksa kembali."
                429 -> "Terlalu banyak request. Coba lagi dalam beberapa saat."
                500, 502, 503 -> "Terjadi kesalahan di server. Silakan coba lagi."
                else -> "Terjadi kesalahan (HTTP $statusCode). Silakan coba lagi."
            }
        } catch (e: Exception) {
            // === Generic exception handling ===
            android.util.Log.e("ParseError", "Unexpected error parsing response: ${e.message}", e)
            when (statusCode) {
                401 -> "Sesi login habis. Silakan login ulang."
                402, 403 -> "Anda tidak memiliki akses ke resource ini."
                404 -> "Resource tidak ditemukan."
                422 -> "Data input tidak valid. Silakan periksa kembali."
                429 -> "Terlalu banyak request. Coba lagi dalam beberapa saat."
                500, 502, 503 -> "Terjadi kesalahan di server. Silakan coba lagi."
                else -> "Terjadi kesalahan (HTTP $statusCode). Silakan coba lagi."
            }
        }
    }

    /**
     * Sinkronkan status pembayaran terbaru dari Xendit
     */
    suspend fun createCheckoutInvoice(token: String, orderId: Int): Result<CheckoutData> {
        return try {
            android.util.Log.i("CheckoutInvoice", "Creating checkout for order: $orderId")
            
            val response = apiService.createCheckoutInvoice(
                token = "Bearer $token",
                request = CheckoutRequest(orderId = orderId)
            )

            android.util.Log.d("CheckoutInvoice", "Response code: ${response.code()}, Success: ${response.isSuccessful}")

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    android.util.Log.i(
                        "CheckoutInvoice",
                        "✅ Checkout success - URL: ${data.paymentUrlOrInvoiceUrl?.take(50)}..."
                    )
                    Result.success(data)
                } else {
                    val errorMsg = "Checkout data is null in response"
                    android.util.Log.e("CheckoutInvoice", errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                // Extract error message dari response body
                val errorBody = response.errorBody()?.string() ?: ""
                val statusCode = response.code()
                
                // Log detail untuk debugging
                android.util.Log.e(
                    "CheckoutError",
                    """
                    ❌ HTTP $statusCode: Error creating checkout
                    Response Body: 
                    $errorBody
                    """.trimIndent()
                )
                
                val detailedError = parseErrorResponse(statusCode, errorBody)
                Result.failure(Exception(detailedError))
            }
        } catch (e: retrofit2.HttpException) {
            // Handle HttpException untuk mendapat body response
            val errorBody = e.response()?.errorBody()?.string() ?: ""
            val statusCode = e.code()
            
            android.util.Log.e(
                "CheckoutHttpException",
                """
                ❌ HTTP $statusCode Error:
                Message: ${e.message}
                Body: $errorBody
                """.trimIndent(),
                e
            )
            
            val detailedError = parseErrorResponse(statusCode, errorBody)
            Result.failure(Exception(detailedError))
        } catch (e: JsonParseException) {
            // JSON parsing error - response format mismatch
            android.util.Log.e(
                "CheckoutJsonException",
                """
                ❌ JSON Parsing Error: ${e.message}
                Check if response format matches CheckoutData model
                """.trimIndent(),
                e
            )
            Result.failure(Exception("Format response tidak sesuai. Error: ${e.message}"))
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("CheckoutTimeout", "Server timeout", e)
            Result.failure(Exception("Koneksi timeout. Silakan coba lagi."))
        } catch (e: java.io.IOException) {
            // Network error
            android.util.Log.e("CheckoutNetworkError", "Failed to connect to server: ${e.message}", e)
            Result.failure(Exception("Tidak bisa terhubung ke server: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("CheckoutException", "Unexpected error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Sinkronkan status pembayaran terbaru dari Xendit
     */
    suspend fun checkPaymentStatus(token: String, orderId: Int): Result<Order> {
        return try {
            val response = apiService.checkPaymentStatus(
                token = "Bearer $token",
                orderId = orderId
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val latestOrder = response.body()?.data?.order
                if (latestOrder != null) {
                    Result.success(latestOrder)
                } else {
                    Result.failure(Exception("Payment status data is null"))
                }
            } else {
                val errorMsg = response.body()?.message ?: "Failed to check payment status"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
