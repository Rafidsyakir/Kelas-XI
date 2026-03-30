<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

/**
 * ============================================
 * MODEL: Product
 * ============================================
 * 
 * Representasi produk di toko Trifhop
 * 
 * RELATIONSHIPS:
 * - belongsTo(Category)
 * - hasMany(OrderItem)
 * - hasManyThrough(Order, through OrderItem)
 * 
 * SCOPES:
 * - available() → filter status & stock
 * - byCategory($slug) → filter by category
 * - featured() → produk unggulan
 * - search($keyword) → cari by name/desc
 * - lowStock() → stock <= 5
 * 
 * METHODS:
 * - decrementStock($qty) → kurangi stock
 * - incrementStock($qty) → tambah stock
 * - isAvailable() → cek ketersediaan
 */
class Product extends Model
{
    use HasFactory, SoftDeletes;

    protected $table = 'products';

    // ================== CONFIGURATION ==================

    /**
     * FILLABLE - Field yang bisa di-mass assign
     * 
     * Keamanan: hanya field ini yang bisa diisi melalui:
     * Product::create($data) atau $product->update($data)
     */
    protected $fillable = [
        'category_id',
        'name',
        'description',
        'specifications',
        'price',
        'stock',
        'condition',
        'size',
        'color',
        'brand',
        'image_url',
        'additional_images',
        'status',
        'is_featured',
        'rating',
        'view_count',
        'purchase_count',
        'last_restocked_at',
    ];

    /**
     * CASTS - Tipe data otomatis saat retrieve
     */
    protected $casts = [
        'price' => 'integer',
        'stock' => 'integer',
        'view_count' => 'integer',
        'purchase_count' => 'integer',
        'is_featured' => 'boolean',
        'rating' => 'integer',
        'additional_images' => 'array',
        'last_restocked_at' => 'datetime',
    ];

    /**
     * APPENDS - Virtual attribute yang di-include di JSON response
     */
    protected $appends = ['is_available', 'absolute_image_url', 'formatted_price'];

    // ================== RELATIONSHIPS ==================

    /**
     * Relasi: Product ← Category (Many To One)
     * Satu product belong ke 1 category
     */
    public function category()
    {
        return $this->belongsTo(Category::class);
    }

    /**
     * Relasi: Product → OrderItems (One To Many)
     * Satu product bisa di-order berkali-kali
     */
    public function orderItems()
    {
        return $this->hasMany(OrderItem::class);
    }

    /**
     * Relasi: Product → Orders (Through OrderItems)
     * Semua order yang masukkan product ini
     */
    public function orders()
    {
        return $this->hasManyThrough(Order::class, OrderItem::class);
    }

    /**
     * Relasi Legacy: ke TransactionDetails (jika masih ada)
     */
    public function transactionDetails()
    {
        return $this->hasMany(TransactionDetail::class);
    }

    // ================== SCOPES (Query Builders) ==================

    /**
     * SCOPE: Available Products
     * Filter: status = available AND stock > 0
     * 
     * Penggunaan: Product::available()->get()
     */
    public function scopeAvailable($query)
    {
        return $query->where('status', 'available')
                     ->where('stock', '>', 0);
    }

    /**
     * SCOPE: Filter By Category
     * 
     * Penggunaan: Product::byCategory('t-shirt')->get()
     */
    public function scopeByCategory($query, $categorySlug)
    {
        return $query->whereHas('category', function ($q) use ($categorySlug) {
            $q->where('slug', $categorySlug);
        });
    }

    /**
     * SCOPE: Featured Products
     * 
     * Penggunaan: Product::featured()->get()
     */
    public function scopeFeatured($query)
    {
        return $query->where('is_featured', true)
                     ->available();
    }

    /**
     * SCOPE: Search by Name or Description
     * 
     * Penggunaan: Product::search('nike')->get()
     */
    public function scopeSearch($query, $keyword)
    {
        return $query->where('name', 'LIKE', "%{$keyword}%")
                     ->orWhere('description', 'LIKE', "%{$keyword}%");
    }

    /**
     * SCOPE: Low Stock Warning
     * Filter: stock <= 5 and stock > 0
     * 
     * Penggunaan: Product::lowStock()->get()
     */
    public function scopeLowStock($query)
    {
        return $query->where('stock', '<=', 5)
                     ->where('stock', '>', 0);
    }

    /**
     * SCOPE: Out of Stock
     * Filter: stock = 0
     */
    public function scopeOutOfStock($query)
    {
        return $query->where('stock', 0);
    }

    // ================== ACCESSORS (Virtual Attributes) ==================

    /**
     * Accessor: is_available
     * Return: boolean apakah produk available
     * 
     * Penggunaan: $product->is_available
     */
    public function getIsAvailableAttribute()
    {
        return $this->status === 'available' && $this->stock > 0;
    }

    /**
     * Accessor: absolute_image_url
     * Return: URL lengkap untuk image (untuk API Android)
     * 
     * Jika image_url = "/images/products/abc.jpg"
     * Return: "http://localhost:8000/images/products/abc.jpg"
     * 
     * Penggunaan: $product->absolute_image_url
     */
    public function getAbsoluteImageUrlAttribute()
    {
        if (filter_var($this->image_url, FILTER_VALIDATE_URL)) {
            return $this->image_url; // Sudah URL
        }
        // Convert path jadi URL lengkap
        return url($this->image_url);
    }

    /**
     * Accessor: formatted_price
     * Return: Harga dengan format Rupiah
     * 
     * Penggunaan: $product->formatted_price => "Rp 150.000"
     */
    public function getFormattedPriceAttribute()
    {
        return 'Rp ' . number_format($this->price, 0, ',', '.');
    }

    // ================== METHODS ==================

    /**
     * Method: Check Availability
     * 
     * Penggunaan: if ($product->isAvailable()) { ... }
     */
    public function isAvailable()
    {
        return $this->status === 'available' && $this->stock > 0;
    }

    /**
     * Method: Decrement Stock (Saat Ada Pembelian)
     * 
     * @param int $quantity Jumlah yang dikurangi
     * @return bool Apakah berhasil
     * 
     * Penggunaan: 
     * if ($product->decrementStock(2)) {
     *     // Berhasil dikurangi
     * } else {
     *     // Stock tidak cukup
     * }
     */
    public function decrementStock(int $quantity)
    {
        if ($this->stock >= $quantity) {
            $this->decrement('stock', $quantity);
            $this->increment('purchase_count');
            
            // Jika stock habis, ubah status
            if ($this->stock == 0) {
                $this->update(['status' => 'sold_out']);
            }
            return true;
        }
        return false;
    }

    /**
     * Method: Increment Stock (Restock atau Return)
     * 
     * @param int $quantity Jumlah yang ditambah
     * 
     * Penggunaan: $product->incrementStock(5)
     */
    public function incrementStock(int $quantity)
    {
        $this->increment('stock', $quantity);
        
        // Jika ada stock lagi, ubah status jadi available
        if ($this->stock > 0 && $this->status == 'sold_out') {
            $this->update(['status' => 'available']);
        }
        
        $this->update(['last_restocked_at' => now()]);
    }

    /**
     * Method: Mark as Featured
     */
    public function markAsFeatured()
    {
        $this->update(['is_featured' => true]);
    }

    /**
     * Method: Unmark Featured
     */
    public function unmarkAsFeatured()
    {
        $this->update(['is_featured' => false]);
    }

    /**
     * Method: Mark Product as Sold/Discontinued
     */
    public function markAsSold()
    {
        $this->update([
            'status' => 'sold_out',
            'stock' => 0,
        ]);
    }
}
