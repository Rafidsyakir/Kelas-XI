<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Product;
use App\Models\Transaction;
use App\Models\User;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        // Total Revenue dari finished transactions
        $totalRevenue = Transaction::where('status', 'finished')
            ->sum('total_price');

        // Total Orders (semua transaksi)
        $totalOrders = Transaction::count();

        // Total produk terjual (status sold)
        $soldProducts = Product::where('status', 'sold')->count();

        // Total stok pakaian yang masih available
        $availableStock = Product::where('status', 'available')->sum('stock');

        // Total Customers
        $totalCustomers = User::where('role', 'customer')->count();

        // Pending orders
        $pendingOrders = Transaction::where('status', 'pending')->count();

        // Sales Analytics - Monthly untuk 6 bulan terakhir (SQLite compatible)
        $salesByMonth = Transaction::select(
                DB::raw("strftime('%Y-%m', ordered_at) as month"),
                DB::raw('SUM(total_price) as total'),
                DB::raw('COUNT(*) as count')
            )
            ->where('ordered_at', '>=', now()->subMonths(6))
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        // Recent Transactions (10 terakhir)
        $recentTransactions = Transaction::with(['user', 'details.product'])
            ->latest()
            ->take(10)
            ->get();

        // Top Selling Products
        $topProducts = Product::with('category')
            ->select('products.*', DB::raw('COUNT(transaction_details.id) as sales_count'))
            ->join('transaction_details', 'products.id', '=', 'transaction_details.product_id')
            ->join('transactions', 'transaction_details.transaction_id', '=', 'transactions.id')
            ->where('transactions.status', 'finished')
            ->groupBy('products.id')
            ->orderBy('sales_count', 'desc')
            ->take(5)
            ->get();

        return view('admin.dashboard', compact(
            'totalRevenue',
            'totalOrders',
            'soldProducts',
            'availableStock',
            'totalCustomers',
            'pendingOrders',
            'salesByMonth',
            'recentTransactions',
            'topProducts'
        ));
    }
}
