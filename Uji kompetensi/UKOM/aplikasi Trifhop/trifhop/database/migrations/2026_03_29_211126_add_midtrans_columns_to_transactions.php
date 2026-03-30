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
        Schema::table('transactions', function (Blueprint $table) {
            // Add Midtrans columns if they don't already exist
            if (!Schema::hasColumn('transactions', 'midtrans_transaction_id')) {
                $table->string('midtrans_transaction_id')->nullable()->comment('Midtrans Order ID');
            }
            
            if (!Schema::hasColumn('transactions', 'midtrans_order_id')) {
                $table->string('midtrans_order_id')->nullable()->comment('Midtrans Order ID (Snap)');
            }
            
            if (!Schema::hasColumn('transactions', 'midtrans_token')) {
                $table->text('midtrans_token')->nullable()->comment('Midtrans Snap Token');
            }
            
            if (!Schema::hasColumn('transactions', 'midtrans_redirect_url')) {
                $table->text('midtrans_redirect_url')->nullable()->comment('Midtrans payment redirect URL');
            }
            
            if (!Schema::hasColumn('transactions', 'midtrans_transaction_status')) {
                $table->string('midtrans_transaction_status')->nullable()->comment('Midtrans transaction status: settlement, pending, deny, cancel, expire, refund');
            }

            // Optional: Mark old Xendit columns for deprecation or drop them
            // if (Schema::hasColumn('transactions', 'xendit_invoice_id')) {
            //     $table->dropColumn('xendit_invoice_id');
            // }
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('transactions', function (Blueprint $table) {
            if (Schema::hasColumn('transactions', 'midtrans_transaction_id')) {
                $table->dropColumn('midtrans_transaction_id');
            }
            if (Schema::hasColumn('transactions', 'midtrans_order_id')) {
                $table->dropColumn('midtrans_order_id');
            }
            if (Schema::hasColumn('transactions', 'midtrans_token')) {
                $table->dropColumn('midtrans_token');
            }
            if (Schema::hasColumn('transactions', 'midtrans_redirect_url')) {
                $table->dropColumn('midtrans_redirect_url');
            }
            if (Schema::hasColumn('transactions', 'midtrans_transaction_status')) {
                $table->dropColumn('midtrans_transaction_status');
            }
        });
    }
};
