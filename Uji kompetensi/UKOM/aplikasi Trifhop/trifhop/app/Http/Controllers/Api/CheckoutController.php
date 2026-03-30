<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Midtrans\Config;
use Midtrans\Transaction as MidtransTransaction;
use Midtrans\Snap;
use Exception;

class CheckoutController extends Controller
{
    /**
     * 🔧 MIDTRANS INTEGRATION: Create payment using Midtrans Snap API
     * 
     * REQUEST: POST /api/checkout
     * {
     *   "order_id": 1
     * }
     * 
     * RESPONSE:
     * {
     *   "success": true,
     *   "message": "Snap token Midtrans berhasil dibuat",
     *   "data": {
     *     "snap_token": "...",
     *     "redirect_url": "https://app.sandbox.midtrans.com/snap/v2/vtweb/...",
     *     "payment_url": "https://app.sandbox.midtrans.com/snap/v2/vtweb/..."
     *   }
     * }
     */
    public function checkout(Request $request)
    {
        $validated = $request->validate([
            'order_id' => 'required|integer|exists:transactions,id',
        ]);

        $order = Transaction::where('id', $validated['order_id'])
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$order) {
            return response()->json([
                'success' => false,
                'message' => 'Order tidak ditemukan',
                'error' => 'Order dengan ID ' . $validated['order_id'] . ' tidak ada untuk user ini',
            ], 404);
        }

        try {
            // === Validasi data yang WAJIB ada sebelum memanggil Midtrans ===
            $user = $request->user();
            $userEmail = $user->email;
            if (empty($userEmail)) {
                return response()->json([
                    'success' => false,
                    'message' => 'Email pengguna tidak tersedia',
                    'error' => 'User email kosong - tidak bisa membuat transaksi pembayaran',
                    'debug' => config('app.debug') ? 'User email is empty' : null,
                ], 422);
            }

            $totalPrice = (int) $order->total_price;
            if ($totalPrice <= 0) {
                return response()->json([
                    'success' => false,
                    'message' => 'Total harga tidak valid',
                    'error' => "Total harga harus lebih dari 0. Nilai saat ini: $totalPrice",
                    'debug' => config('app.debug') ? "Amount must be greater than 0, got: {$totalPrice}" : null,
                ], 422);
            }

            // === Midtrans Configuration ===
            Config::$serverKey = (string) config('services.midtrans.server_key');
            Config::$clientKey = (string) config('services.midtrans.client_key');
            Config::$isSanitized = true;
            Config::$is3ds = (bool) config('services.midtrans.enable_3d_secure', true);
            Config::$curlOptions[CURLOPT_TIMEOUT] = 30;

            // Set environment
            $mode = config('services.midtrans.mode', 'sandbox');
            Config::$isProduction = ($mode === 'production');

            if (empty(Config::$serverKey) || empty(Config::$clientKey)) {
                return response()->json([
                    'success' => false,
                    'message' => 'Konfigurasi Midtrans tidak lengkap',
                    'error' => 'MIDTRANS_SERVER_KEY atau MIDTRANS_CLIENT_KEY tidak dikonfigurasi di .env',
                    'debug' => config('app.debug') ? 'Midtrans keys not configured' : null,
                ], 500);
            }

            // === Prepare transaction data for Midtrans ===
            $orderId = 'TRIFHOP-ORDER-' . $order->id . '-' . time();

            // Get order items from database (transaction details)
            $items = [];
            $orderDetails = $order->details()->get() ?? [];
            if ($orderDetails && count($orderDetails) > 0) {
                foreach ($orderDetails as $detail) {
                    $items[] = [
                        'id' => (string) $detail->id,
                        'price' => (int) $detail->price_at_purchase,
                        'quantity' => (int) $detail->quantity,
                        'name' => (string) ($detail->product->name ?? 'Product #' . $detail->product_id),
                    ];
                }
            } else {
                // Fallback jika items tidak ada
                $items[] = [
                    'id' => (string) $order->id,
                    'price' => $totalPrice,
                    'quantity' => 1,
                    'name' => 'Trifhop Order #' . $order->id,
                ];
            }

            $transactionData = [
                'transaction_details' => [
                    'order_id' => $orderId,
                    'gross_amount' => $totalPrice,
                ],
                'customer_details' => [
                    'first_name' => $user->name ?? 'Customer',
                    'email' => $userEmail,
                    'phone' => $user->phone ?? '0',
                ],
                'item_details' => $items,
            ];

            \Log::info('📤 Creating Midtrans transaction', [
                'order_id' => $orderId,
                'amount' => $totalPrice,
                'customer_email' => $userEmail,
                'items_count' => count($items),
            ]);

            // === Create Snap token ===
            $snapToken = Snap::getSnapToken($transactionData);

            // === Generate payment redirect URL ===
            $paymentUrl = 'https://app.' . ($mode === 'production' ? '' : 'sandbox.') . 'midtrans.com/snap/v2/vtweb/' . $snapToken;

            // === Update order with Midtrans transaction details ===
            $order->update([
                'payment_status' => 'PENDING',
                'midtrans_transaction_id' => $orderId,
                'midtrans_order_id' => $orderId,
                'midtrans_token' => $snapToken,
                'midtrans_redirect_url' => $paymentUrl,
            ]);

            \Log::info('✅ Midtrans snap token created', [
                'order_id' => $order->id,
                'midtrans_order_id' => $orderId,
                'snap_token' => substr($snapToken, 0, 20) . '...',
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Snap token Midtrans berhasil dibuat',
                'data' => [
                    'order_id' => $order->id,
                    'transaction_id' => $orderId,
                    'amount' => (float) $order->total_price,
                    'customer_email' => (string) $userEmail,
                    'description' => 'Pembayaran Trifhop',
                    'snap_token' => $snapToken,
                    'redirect_url' => $paymentUrl,
                    'payment_url' => $paymentUrl,
                    'mode' => $mode,
                ],
            ]);

        } catch (Exception $e) {
            // === Midtrans Exception Handling ===
            \Log::error('❌ Midtrans checkout error', [
                'message' => $e->getMessage(),
                'exception_class' => get_class($e),
                'file' => $e->getFile(),
                'line' => $e->getLine(),
                'order_id' => $validated['order_id'] ?? null,
            ]);

            // Check for specific error types
            $errorMessage = $e->getMessage();
            $statusCode = 500;

            if (str_contains($errorMessage, 'authentication') || str_contains($errorMessage, 'unauthorized') || str_contains($errorMessage, '401')) {
                return response()->json([
                    'success' => false,
                    'message' => 'Gagal autentikasi dengan Midtrans',
                    'error' => 'Konfigurasi Midtrans API key tidak valid atau kadaluarsa',
                    'debug' => config('app.debug') ? 'Check MIDTRANS_SERVER_KEY and MIDTRANS_CLIENT_KEY in .env' : null,
                ], 401);
            }

            if (str_contains($errorMessage, 'connection') || str_contains($errorMessage, 'timeout')) {
                return response()->json([
                    'success' => false,
                    'message' => 'Gagal terhubung ke Midtrans',
                    'error' => 'Server Midtrans sedang tidak merespons. Silakan coba lagi.',
                ], 503);
            }

            return response()->json([
                'success' => false,
                'message' => 'Gagal membuat transaksi pembayaran',
                'error' => $errorMessage,
                'debug' => config('app.debug') ? [
                    'exception_class' => get_class($e),
                    'file' => $e->getFile(),
                    'line' => $e->getLine(),
                ] : null,
            ], $statusCode);
        }
    }

    /**
     * Handle Midtrans payment notifications/callbacks
     */
    public function notification(Request $request)
    {
        $serverKey = (string) config('services.midtrans.server_key');
        if (empty($serverKey)) {
            return response()->json([
                'success' => false,
                'message' => 'MIDTRANS_SERVER_KEY not configured',
            ], 500);
        }

        $transactionStatus = $request->input('transaction_status');
        $orderId = $request->input('order_id');
        $signatureKey = $request->input('signature_key');

        if (!$orderId || !$transactionStatus) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid notification payload',
            ], 422);
        }

        // Verify signature
        $grossAmount = $request->input('gross_amount');
        $expectedSignature = hash('sha512', $orderId . $transactionStatus . $grossAmount . $serverKey);

        if ($signatureKey !== $expectedSignature) {
            \Log::warning('⚠️ Invalid Midtrans notification signature', [
                'order_id' => $orderId,
                'expected' => $expectedSignature,
                'received' => $signatureKey,
            ]);
            return response()->json([
                'success' => false,
                'message' => 'Invalid signature',
            ], 401);
        }

        // Find order
        $order = Transaction::where('midtrans_order_id', $orderId)->first();
        if (!$order) {
            \Log::warning('⚠️ Midtrans notification for unknown order', [
                'order_id' => $orderId,
            ]);
            return response()->json([
                'success' => false,
                'message' => 'Order not found',
            ], 404);
        }

        // Update order payment status based on transaction status
        DB::transaction(function () use ($order, $transactionStatus, $request): void {
            $paymentStatus = match ($transactionStatus) {
                'settlement' => 'PAID',
                'capture' => 'PAID',
                'pending' => 'PENDING',
                'deny' => 'FAILED',
                'cancel' => 'CANCELLED',
                'expire' => 'EXPIRED',
                'refund' => 'REFUND',
                default => 'UNKNOWN',
            };

            $order->update([
                'payment_status' => $paymentStatus,
                'midtrans_transaction_status' => $transactionStatus,
            ]);

            \Log::info('✅ Midtrans notification processed', [
                'order_id' => $order->id,
                'midtrans_order_id' => $orderId,
                'transaction_status' => $transactionStatus,
                'payment_status' => $paymentStatus,
            ]);
        });

        return response()->json([
            'success' => true,
            'message' => 'Notification processed successfully',
        ]);
    }

    /**
     * Check payment status for a specific order
     */
    public function checkPaymentStatus(Request $request, int $orderId)
    {
        $order = Transaction::where('id', $orderId)
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$order) {
            return response()->json([
                'success' => false,
                'message' => 'Order tidak ditemukan',
            ], 404);
        }

        if (!$order->midtrans_order_id) {
            return response()->json([
                'success' => false,
                'message' => 'Midtrans transaction belum dibuat untuk order ini',
            ], 422);
        }

        try {
            Config::$serverKey = config('services.midtrans.server_key');
            Config::$isProduction = config('services.midtrans.mode') === 'production';
            
            $status = MidtransTransaction::status($order->midtrans_order_id);
            
            return response()->json([
                'success' => true,
                'data' => [
                    'order_id' => $order->id,
                    'transaction_status' => $status->transaction_status,
                    'payment_status' => $this->mapMidtransStatus($status->transaction_status),
                    'gross_amount' => $status->gross_amount ?? $order->total_price,
                ]
            ]);
        } catch (Exception $e) {
            \Log::error('❌ Error checking Midtrans payment status', [
                'order_id' => $orderId,
                'error' => $e->getMessage(),
            ]);

            return response()->json([
                'success' => false,
                'message' => 'Gagal memeriksa status pembayaran',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Map Midtrans transaction status to payment status
     */
    private function mapMidtransStatus(string $transactionStatus): string
    {
        return match ($transactionStatus) {
            'settlement', 'capture' => 'PAID',
            'pending' => 'PENDING',
            'deny' => 'FAILED',
            'cancel' => 'CANCELLED',
            'expire' => 'EXPIRED',
            'refund' => 'REFUND',
            default => 'UNKNOWN',
        };
    }
}
