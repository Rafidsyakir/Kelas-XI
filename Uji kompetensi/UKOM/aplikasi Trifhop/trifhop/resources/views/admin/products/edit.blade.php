@extends('layouts.admin')

@section('title', 'Edit Product')

@section('content')
<div class="animate-fade-in-up max-w-4xl">
    <!-- Header -->
    <div class="mb-6">
        <h2 class="text-2xl font-bold text-white">Edit Product</h2>
        <p class="text-slate-400 text-sm mt-1">Update product information</p>
    </div>

    <form action="{{ route('admin.products.update', $product->id) }}" method="POST" enctype="multipart/form-data">
        @csrf
        @method('PUT')
        
        <div class="bg-surface-dark rounded-2xl border border-slate-700/50 overflow-hidden">
            <div class="p-6 border-b border-slate-700/50 bg-slate-800/30">
                <h3 class="text-lg font-bold text-white">Product Information</h3>
                <p class="text-slate-400 text-sm mt-1">Update the details below</p>
            </div>
            
            <div class="p-8 space-y-6">
                <!-- Product Name -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Product Name <span class="text-red-400">*</span>
                    </label>
                    <input type="text" name="name" required 
                        class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                        placeholder="e.g., Vintage Denim Jacket"
                        value="{{ old('name', $product->name) }}">
                    @error('name')
                        <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                    @enderror
                </div>

                <!-- Description -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Description <span class="text-red-400">*</span>
                    </label>
                    <textarea name="description" required rows="4"
                        class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all resize-none"
                        placeholder="Describe the product...">{{ old('description', $product->description) }}</textarea>
                    @error('description')
                        <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                    @enderror
                </div>

                <!-- Category and Price -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Category <span class="text-red-400">*</span>
                        </label>
                        <select name="category_id" required
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all">
                            <option value="">Select Category</option>
                            @foreach($categories as $category)
                                <option value="{{ $category->id }}" {{ old('category_id', $product->category_id) == $category->id ? 'selected' : '' }}>
                                    {{ $category->name }}
                                </option>
                            @endforeach
                        </select>
                        @error('category_id')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Price (Rp) <span class="text-red-400">*</span>
                        </label>
                        <input type="number" name="price" required step="0.01" min="0"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                            placeholder="0.00"
                            value="{{ old('price', $product->price) }}">
                        @error('price')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                </div>

                <!-- Stock and Status -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Stock Quantity <span class="text-red-400">*</span>
                        </label>
                        <input type="number" name="stock" required min="0"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                            placeholder="0"
                            value="{{ old('stock', $product->stock) }}">
                        @error('stock')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Status <span class="text-red-400">*</span>
                        </label>
                        <select name="status" required
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all">
                            <option value="available" {{ old('status', $product->status) == 'available' ? 'selected' : '' }}>Available</option>
                            <option value="sold" {{ old('status', $product->status) == 'sold' ? 'selected' : '' }}>Sold</option>
                        </select>
                        @error('status')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                </div>

                <!-- Condition, Size, Brand -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Condition
                        </label>
                        <input type="text" name="condition"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                            placeholder="e.g., Like New"
                            value="{{ old('condition', $product->condition) }}">
                        @error('condition')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Size
                        </label>
                        <input type="text" name="size"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                            placeholder="e.g., M, L, XL"
                            value="{{ old('size', $product->size) }}">
                        @error('size')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>

                    <div>
                        <label class="block text-sm font-semibold text-slate-300 mb-2">
                            Brand
                        </label>
                        <input type="text" name="brand"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                            placeholder="e.g., Nike, Adidas"
                            value="{{ old('brand', $product->brand) }}">
                        @error('brand')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                </div>

                <!-- Current Image -->
                @if($product->image_url)
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Current Image
                    </label>
                    <div class="relative inline-block">
                        @php
                            $currentImageUrl = filter_var($product->image_url, FILTER_VALIDATE_URL) 
                                ? $product->image_url 
                                : asset($product->image_url);
                        @endphp
                        <img src="{{ $currentImageUrl }}" alt="{{ $product->name }}" 
                            class="w-48 h-48 object-cover rounded-xl border-2 border-slate-700 shadow-lg"
                            onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\"w-48 h-48 bg-slate-800 rounded-xl border-2 border-slate-700 flex items-center justify-center\"><span class=\"material-symbols-outlined text-4xl text-slate-500\">broken_image</span></div>'">
                        <div class="absolute top-2 right-2 bg-emerald-500 text-white text-xs font-bold px-2 py-1 rounded-lg shadow-lg">
                            Current
                        </div>
                    </div>
                </div>
                @endif

                <!-- Image Upload -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Update Product Image <span class="text-slate-500 text-xs">(Optional - leave empty to keep current image)</span>
                    </label>
                    <div class="relative">
                        <input type="file" name="image" accept="image/*" id="imageInput"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-primary/20 file:text-primary file:font-medium hover:file:bg-primary/30 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all">
                        @error('image')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                    <div id="imagePreview" class="mt-4 hidden">
                        <p class="text-sm text-slate-400 mb-2">New Image Preview:</p>
                        <img src="" alt="Preview" class="w-48 h-48 object-cover rounded-xl border-2 border-primary">
                    </div>
                </div>
            </div>

            <div class="p-6 bg-slate-900/30 border-t border-slate-700/50 flex items-center justify-between">
                <a href="{{ route('admin.products.index') }}" class="px-6 py-2.5 bg-slate-800 border border-slate-700 text-slate-300 rounded-xl font-semibold hover:bg-slate-700 hover:text-white transition-all flex items-center space-x-2">
                    <span class="material-symbols-outlined text-lg">arrow_back</span>
                    <span>Back</span>
                </a>
                <button type="submit" class="px-6 py-2.5 bg-primary hover:brightness-110 text-white rounded-xl font-semibold shadow-lg shadow-primary/30 transition-all flex items-center space-x-2">
                    <span class="material-symbols-outlined text-lg">save</span>
                    <span>Update Product</span>
                </button>
            </div>
        </div>
    </form>
</div>
@endsection

@push('scripts')
<script>
    document.getElementById('imageInput').addEventListener('change', function(e) {
        const preview = document.getElementById('imagePreview');
        const file = e.target.files[0];
        
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.querySelector('img').src = e.target.result;
                preview.classList.remove('hidden');
            }
            reader.readAsDataURL(file);
        } else {
            preview.classList.add('hidden');
        }
    });
</script>
@endpush
