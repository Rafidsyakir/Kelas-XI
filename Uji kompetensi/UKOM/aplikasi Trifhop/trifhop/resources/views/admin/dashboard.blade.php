@extends('layouts.admin')

@section('title', 'Dashboard Overview')

@section('content')
<div class="flex items-end justify-between mb-8 animate-fade-in-up">
    <div>
        <h2 class="text-3xl font-bold text-white tracking-tight">Dashboard Overview</h2>
        <p class="text-slate-400 text-sm mt-2">Welcome back! Here's what's happening with your store today.</p>
    </div>
    <button onclick="location.reload()" class="flex items-center gap-2 bg-surface-dark border border-slate-700 px-5 py-2.5 rounded-xl text-sm font-semibold text-slate-300 hover:bg-slate-800 hover:text-white hover:border-slate-600 transition-all duration-300">
        <span class="material-symbols-outlined text-lg">refresh</span>
        Refresh
    </button>
</div>

<!-- Stats Cards -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
    <!-- Total Revenue -->
    <div class="bg-surface-dark p-6 rounded-2xl border border-slate-700/50 hover:border-primary/30 shadow-sm hover:shadow-glow transition-all duration-300 group">
        <div class="flex justify-between items-start mb-4">
            <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-white transition-all duration-300">
                <span class="material-symbols-outlined text-2xl">account_balance_wallet</span>
            </div>
            <span class="text-emerald-400 text-[10px] font-bold bg-emerald-400/10 px-2 py-1 rounded-full border border-emerald-400/20">Active</span>
        </div>
        <p class="text-slate-400 text-xs font-bold uppercase tracking-wider">Total Income</p>
        <h3 class="text-2xl font-bold text-white mt-1">Rp {{ number_format($totalRevenue, 0, ',', '.') }}</h3>
    </div>

    <!-- Total Items Sold -->
    <div class="bg-surface-dark p-6 rounded-2xl border border-slate-700/50 hover:border-primary/30 shadow-sm hover:shadow-glow transition-all duration-300 group">
        <div class="flex justify-between items-start mb-4">
            <div class="w-12 h-12 bg-indigo-500/10 rounded-xl flex items-center justify-center text-indigo-400 group-hover:bg-indigo-500 group-hover:text-white transition-all duration-300">
                <span class="material-symbols-outlined text-2xl">shopping_cart_checkout</span>
            </div>
            <span class="text-emerald-400 text-[10px] font-bold bg-emerald-400/10 px-2 py-1 rounded-full border border-emerald-400/20">+{{ $soldProducts }}</span>
        </div>
        <p class="text-slate-400 text-xs font-bold uppercase tracking-wider">Items Sold</p>
        <h3 class="text-2xl font-bold text-white mt-1">{{ number_format($soldProducts) }}</h3>
    </div>

    <!-- Ready Stock -->
    <div class="bg-surface-dark p-6 rounded-2xl border border-slate-700/50 hover:border-primary/30 shadow-sm hover:shadow-glow transition-all duration-300 group">
        <div class="flex justify-between items-start mb-4">
            <div class="w-12 h-12 bg-amber-500/10 rounded-xl flex items-center justify-center text-amber-400 group-hover:bg-amber-500 group-hover:text-white transition-all duration-300">
                <span class="material-symbols-outlined text-2xl">inventory</span>
            </div>
            @if($availableStock < 100)
            <span class="text-amber-400 text-[10px] font-bold bg-amber-400/10 px-2 py-1 rounded-full border border-amber-400/20">Low Stock</span>
            @else
            <span class="text-emerald-400 text-[10px] font-bold bg-emerald-400/10 px-2 py-1 rounded-full border border-emerald-400/20">Good</span>
            @endif
        </div>
        <p class="text-slate-400 text-xs font-bold uppercase tracking-wider">Ready Stock</p>
        <h3 class="text-2xl font-bold text-white mt-1">{{ number_format($availableStock) }}</h3>
    </div>

    <!-- Pending Orders -->
    <div class="bg-surface-dark p-6 rounded-2xl border border-slate-700/50 hover:border-primary/30 shadow-sm hover:shadow-glow transition-all duration-300 group">
        <div class="flex justify-between items-start mb-4">
            <div class="w-12 h-12 bg-rose-500/10 rounded-xl flex items-center justify-center text-rose-400 group-hover:bg-rose-500 group-hover:text-white transition-all duration-300">
                <span class="material-symbols-outlined text-2xl">pending_actions</span>
            </div>
            @if($pendingOrders > 0)
            <span class="text-rose-400 text-[10px] font-bold bg-rose-400/10 px-2 py-1 rounded-full border border-rose-400/20">Needs Action</span>
            @else
            <span class="text-emerald-400 text-[10px] font-bold bg-emerald-400/10 px-2 py-1 rounded-full border border-emerald-400/ 20">All Clear</span>
            @endif
        </div>
        <p class="text-slate-400 text-xs font-bold uppercase tracking-wider">Pending Orders</p>
        <h3 class="text-2xl font-bold text-white mt-1">{{ number_format($pendingOrders) }}</h3>
    </div>
</div>

<!-- Sales Analytics & Recent Activity -->
<div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-8">
    <!-- Sales Analytics Chart -->
    <div class="lg:col-span-2 bg-surface-dark rounded-2xl border border-slate-700/50 p-6 shadow-sm">
        <div class="flex items-center justify-between mb-8">
            <div>
                <h3 class="text-lg font-bold text-white">Sales Analytics</h3>
                <p class="text-sm text-slate-400">Monthly revenue growth & performance</p>
            </div>
            <div class="flex gap-2 p-1 bg-slate-800 rounded-xl">
                <button class="px-4 py-1.5 text-xs font-bold rounded-lg bg-primary text-white shadow-lg shadow-primary/20">Revenue</button>
                <button class="px-4 py-1.5 text-xs font-bold rounded-lg text-slate-400 hover:text-white transition-colors">Orders</button>
            </div>
        </div>
        <div class="h-64 w-full relative">
            <canvas id="salesChart"></canvas>
        </div>
    </div>

    <!-- Recent Activity -->
    <div class="bg-surface-dark rounded-2xl border border-slate-700/50 p-6 shadow-sm">
        <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-bold text-white">Recent Activity</h3>
            <a href="{{ route('admin.orders.index') }}" class="text-primary text-xs font-bold hover:brightness-125 hover:underline">View All</a>
        </div>
        <div class="space-y-6">
            @if($recentTransactions->count() > 0)
                @foreach($recentTransactions->take(4) as $transaction)
                <div class="flex gap-4 group">
                    <div class="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center text-primary shrink-0 border border-slate-700 group-hover:border-primary/50 transition-colors">
                        <span class="material-symbols-outlined text-xl">shopping_cart</span>
                    </div>
                    <div class="flex-1">
                        <p class="text-sm font-semibold text-white">New order #{{ $transaction->invoice_number }}</p>
                        <p class="text-xs text-slate-500 mt-0.5">{{ $transaction->ordered_at->diffForHumans() }}</p>
                        <p class="text-xs text-primary mt-1">Rp {{ number_format($transaction->total_price, 0, ',', '.') }}</p>
                    </div>
                </div>
                @endforeach
            @else
                <div class="text-center py-8 text-slate-500">
                    <span class="material-symbols-outlined text-4xl mb-2">inbox</span>
                    <p class="text-sm">No recent activity</p>
                </div>
            @endif
        </div>
    </div>
</div>

<!-- Recent Transactions Table -->
<div class="bg-surface-dark rounded-2xl border border-slate-700/50 shadow-sm overflow-hidden mt-8">
    <div class="p-6 border-b border-slate-700 flex items-center justify-between bg-slate-800/30">
        <h3 class="text-lg font-bold text-white">Recent Transactions</h3>
        <div class="flex gap-2">
            <a href="{{ route('admin.orders.index') }}" class="bg-slate-800 border border-slate-700 px-4 py-1.5 rounded-lg text-xs font-bold text-slate-300 hover:text-white hover:bg-slate-700 transition-all">View All</a>
        </div>
    </div>
    <div class="overflow-x-auto">
        <table class="w-full text-left">
            <thead class="bg-slate-800/50">
                <tr>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Transaction ID</th>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Date</th>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Customer</th>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Amount</th>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Status</th>
                    <th class="px-6 py-4 text-[10px] font-bold text-slate-500 uppercase tracking-widest">Action</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-700/50">
                @forelse($recentTransactions as $transaction)
                <tr class="hover:bg-slate-700/30 transition-colors">
                    <td class="px-6 py-4 text-sm font-bold text-white">#{{ $transaction->invoice_number }}</td>
                    <td class="px-6 py-4 text-sm text-slate-400">{{ $transaction->ordered_at->format('M d, Y') }}</td>
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-2">
                            <div class="w-7 h-7 rounded-full bg-primary/20 flex items-center justify-center text-[10px] font-bold text-primary border border-primary/30">
                                {{ strtoupper(substr($transaction->user->name ?? 'U', 0, 2)) }}
                            </div>
                            <span class="text-sm font-medium text-slate-300">{{ $transaction->user->name ?? 'Unknown' }}</span>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-sm font-bold text-white">Rp {{ number_format($transaction->total_price, 0, ',', '.') }}</td>
                    <td class="px-6 py-4">
                        @if($transaction->status === 'finished')
                            <span class="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider">Completed</span>
                        @elseif($transaction->status === 'pending')
                            <span class="bg-amber-500/10 text-amber-400 border border-amber-500/20 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider">Pending</span>
                        @elseif($transaction->status === 'cancelled')
                            <span class="bg-rose-500/10 text-rose-400 border border-rose-500/20 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider">Cancelled</span>
                        @else
                            <span class="bg-blue-500/10 text-blue-400 border border-blue-500/20 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider">{{ ucfirst($transaction->status) }}</span>
                        @endif
                    </td>
                    <td class="px-6 py-4">
                        <a href="{{ route('admin.orders.show', $transaction->id) }}" class="text-slate-500 hover:text-primary transition-colors">
                            <span class="material-symbols-outlined text-lg">visibility</span>
                        </a>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-8 text-center text-slate-500">
                        <span class="material-symbols-outlined text-4xl mb-2 block">receipt_long</span>
                        <p>No transactions yet</p>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
@endsection

@push('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('salesChart');
    if (ctx) {
        const salesData = @json($salesByMonth);
        
        const labels = salesData.map(item => {
            const date = new Date(item.month + '-01');
            return date.toLocaleDateString('en-US', { month: 'short' });
        });
        
        const data = salesData.map(item => item.total);
        
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Revenue',
                    data: data,
                    borderColor: '#137FEC',
                    backgroundColor: 'rgba(19, 127, 236, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#137FEC',
                    pointBorderColor: '#1E293B',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: '#1E293B',
                        titleColor: '#fff',
                        bodyColor: '#94a3b8',
                        borderColor: '#334155',
                        borderWidth: 1,
                        padding: 12,
                        displayColors: false,
                        callbacks: {
                            label: function(context) {
                                return 'Rp ' + context.parsed.y.toLocaleString('id-ID');
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#334155',
                            drawBorder: false
                        },
                        ticks: {
                            color: '#64748b',
                            callback: function(value) {
                                return 'Rp ' + (value / 1000) + 'k';
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            color: '#64748b'
                        }
                    }
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });
    }
});
</script>
@endpush
