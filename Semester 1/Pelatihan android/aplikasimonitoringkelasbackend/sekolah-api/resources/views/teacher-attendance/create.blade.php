@extends('layouts.admin')

@section('title', 'Tambah Kehadiran Guru')

@section('content')
<div class="page-header">
    <div class="page-header-content">
        <h1 class="page-title">
            <i class="fas fa-plus-circle"></i>
            Tambah Kehadiran Guru
        </h1>
        <p class="page-subtitle">Catat kehadiran guru baru dengan mudah</p>
    </div>
    <a href="{{ route('teacher-attendance.index') }}" class="btn btn-glass">
        <i class="fas fa-arrow-left"></i> Kembali
    </a>
</div>

<div class="form-card">
    <div class="form-header">
        <div class="form-icon">
            <i class="fas fa-clipboard-check"></i>
        </div>
        <div class="form-header-text">
            <h2>Form Kehadiran Guru</h2>
            <p>Isi data kehadiran dengan lengkap</p>
        </div>
    </div>

    <div class="form-body">
        @if($errors->any())
            <div class="alert-modern alert-error">
                <div class="alert-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="alert-content">
                    <strong>Terjadi Kesalahan!</strong>
                    <ul>
                        @foreach($errors->all() as $error)
                            <li>{{ $error }}</li>
                        @endforeach
                    </ul>
                </div>
            </div>
        @endif

        <form action="{{ route('teacher-attendance.store') }}" method="POST">
            @csrf

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-user-tie"></i>
                    <span>Informasi Dasar</span>
                </div>
                
                <div class="form-grid">
                    <div class="form-group-modern">
                        <label for="guru_id" class="form-label-modern">
                            Pilih Guru <span class="required">*</span>
                        </label>
                        <div class="select-wrapper">
                            <select name="guru_id" id="guru_id" class="form-select-modern" required>
                                <option value="">🔍 Cari dan pilih guru...</option>
                                @foreach($gurus as $guru)
                                    <option value="{{ $guru->id }}" {{ old('guru_id') == $guru->id ? 'selected' : '' }}>
                                        {{ $guru->nama }} ({{ $guru->nip }})
                                    </option>
                                @endforeach
                            </select>
                            <i class="fas fa-chevron-down select-arrow"></i>
                        </div>
                    </div>

                    <div class="form-group-modern">
                        <label for="tanggal" class="form-label-modern">
                            Tanggal <span class="required">*</span>
                        </label>
                        <div class="input-wrapper">
                            <i class="fas fa-calendar-alt input-icon"></i>
                            <input type="date" name="tanggal" id="tanggal" class="form-input-modern" 
                                   value="{{ old('tanggal', date('Y-m-d')) }}" required>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-clipboard-list"></i>
                    <span>Status Kehadiran</span>
                </div>
                
                <div class="status-selector">
                    <label class="status-option-card">
                        <input type="radio" name="status" value="Hadir" {{ old('status', 'Hadir') == 'Hadir' ? 'checked' : '' }} required>
                        <div class="status-card-content status-hadir">
                            <div class="status-icon-large">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <span class="status-text">Hadir</span>
                            <span class="status-desc">Guru hadir tepat waktu</span>
                        </div>
                    </label>

                    <label class="status-option-card">
                        <input type="radio" name="status" value="Tidak Hadir" {{ old('status') == 'Tidak Hadir' ? 'checked' : '' }}>
                        <div class="status-card-content status-tidak-hadir">
                            <div class="status-icon-large">
                                <i class="fas fa-times-circle"></i>
                            </div>
                            <span class="status-text">Tidak Hadir</span>
                            <span class="status-desc">Guru tidak hadir</span>
                        </div>
                    </label>

                    <label class="status-option-card">
                        <input type="radio" name="status" value="Terlambat" {{ old('status') == 'Terlambat' ? 'checked' : '' }}>
                        <div class="status-card-content status-terlambat">
                            <div class="status-icon-large">
                                <i class="fas fa-clock"></i>
                            </div>
                            <span class="status-text">Terlambat</span>
                            <span class="status-desc">Guru hadir terlambat</span>
                        </div>
                    </label>

                    <label class="status-option-card">
                        <input type="radio" name="status" value="Izin" {{ old('status') == 'Izin' ? 'checked' : '' }}>
                        <div class="status-card-content status-izin">
                            <div class="status-icon-large">
                                <i class="fas fa-envelope-open-text"></i>
                            </div>
                            <span class="status-text">Izin</span>
                            <span class="status-desc">Guru izin/cuti</span>
                        </div>
                    </label>
                </div>
            </div>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-clock"></i>
                    <span>Waktu Kehadiran</span>
                </div>
                
                <div class="form-grid">
                    <div class="form-group-modern">
                        <label for="jam_masuk" class="form-label-modern">Jam Masuk</label>
                        <div class="input-wrapper">
                            <i class="fas fa-sign-in-alt input-icon text-success"></i>
                            <input type="time" name="jam_masuk" id="jam_masuk" class="form-input-modern" value="{{ old('jam_masuk') }}">
                        </div>
                    </div>

                    <div class="form-group-modern">
                        <label for="jam_keluar" class="form-label-modern">Jam Keluar</label>
                        <div class="input-wrapper">
                            <i class="fas fa-sign-out-alt input-icon text-danger"></i>
                            <input type="time" name="jam_keluar" id="jam_keluar" class="form-input-modern" value="{{ old('jam_keluar') }}">
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="section-title">
                    <i class="fas fa-sticky-note"></i>
                    <span>Keterangan Tambahan</span>
                </div>
                
                <div class="form-group-modern">
                    <label for="keterangan" class="form-label-modern">Keterangan</label>
                    <textarea name="keterangan" id="keterangan" rows="4" class="form-textarea-modern" 
                              placeholder="Contoh: Izin sakit dengan surat dokter...">{{ old('keterangan') }}</textarea>
                </div>
            </div>

            <div class="form-actions-modern">
                <button type="submit" class="btn-submit">
                    <i class="fas fa-save"></i>
                    <span>Simpan Kehadiran</span>
                </button>
                <a href="{{ route('teacher-attendance.index') }}" class="btn-cancel">
                    <i class="fas fa-times"></i>
                    <span>Batal</span>
                </a>
            </div>
        </form>
    </div>
</div>

@include('teacher-attendance.partials.form-styles')
@endsection
