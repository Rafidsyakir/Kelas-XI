package com.example.trifhop.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("invoice_code")
    val invoiceCode: String,
    
    @SerializedName("total_price")
    val totalPrice: Double,
    
    @SerializedName("status")
    val status: String, // pending, packed, sent, finished

    @SerializedName("payment_status")
    val paymentStatus: String? = null,
    
    @SerializedName("customer_name")
    val customerName: String,
    
    @SerializedName("customer_phone")
    val customerPhone: String,
    
    @SerializedName("shipping_address")
    val shippingAddress: String,
    
    @SerializedName("ordered_at")
    val orderedAt: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("user_id")
    val userId: Int? = null,
    
    @SerializedName("user")
    val user: User? = null,
    
    @SerializedName("details")
    val details: List<TransactionDetail>? = null
)

data class TransactionDetail(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("transaction_id")
    val transactionId: Int,
    
    @SerializedName("product_id")
    val productId: Int,
    
    @SerializedName("price_at_purchase")
    val priceAtPurchase: Double,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("product")
    val product: Product? = null
)

// Request untuk create order - match dengan backend
data class CreateOrderRequest(
    @SerializedName("products")
    val products: List<OrderProduct>,
    
    @SerializedName("customer_name")
    val customerName: String,
    
    @SerializedName("customer_phone")
    val customerPhone: String,
    
    @SerializedName("shipping_address")
    val shippingAddress: String,

    @SerializedName("shipping_fee")
    val shippingFee: Int = 10000
)

data class CheckoutRequest(
    @SerializedName("order_id")
    val orderId: Int
)

// Hasil create order
data class OrderCreateResult(
    val order: Order
)

data class OrderProduct(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("quantity")
    val quantity: Int
)
