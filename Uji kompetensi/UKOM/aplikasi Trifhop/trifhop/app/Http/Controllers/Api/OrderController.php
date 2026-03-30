<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use App\Models\Product;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Validation\ValidationException;

class OrderController extends Controller
{
    /**
     * Create new order (checkout)
     * 🔧 DEEP SURGERY: Now returns SPECIFIC error messages, not generic "terjadi kesalahan di server"
     * 
     * REQUEST:
     * {
     *   "products": [{"id": 43, "quantity": 1}],
     *   "customer_name": "Rafid",
     *   "customer_phone": "081234567890",
     *   "shipping_address": "Jl. Test No. 1",
     *   "shipping_fee": 10000
     * }
     */
    public function store(Request $request)
    {
        // === DEBUG: Log incoming request ===
        Log::info('📋 ORDER CREATE REQUEST', [
            'user_id' => $request->user()?->id ?? 'UNAUTHENTICATED',
            'user_email' => $request->user()?->email ?? 'NONE',
            'request_data' => $request->all(),
            'headers' => $request->headers->all(),
        ]);

        try {
            // === STEP 1: Validate authentication ===
            $user = $request->user();
            if (!$user) {
                Log::warning('❌ User not authenticated for order creation');
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthenticated',
                    'error' => 'Please login first',
                ], 401);
            }

            // === STEP 2: Validate input data ===
            $validated = $request->validate([
                'products'         => 'required|array|min:1',
                'products.*.id'    => 'required|integer|exists:products,id',
                'products.*.quantity' => 'required|integer|min:1',
                'customer_name'    => 'required|string|max:255',
                'customer_phone'   => 'required|string|max:20',
                'shipping_address' => 'required|string|max:500',
                'shipping_fee'     => 'nullable|numeric|min:0',
            ]);

            Log::info('✅ Validation passed', ['validated_data' => $validated]);

            // === STEP 3: Begin transaction ===
            DB::beginTransaction();
            Log::info('🔄 Database transaction started');

            // === STEP 4: Validate and calculate product totals ===
            $productSubtotal = 0;
            $productsToUpdate = [];

            foreach ($validated['products'] as $item) {
                $product = Product::find($item['id']);

                if (!$product) {
                    throw new \Exception("❌ Produk dengan ID {$item['id']} tidak ditemukan di database");
                }

                if (!$product->isAvailable()) {
                    throw new \Exception("❌ Produk '{$product->name}' tidak tersedia (status: {$product->status})");
                }

                if ((int) $product->stock < (int) $item['quantity']) {
                    throw new \Exception("❌ Stok '{$product->name}' tidak cukup. Tersedia: {$product->stock}, Diminta: {$item['quantity']}");
                }

                $productSubtotal += $product->price * $item['quantity'];
                $productsToUpdate[] = [
                    'product'  => $product,
                    'quantity' => $item['quantity'],
                ];
            }

            Log::info('✅ Products validated', [
                'products_count' => count($productsToUpdate),
                'subtotal' => $productSubtotal,
            ]);

            // === STEP 5: Calculate total with shipping ===
            $shippingFee = (int) ($validated['shipping_fee'] ?? 10000);
            $totalPrice  = $productSubtotal + $shippingFee;

            Log::info('💰 Pricing calculated', [
                'subtotal' => $productSubtotal,
                'shipping_fee' => $shippingFee,
                'total_price' => $totalPrice,
            ]);

            // === STEP 6: Create transaction in database ===
            $transaction = Transaction::create([
                'user_id'          => $user->id,
                'invoice_code'     => Transaction::generateInvoiceCode(),
                'total_price'      => $totalPrice,
                'status'           => 'pending',
                'payment_status'   => 'UNPAID',
                'customer_name'    => $validated['customer_name'],
                'customer_phone'   => $validated['customer_phone'],
                'shipping_address' => $validated['shipping_address'],
                'ordered_at'       => now(),
            ]);

            Log::info('✅ Transaction created', [
                'transaction_id' => $transaction->id,
                'invoice_code' => $transaction->invoice_code,
            ]);

            // === STEP 7: Create transaction details and update stock ===
            foreach ($productsToUpdate as $item) {
                $product  = $item['product'];
                $quantity = $item['quantity'];

                $transaction->details()->create([
                    'product_id'         => $product->id,
                    'price_at_purchase'  => $product->price,
                    'quantity'           => $quantity,
                ]);

                $newStock = $product->stock - $quantity;
                $product->update([
                    'stock'  => $newStock,
                    'status' => $newStock > 0 ? 'available' : 'sold',
                ]);

                Log::info('✅ Product stock updated', [
                    'product_id' => $product->id,
                    'product_name' => $product->name,
                    'old_stock' => $product->stock,
                    'new_stock' => $newStock,
                ]);
            }

            // === STEP 8: Commit transaction ===
            DB::commit();
            Log::info('✅ Database transaction committed');

            // === STEP 9: Load and return complete transaction data ===
            $transaction->load(['details.product', 'user']);

            Log::info('🎉 Order created successfully', [
                'order_id' => $transaction->id,
                'user_id' => $user->id,
                'total_price' => $totalPrice,
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Order created successfully',
                'data'    => $transaction,
            ], 201);

        } catch (ValidationException $e) {
            DB::rollBack();
            
            // 🔧 DEEP SURGERY: Extract specific validation errors with USER-FRIENDLY messages
            $validationErrors = $e->errors();
            $errorMessages = [];
            $userFriendlyMessage = '';
            
            foreach ($validationErrors as $field => $messages) {
                foreach ($messages as $message) {
                    $errorMessages[] = "[$field] $message";
                    
                    // Extract product ID from field if validation is about product
                    if (str_contains($field, 'products')) {
                        if (str_contains($message, 'invalid') || str_contains($message, 'selected')) {
                            $userFriendlyMessage = 'ID produk tidak valid atau produk tidak ditemukan. Silakan refresh daftar produk dan coba lagi.';
                        }
                    }
                }
            }
            
            Log::error('❌ VALIDATION FAILED', [
                'errors' => $errorMessages,
                'all_data' => $request->all(),
            ]);
            
            return response()->json([
                'success' => false,
                'message' => $userFriendlyMessage ?: 'Validasi gagal',
                'error' => $userFriendlyMessage ?: implode(' | ', $errorMessages),
                'errors' => $validationErrors,
                'error_type' => 'VALIDATION',
            ], 422);

        } catch (\Exception $e) {
            DB::rollBack();
            
            // 🔧 DEEP SURGERY: Log detailed error information
            Log::error('❌ ORDER CREATION FAILED - DEEP SURGERY', [
                'error_message' => $e->getMessage(),
                'error_code' => $e->getCode(),
                'file' => $e->getFile(),
                'line' => $e->getLine(),
                'trace' => $e->getTraceAsString(),
                'user_id' => $request->user()?->id,
                'request_data' => $request->all(),
            ]);

            // 🔧 DEEP SURGERY: Return SPECIFIC error message to Android, not generic "terjadi kesalahan di server"
            $errorMessage = $e->getMessage();
            
            // Extract meaningful error from exception
            $specificError = $this->extractSpecificError($errorMessage);
            
            return response()->json([
                'success' => false,
                'message' => $specificError['user_message'],
                'error'   => $errorMessage,  // Technical error for logs
                'error_type' => $specificError['type'],
                'debug' => config('app.debug') ? [
                    'exception_class' => get_class($e),
                    'code' => $e->getCode(),
                    'file' => $e->getFile(),
                    'line' => $e->getLine(),
                ] : null,
            ], 500);
        }
    }

    /**
     * 🔧 DEEP SURGERY: Extract specific error message from exception
     */
    private function extractSpecificError($errorMessage): array
    {
        // Product not found
        if (str_contains($errorMessage, 'tidak ditemukan')) {
            return [
                'type' => 'PRODUCT_NOT_FOUND',
                'user_message' => 'Produk tidak ditemukan: ' . $errorMessage,
            ];
        }
        
        // Product not available
        if (str_contains($errorMessage, 'tidak tersedia')) {
            return [
                'type' => 'PRODUCT_UNAVAILABLE',
                'user_message' => 'Produk tidak tersedia: ' . $errorMessage,
            ];
        }
        
        // Insufficient stock
        if (str_contains($errorMessage, 'Stok') || str_contains($errorMessage, 'stock')) {
            return [
                'type' => 'INSUFFICIENT_STOCK',
                'user_message' => 'Stok tidak cukup: ' . $errorMessage,
            ];
        }
        
        // Database error
        if (str_contains($errorMessage, 'Database') || str_contains($errorMessage, 'database')) {
            return [
                'type' => 'DATABASE_ERROR',
                'user_message' => 'Gagal menyimpan pesanan ke database. Silakan coba lagi.',
            ];
        }
        
        // Default: return error message as-is
        return [
            'type' => 'UNKNOWN_ERROR',
            'user_message' => $errorMessage,
        ];
    }

    /**
     * Get current orders (pending, packed, sent)
     */
    public function current(Request $request)
    {
        try {
            $orders = Transaction::with(['details.product'])
                ->where('user_id', $request->user()->id)
                ->whereIn('status', ['pending', 'packed', 'sent'])
                ->latest()
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Current orders retrieved successfully',
                'data' => $orders,
            ]);
        } catch (\Exception $e) {
            // 🔧 DEEP SURGERY: Return specific error message
            Log::error('Failed to fetch current orders', [
                'error' => $e->getMessage(),
                'user_id' => $request->user()?->id,
            ]);
            
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil pesanan aktif',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Get past orders (finished)
     */
    public function past(Request $request)
    {
        try {
            $orders = Transaction::with(['details.product'])
                ->where('user_id', $request->user()->id)
                ->where('status', 'finished')
                ->latest()
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Past orders retrieved successfully',
                'data' => $orders,
            ]);
        } catch (\Exception $e) {
            // 🔧 DEEP SURGERY: Return specific error message
            Log::error('Failed to fetch past orders', [
                'error' => $e->getMessage(),
                'user_id' => $request->user()?->id,
            ]);
            
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil pesanan selesai',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
