@extends('layouts.admin')

@section('title', 'Product Inventory Management')

@section('content')
<div class="mb-8 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
    <div>
        <h1 class="text-3xl font-extrabold tracking-tight text-white">Inventory Management</h1>
        <p class="mt-2 text-slate-400">Monitor your product stocks, manage SKUs and organize categories.</p>
    </div>
    <a href="{{ route('admin.products.create') }}" class="flex h-11 items-center justify-center gap-2 rounded-lg bg-primary px-6 text-sm font-bold text-white shadow-lg shadow-primary/30 hover:bg-primary/90 transition-all active:scale-95 w-full md:w-auto">
        <span class="material-symbols-outlined">add</span>
        Add New Product
    </a>
</div>

<!-- Filter Tabs -->
<div class="mb-6 flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
    <button class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-lg bg-primary px-4 text-sm font-semibold text-white">
        All Products <span class="rounded bg-white/20 px-1.5 py-0.5 text-xs">{{ $totalProducts }}</span>
    </button>
    <button class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-lg bg-slate-800 px-4 text-sm font-medium text-slate-300 hover:bg-slate-700 transition-colors border border-slate-700/50">
        In Stock <span class="rounded bg-emerald-900/30 px-1.5 py-0.5 text-xs text-emerald-400">{{ $inStock }}</span>
    </button>
    <button class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-lg bg-slate-800 px-4 text-sm font-medium text-slate-300 hover:bg-slate-700 transition-colors border border-slate-700/50">
        Low Stock <span class="rounded bg-orange-900/30 px-1.5 py-0.5 text-xs text-orange-400">{{ $lowStock }}</span>
    </button>
    <button class="flex h-9 shrink-0 items-center justify-center gap-2 rounded-lg bg-slate-800 px-4 text-sm font-medium text-slate-300 hover:bg-slate-700 transition-colors border border-slate-700/50">
        Out of Stock <span class="rounded bg-red-900/30 px-1.5 py-0.5 text-xs text-red-400">{{ $outOfStock }}</span>
    </button>
</div>

<!-- Products Table -->
<div class="overflow-hidden rounded-xl border border-slate-800 bg-surface-dark shadow-2xl">
    <div class="overflow-x-auto">
        <table class="w-full text-left">
            <thead>
                <tr class="border-b border-slate-800 bg-slate-800/40">
                    <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-400">Product</th>
                    <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-400">SKU</th>
                    <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-400">Category</th>
                    <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-400">Price</th>
                    <th class="px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-400">Stock Status</th>
                    <th class="px-6 py-4 text-right text-xs font-bold uppercase tracking-wider text-slate-400">Actions</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-800/60">
                @forelse($products as $product)
                <tr class="group hover:bg-slate-800/40 transition-colors">
                    <td class="px-6 py-4">
                        <div class="flex items-center gap-4">
                            <div class="h-16 w-16 overflow-hidden rounded-xl bg-slate-700/50 product-glow border-2 border-slate-600/30 shadow-lg flex-shrink-0">
                                @if($product->image_url)
                                    @php
                                        // Check if it's a full URL or local path
                                        $imageUrl = filter_var($product->image_url, FILTER_VALIDATE_URL) 
                                            ? $product->image_url 
                                            : asset($product->image_url);
                                    @endphp
                                    <img alt="{{ $product->name }}" class="h-full w-full object-cover" src="{{ $imageUrl }}" onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\'h-full w-full flex items-center justify-center text-slate-500\'><span class=\'material-symbols-outlined text-2xl\'>image</span></div>'"/>
                                @else
                                    <div class="h-full w-full flex items-center justify-center text-slate-500 bg-slate-800/50">
                                        <span class="material-symbols-outlined text-2xl">photo</span>
                                    </div>
                                @endif
                            </div>
                            <div class="min-w-0 flex-1">
                                <span class="font-semibold text-white block truncate">{{ $product->name }}</span>
                                <p class="text-xs text-slate-500 mt-0.5 line-clamp-2">{{ Str::limit($product->description, 60) }}</p>
                            </div>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-sm text-slate-400">SKU-{{ str_pad($product->id, 4, '0', STR_PAD_LEFT) }}</td>
                    <td class="px-6 py-4">
                        <span class="inline-flex rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary border border-primary/20">
                            {{ $product->category->name ?? 'Uncategorized' }}
                        </span>
                    </td>
                    <td class="px-6 py-4 font-medium text-white">Rp {{ number_format($product->price, 0, ',', '.') }}</td>
                    <td class="px-6 py-4">
                        <div class="flex flex-col gap-1.5">
                            <div class="flex items-center justify-between text-xs font-medium">
                                @if($product->stock > 5)
                                    <span class="text-emerald-400">In Stock</span>
                                    <span class="text-slate-400">{{ $product->stock }} units</span>
                                @elseif($product->stock > 0)
                                    <span class="text-orange-400">Low Stock</span>
                                    <span class="text-slate-400">{{ $product->stock }} units</span>
                                @else
                                    <span class="text-red-400">Out of Stock</span>
                                    <span class="text-slate-400">0 units</span>
                                @endif
                            </div>
                            <div class="h-1.5 w-full rounded-full bg-slate-800">
                                @php
                                    $stockPercentage = min(($product->stock / 20) * 100, 100);
                                    $colorClass = $product->stock > 5 ? 'bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.4)]' : ($product->stock > 0 ? 'bg-orange-500 shadow-[0_0_8px_rgba(249,115,22,0.4)]' : 'bg-red-500 shadow-[0_0_8px_rgba(239,68,68,0.4)]');
                                @endphp
                                <div class="h-full rounded-full {{ $colorClass }}" style="width: {{ $stockPercentage }}%"></div>
                            </div>
                        </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                        <div class="flex justify-end gap-2">
                            <a href="{{ route('admin.products.edit', $product) }}" class="rounded-lg p-2 text-slate-500 hover:bg-slate-700 hover:text-primary transition-all">
                                <span class="material-symbols-outlined text-[20px]">edit</span>
                            </a>
                            <form action="{{ route('admin.products.destroy', $product) }}" method="POST" onsubmit="return confirm('Are you sure you want to delete this product?');" class="inline">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="rounded-lg p-2 text-slate-500 hover:bg-slate-700 hover:text-red-400 transition-all">
                                    <span class="material-symbols-outlined text-[20px]">delete</span>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-slate-500">
                        <span class="material-symbols-outlined text-5xl mb-3 block">inventory_2</span>
                        <p class="text-lg font-semibold">No products found</p>
                        <p class="text-sm mt-1">Start by adding your first product</p>
                        <a href="{{ route('admin.products.create') }}" class="inline-flex items-center gap-2 mt-4 bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary/90 transition-all">
                            <span class="material-symbols-outlined text-sm">add</span>
                            Add Product
                        </a>
                    </td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
    
    @if($products->hasPages())
    <div class="flex items-center justify-between border-t border-slate-800 bg-slate-800/20 px-6 py-4">
        <p class="text-sm text-slate-400">
            Showing <span class="font-semibold text-white">{{ $products->firstItem() }}</span> to 
            <span class="font-semibold text-white">{{ $products->lastItem() }}</span> of 
            <span class="font-semibold text-white">{{ $products->total() }}</span> results
        </p>
        <div class="flex items-center gap-2">
            @if ($products->onFirstPage())
                <button class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-slate-400 disabled:opacity-30" disabled>
                    <span class="material-symbols-outlined">chevron_left</span>
                </button>
            @else
                <a href="{{ $products->previousPageUrl() }}" class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-slate-400 hover:bg-slate-700">
                    <span class="material-symbols-outlined">chevron_left</span>
                </a>
            @endif

            @foreach ($products->getUrlRange(1, min(5, $products->lastPage())) as $page => $url)
                @if ($page == $products->currentPage())
                    <button class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-sm font-bold text-white shadow-sm shadow-primary/20">{{ $page }}</button>
                @else
                    <a href="{{ $url }}" class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-sm font-medium text-slate-400 hover:bg-slate-700 transition-colors">{{ $page }}</a>
                @endif
            @endforeach

            @if($products->lastPage() > 5)
                <span class="px-2 text-slate-600">...</span>
                <a href="{{ $products->url($products->lastPage()) }}" class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-sm font-medium text-slate-400 hover:bg-slate-700 transition-colors">{{ $products->lastPage() }}</a>
            @endif

            @if ($products->hasMorePages())
                <a href="{{ $products->nextPageUrl() }}" class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-slate-400 hover:bg-slate-700 transition-colors">
                    <span class="material-symbols-outlined">chevron_right</span>
                </a>
            @else
                <button class="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-700 bg-slate-800 text-slate-400 disabled:opacity-30" disabled>
                    <span class="material-symbols-outlined">chevron_right</span>
                </button>
            @endif
        </div>
    </div>
    @endif
</div>

<!-- Stats Summary -->
<section class="mt-8 border-t border-slate-800 bg-background-dark py-10">
    <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <div class="rounded-xl bg-slate-800/40 p-6 border border-slate-700/50">
            <p class="text-sm font-medium text-slate-400">Total Products</p>
            <h3 class="mt-1 text-2xl font-bold text-white">{{ number_format($totalProducts) }}</h3>
        </div>
        <div class="rounded-xl bg-slate-800/40 p-6 border border-slate-700/50">
            <p class="text-sm font-medium text-slate-400">In Stock</p>
            <h3 class="mt-1 text-2xl font-bold text-emerald-400">{{ number_format($inStock) }}</h3>
        </div>
        <div class="rounded-xl bg-slate-800/40 p-6 border border-slate-700/50">
            <p class="text-sm font-medium text-slate-400">Low Stock Alert</p>
            <h3 class="mt-1 text-2xl font-bold text-orange-400">{{ number_format($lowStock) }}</h3>
        </div>
        <div class="rounded-xl bg-slate-800/40 p-6 border border-slate-700/50">
            <p class="text-sm font-medium text-slate-400">Out of Stock</p>
            <h3 class="mt-1 text-2xl font-bold text-red-400">{{ number_format($outOfStock) }}</h3>
        </div>
    </div>
</section>
@endsection
