<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // 1. Tabel Users
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('email')->unique();
            $table->timestamp('email_verified_at')->nullable();
            $table->string('password');
            $table->enum('role', ['admin', 'customer'])->default('customer');
            $table->string('phone')->nullable();
            $table->text('address')->nullable();
            $table->rememberToken();
            $table->timestamps();
        });

        // 2. Password Reset Tokens
        Schema::create('password_reset_tokens', function (Blueprint $table) {
            $table->string('email')->primary();
            $table->string('token');
            $table->timestamp('created_at')->nullable();
        });

        // 3. Sessions
        Schema::create('sessions', function (Blueprint $table) {
            $table->string('id')->primary();
            $table->foreignId('user_id')->nullable()->index();
            $table->string('ip_address', 45)->nullable();
            $table->text('user_agent')->nullable();
            $table->longText('payload');
            $table->integer('last_activity')->index();
        });

        // 4. Categories
        Schema::create('categories', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('slug')->unique();
            $table->string('icon_url')->nullable();
            $table->timestamps();
        });

        // 5. Products (Enhanced Schema)
        Schema::create('products', function (Blueprint $table) {
            $table->id();
            $table->foreignId('category_id')->constrained('categories')->onDelete('cascade');
            $table->string('name');
            $table->text('description');
            $table->longText('specifications')->nullable();
            $table->integer('price'); // Harga dalam Rupiah (integer untuk presisi)
            $table->integer('stock')->default(0);
            $table->string('condition'); // new, like-new, good, fair
            $table->string('size')->nullable(); // S, M, L, XL, dll
            $table->string('color')->nullable();
            $table->string('brand')->nullable();
            $table->string('image_url');
            $table->json('additional_images')->nullable();
            $table->enum('status', ['available', 'sold_out', 'discontinued'])->default('available');
            $table->boolean('is_featured')->default(false);
            $table->integer('rating')->default(0);
            $table->integer('view_count')->default(0);
            $table->integer('purchase_count')->default(0);
            $table->timestamp('last_restocked_at')->nullable();
            $table->timestamps();
            $table->softDeletes();
            
            // Indexes
            $table->index('category_id');
            $table->index('status');
            $table->index('stock');
        });

        // 6. Transactions
        Schema::create('transactions', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->string('invoice_code')->unique();
            $table->decimal('total_price', 10, 2);
            $table->enum('status', ['pending', 'packed', 'sent', 'finished'])->default('pending');
            $table->string('customer_name');
            $table->string('customer_phone');
            $table->text('shipping_address');
            $table->timestamp('ordered_at');
            $table->timestamp('packed_at')->nullable();
            $table->timestamp('sent_at')->nullable();
            $table->timestamp('finished_at')->nullable();
            $table->timestamps();
        });

        // 7. Transaction Details
        Schema::create('transaction_details', function (Blueprint $table) {
            $table->id();
            $table->foreignId('transaction_id')->constrained('transactions')->onDelete('cascade');
            $table->foreignId('product_id')->constrained('products')->onDelete('cascade');
            $table->decimal('price_at_purchase', 10, 2);
            $table->integer('quantity')->default(1);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('transaction_details');
        Schema::dropIfExists('transactions');
        Schema::dropIfExists('products');
        Schema::dropIfExists('categories');
        Schema::dropIfExists('sessions');
        Schema::dropIfExists('password_reset_tokens');
        Schema::dropIfExists('users');
    }
};
