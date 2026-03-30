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
            $table->string('payment_status')->default('UNPAID')->after('status');
            $table->string('xendit_invoice_id')->nullable()->after('payment_status');
            $table->string('xendit_external_id')->nullable()->index()->after('xendit_invoice_id');
            $table->text('xendit_invoice_url')->nullable()->after('xendit_external_id');
            $table->timestamp('paid_at')->nullable()->after('finished_at');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // SQLite doesn't support dropping columns properly
        // This migration is intended to be permanent after successful run
        // If reverting is needed, rebuild the database instead
        // Schema::table('transactions', function (Blueprint $table) {
        //     $table->dropColumn([
        //         'payment_status',
        //         'xendit_invoice_id',
        //         'xendit_external_id',
        //         'xendit_invoice_url',
        //         'paid_at',
        //     ]);
        // });
    }
};
