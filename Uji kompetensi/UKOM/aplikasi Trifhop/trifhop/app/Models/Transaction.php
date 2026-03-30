<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;

class Transaction extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'invoice_code',
        'total_price',
        'status',
        'payment_status',
        'xendit_invoice_id',
        'xendit_external_id',
        'xendit_invoice_url',
        'midtrans_transaction_id',
        'midtrans_order_id',
        'midtrans_token',
        'midtrans_redirect_url',
        'midtrans_transaction_status',
        'paid_at',
        'customer_name',
        'customer_phone',
        'shipping_address',
        'ordered_at',
        'packed_at',
        'sent_at',
        'finished_at',
    ];

    protected $casts = [
        'total_price' => 'float',
        'paid_at' => 'datetime',
        'ordered_at' => 'datetime',
        'packed_at' => 'datetime',
        'sent_at' => 'datetime',
        'finished_at' => 'datetime',
    ];

    // Accessor untuk payment_url (alias untuk xendit_invoice_url)
    public function getPaymentUrlAttribute()
    {
        return $this->xendit_invoice_url;
    }

    public function setPaymentUrlAttribute($value)
    {
        $this->attributes['xendit_invoice_url'] = $value;
    }

    // Accessor untuk external_id (alias untuk xendit_external_id)
    public function getExternalIdAttribute()
    {
        return $this->xendit_external_id;
    }

    public function setExternalIdAttribute($value)
    {
        $this->attributes['xendit_external_id'] = $value;
    }

    // Relasi ke user
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    // Relasi ke transaction details
    public function details()
    {
        return $this->hasMany(TransactionDetail::class);
    }

    // Alias for items
    public function items()
    {
        return $this->details();
    }

    // Generate invoice code
    public static function generateInvoiceCode()
    {
        $prefix = 'TRF';
        $date = Carbon::now()->format('Ymd');
        $random = strtoupper(substr(md5(uniqid()), 0, 6));
        return $prefix . $date . $random;
    }

    // Update status
    public function updateStatus($status)
    {
        $timestampField = $status . '_at';
        $this->update([
            'status' => $status,
            $timestampField => Carbon::now(),
        ]);
    }

    // Scope untuk pending orders
    public function scopePending($query)
    {
        return $query->where('status', 'pending');
    }

    // Scope untuk finished orders
    public function scopeFinished($query)
    {
        return $query->where('status', 'finished');
    }
}
