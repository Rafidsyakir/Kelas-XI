<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Product;
use App\Models\Transaction;
use App\Models\TransactionDetail;
use Illuminate\Support\Facades\DB;

class ReportController extends Controller
{
    public function index()
    {
        // Total Revenue (finished orders)
        $totalRevenue = Transaction::where('status', 'finished')
            ->sum('total_price');

        // Total Orders
        $totalOrders = Transaction::count();

        // Total Products Sold
        $productsSold = TransactionDetail::whereHas('transaction', function ($query) {
            $query->where('status', 'finished');
        })->sum('quantity');

        // Average Order Value
        $averageOrder = $totalOrders > 0 ? $totalRevenue / $totalOrders : 0;

        // Monthly Sales Trend (12 bulan terakhir) - SQLite compatible
        $salesByMonth = Transaction::select(
                DB::raw("strftime('%Y-%m', ordered_at) as month"),
                DB::raw('SUM(total_price) as total'),
                DB::raw('COUNT(*) as orders')
            )
            ->where('ordered_at', '>=', now()->subMonths(12))
            ->where('status', 'finished')
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        // Sales by Category
        $salesByCategory = DB::table('categories')
            ->join('products', 'categories.id', '=', 'products.category_id')
            ->join('transaction_details', 'products.id', '=', 'transaction_details.product_id')
            ->join('transactions', 'transaction_details.transaction_id', '=', 'transactions.id')
            ->where('transactions.status', 'finished')
            ->select('categories.name', DB::raw('SUM(transaction_details.price_at_purchase * transaction_details.quantity) as total'))
            ->groupBy('categories.id', 'categories.name')
            ->get();

        // Top Selling Products with category
        $topProducts = Product::with('category')
            ->select('products.*', 
                DB::raw('COUNT(transaction_details.id) as sales_count'),
                DB::raw('SUM(transaction_details.quantity) as total_sold')
            )
            ->join('transaction_details', 'products.id', '=', 'transaction_details.product_id')
            ->join('transactions', 'transaction_details.transaction_id', '=', 'transactions.id')
            ->where('transactions.status', 'finished')
            ->groupBy('products.id')
            ->orderBy('sales_count', 'desc')
            ->take(10)
            ->get();

        // Recent Transactions
        $recentTransactions = Transaction::with(['user', 'details.product'])
            ->latest()
            ->take(15)
            ->get();

        return view('admin.reports.index', compact(
            'totalRevenue',
            'totalOrders',
            'productsSold',
            'salesByMonth',
            'salesByCategory',
            'topProducts'
        ));
    }
}

