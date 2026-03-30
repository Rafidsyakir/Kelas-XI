<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProductController;
use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\OrderController;
use App\Http\Controllers\Api\CheckoutController;
use App\Http\Controllers\Api\UserController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
*/

// PUBLIC ROUTES (No authentication required)
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);
Route::post('/auth/google', [AuthController::class, 'googleLogin']);

// Products
Route::get('/products', [ProductController::class, 'index']);
Route::get('/products/{id}', [ProductController::class, 'show']);
Route::get('/products/category/{categorySlug}', [ProductController::class, 'byCategory']);

// Categories
Route::get('/categories', [CategoryController::class, 'index']);
Route::get('/categories/{id}', [CategoryController::class, 'show']);

// 🔧 Midtrans Webhook (PUBLIC - called directly by Midtrans server)
Route::post('/midtrans/notification', [CheckoutController::class, 'notification']);

// PROTECTED ROUTES (Requires authentication)
Route::middleware('auth:sanctum')->group(function () {
    // Auth
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
    
    // User Profile
    Route::get('/user', [UserController::class, 'profile']);
    Route::put('/user/profile', [UserController::class, 'updateProfile']);
    Route::post('/user/change-password', [UserController::class, 'changePassword']);
    
    // Orders
    Route::post('/orders', [OrderController::class, 'store']);
    Route::post('/checkout', [CheckoutController::class, 'checkout']);
    Route::get('/orders/current', [OrderController::class, 'current']);
    Route::get('/orders/past', [OrderController::class, 'past']);
    Route::get('/orders/{id}', [OrderController::class, 'show']);
    Route::get('/orders/{id}/payment-status', [CheckoutController::class, 'checkPaymentStatus']);
    Route::get('/orders/track/{invoiceCode}', [OrderController::class, 'track']);
});
