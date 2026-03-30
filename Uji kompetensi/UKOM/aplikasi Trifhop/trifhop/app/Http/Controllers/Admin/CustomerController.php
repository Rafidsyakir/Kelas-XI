<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Transaction;

class CustomerController extends Controller
{
    public function index()
    {
        $customers = User::where('role', 'customer')
            ->withCount(['transactions as orders_count'])
            ->latest()
            ->paginate(20);
        
        // Stats untuk dashboard customers
        $totalCustomers = User::where('role', 'customer')->count();
        $activeToday = User::where('role', 'customer')
            ->whereDate('created_at', today())
            ->count();
        $newThisMonth = User::where('role', 'customer')
            ->whereMonth('created_at', now()->month)
            ->whereYear('created_at', now()->year)
            ->count();
        $totalOrders = \App\Models\Transaction::count();

        return view('admin.customers.index', compact('customers', 'totalCustomers', 'activeToday', 'newThisMonth', 'totalOrders'));
    }

    public function show($id)
    {
        $customer = User::where('role', 'customer')->findOrFail($id);
        
        $orders = Transaction::with(['details.product'])
            ->where('user_id', $id)
            ->latest()
            ->paginate(10);

        $totalOrders = Transaction::where('user_id', $id)->count();
        $totalSpent = Transaction::where('user_id', $id)
            ->where('status', 'finished')
            ->sum('total_price');

        return view('admin.customers.show', compact('customer', 'orders', 'totalOrders', 'totalSpent'));
    }
}

