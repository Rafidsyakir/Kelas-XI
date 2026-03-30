<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

/**
 * ============================================
 * MODEL: OrderItem
 * ============================================
 * 
 * Representasi satu item produk dalam order
 * 
 * Contoh:
 * Order 123:
 *   - OrderItem 1: Produk 5 (Nike Jacket) × 2 = Rp 900.000
 *   - OrderItem 2: Produk 10 (T-Shirt) × 3 = Rp 450.000
 * Total Order = Rp 1.350.000
 * 
 * RELATIONSHIPS:
 * - belongsTo(Order)
 * - belongsTo(Product)
 * 
 * KEY FEATURES:
 * - price_at_purchase: Harga saat dibeli (snapshot)
 *   Penting untuk tracking jika harga produk berubah
 * 
 * - product_name, product_size, etc: Snapshot attributes
 *   Jika produk di-delete, order history tetap ada
 */
class OrderItem extends Model
{
    use HasFactory;

    protected $table = 'order_items';

    // ================== CONFIGURATION ==================

    /**
     * Fillable
     */
    protected $fillable = [
        'order_id',
        'product_id',
        'quantity',
        'price_at_purchase',
        'subtotal',
        'product_name',
        'product_size',
        'product_condition',
        'product_image_url',
        'item_status',
    ];

    /**
     * Casts
     */
    protected $casts = [
        'quantity' => 'integer',
        'price_at_purchase' => 'integer',
        'subtotal' => 'integer',
    ];

    /**
     * Appends
     */
    protected $appends = ['formatted_price', 'formatted_subtotal'];

    // ================== RELATIONSHIPS ==================

    /**
     * Relasi: OrderItem ← Order (Many To One)
     * Banyak items belong ke 1 order
     */
    public function order()
    {
        return $this->belongsTo(Order::class);
    }

    /**
     * Relasi: OrderItem ← Product (Many To One)
     * Banyak order items rujuk ke 1 produk
     */
    public function product()
    {
        return $this->belongsTo(Product::class);
    }

    // ================== SCOPES ==================

    /**
     * SCOPE: By Order
     */
    public function scopeByOrder($query, $orderId)
    {
        return $query->where('order_id', $orderId);
    }

    /**
     * SCOPE: By Status
     */
    public function scopeByStatus($query, $status)
    {
        return $query->where('item_status', $status);
    }

    /**
     * SCOPE: Pending Items
     */
    public function scopePending($query)
    {
        return $query->where('item_status', 'pending');
    }

    /**
     * SCOPE: Shipped Items
     */
    public function scopeShipped($query)
    {
        return $query->where('item_status', 'shipped');
    }

    // ================== ACCESSORS ==================

    /**
     * Accessor: formatted_price
     */
    public function getFormattedPriceAttribute()
    {
        return 'Rp ' . number_format($this->price_at_purchase, 0, ',', '.');
    }

    /**
     * Accessor: formatted_subtotal
     */
    public function getFormattedSubtotalAttribute()
    {
        return 'Rp ' . number_format($this->subtotal, 0, ',', '.');
    }

    // ================== METHODS ==================

    /**
     * Method: Calculate Subtotal
     */
    public function calculateSubtotal()
    {
        $this->subtotal = $this->price_at_purchase * $this->quantity;
        return $this;
    }

    /**
     * Method: Create from Product & Quantity
     * 
     * Static helper untuk mudah create OrderItem
     * 
     * Penggunaan:
     * $item = OrderItem::createFromProduct($product, $quantity)
     */
    public static function createFromProduct(Product $product, int $quantity)
    {
        return new static([
            'product_id' => $product->id,
            'quantity' => $quantity,
            'price_at_purchase' => $product->price,
            'subtotal' => $product->price * $quantity,
            'product_name' => $product->name,
            'product_size' => $product->size,
            'product_condition' => $product->condition,
            'product_image_url' => $product->image_url,
            'item_status' => 'pending',
        ]);
    }

    /**
     * Method: Mark as Shipped
     */
    public function markAsShipped()
    {
        $this->update(['item_status' => 'shipped']);
        return $this;
    }

    /**
     * Method: Mark as Delivered
     */
    public function markAsDelivered()
    {
        $this->update(['item_status' => 'delivered']);
        return $this;
    }
}
