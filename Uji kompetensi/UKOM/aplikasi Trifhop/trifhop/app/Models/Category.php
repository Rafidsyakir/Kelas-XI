<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    use HasFactory;

    protected $fillable = [
        'name',
        'slug',
        'description',
        'icon_url',
    ];

    // Relasi ke products
    public function products()
    {
        return $this->hasMany(Product::class);
    }

    // Hitung jumlah produk per kategori
    public function productsCount()
    {
        return $this->products()->count();
    }

    // Hitung jumlah produk available
    public function availableProductsCount()
    {
        return $this->products()->where('status', 'available')->count();
    }
}
