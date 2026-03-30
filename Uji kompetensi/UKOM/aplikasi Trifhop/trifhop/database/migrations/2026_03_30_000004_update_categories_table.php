<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

/**
 * FASE 1.4: Penyempurnaan Tabel Categories
 * 
 * Menambahkan kolom yang mungkin sudah ada atau perlu di-update
 */
return new class extends Migration
{
    public function up(): void
    {
        // Cek apakah tabel sudah ada
        if (!Schema::hasTable('categories')) {
            Schema::create('categories', function (Blueprint $table) {
                $table->id();
                $table->string('name', 255)->unique();
                $table->string('slug', 255)->unique();
                $table->text('description')->nullable();
                $table->string('icon_url')->nullable();
                $table->boolean('is_active')->default(true);
                $table->integer('order')->default(0);
                $table->timestamps();

                $table->index('slug');
                $table->index('is_active');
            });
        } else {
            // Jika tabel sudah ada, tambahkan kolom yang mungkin kurang
            if (!Schema::hasColumn('categories', 'description')) {
                Schema::table('categories', function (Blueprint $table) {
                    $table->text('description')->nullable()->after('slug');
                });
            }

            if (!Schema::hasColumn('categories', 'is_active')) {
                Schema::table('categories', function (Blueprint $table) {
                    $table->boolean('is_active')->default(true)->after('icon_url');
                });
            }

            if (!Schema::hasColumn('categories', 'order')) {
                Schema::table('categories', function (Blueprint $table) {
                    $table->integer('order')->default(0)->after('is_active');
                });
            }
        }
    }

    public function down(): void
    {
        Schema::dropIfExists('categories');
    }
};
