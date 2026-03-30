package com.example.trifhop.data.network

import com.example.trifhop.data.model.*
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service Interface untuk Trifhop Backend Laravel
 */
interface ApiService {
    
    // ========== AUTH ENDPOINTS ==========
    
    /**
     * POST /api/register
     * Register new user
     */
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse
    
    /**
     * POST /api/login
     * Login user
     */
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * POST /api/auth/google
     * Login atau register via Google Sign-In
     */
    @POST("auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): LoginResponse

    /**
     * POST /api/logout
     * Logout user
     */
    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): ApiResponse<Unit>
    
    // ========== CATEGORY ENDPOINTS ==========
    
    /**
     * GET /api/categories
     * Mengambil semua kategori dari database
     */
    @GET("categories")
    suspend fun getCategories(): Response<CategoryResponse>
    
    /**
     * GET /api/categories/{id}
     * Mengambil detail kategori beserta produknya
     */
    @GET("categories/{id}")
    suspend fun getCategoryDetail(@Path("id") categoryId: Int): Response<CategoryDetailResponse>
    
    // ========== PRODUCT ENDPOINTS ==========
    
    /**
     * GET /api/products
     * Mengambil semua produk, bisa filter berdasarkan kategori
     */
    @GET("products")
    suspend fun getProducts(
        @Query("category_id") categoryId: Int? = null
    ): Response<ProductResponse>
    
    /**
     * GET /api/products/{id}
     * Mengambil detail produk berdasarkan ID
     */
    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") productId: Int): Response<ProductDetailResponse>
    
    // ========== ORDER ENDPOINTS ==========
    
    /**
     * POST /api/orders
     * Create new order (checkout)
     * Requires authentication token
     */
    @POST("orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<OrderResponse>

    /**
     * POST /api/checkout
     * Create Xendit invoice for existing order
     */
    @POST("checkout")
    suspend fun createCheckoutInvoice(
        @Header("Authorization") token: String,
        @Body request: CheckoutRequest
    ): Response<CheckoutResponse>
    
    /**
     * GET /api/orders/current
     * Get current orders (pending, packed, sent)
     */
    @GET("orders/current")
    suspend fun getCurrentOrders(
        @Header("Authorization") token: String
    ): Response<OrderListResponse>
    
    /**
     * GET /api/orders/past
     * Get past orders (finished)
     */
    @GET("orders/past")
    suspend fun getPastOrders(
        @Header("Authorization") token: String
    ): Response<OrderListResponse>
    
    /**
     * GET /api/orders/{id}
     * Get order detail by ID
     */
    @GET("orders/{id}")
    suspend fun getOrderById(
        @Header("Authorization") token: String,
        @Path("id") orderId: Int
    ): Response<OrderResponse>

    /**
     * GET /api/orders/{id}/payment-status
     * Check latest payment status by syncing with Xendit
     */
    @GET("orders/{id}/payment-status")
    suspend fun checkPaymentStatus(
        @Header("Authorization") token: String,
        @Path("id") orderId: Int
    ): Response<PaymentStatusResponse>
}

// Response Models sesuai dengan API Laravel
data class CategoryResponse(
    val success: Boolean = true,
    val message: String,
    val data: List<Category>
)

data class CategoryDetailResponse(
    val success: Boolean = true,
    val message: String,
    val data: CategoryWithProducts
)

data class CategoryWithProducts(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val products: List<Product>
)

data class ProductResponse(
    val success: Boolean = true,
    val message: String,
    val data: List<Product>
)

data class ProductDetailResponse(
    val success: Boolean = true,
    val message: String,
    val data: Product
)

data class OrderResponse(
    val success: Boolean = true,
    val message: String,
    val data: Order
)

data class OrderListResponse(
    val success: Boolean = true,
    val message: String,
    val data: List<Order>
)

data class CheckoutResponse(
    val success: Boolean = true,
    val message: String,
    val data: CheckoutData?
)

data class CheckoutData(
    @SerializedName("order_id")
    val orderId: Int = 0,
    
    // Midtrans fields (primary)
    @SerializedName("transaction_id")
    val transactionId: String = "",
    @SerializedName("snap_token")
    val snapToken: String = "",
    @SerializedName("redirect_url")
    val redirectUrl: String = "",
    @SerializedName("payment_url")
    val paymentUrl: String = "",
    
    // Legacy Xendit fields (backward compatibility)
    @SerializedName("external_id")
    val externalId: String? = null,
    @SerializedName("invoice_id")
    val invoiceId: String? = null,
    @SerializedName("invoice_url")
    val invoiceUrl: String? = null,
    @SerializedName("invoice_status")
    val invoiceStatus: String? = null
) {
    // Helper property to get payment URL (works for both Xendit and Midtrans)
    val paymentUrlOrInvoiceUrl: String?
        get() {
            return when {
                redirectUrl.isNotEmpty() -> redirectUrl
                paymentUrl.isNotEmpty() -> paymentUrl
                invoiceUrl?.isNotEmpty() == true -> invoiceUrl
                else -> null
            }
        }
}

data class PaymentStatusResponse(
    val success: Boolean = true,
    val message: String,
    val data: PaymentStatusData?
)

data class PaymentStatusData(
    val order: Order,
    @SerializedName("invoice_status")
    val invoiceStatus: String,
    @SerializedName("invoice_url")
    val invoiceUrl: String?,
    @SerializedName("payment_status")
    val paymentStatus: String?
)
