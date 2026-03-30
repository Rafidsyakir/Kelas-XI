@extends('layouts.admin')

@section('title', 'Add Product')

@section('content')
<div class="animate-fade-in-up max-w-4xl">
    <!-- Header -->
    <div class="mb-6">
        <h2 class="text-2xl font-bold text-white">Add New Product</h2>
        <p class="text-slate-400 text-sm mt-1">Add a new product to your inventory</p>
    </div>

    <form action="{{ route('admin.products.store') }}" method="POST" enctype="multipart/form-data">
        @csrf
        
        <div class="bg-surface-dark rounded-2xl border border-slate-700/50 overflow-hidden">
            <div class="p-6 border-b border-slate-700/50 bg-slate-800/30">
                <h3 class="text-lg font-bold text-white">Product Information</h3>
                <p class="text-slate-400 text-sm mt-1">Fill in the details below</p>
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
                        value="{{ old('name') }}">
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
                        placeholder="Describe the product...">{{ old('description') }}</textarea>
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
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all">
                            <option value="">Select Category</option>
                            @foreach($categories as $category)
                                <option value="{{ $category->id }}" {{ old('category_id') == $category->id ? 'selected' : '' }}>
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
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all"
                            placeholder="0.00"
                            value="{{ old('price') }}">
                        @error('price')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                </div>

                <!-- Stock -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Stock Quantity <span class="text-red-400">*</span>
                    </label>
                    <input type="number" name="stock" required min="0"
                        class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all"
                        placeholder="0"
                        value="{{ old('stock') }}">
                    @error('stock')
                        <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                    @enderror
                </div>

                <!-- Image Upload -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Product Image <span class="text-red-400">*</span>
                    </label>
                    <div class="relative">
                        <input type="file" name="image" accept="image/*" required id="imageInput"
                            class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-primary/20 file:text-primary file:font-medium hover:file:bg-primary/30 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all">
                        @error('image')
                            <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                        @enderror
                    </div>
                    <div id="imagePreview" class="mt-4 hidden">
                        <p class="text-sm text-slate-400 mb-2">Preview:</p>
                        <img src="" alt="Preview" class="w-48 h-48 object-cover rounded-xl border-2 border-primary shadow-lg">
                    </div>
                </div>
            </div>

            <div class="p-6 bg-slate-900/30 border-t border-slate-700/50 flex items-center justify-between">
                <a href="{{ route('admin.products.index') }}" class="px-6 py-2.5 bg-slate-800 border border-slate-700 text-slate-300 rounded-xl font-semibold hover:bg-slate-700 hover:text-white transition-all flex items-center space-x-2">
                    <span class="material-symbols-outlined text-lg">arrow_back</span>
                    <span>Back</span>
                </a>
                <button type="submit" class="px-6 py-2.5 bg-primary hover:brightness-110 text-white rounded-xl font-semibold shadow-lg shadow-primary/30 transition-all flex items-center space-x-2">
                    <span class="material-symbols-outlined text-lg">add_circle</span>
                    <span>Create Product</span>
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
