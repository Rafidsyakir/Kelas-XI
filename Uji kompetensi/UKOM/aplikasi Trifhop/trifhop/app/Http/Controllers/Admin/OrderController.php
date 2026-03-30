<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use Illuminate\Http\Request;

class OrderController extends Controller
{
    public function index(Request $request)
    {
        $query = Transaction::with(['user', 'details.product']);

        // Filter by status
        if ($request->has('status') && $request->status != 'all') {
            $query->where('status', $request->status);
        }

        $orders = $query->latest()->paginate(20);
        
        // Stats untuk dashboard orders
        $totalOrders = Transaction::count();
        $pendingOrders = Transaction::where('status', 'pending')->count();
        $completedOrders = Transaction::where('status', 'completed')->count();
        $totalRevenue = Transaction::sum('total_amount');

        return view('admin.orders.index', compact('orders', 'totalOrders', 'pendingOrders', 'completedOrders', 'totalRevenue'));
    }

    public function show($id)
    {
        $order = Transaction::with(['user', 'details.product'])->findOrFail($id);
        return view('admin.orders.show', compact('order'));
    }

    public function updateStatus(Request $request, $id)
    {
        $order = Transaction::findOrFail($id);

        $validated = $request->validate([
            'status' => 'required|in:pending,packed,sent,finished',
        ]);

        $order->updateStatus($validated['status']);

        return redirect()->route('admin.orders.show', $id)
            ->with('success', 'Order status updated successfully');
    }
}
