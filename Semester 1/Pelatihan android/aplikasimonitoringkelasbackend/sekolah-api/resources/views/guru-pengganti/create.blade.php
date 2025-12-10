@extends('layouts.admin')

@section('title', 'Tambah Guru Pengganti')

@section('styles')
<style>
    .form-card {
        background: #1e293b;
        border-radius: 16px;
        padding: 30px;
        box-shadow: var(--shadow);
        max-width: 800px;
        margin: 0 auto;
    }

    .form-header {
        text-align: center;
        margin-bottom: 30px;
        padding-bottom: 20px;
        border-bottom: 2px solid #334155;
    }

    .form-header i {
        font-size: 48px;
        color: var(--primary);
        margin-bottom: 15px;
    }

    .form-header h2 {
        font-size: 24px;
        color: #e2e8f0;
        margin-bottom: 5px;
    }

    .form-header p {
        color: #94a3b8;
        font-size: 14px;
    }

    .form-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
    }

    .form-group {
        margin-bottom: 20px;
    }

    .form-group.full-width {
        grid-column: span 2;
    }

    .form-group label {
        display: block;
        font-size: 14px;
        font-weight: 600;
        color: #e2e8f0;
        margin-bottom: 8px;
    }

    .form-group label .required {
        color: var(--danger);
    }

    .form-control {
        width: 100%;
        padding: 14px 16px;
        border: 2px solid #334155;
        border-radius: 12px;
        font-size: 14px;
        transition: var(--transition);
        background: #0f172a;
        color: #e2e8f0;
    }

    .form-control:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    }

    .form-control:disabled {
        background: #1e293b;
        cursor: not-allowed;
        opacity: 0.5;
    }

    textarea.form-control {
        min-height: 120px;
        resize: vertical;
    }

    select.form-control {
        cursor: pointer;
    }

    .form-hint {
        font-size: 12px;
        color: #94a3b8;
        margin-top: 6px;
    }

    .guru-preview {
        background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.1));
        border-radius: 12px;
        padding: 15px;
        margin-top: 10px;
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .guru-preview .avatar {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background: linear-gradient(135deg, var(--primary), var(--secondary));
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: 700;
        font-size: 18px;
    }

    .guru-preview .info strong {
        display: block;
        color: #e2e8f0;
        font-size: 15px;
    }

    .guru-preview .info span {
        font-size: 13px;
        color: #94a3b8;
    }

    .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 15px;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 2px solid #334155;
    }

    .btn-primary {
        background: linear-gradient(135deg, var(--primary), var(--primary-dark));
        color: white;
        border: none;
        padding: 14px 28px;
        border-radius: 12px;
        font-size: 15px;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 10px;
        transition: var(--transition);
        text-decoration: none;
    }

    .btn-primary:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
    }

    .btn-secondary {
        background: #334155;
        color: #e2e8f0;
        border: none;
        padding: 14px 28px;
        border-radius: 12px;
        font-size: 15px;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 10px;
        transition: var(--transition);
        text-decoration: none;
    }

    .btn-secondary:hover {
        background: #475569;
        color: white;
    }

    .alert-danger {
        background: rgba(239, 68, 68, 0.15);
        border-left: 4px solid #ef4444;
        color: #fca5a5;
        padding: 16px 20px;
        border-radius: 12px;
        margin-bottom: 20px;
    }

    .alert-danger ul {
        margin: 10px 0 0 20px;
    }

    @media (max-width: 768px) {
        .form-grid {
            grid-template-columns: 1fr;
        }
        .form-group.full-width {
            grid-column: span 1;
        }
    }
</style>
@endsection

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-plus-circle"></i>
            Tambah Guru Pengganti
        </h1>
        <p class="page-subtitle">Buat data penggantian guru baru</p>
    </div>
    <a href="{{ route('guru-pengganti.index') }}" class="btn-secondary">
        <i class="fas fa-arrow-left"></i>
        Kembali
    </a>
</div>

<div class="form-card">
    <div class="form-header">
        <i class="fas fa-exchange-alt"></i>
        <h2>Form Guru Pengganti</h2>
        <p>Isi data penggantian guru dengan lengkap</p>
    </div>

    @if($errors->any())
    <div class="alert-danger">
        <strong><i class="fas fa-exclamation-circle"></i> Terjadi Kesalahan!</strong>
        <ul>
            @foreach($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
    @endif

    <form action="{{ route('guru-pengganti.store') }}" method="POST">
        @csrf
        
        <div class="form-grid">
            <div class="form-group">
                <label>Tanggal <span class="required">*</span></label>
                <input type="date" name="tanggal" class="form-control" value="{{ old('tanggal', date('Y-m-d')) }}" required>
            </div>

            <div class="form-group">
                <label>Jadwal Pelajaran <span class="required">*</span></label>
                <select name="jadwal_id" id="jadwal_id" class="form-control" required>
                    <option value="">-- Pilih Jadwal --</option>
                    @foreach($jadwals as $jadwal)
                        <option value="{{ $jadwal->id }}" 
                                data-guru-id="{{ $jadwal->guru_id }}"
                                data-guru-nama="{{ $jadwal->guru->nama ?? 'N/A' }}"
                                {{ old('jadwal_id') == $jadwal->id ? 'selected' : '' }}>
                            {{ $jadwal->hari }} - {{ $jadwal->mata_pelajaran }} - {{ $jadwal->kelas->nama_kelas ?? 'N/A' }} ({{ substr($jadwal->jam_mulai, 0, 5) }})
                        </option>
                    @endforeach
                </select>
                <p class="form-hint">Pilih jadwal yang akan digantikan</p>
            </div>

            <div class="form-group">
                <label>Guru Asli (Yang Digantikan) <span class="required">*</span></label>
                <select name="guru_asli_id" id="guru_asli_id" class="form-control" required>
                    <option value="">-- Pilih Guru Asli --</option>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ old('guru_asli_id') == $guru->id ? 'selected' : '' }}>
                            {{ $guru->nama }} - {{ $guru->mata_pelajaran }}
                        </option>
                    @endforeach
                </select>
                <div id="guru-asli-preview" class="guru-preview" style="display: none;">
                    <div class="avatar" id="guru-asli-avatar">-</div>
                    <div class="info">
                        <strong id="guru-asli-nama">-</strong>
                        <span>Guru yang akan digantikan</span>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label>Guru Pengganti <span class="required">*</span></label>
                <select name="guru_pengganti_id" id="guru_pengganti_id" class="form-control" required>
                    <option value="">-- Pilih Guru Pengganti --</option>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ old('guru_pengganti_id') == $guru->id ? 'selected' : '' }}>
                            {{ $guru->nama }} - {{ $guru->mata_pelajaran }}
                        </option>
                    @endforeach
                </select>
                <p class="form-hint">Pilih guru yang akan menggantikan</p>
            </div>

            <div class="form-group">
                <label>Alasan <span class="required">*</span></label>
                <select name="alasan" class="form-control" required>
                    <option value="">-- Pilih Alasan --</option>
                    <option value="Sakit" {{ old('alasan') == 'Sakit' ? 'selected' : '' }}>🤒 Sakit</option>
                    <option value="Izin" {{ old('alasan') == 'Izin' ? 'selected' : '' }}>📝 Izin</option>
                    <option value="Cuti" {{ old('alasan') == 'Cuti' ? 'selected' : '' }}>🏖️ Cuti</option>
                    <option value="Dinas Luar" {{ old('alasan') == 'Dinas Luar' ? 'selected' : '' }}>🚗 Dinas Luar</option>
                    <option value="Lainnya" {{ old('alasan') == 'Lainnya' ? 'selected' : '' }}>📋 Lainnya</option>
                </select>
            </div>

            <div class="form-group">
                <label>Status <span class="required">*</span></label>
                <select name="status" class="form-control" required>
                    <option value="Pending" {{ old('status', 'Pending') == 'Pending' ? 'selected' : '' }}>⏳ Pending</option>
                    <option value="Disetujui" {{ old('status') == 'Disetujui' ? 'selected' : '' }}>✅ Disetujui</option>
                    <option value="Ditolak" {{ old('status') == 'Ditolak' ? 'selected' : '' }}>❌ Ditolak</option>
                    <option value="Selesai" {{ old('status') == 'Selesai' ? 'selected' : '' }}>✔️ Selesai</option>
                </select>
            </div>

            <div class="form-group full-width">
                <label>Keterangan Tambahan</label>
                <textarea name="keterangan" class="form-control" placeholder="Tambahkan keterangan jika diperlukan...">{{ old('keterangan') }}</textarea>
            </div>
        </div>

        <div class="form-actions">
            <a href="{{ route('guru-pengganti.index') }}" class="btn-secondary">
                <i class="fas fa-times"></i>
                Batal
            </a>
            <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i>
                Simpan Data
            </button>
        </div>
    </form>
</div>
@endsection

@section('scripts')
<script>
document.addEventListener('DOMContentLoaded', function() {
    const jadwalSelect = document.getElementById('jadwal_id');
    const guruAsliSelect = document.getElementById('guru_asli_id');
    const guruAsliPreview = document.getElementById('guru-asli-preview');
    const guruAsliAvatar = document.getElementById('guru-asli-avatar');
    const guruAsliNama = document.getElementById('guru-asli-nama');

    jadwalSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        const guruId = selectedOption.dataset.guruId;
        const guruNama = selectedOption.dataset.guruNama;

        if (guruId) {
            guruAsliSelect.value = guruId;
            guruAsliPreview.style.display = 'flex';
            guruAsliAvatar.textContent = guruNama.charAt(0).toUpperCase();
            guruAsliNama.textContent = guruNama;
        } else {
            guruAsliSelect.value = '';
            guruAsliPreview.style.display = 'none';
        }
    });

    guruAsliSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        if (selectedOption.value) {
            guruAsliPreview.style.display = 'flex';
            guruAsliAvatar.textContent = selectedOption.text.charAt(0).toUpperCase();
            guruAsliNama.textContent = selectedOption.text.split(' - ')[0];
        } else {
            guruAsliPreview.style.display = 'none';
        }
    });
});
</script>
@endsection
