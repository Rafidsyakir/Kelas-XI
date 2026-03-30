@extends('layouts.admin')

@section('title', 'Orders Management')

@section('content')
<div class="flex flex-wrap justify-between items-end gap-6 mb-8">
    <div class="flex flex-col gap-2">
        <h1 class="text-white text-3xl font-extrabold tracking-tight">Orders Management</h1>
        <p class="text-slate-400 text-base">Effortlessly track, update, and manage all your customer transactions in one place.</p>
    </div>
    <div class="flex gap-3">
        <button onclick="window.print()" class="flex items-center gap-2 rounded-lg h-10 px-4 bg-gray-800 border border-border-dark text-slate-300 text-sm font-semibold hover:bg-gray-700 transition-colors">
            <span class="material-symbols-outlined text-lg">download</span>
            <span>Export CSV</span>
        </button>
        <button class="flex items-center gap-2 rounded-lg h-10 px-4 bg-gray-800 border border-border-dark text-slate-300 text-sm font-semibold hover:bg-gray-700 transition-colors">
            <span class="material-symbols-outlined text-lg">filter_list</span>
            <span>Filters</span>
        </button>
    </div>
</div>

<!-- Filter Tabs -->
<div class="flex gap-3 mb-6 overflow-x-auto pb-2 scrollbar-hide">
    <a href="{{ route('admin.orders.index') }}" class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-full {{ !request('status') ? 'bg-primary text-white' : 'bg-gray-800 text-slate-300 border border-border-dark hover:border-primary' }} px-5 text-sm font-bold transition-colors">
        All Orders <span class="{{ !request('status') ? 'bg-white/20' : 'bg-gray-700' }} px-1.5 rounded text-xs text-slate-400">{{ $totalOrders }}</span>
    </a>
    <a href="{{ route('admin.orders.index', ['status' => 'pending']) }}" class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-full {{ request('status') === 'pending' ? 'bg-primary text-white' : 'bg-gray-800 text-slate-300 border border-border-dark hover:border-primary' }} px-5 text-sm font-medium transition-colors">
        Pending <span class="{{ request('status') === 'pending' ? 'bg-white/20' : 'bg-gray-700' }} px-1.5 rounded text-xs text-slate-400">{{ $pendingOrders }}</span>
    </a>
    <a href="{{ route('admin.orders.index', ['status' => 'finished']) }}" class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-full {{ request('status') === 'finished' ? 'bg-primary text-white' : 'bg-gray-800 text-slate-300 border border-border-dark hover:border-primary' }} px-5 text-sm font-medium transition-colors">
        Completed <span class="{{ request('status') === 'finished' ? 'bg-white/20' : 'bg-gray-700' }} px-1.5 rounded text-xs text-slate-400">{{ $completedOrders }}</span>
    </a>
</div>

<!-- Orders Table -->
<div class="bg-surface-dark rounded-xl shadow-2xl border border-slate-800 overflow-hidden">
    <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
            <thead>
                <tr class="bg-gray-900/80 border-b border-border-dark">
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider">Invoice ID</th>
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider">Customer Name</th>
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider">Shipping Address</th>
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider">Total Amount</th>
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider">Status</th>
                    <th class="px-6 py-4 text-slate-400 text-xs font-bold uppercase tracking-wider text-right">Actions</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-border-dark/50">
                @forelse($orders as $order)
                <tr class="hover:bg-primary/5 transition-colors group">
                    <td class="px-6 py-5">
                        <span class="text-primary font-bold">#{{ $order->invoice_number }}</span>
                    </td>
                    <td class="px-6 py-5">
                        <div class="flex items-center gap-3">
                            <div class="size-8 rounded-full bg-primary/20 text-primary flex items-center justify-center text-xs font-bold border border-primary/30">
                                {{ strtoupper(substr($order->user->name ?? 'U', 0, 2)) }}
                            </div>
                            <span class="font-semibold text-slate-100">{{ $order->user->name ?? 'Unknown' }}</span>
                        </div>
                    </td>
                    <td class="px-6 py-5">
                        <span class="text-slate-400 text-sm">{{ Str::limit($order->shipping_address ?? 'No address', 40) }}</span>
                    </td>
                    <td class="px-6 py-5">
                        <span class="font-mono font-bold text-slate-100">Rp {{ number_format($order->total_price, 0, ',', '.') }}</span>
                    </td>
                    <td class="px-6 py-5">
                        @if($order->status === 'finished')
                            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-950/40 text-emerald-400 border border-emerald-500/30">
                                <span class="size-1.5 rounded-full bg-emerald-400"></span>
                                Completed
                            </span>
                        @elseif($order->status === 'pending')
                            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-950/40 text-amber-400 border border-amber-500/30">
                                <span class="size-1.5 rounded-full bg-amber-400 animate-pulse"></span>
                                Pending
                            </span>
                        @elseif($order->status === 'cancelled')
                            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-red-950/40 text-red-400 border border-red-500/30">
                                <span class="size-1.5 rounded-full bg-red-400"></span>
                                Cancelled
                            </span>
                        @else
                            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-sky-950/40 text-sky-400 border border-sky-500/30">
                                <span class="size-1.5 rounded-full bg-sky-400"></span>
                                {{ ucfirst($order->status) }}
                            </span>
                        @endif
                    </td>
                    <td class="px-6 py-5 text-right">
                        <div class="flex justify-end gap-2">
                            <a href="{{ route('admin.orders.show', $order) }}" class="p-2 rounded-lg text-slate-500 hover:text-primary hover:bg-primary/20 transition-all" title="View">
                                <span class="material-symbols-outlined text-lg">visibility</span>
                            </a>
                            @if($order->status === 'pending')
                            <form action="{{ route('admin.orders.update-status', $order) }}" method="POST" class="inline">
                                @csrf
                                @method('PATCH')
                                <input type="hidden" name="status" value="finished">
                                <button type="submit" class="p-2 rounded-lg text-slate-500 hover:text-emerald-400 hover:bg-emerald-500/10 transition-all" title="Complete Order">
                                    <span class="material-symbols-outlined text-lg">check_circle</span>
                                </button>
                            </form>
                            @endif
                        </div>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-slate-500">
                        <span class="material-symbols-outlined text-5xl mb-3 block">shopping_cart</span>
                        <p class="text-lg font-semibold">No orders found</p>
                        <p class="text-sm mt-1">Orders will appear here once customers make purchases</p>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
    
    @if($orders->hasPages())
    <div class="px-6 py-4 flex items-center justify-between border-t border-border-dark bg-gray-900/50">
        <p class="text-sm text-slate-500">
            Showing <span class="text-slate-300 font-medium">{{ $orders->firstItem() }}</span> to 
            <span class="text-slate-300 font-medium">{{ $orders->lastItem() }}</span> of 
            <span class="text-slate-300 font-medium">{{ $orders->total() }}</span> orders
        </p>
        <div class="flex items-center gap-2">
            @if ($orders->onFirstPage())
                <button class="p-2 rounded border border-border-dark bg-gray-800 text-slate-500" disabled>
                    <span class="material-symbols-outlined text-lg leading-none">chevron_left</span>
                </button>
            @else
                <a href="{{ $orders->previousPageUrl() }}" class="p-2 rounded border border-border-dark bg-gray-800 text-slate-500 hover:text-slate-200 transition-colors">
                    <span class="material-symbols-outlined text-lg leading-none">chevron_left</span>
                </a>
            @endif

            @foreach ($orders->getUrlRange(1, min(5, $orders->lastPage())) as $page => $url)
                @if ($page == $orders->currentPage())
                    <button class="size-8 rounded flex items-center justify-center bg-primary text-white text-sm font-bold shadow-lg shadow-primary/20">{{ $page }}</button>
                @else
                    <a href="{{ $url }}" class="size-8 rounded flex items-center justify-center bg-gray-800 text-slate-400 text-sm font-medium hover:bg-gray-700 transition-colors border border-border-dark">{{ $page }}</a>
                @endif
            @endforeach

            @if ($orders->hasMorePages())
                <a href="{{ $orders->nextPageUrl() }}" class="p-2 rounded border border-border-dark bg-gray-800 text-slate-500 hover:text-slate-200 transition-colors">
                    <span class="material-symbols-outlined text-lg leading-none">chevron_right</span>
                </a>
            @else
                <button class="p-2 rounded border border-border-dark bg-gray-800 text-slate-500" disabled>
                    <span class="material-symbols-outlined text-lg leading-none">chevron_right</span>
                </button>
            @endif
        </div>
    </div>
    @endif
</div>

<!-- Order Stats -->
<div class="mt-8 grid grid-cols-1 md:grid-cols-4 gap-6">
    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-primary text-2xl">shopping_cart</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Total Orders</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($totalOrders) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-amber-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-amber-400 text-2xl">pending_actions</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Pending</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($pendingOrders) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-emerald-400 text-2xl">check_circle</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Completed</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($completedOrders) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-blue-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-blue-400 text-2xl">account_balance_wallet</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Total Revenue</p>
                <h3 class="text-xl font-bold text-white">Rp {{ number_format($totalRevenue ?? 0, 0, ',', '.') }}</h3>
            </div>
        </div>
    </div>
</div>
@endsection
