<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

/**
 * FASE 1.3: Tabel Order Items
 * 
 * Struktur:
 * - id: Primary Key
 * - order_id: Foreign Key -> orders (order yang berisi item ini)
 * - product_id: Foreign Key -> products (produk apa yang dipesan)
 * - quantity: Jumlah yang dipesan
 * - price_at_purchase: Harga saat pembelian (bisa berbeda dari harga sekarang)
 * - subtotal: Harga x Quantity
 * - created_at, updated_at: Timestamps
 * 
 * Relationship:
 * - Belongs to Order (N:1)
 * - Belongs to Product (N:1)
 * 
 * Cara Kerja:
 * User order 2 produk berbeda:
 * - Order ID 1 → OrderItem 1 (Product 5, qty 2) + OrderItem 2 (Product 10, qty 1)
 * - ini cara kita tracking apa aja yang dibeli dalam satu transaksi
 */
return new class extends Migration
{
    public function up(): void
    {
        Schema::create('order_items', function (Blueprint $table) {
            // PRIMARY KEY
            $table->id();

            // RELATIONSHIPS (Composite key)
            $table->unsignedBigInteger('order_id');
            $table->foreign('order_id')
                ->references('id')
                ->on('orders')
                ->onDelete('cascade'); // Jika order dihapus, items juga terhapus

            $table->unsignedBigInteger('product_id');
            $table->foreign('product_id')
                ->references('id')
                ->on('products')
                ->onDelete('restrict'); // Jika product dihapus, error (jangan rusak order history)

            // ITEM DETAILS
            $table->integer('quantity'); // Berapa banyak produk ini dipesan
            
            // PRICING
            $table->integer('price_at_purchase'); // Harga produk saat dibeli (untuk history)
            $table->integer('subtotal'); // = price_at_purchase * quantity

            // ITEM ATTRIBUTES (Snapshot dari produk saat dibeli)
            $table->string('product_name', 255); // Nama produk saat dibeli
            $table->string('product_size', 50); // Ukuran saat dibeli
            $table->string('product_condition', 50); // Kondisi saat dibeli
            $table->string('product_image_url'); // Gambar saat dibeli (snapshot)

            // FULFILLMENT
            $table->enum('item_status', ['pending', 'packed', 'shipped', 'delivered', 'returned', 'cancelled'])
                ->default('pending');

            // TIMESTAMPS
            $table->timestamps();

            // INDEXES
            $table->index('order_id');
            $table->index('product_id');
            $table->index('item_status');

            // UNIQUE CONSTRAINT (1 product hanya 1x dalam 1 order, atau tidak?)
            // Bisa di-disable jika user boleh order produk sama berkali-kali dalam satu order
            // $table->unique(['order_id', 'product_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('order_items');
    }
};
