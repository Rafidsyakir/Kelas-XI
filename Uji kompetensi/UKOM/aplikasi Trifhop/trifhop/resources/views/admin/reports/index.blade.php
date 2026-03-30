@extends('admin.layouts.app')

@section('title', 'Reports')
@section('page-title', 'Sales Reports')
@section('page-description', 'View detailed analytics and insights')

@section('styles')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
@endsection

@section('content')
<div class="animate-fade-in-up">
    <!-- Period Selector -->
    <div class="glass-effect rounded-2xl p-6 mb-8">
        <div class="flex items-center justify-between">
            <div>
                <h2 class="text-xl font-bold text-white mb-1">Report Period</h2>
                <p class="text-slate-400 text-sm">Select date range for analytics</p>
            </div>
            <div class="flex items-center space-x-3">
                <input type="date" value="{{ request('start_date', date('Y-m-01')) }}" 
                    class="px-4 py-2 bg-slate-800/50 border border-slate-700 rounded-xl text-white focus:outline-none focus:border-blue-500 transition-all">
                <span class="text-slate-500">to</span>
                <input type="date" value="{{ request('end_date', date('Y-m-d')) }}" 
                    class="px-4 py-2 bg-slate-800/50 border border-slate-700 rounded-xl text-white focus:outline-none focus:border-blue-500 transition-all">
                <button class="px-6 py-2 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white rounded-xl font-semibold shadow-lg shadow-blue-500/30 transition-all">
                    <i class="fas fa-filter mr-2"></i>Apply
                </button>
            </div>
        </div>
    </div>

    <!-- Key Metrics -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="glass-effect rounded-2xl p-6 border-l-4 border-blue-500">
            <div class="flex items-center justify-between mb-3">
                <div class="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center">
                    <i class="fas fa-dollar-sign text-blue-400 text-xl"></i>
                </div>
                <span class="px-2 py-1 bg-green-500/20 text-green-400 rounded-lg text-xs font-bold">+12.5%</span>
            </div>
            <p class="text-slate-400 text-sm font-medium mb-1">Total Revenue</p>
            <h3 class="text-3xl font-bold text-white">Rp {{ number_format($totalRevenue, 0, ',', '.') }}</h3>
        </div>
        
        <div class="glass-effect rounded-2xl p-6 border-l-4 border-green-500">
            <div class="flex items-center justify-between mb-3">
                <div class="w-12 h-12 bg-green-500/20 rounded-xl flex items-center justify-center">
                    <i class="fas fa-shopping-cart text-green-400 text-xl"></i>
                </div>
                <span class="px-2 py-1 bg-green-500/20 text-green-400 rounded-lg text-xs font-bold">+8.3%</span>
            </div>
            <p class="text-slate-400 text-sm font-medium mb-1">Total Orders</p>
            <h3 class="text-3xl font-bold text-white">{{ number_format($totalOrders) }}</h3>
        </div>
        
        <div class="glass-effect rounded-2xl p-6 border-l-4 border-purple-500">
            <div class="flex items-center justify-between mb-3">
                <div class="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center">
                    <i class="fas fa-chart-line text-purple-400 text-xl"></i>
                </div>
                <span class="px-2 py-1 bg-purple-500/20 text-purple-400 rounded-lg text-xs font-bold">Avg</span>
            </div>
            <p class="text-slate-400 text-sm font-medium mb-1">Average Order</p>
            <h3 class="text-2xl font-bold text-white">Rp {{ number_format($totalOrders > 0 ? $totalRevenue / $totalOrders : 0, 0, ',', '.') }}</h3>
        </div>
        
        <div class="glass-effect rounded-2xl p-6 border-l-4 border-cyan-500">
            <div class="flex items-center justify-between mb-3">
                <div class="w-12 h-12 bg-cyan-500/20 rounded-xl flex items-center justify-center">
                    <i class="fas fa-box text-cyan-400 text-xl"></i>
                </div>
                <span class="px-2 py-1 bg-cyan-500/20 text-cyan-400 rounded-lg text-xs font-bold">Live</span>
            </div>
            <p class="text-slate-400 text-sm font-medium mb-1">Products Sold</p>
            <h3 class="text-3xl font-bold text-white">{{ number_format($productsSold) }}</h3>
        </div>
    </div>

    <!-- Charts Row -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        <!-- Sales Chart -->
        <div class="glass-effect rounded-2xl overflow-hidden">
            <div class="p-6 border-b border-slate-800">
                <h3 class="text-lg font-bold text-white">Sales Trend</h3>
                <p class="text-slate-400 text-sm">Monthly sales overview</p>
            </div>
            <div class="p-6">
                <canvas id="salesChart" height="300"></canvas>
            </div>
        </div>

        <!-- Category Distribution -->
        <div class="glass-effect rounded-2xl overflow-hidden">
            <div class="p-6 border-b border-slate-800">
                <h3 class="text-lg font-bold text-white">Category Performance</h3>
                <p class="text-slate-400 text-sm">Sales by category</p>
            </div>
            <div class="p-6">
                <canvas id="categoryChart" height="300"></canvas>
            </div>
        </div>
    </div>

    <!-- Top Products -->
    <div class="glass-effect rounded-2xl overflow-hidden">
        <div class="p-6 border-b border-slate-800">
            <h3 class="text-lg font-bold text-white">Top Selling Products</h3>
            <p class="text-slate-400 text-sm">Best performers this period</p>
        </div>
        <div class="p-6">
            <div class="space-y-4">
                @foreach($topProducts as $index => $product)
                <div class="flex items-center justify-between p-4 bg-slate-900/30 rounded-xl hover:bg-slate-900/50 transition-all">
                    <div class="flex items-center space-x-4">
                        <div class="w-12 h-12 bg-gradient-to-br {{ $index == 0 ? 'from-yellow-500 to-orange-500' : ($index == 1 ? 'from-slate-400 to-slate-500' : 'from-orange-600 to-orange-700') }} rounded-xl flex items-center justify-center shadow-lg font-bold text-white">
                            #{{ $index + 1 }}
                        </div>
                        <img src="{{ asset($product->image_url) }}" alt="{{ $product->name }}" class="w-14 h-14 rounded-lg object-cover border-2 border-slate-700">
                        <div>
                            <p class="font-semibold text-white">{{ $product->name }}</p>
                            <p class="text-sm text-slate-400">{{ $product->category->name }}</p>
                        </div>
                    </div>
                    <div class="text-right">
                        <p class="font-bold text-white text-lg">{{ $product->total_sold ?? 0 }} sold</p>
                        <p class="text-sm text-green-400">Rp {{ number_format($product->price * ($product->total_sold ?? 0), 0, ',', '.') }}</p>
                    </div>
                </div>
                @endforeach
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // Sales Chart
    const salesCtx = document.getElementById('salesChart').getContext('2d');
    const salesChart = new Chart(salesCtx, {
        type: 'line',
        data: {
            labels: {!! json_encode($salesByMonth->pluck('month')) !!},
            datasets: [{
                label: 'Revenue (Rp)',
                data: {!! json_encode($salesByMonth->pluck('total')) !!},
                borderColor: '#3b82f6',
                backgroundColor: function(context) {
                    const ctx = context.chart.ctx;
                    const gradient = ctx.createLinearGradient(0, 0, 0, 300);
                    gradient.addColorStop(0, 'rgba(59, 130, 246, 0.3)');
                    gradient.addColorStop(1, 'rgba(59, 130, 246, 0)');
                    return gradient;
                },
                fill: true,
                tension: 0.4,
                borderWidth: 3,
                pointBackgroundColor: '#3b82f6',
                pointBorderColor: '#1e293b',
                pointBorderWidth: 3,
                pointRadius: 6,
                pointHoverRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    titleColor: '#e2e8f0',
                    bodyColor: '#cbd5e1',
                    borderColor: '#334155',
                    borderWidth: 1,
                    padding: 12,
                    displayColors: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#1e293b' },
                    ticks: { color: '#94a3b8' }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: '#94a3b8' }
                }
            }
        }
    });

    // Category Chart
    const categoryCtx = document.getElementById('categoryChart').getContext('2d');
    const categoryChart = new Chart(categoryCtx, {
        type: 'doughnut',
        data: {
            labels: {!! json_encode($salesByCategory->pluck('name')) !!},
            datasets: [{
                data: {!! json_encode($salesByCategory->pluck('total')) !!},
                backgroundColor: [
                    '#3b82f6', '#10b981', '#f59e0b', '#ef4444', 
                    '#8b5cf6', '#06b6d4', '#ec4899', '#14b8a6'
                ],
                borderWidth: 3,
                borderColor: '#0f172a'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        color: '#cbd5e1',
                        padding: 15,
                        font: { size: 13 }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    titleColor: '#e2e8f0',
                    bodyColor: '#cbd5e1',
                    borderColor: '#334155',
                    borderWidth: 1,
                    padding: 12
                }
            }
        }
    });
</script>
@endsection
