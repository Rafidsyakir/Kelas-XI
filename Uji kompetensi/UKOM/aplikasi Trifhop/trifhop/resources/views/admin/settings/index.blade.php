@extends('admin.layouts.app')

@section('title', 'Pengaturan Aplikasi')

@section('content')
<div class="space-y-6 animate-fade-in-up">
    <!-- Header -->
    <div class="flex items-center justify-between">
        <div>
            <h1 class="text-4xl font-bold text-white mb-2">Pengaturan Aplikasi</h1>
            <p class="text-slate-400">Kelola setelan dan branding aplikasi Trifhop</p>
        </div>
    </div>

    <!-- Alert Messages -->
    @if ($errors->any())
        <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4">
            <div class="flex items-start space-x-3">
                <i class="fas fa-exclamation-circle text-red-400 mt-1"></i>
                <div class="flex-1">
                    <h3 class="text-red-400 font-semibold mb-1">Terjadi Kesalahan</h3>
                    <ul class="text-red-300 text-sm space-y-1">
                        @foreach ($errors->all() as $error)
                            <li>{{ $error }}</li>
                        @endforeach
                    </ul>
                </div>
            </div>
        </div>
    @endif

    @if (session('success'))
        <div class="bg-green-500/10 border border-green-500/30 rounded-xl p-4">
            <div class="flex items-start space-x-3">
                <i class="fas fa-check-circle text-green-400 mt-1"></i>
                <div>
                    <p class="text-green-400 font-semibold">{{ session('success') }}</p>
                </div>
            </div>
        </div>
    @endif

    @if (session('error'))
        <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4">
            <div class="flex items-start space-x-3">
                <i class="fas fa-times-circle text-red-400 mt-1"></i>
                <div>
                    <p class="text-red-400 font-semibold">{{ session('error') }}</p>
                </div>
            </div>
        </div>
    @endif

    <!-- Settings Container -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Form -->
        <div class="lg:col-span-2">
            <form action="{{ route('admin.settings.update') }}" method="POST" enctype="multipart/form-data" class="glass-effect rounded-2xl p-8 space-y-6">
                @csrf

                <!-- App Name Section -->
                <div>
                    <label class="block text-white font-semibold mb-3">
                        <i class="fas fa-heading mr-2 text-blue-400"></i>Nama Aplikasi
                    </label>
                    <input 
                        type="text" 
                        name="app_name" 
                        value="{{ old('app_name', $appName) }}"
                        class="w-full px-4 py-3 bg-slate-800/50 border border-slate-700 rounded-xl focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-white placeholder-slate-500 transition"
                        placeholder="Masukkan nama aplikasi"
                        required>
                    <p class="text-slate-400 text-sm mt-2">Nama ini akan ditampilkan di berbagai tempat di aplikasi</p>
                </div>

                <!-- App Description Section -->
                <div>
                    <label class="block text-white font-semibold mb-3">
                        <i class="fas fa-align-left mr-2 text-blue-400"></i>Deskripsi Aplikasi
                    </label>
                    <textarea 
                        name="app_description" 
                        rows="4"
                        class="w-full px-4 py-3 bg-slate-800/50 border border-slate-700 rounded-xl focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-white placeholder-slate-500 transition resize-none"
                        placeholder="Masukkan deskripsi aplikasi">{{ old('app_description', $appDescription) }}</textarea>
                    <p class="text-slate-400 text-sm mt-2">Deskripsi singkat tentang aplikasi Anda</p>
                </div>

                <!-- Logo Upload Section -->
                <div class="border-t border-slate-700 pt-6">
                    <label class="block text-white font-semibold mb-4">
                        <i class="fas fa-image mr-2 text-blue-400"></i>Logo Aplikasi
                    </label>

                    <!-- Current Logo Display -->
                    @if ($logoPath)
                        <div class="mb-4 flex items-center space-x-4">
                            <div class="w-24 h-24 rounded-xl border border-slate-700 p-2 flex items-center justify-center bg-slate-800/30">
                                <img src="{{ asset('storage/' . $logoPath) }}" alt="Current Logo" class="max-w-full max-h-full object-contain">
                            </div>
                            <div>
                                <p class="text-slate-300 text-sm mb-2">Logo saat ini</p>
                                <form action="{{ route('admin.settings.delete-logo') }}" method="POST" class="inline" onsubmit="return confirm('Yakin ingin menghapus logo?')">
                                    @csrf
                                    @method('DELETE')
                                    <button type="submit" class="px-3 py-2 bg-red-500/20 border border-red-500/30 rounded-lg text-red-400 hover:bg-red-500/30 transition text-sm font-medium">
                                        <i class="fas fa-trash mr-2"></i>Hapus Logo
                                    </button>
                                </form>
                            </div>
                        </div>
                    @endif

                    <!-- Upload Input -->
                    <div class="relative">
                        <input 
                            type="file" 
                            name="app_logo" 
                            accept="image/*"
                            id="logoInput"
                            class="w-full px-4 py-3 bg-slate-800/50 border border-slate-700 rounded-xl focus:border-blue-500 focus:ring-1 focus:ring-blue-500 text-slate-400 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-blue-500/20 file:text-blue-400 file:cursor-pointer hover:file:bg-blue-500/30 transition">
                    </div>
                    <p class="text-slate-400 text-sm mt-2">
                        <i class="fas fa-info-circle mr-1"></i>Format: JPEG, PNG, JPG, GIF | Ukuran maksimal: 5MB
                    </p>

                    <!-- Preview -->
                    <div id="logoPreview" class="mt-4 hidden">
                        <p class="text-slate-300 text-sm mb-2">Pratinjau</p>
                        <div class="w-32 h-32 rounded-xl border border-slate-700 p-2 flex items-center justify-center bg-slate-800/30">
                            <img id="previewImage" src="" alt="Preview" class="max-w-full max-h-full object-contain">
                        </div>
                    </div>
                </div>

                <!-- Submit Button -->
                <div class="border-t border-slate-700 pt-6 flex justify-end space-x-3">
                    <a href="{{ route('admin.dashboard') }}" class="px-6 py-3 border border-slate-600 text-slate-300 rounded-lg hover:bg-slate-800/50 transition font-medium">
                        Batal
                    </a>
                    <button type="submit" class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-500 text-white rounded-lg hover:from-blue-700 hover:to-blue-600 transition font-semibold shadow-lg shadow-blue-500/20 flex items-center space-x-2">
                        <i class="fas fa-save"></i>
                        <span>Simpan Perubahan</span>
                    </button>
                </div>
            </form>
        </div>

        <!-- Info Panel -->
        <div class="lg:col-span-1">
            <div class="glass-effect rounded-2xl p-6 space-y-4">
                <div>
                    <h3 class="text-white font-semibold mb-2 flex items-center space-x-2">
                        <i class="fas fa-lightbulb text-yellow-400"></i>
                        <span>Tips</span>
                    </h3>
                    <ul class="space-y-2 text-slate-300 text-sm">
                        <li class="flex space-x-2">
                            <span class="text-blue-400 flex-shrink-0">•</span>
                            <span>Logo akan ditampilkan di web admin dan aplikasi mobile</span>
                        </li>
                        <li class="flex space-x-2">
                            <span class="text-blue-400 flex-shrink-0">•</span>
                            <span>Gunakan logo dengan background transparan untuk hasil terbaik</span>
                        </li>
                        <li class="flex space-x-2">
                            <span class="text-blue-400 flex-shrink-0">•</span>
                            <span>Ukuran ideal: 512x512 pixel atau lebih</span>
                        </li>
                        <li class="flex space-x-2">
                            <span class="text-blue-400 flex-shrink-0">•</span>
                            <span>Format PNG atau JPG akan memberikan hasil terbaik</span>
                        </li>
                    </ul>
                </div>

                <div class="border-t border-slate-700 pt-4">
                    <h3 class="text-white font-semibold mb-2 flex items-center space-x-2">
                        <i class="fas fa-info-circle text-blue-400"></i>
                        <span>Informasi</span>
                    </h3>
                    <p class="text-slate-400 text-sm">
                        Perubahan yang dilakukan di sini akan langsung diterapkan ke seluruh aplikasi setelah halaman dimuat ulang.
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // Preview logo
    document.getElementById('logoInput').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('previewImage').src = e.target.result;
                document.getElementById('logoPreview').classList.remove('hidden');
            };
            reader.readAsDataURL(file);
        }
    });
</script>
@endsection
