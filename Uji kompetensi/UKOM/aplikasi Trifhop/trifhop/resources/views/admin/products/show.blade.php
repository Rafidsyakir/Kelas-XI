@extends('layouts.admin')

@section('title', 'Product Details')

@section('content')
<div class="animate-fade-in-up max-w-5xl">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
        <div>
            <h2 class="text-2xl font-bold text-white">Product Details</h2>
            <p class="text-slate-400 text-sm mt-1">View complete product information</p>
        </div>
        <div class="flex items-center gap-3">
            <a href="{{ route('admin.products.edit', $product->id) }}" class="px-4 py-2 bg-primary hover:brightness-110 text-white rounded-xl font-semibold transition-all flex items-center gap-2">
                <span class="material-symbols-outlined text-lg">edit</span>
                <span>Edit</span>
            </a>
            <a href="{{ route('admin.products.index') }}" class="px-4 py-2 bg-slate-800 border border-slate-700 text-slate-300 hover:bg-slate-700 hover:text-white rounded-xl font-semibold transition-all flex items-center gap-2">
                <span class="material-symbols-outlined text-lg">arrow_back</span>
                <span>Back</span>
            </a>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Product Image -->
        <div class="lg:col-span-1">
            <div class="bg-surface-dark rounded-2xl border border-slate-700/50 p-6">
                <h3 class="text-lg font-bold text-white mb-4">Product Image</h3>
                @if($product->image_url)
                    @php
                        $productImageUrl = filter_var($product->image_url, FILTER_VALIDATE_URL) 
                            ? $product->image_url 
                            : asset($product->image_url);
                    @endphp
                    <img src="{{ $productImageUrl }}" alt="{{ $product->name }}" 
                        class="w-full h-auto rounded-xl border-2 border-slate-700 shadow-xl"
                        onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\"w-full aspect-square bg-slate-800 rounded-xl flex items-center justify-center border-2 border-slate-700\"><div class=\"text-center\"><span class=\"material-symbols-outlined text-6xl text-slate-600\">broken_image</span><p class=\"text-slate-500 text-sm mt-2\">Image not available</p></div></div>'">
                @else
                    <div class="w-full aspect-square bg-slate-800 rounded-xl flex items-center justify-center border-2 border-slate-700">
                        <div class="text-center">
                            <span class="material-symbols-outlined text-6xl text-slate-600">photo</span>
                            <p class="text-slate-500 text-sm mt-2">No image uploaded</p>
                        </div>
                    </div>
                @endif

                <!-- Status Badge -->
                <div class="mt-4 flex items-center justify-center">
                    @if($product->status === 'available')
                        <span class="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-4 py-2 rounded-full text-sm font-bold uppercase tracking-wider">
                            Available
                        </span>
                    @elseif($product->status === 'sold')
                        <span class="bg-rose-500/10 text-rose-400 border border-rose-500/20 px-4 py-2 rounded-full text-sm font-bold uppercase tracking-wider">
                            Sold
                        </span>
                    @else
                        <span class="bg-slate-500/10 text-slate-400 border border-slate-500/20 px-4 py-2 rounded-full text-sm font-bold uppercase tracking-wider">
                            {{ ucfirst($product->status) }}
                        </span>
                    @endif
                </div>
            </div>
        </div>

        <!-- Product Information -->
        <div class="lg:col-span-2 space-y-6">
            <!-- Basic Info -->
            <div class="bg-surface-dark rounded-2xl border border-slate-700/50 p-6">
                <h3 class="text-lg font-bold text-white mb-4">Basic Information</h3>
                <div class="space-y-4">
                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Product Name</label>
                        <p class="text-white font-semibold text-lg mt-1">{{ $product->name }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Description</label>
                        <p class="text-slate-300 mt-1 leading-relaxed">{{ $product->description }}</p>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Category</label>
                            <p class="text-white font-semibold mt-1">{{ $product->category->name ?? 'Uncategorized' }}</p>
                        </div>

                        <div>
                            <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Price</label>
                            <p class="text-primary font-bold text-xl mt-1">Rp {{ number_format($product->price, 0, ',', '.') }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Inventory & Details -->
            <div class="bg-surface-dark rounded-2xl border border-slate-700/50 p-6">
                <h3 class="text-lg font-bold text-white mb-4">Inventory & Details</h3>
                <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Stock</label>
                        <p class="text-white font-semibold text-lg mt-1">{{ $product->stock }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Condition</label>
                        <p class="text-white font-semibold mt-1">{{ $product->condition ?? '-' }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Size</label>
                        <p class="text-white font-semibold mt-1">{{ $product->size ?? '-' }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Brand</label>
                        <p class="text-white font-semibold mt-1">{{ $product->brand ?? '-' }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Created</label>
                        <p class="text-slate-300 text-sm mt-1">{{ $product->created_at->format('M d, Y') }}</p>
                    </div>

                    <div>
                        <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Last Updated</label>
                        <p class="text-slate-300 text-sm mt-1">{{ $product->updated_at->format('M d, Y') }}</p>
                    </div>
                </div>
            </div>

            <!-- Actions -->
            <div class="bg-surface-dark rounded-2xl border border-slate-700/50 p-6">
                <h3 class="text-lg font-bold text-white mb-4">Actions</h3>
                <div class="flex items-center gap-3">
                    <a href="{{ route('admin.products.edit', $product->id) }}" class="flex-1 px-4 py-3 bg-primary hover:brightness-110 text-white rounded-xl font-semibold transition-all flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined">edit</span>
                        <span>Edit Product</span>
                    </a>
                    <form action="{{ route('admin.products.destroy', $product->id) }}" method="POST" class="flex-1" onsubmit="return confirm('Are you sure you want to delete this product?')">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="w-full px-4 py-3 bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/20 hover:border-red-500/30 rounded-xl font-semibold transition-all flex items-center justify-center gap-2">
                            <span class="material-symbols-outlined">delete</span>
                            <span>Delete Product</span>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
