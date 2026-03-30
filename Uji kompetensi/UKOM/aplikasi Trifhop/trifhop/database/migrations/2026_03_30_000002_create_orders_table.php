<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

/**
 * FASE 1.2: Tabel Orders (Fokus pada Payment/Checkout)
 * 
 * Struktur:
 * - id: Primary Key
 * - user_id: Foreign Key -> users (pembeli)
 * - total_price: Total harga dalam Rupiah
 * - status: pending, paid, failed, cancelled
 * - payment_method: midtrans, bank_transfer, manual
 * - snap_token: Token dari Midtrans Snap API
 * - payment_url: URL pembayaran dari Midtrans
 * - customer_name, customer_phone, shipping_address: Detail pengiriman
 * - shipping_fee: Biaya pengiriman
 * - midtrans_transaction_id: Transaction ID dari Midtrans (untuk webhook)
 * - paid_at: Timestamp kapan pembayaran berhasil
 * - created_at, updated_at: Timestamps
 * 
 * Relationship:
 * - Belongs to User (N:1)
 * - Has many OrderItems (1:N)
 */
return new class extends Migration
{
    public function up(): void
    {
        Schema::create('orders', function (Blueprint $table) {
            // PRIMARY KEY
            $table->id();

            // RELATIONSHIPS
            $table->unsignedBigInteger('user_id');
            $table->foreign('user_id')
                ->references('id')
                ->on('users')
                ->onDelete('cascade');

            // ORDER INFORMATION
            $table->string('order_code', 50)->unique(); // Kode order unik (e.g., ORD-20260330-001)
            $table->text('description')->nullable(); // Catatan tambahan

            // PRICING
            $table->integer('subtotal'); // Total harga produk (sebelum ongkir)
            $table->integer('shipping_fee')->default(0); // Biaya pengiriman
            $table->integer('discount_amount')->default(0); // Diskon (jika ada)
            $table->integer('total_price'); // Total = subtotal + shipping_fee - discount_amount

            // PAYMENT INFORMATION
            $table->enum('payment_method', ['midtrans', 'bank_transfer', 'manual'])
                ->default('midtrans');
            
            $table->enum('payment_status', ['pending', 'paid', 'failed', 'cancelled', 'waiting'])
                ->default('pending');

            // Midtrans Integration
            $table->string('snap_token', 500)->nullable(); // Token dari Midtrans Snap API
            $table->string('payment_url', 1000)->nullable(); // URL pembayaran dari Midtrans
            $table->string('midtrans_transaction_id', 100)->nullable(); // Transaction ID dari Midtrans (untuk webhook)
            $table->json('midtrans_response')->nullable(); // Simpan response dari Midtrans (debug)

            // CUSTOMER INFORMATION (Bisa berbeda dari user profile)
            $table->string('customer_name', 255);
            $table->string('customer_phone', 20);
            $table->text('shipping_address');
            $table->string('shipping_province')->nullable();
            $table->string('shipping_city')->nullable();
            $table->string('shipping_postal_code', 10)->nullable();

            // SHIPPING & DELIVERY
            $table->enum('shipping_status', ['pending', 'ready', 'shipped', 'delivered', 'returned'])
                ->default('pending');
            $table->string('shipping_provider')->nullable(); // JNE, TIKI, POS, etc
            $table->string('tracking_number', 100)->nullable(); // Nomor resi

            // TIMESTAMPS
            $table->timestamp('paid_at')->nullable(); // Kapan dibayar
            $table->timestamp('shipped_at')->nullable(); // Kapan dikirim
            $table->timestamp('delivered_at')->nullable(); // Kapan diterima
            $table->timestamps(); // created_at, updated_at

            // META
            $table->boolean('is_urgent')->default(false); // Pengiriman express?
            $table->text('admin_notes')->nullable(); // Catatan admin

            // INDEXES
            $table->index('user_id');
            $table->index('payment_status');
            $table->index('shipping_status');
            $table->index('order_code');
            $table->index('midtrans_transaction_id');
            $table->index('created_at');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('orders');
    }
};
