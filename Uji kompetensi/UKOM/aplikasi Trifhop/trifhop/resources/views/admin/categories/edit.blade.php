@extends('layouts.admin')

@section('title', 'Edit Category')

@section('content')
<div class="animate-fade-in-up max-w-2xl">
    <!-- Header -->
    <div class="mb-6">
        <h2 class="text-2xl font-bold text-white">Edit Category</h2>
        <p class="text-slate-400 text-sm mt-1">Update category information</p>
    </div>

    <form action="{{ route('admin.categories.update', $category->id) }}" method="POST">
        @csrf
        @method('PUT')

        <div class="bg-surface-dark rounded-2xl border border-slate-700/50 overflow-hidden">
            <div class="p-6 border-b border-slate-700/50 bg-slate-800/30 flex items-center gap-4">
                <div class="w-12 h-12 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center overflow-hidden flex-shrink-0">
                    @if($category->icon_url)
                        <img src="{{ $category->icon_url }}" alt="{{ $category->name }}" class="w-8 h-8 object-contain">
                    @else
                        <span class="material-symbols-outlined text-xl text-primary">category</span>
                    @endif
                </div>
                <div>
                    <h3 class="text-lg font-bold text-white">{{ $category->name }}</h3>
                    <p class="text-slate-400 text-sm">{{ $category->products_count ?? 0 }} products</p>
                </div>
            </div>

            <div class="p-8 space-y-6">
                <!-- Category Name -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Category Name <span class="text-red-400">*</span>
                    </label>
                    <input type="text" name="name" required
                        class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                        placeholder="e.g., Hoodie, Vintage, Jacket"
                        value="{{ old('name', $category->name) }}">
                    @error('name')
                        <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                    @enderror
                </div>

                <!-- Icon URL -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        Icon URL <span class="text-slate-500 text-xs">(Optional)</span>
                    </label>
                    <input type="url" name="icon_url"
                        class="w-full px-4 py-3 bg-slate-900/50 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/20 transition-all"
                        placeholder="https://example.com/icon.png"
                        value="{{ old('icon_url', $category->icon_url) }}"
                        id="iconUrlInput">
                    @error('icon_url')
                        <p class="text-red-400 text-sm mt-1">{{ $message }}</p>
                    @enderror

                    <!-- Icon Preview -->
                    <div id="iconPreview" class="mt-3 {{ $category->icon_url ? '' : 'hidden' }}">
                        <p class="text-xs text-slate-400 mb-2">Icon Preview:</p>
                        <div class="w-16 h-16 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center overflow-hidden">
                            <img id="iconPreviewImg" src="{{ $category->icon_url ?? '' }}" alt="Icon Preview" class="w-10 h-10 object-contain">
                        </div>
                    </div>

                    <!-- No Icon Placeholder -->
                    <div id="iconPlaceholder" class="mt-3 {{ $category->icon_url ? 'hidden' : '' }}">
                        <p class="text-xs text-slate-400 mb-2">No icon set — default icon will be used:</p>
                        <div class="w-16 h-16 rounded-xl bg-primary/10 border border-slate-700 flex items-center justify-center">
                            <span class="material-symbols-outlined text-2xl text-primary">category</span>
                        </div>
                    </div>
                </div>

                <!-- Slug Info -->
                <div class="p-4 bg-slate-800/50 rounded-xl border border-slate-700/50">
                    <div class="flex items-center gap-2 mb-1">
                        <span class="material-symbols-outlined text-sm text-slate-400">info</span>
                        <span class="text-xs font-bold text-slate-400 uppercase tracking-wider">Slug</span>
                    </div>
                    <p class="text-sm text-slate-300 font-mono">{{ $category->slug }}</p>
                    <p class="text-xs text-slate-500 mt-1">Slug is auto-generated from the name when saved</p>
                </div>
            </div>

            <div class="p-6 bg-slate-900/30 border-t border-slate-700/50 flex items-center justify-between">
                <a href="{{ route('admin.categories.index') }}" class="px-6 py-2.5 bg-slate-800 border border-slate-700 text-slate-300 rounded-xl font-semibold hover:bg-slate-700 hover:text-white transition-all flex items-center gap-2">
                    <span class="material-symbols-outlined text-lg">arrow_back</span>
                    <span>Back</span>
                </a>
                <button type="submit" class="px-6 py-2.5 bg-primary hover:brightness-110 text-white rounded-xl font-semibold shadow-lg shadow-primary/30 transition-all flex items-center gap-2">
                    <span class="material-symbols-outlined text-lg">save</span>
                    <span>Update Category</span>
                </button>
            </div>
        </div>
    </form>
</div>
@endsection

@push('scripts')
<script>
    const iconUrlInput = document.getElementById('iconUrlInput');
    const iconPreview = document.getElementById('iconPreview');
    const iconPreviewImg = document.getElementById('iconPreviewImg');
    const iconPlaceholder = document.getElementById('iconPlaceholder');

    iconUrlInput.addEventListener('input', function() {
        const url = this.value.trim();
        if (url) {
            iconPreviewImg.src = url;
            iconPreview.classList.remove('hidden');
            iconPlaceholder.classList.add('hidden');
        } else {
            iconPreview.classList.add('hidden');
            iconPlaceholder.classList.remove('hidden');
        }
    });
</script>
@endpush
