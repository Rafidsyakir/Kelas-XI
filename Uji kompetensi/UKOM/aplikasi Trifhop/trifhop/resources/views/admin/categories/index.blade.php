@extends('layouts.admin')

@section('title', 'Product Categories')

@section('content')
<div class="flex flex-wrap items-center justify-between gap-4 mb-8">
    <div class="flex flex-col gap-1">
        <h1 class="text-white text-3xl font-extrabold tracking-tight">Product Categories</h1>
        <p class="text-slate-400 text-base">Manage and organize your store's product hierarchy</p>
    </div>
    <div class="flex gap-3">
        <button onclick="window.print()" class="flex items-center justify-center gap-2 rounded-lg h-11 px-6 bg-surface-dark text-slate-100 text-sm font-bold border border-slate-700 hover:bg-slate-700 transition-colors">
            <span class="material-symbols-outlined text-[20px]">print</span>
            Export
        </button>
        <a href="{{ route('admin.categories.create') }}" class="flex items-center justify-center gap-2 rounded-lg h-11 px-6 bg-primary text-white text-sm font-bold shadow-lg shadow-primary/30 hover:bg-primary/90 transition-all active:scale-95">
            <span class="material-symbols-outlined text-[20px]">add</span>
            Create Category
        </a>
    </div>
</div>

@if($categories->isEmpty())
    <div class="bg-surface-dark rounded-2xl border border-slate-800 p-12 text-center">
        <span class="material-symbols-outlined text-6xl text-slate-600 mb-4 block">category</span>
        <h3 class="text-xl font-bold text-white mb-2">No Categories Yet</h3>
        <p class="text-slate-400 mb-6">Start organizing your products by creating categories</p>
        <a href="{{ route('admin.categories.create') }}" class="inline-flex items-center gap-2 bg-primary text-white px-6 py-3 rounded-lg hover:bg-primary/90 transition-all font-bold">
            <span class="material-symbols-outlined">add</span>
            Create First Category
        </a>
    </div>
@else
    <!-- Categories Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        @foreach($categories as $category)
        <div class="category-card group bg-surface-dark p-6 rounded-2xl border border-slate-800 cursor-pointer hover:border-primary/30 transition-all duration-300">
            <div class="flex justify-between items-start mb-6">
                <div class="size-14 rounded-2xl bg-primary/10 flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-white transition-all duration-300">
                    @if($category->icon_url)
                        <img src="{{ $category->icon_url }}" alt="{{ $category->name }}" class="w-8 h-8"/>
                    @else
                        <span class="material-symbols-outlined text-[32px]">category</span>
                    @endif
                </div>
                <div class="flex gap-2">
                    <a href="{{ route('admin.categories.edit', $category) }}" class="text-slate-500 hover:text-primary transition-colors">
                        <span class="material-symbols-outlined text-[20px]">edit</span>
                    </a>
                    <form action="{{ route('admin.categories.destroy', $category) }}" method="POST" onsubmit="return confirm('Are you sure? This will affect {{ $category->products_count }} products.');" class="inline">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="text-slate-500 hover:text-red-500 transition-colors">
                            <span class="material-symbols-outlined text-[20px]">delete</span>
                        </button>
                    </form>
                </div>
            </div>
            <div class="flex flex-col gap-2">
                <h3 class="text-white text-lg font-bold">{{ $category->name }}</h3>
                <div class="flex items-center gap-2">
                    <span class="inline-flex items-center rounded-lg bg-primary/10 px-2.5 py-1 text-xs font-bold text-primary">
                        {{ $category->products_count }} {{ Str::plural('Product', $category->products_count) }}
                    </span>
                    <span class="text-[10px] text-slate-500 font-medium tracking-wide">ID: {{ $category->id }}</span>
                </div>
                @if($category->description)
                    <p class="text-xs text-slate-400 mt-2">{{ Str::limit($category->description, 80) }}</p>
                @endif
            </div>
            
            <!-- Quick Actions -->
            <div class="mt-4 pt-4 border-t border-slate-800 flex gap-2">
                <a href="{{ route('admin.products.index', ['category' => $category->id]) }}" class="flex-1 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white px-3 py-2 rounded-lg text-xs font-semibold transition-all text-center">
                    View Products
                </a>
                <a href="{{ route('admin.categories.edit', $category) }}" class="bg-primary/10 hover:bg-primary/20 text-primary px-3 py-2 rounded-lg text-xs font-semibold transition-all">
                    Edit
                </a>
            </div>
        </div>
        @endforeach
    </div>

    <!-- Category Statistics -->
    <div class="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
            <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
                    <span class="material-symbols-outlined text-primary text-2xl">grid_view</span>
                </div>
                <div>
                    <p class="text-sm font-medium text-slate-400">Total Categories</p>
                    <h3 class="text-2xl font-bold text-white">{{ $categories->count() }}</h3>
                </div>
            </div>
        </div>

        <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
            <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-emerald-500/10 rounded-xl flex items-center justify-center">
                    <span class="material-symbols-outlined text-emerald-400 text-2xl">inventory_2</span>
                </div>
                <div>
                    <p class="text-sm font-medium text-slate-400">Total Products</p>
                    <h3 class="text-2xl font-bold text-white">{{ $categories->sum('products_count') }}</h3>
                </div>
            </div>
        </div>

        <div class="bg-surface-dark rounded-xl p-6 border border-slate-800">
            <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-amber-500/10 rounded-xl flex items-center justify-center">
                    <span class="material-symbols-outlined text-amber-400 text-2xl">trending_up</span>
                </div>
                <div>
                    <p class="text-sm font-medium text-slate-400">Avg Products/Category</p>
                    <h3 class="text-2xl font-bold text-white">
                        {{ $categories->count() > 0 ? number_format($categories->sum('products_count') / $categories->count(), 1) : 0 }}
                    </h3>
                </div>
            </div>
        </div>
    </div>
@endif
@endsection

@push('styles')
<style>
.category-card {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.category-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 0 20px rgba(19, 127, 236, 0.15);
}
</style>
@endpush
