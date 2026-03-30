<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

/**
 * ============================================
 * MODEL: Order
 * ============================================
 * 
 * Representasi order/checkout dengan payment
 * Fokus pada PAYMENT & CHECKOUT (bukan shipping)
 * Shipping info ada di table tapi mostly untuk reference
 * 
 * RELATIONSHIPS:
 * - belongsTo(User) - Pembeli
 * - hasMany(OrderItem) - Item dalam order ini
 * 
 * SCOPES:
 * - pending() → belum dibayar
 * - paid() → sudah dibayar
 * - failed() → pembayaran gagal
 * - byUser($userId) → order milik user tertentu
 * 
 * KEY FIELDS UNTUK MIDTRANS:
 * - snap_token: Token dari Midtrans (untuk di-pass ke mobile)
 * - payment_url: URL pembayaran Midtrans
 * - midtrans_transaction_id: ID transaksi dari Midtrans (untuk webhook)
 * - midtrans_response: Response JSON dari Midtrans (debug)
 */
class Order extends Model
{
    use HasFactory;

    protected $table = 'orders';

    // ================== CONFIGURATION ==================

    /**
     * Fillable - Field yang bisa di-mass assign
     */
    protected $fillable = [
        'user_id',
        'order_code',
        'description',
        'subtotal',
        'shipping_fee',
        'discount_amount',
        'total_price',
        'payment_method',
        'payment_status',
        'snap_token',
        'payment_url',
        'midtrans_transaction_id',
        'midtrans_response',
        'customer_name',
        'customer_phone',
        'shipping_address',
        'shipping_province',
        'shipping_city',
        'shipping_postal_code',
        'shipping_status',
        'shipping_provider',
        'tracking_number',
        'paid_at',
        'shipped_at',
        'delivered_at',
        'is_urgent',
        'admin_notes',
    ];

    /**
     * Casts
     */
    protected $casts = [
        'subtotal' => 'integer',
        'shipping_fee' => 'integer',
        'discount_amount' => 'integer',
        'total_price' => 'integer',
        'midtrans_response' => 'array',
        'is_urgent' => 'boolean',
        'paid_at' => 'datetime',
        'shipped_at' => 'datetime',
        'delivered_at' => 'datetime',
    ];

    /**
     * Appends - Virtual attribute
     */
    protected $appends = ['is_paid', 'formatted_total_price'];

    // ================== RELATIONSHIPS ==================

    /**
     * Relasi: Order ← User (Many To One)
     * Satu order milik 1 user
     */
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    /**
     * Relasi: Order → OrderItems (One To Many)
     * Order bisa punya banyak items (produk)
     */
    public function items()
    {
        return $this->hasMany(OrderItem::class);
    }

    // ================== SCOPES ==================

    /**
     * SCOPE: Pending Orders (Belum dibayar)
     */
    public function scopePending($query)
    {
        return $query->where('payment_status', 'pending');
    }

    /**
     * SCOPE: Waiting for Payment
     */
    public function scopeWaiting($query)
    {
        return $query->where('payment_status', 'waiting');
    }

    /**
     * SCOPE: Paid Orders
     */
    public function scopePaid($query)
    {
        return $query->where('payment_status', 'paid');
    }

    /**
     * SCOPE: Failed Payment
     */
    public function scopeFailed($query)
    {
        return $query->where('payment_status', 'failed');
    }

    /**
     * SCOPE: Cancelled Orders
     */
    public function scopeCancelled($query)
    {
        return $query->where('payment_status', 'cancelled');
    }

    /**
     * SCOPE: By User
     * 
     * Penggunaan: Order::byUser(5)->get()
     */
    public function scopeByUser($query, $userId)
    {
        return $query->where('user_id', $userId);
    }

    /**
     * SCOPE: By Order Code
     */
    public function scopeByCode($query, $code)
    {
        return $query->where('order_code', $code);
    }

    /**
     * SCOPE: By Midtrans Transaction ID
     * Untuk lookup webhook dari Midtrans
     */
    public function scopeByMidtransId($query, $transactionId)
    {
        return $query->where('midtrans_transaction_id', $transactionId);
    }

    /**
     * SCOPE: Recent Orders (Last 7 days)
     */
    public function scopeRecent($query)
    {
        return $query->where('created_at', '>=', now()->subDays(7))
                     ->latest();
    }

    // ================== ACCESSORS ==================

    /**
     * Accessor: is_paid
     * Return: boolean apakah order sudah dibayar
     */
    public function getIsPaidAttribute()
    {
        return $this->payment_status === 'paid';
    }

    /**
     * Accessor: formatted_total_price
     * Return: Format Rupiah
     */
    public function getFormattedTotalPriceAttribute()
    {
        return 'Rp ' . number_format($this->total_price, 0, ',', '.');
    }

    // ================== METHODS ==================

    /**
     * Method: Mark as Paid
     * 
     * Dipanggil dari webhook Midtrans saat pembayaran sukses
     */
    public function markAsPaid($midtransTransactionId = null)
    {
        $this->update([
            'payment_status' => 'paid',
            'paid_at' => now(),
            'midtrans_transaction_id' => $midtransTransactionId ?? $this->midtrans_transaction_id,
        ]);

        // Kurangi stock produk
        $this->decrementProductStocks();

        return $this;
    }

    /**
     * Method: Mark as Failed
     */
    public function markAsFailed()
    {
        $this->update([
            'payment_status' => 'failed',
        ]);
        return $this;
    }

    /**
     * Method: Decrement Product Stocks
     * 
     * Dipanggil saat pembayaran sukses
     * Kurangi stock semua produk yang ada di order ini
     */
    public function decrementProductStocks()
    {
        foreach ($this->items as $item) {
            $item->product->decrementStock($item->quantity);
        }
    }

    /**
     * Method: Calculate Total
     * 
     * Hitung ulang total berdasarkan items yang ada
     */
    public function recalculateTotal()
    {
        $subtotal = $this->items->sum(function ($item) {
            return $item->subtotal;
        });

        $this->update([
            'subtotal' => $subtotal,
            'total_price' => $subtotal + $this->shipping_fee - $this->discount_amount,
        ]);

        return $this;
    }

    /**
     * Method: Get Amount for Midtrans
     * Return: integer dalam cent (default Midtrans format)
     */
    public function getMidtransAmount()
    {
        return $this->total_price; // dalam Rupiah (sudah integer)
    }

    /**
     * Method: Generate Unique Order Code
     * Format: ORD-YYYYMMDD-XXXXX
     * 
     * Static method
     */
    public static function generateOrderCode()
    {
        $date = now()->format('Ymd');
        $count = static::whereDate('created_at', now())->count() + 1;
        return sprintf('ORD-%s-%05d', $date, $count);
    }
}
