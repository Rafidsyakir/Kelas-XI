<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class TransactionDetail extends Model
{
    use HasFactory;

    protected $fillable = [
        'transaction_id',
        'product_id',
        'price_at_purchase',
        'quantity',
    ];

    protected $casts = [
        'price_at_purchase' => 'float',
    ];

    // Relasi ke transaction
    public function transaction()
    {
        return $this->belongsTo(Transaction::class);
    }

    // Relasi ke product
    public function product()
    {
        return $this->belongsTo(Product::class);
    }

    // Calculate subtotal
    public function getSubtotalAttribute()
    {
        return $this->price_at_purchase * $this->quantity;
    }
}
