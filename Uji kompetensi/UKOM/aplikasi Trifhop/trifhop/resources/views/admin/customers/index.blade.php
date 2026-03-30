@extends('layouts.admin')

@section('title', 'Customer Directory')

@section('content')
<div class="flex flex-wrap items-end justify-between gap-4 mb-8">
    <div class="space-y-1">
        <h3 class="text-3xl font-black text-white tracking-tight">Customer Management</h3>
        <p class="text-slate-400 font-medium">Manage and monitor your customer base with real-time status updates.</p>
    </div>
    <button onclick="window.print()" class="flex items-center gap-2 bg-primary text-white px-5 py-2.5 rounded-lg font-bold shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all">
        <span class="material-symbols-outlined">download</span>
        Export Data
    </button>
</div>

<!-- Customer Table -->
<div class="bg-surface-dark rounded-xl border border-slate-800 shadow-2xl overflow-hidden">
    <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
            <thead>
                <tr class="bg-slate-800/50 border-b border-slate-700">
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest w-16">Profile</th>
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Name</th>
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest">Email Address</th>
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest text-center">Orders</th>
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest text-center">Joined</th>
                    <th class="px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-widest text-right">Actions</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-800">
                @forelse($customers as $customer)
                <tr class="hover:bg-slate-800/40 transition-colors group">
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="w-10 h-10 rounded-full bg-primary/20 text-primary flex items-center justify-center text-sm font-bold border border-primary/30">
                            {{ strtoupper(substr($customer->name, 0, 2)) }}
                        </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-sm font-bold text-white group-hover:text-primary transition-colors">{{ $customer->name }}</div>
                        <div class="text-xs text-slate-500 font-medium">Joined {{ $customer->created_at->format('M Y') }}</div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                        <span class="text-sm text-slate-300">{{ $customer->email }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        @if($customer->orders_count > 0)
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-primary/10 text-primary border border-primary/20">{{ $customer->orders_count }}</span>
                        @else
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-slate-700/50 text-slate-400 border border-slate-600">0</span>
                        @endif
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-center">
                        <span class="text-sm text-slate-400">{{ $customer->created_at->diffForHumans() }}</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-right">
                        <a href="{{ route('admin.customers.show', $customer) }}" class="text-primary hover:text-white text-sm font-bold transition-all px-3 py-1.5 rounded-md hover:bg-primary/20">
                            View Details
                        </a>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-slate-500">
                        <span class="material-symbols-outlined text-5xl mb-3 block">group</span>
                        <p class="text-lg font-semibold">No customers yet</p>
                        <p class="text-sm mt-1">Customers will appear here once they register</p>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
    
    @if($customers->hasPages())
    <div class="px-6 py-4 bg-slate-800/30 border-t border-slate-800 flex items-center justify-between">
        <p class="text-xs font-bold text-slate-400 uppercase tracking-widest">
            Showing {{ $customers->firstItem() }} - {{ $customers->lastItem() }} of {{ $customers->total() }} customers
        </p>
        <div class="flex items-center gap-1">
            @if ($customers->onFirstPage())
                <button class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-400" disabled>
                    <span class="material-symbols-outlined text-lg">chevron_left</span>
                </button>
            @else
                <a href="{{ $customers->previousPageUrl() }}" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-700 transition-colors text-slate-400">
                    <span class="material-symbols-outlined text-lg">chevron_left</span>
                </a>
            @endif

            @foreach ($customers->getUrlRange(1, min(4, $customers->lastPage())) as $page => $url)
                @if ($page == $customers->currentPage())
                    <button class="w-8 h-8 flex items-center justify-center rounded-lg bg-primary text-white text-xs font-black">{{ $page }}</button>
                @else
                    <a href="{{ $url }}" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-700 transition-colors text-xs font-bold text-slate-300">{{ $page }}</a>
                @endif
            @endforeach

            @if ($customers->hasMorePages())
                <a href="{{ $customers->nextPageUrl() }}" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-700 transition-colors text-slate-400">
                    <span class="material-symbols-outlined text-lg">chevron_right</span>
                </a>
            @else
                <button class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-400" disabled>
                    <span class="material-symbols-outlined text-lg">chevron_right</span>
                </button>
            @endif
        </div>
    </div>
    @endif
</div>

<!-- Customer Statistics -->
<div class="mt-8 grid grid-cols-1 md:grid-cols-4 gap-6">
    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-primary text-2xl">group</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Total Customers</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($totalCustomers) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-emerald-400 text-2xl">person_add</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">New Today</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($activeToday) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-blue-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-blue-400 text-2xl">trending_up</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">New This Month</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($newThisMonth) }}</h3>
            </div>
        </div>
    </div>

    <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
        <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-amber-500/10 rounded-xl flex items-center justify-center">
                <span class="material-symbols-outlined text-amber-400 text-2xl">shopping_cart</span>
            </div>
            <div>
                <p class="text-sm font-medium text-slate-400">Total Orders</p>
                <h3 class="text-2xl font-bold text-white">{{ number_format($totalOrders) }}</h3>
            </div>
        </div>
    </div>
</div>
@endsection
