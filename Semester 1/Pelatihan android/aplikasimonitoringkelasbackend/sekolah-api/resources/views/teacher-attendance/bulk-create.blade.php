@extends('layouts.admin')

@section('title', 'Input Massal Kehadiran')

@section('content')
<div class="page-header">
    <div class="page-header-content">
        <h1 class="page-title">
            <i class="fas fa-users-cog"></i>
            Input Massal Kehadiran
        </h1>
        <p class="page-subtitle">Input kehadiran semua guru dalam satu waktu</p>
    </div>
    <a href="{{ route('teacher-attendance.index') }}" class="btn btn-glass">
        <i class="fas fa-arrow-left"></i> Kembali
    </a>
</div>

<div class="bulk-create-container">
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

    <form action="{{ route('teacher-attendance.bulk-store') }}" method="POST" id="bulkForm">
        @csrf

        <!-- Date Selection Card -->
        <div class="date-selection-card">
            <div class="date-icon-wrapper">
                <div class="date-icon-bg">
                    <i class="fas fa-calendar-check"></i>
                </div>
            </div>
            <div class="date-input-section">
                <label class="date-label">Pilih Tanggal Kehadiran</label>
                <input type="date" name="tanggal" id="tanggal" class="date-input-large" 
                       value="{{ old('tanggal', date('Y-m-d')) }}" required>
            </div>
            <div class="date-action-section">
                <button type="button" class="btn-today" onclick="document.getElementById('tanggal').value='{{ date('Y-m-d') }}'">
                    <i class="fas fa-calendar-day"></i> Hari Ini
                </button>
            </div>
        </div>

        <!-- Quick Action Bar -->
        <div class="quick-action-bar">
            <div class="quick-action-title">
                <i class="fas fa-magic"></i>
                Set Semua Guru:
            </div>
            <div class="quick-action-buttons">
                <button type="button" class="quick-btn quick-btn-hadir" onclick="setAllStatus('Hadir')">
                    <i class="fas fa-check-circle"></i> Semua Hadir
                </button>
                <button type="button" class="quick-btn quick-btn-tidak-hadir" onclick="setAllStatus('Tidak Hadir')">
                    <i class="fas fa-times-circle"></i> Semua Tidak Hadir
                </button>
                <button type="button" class="quick-btn quick-btn-reset" onclick="resetAll()">
                    <i class="fas fa-undo"></i> Reset
                </button>
            </div>
        </div>

        <!-- Teachers Grid -->
        <div class="teachers-attendance-grid">
            @foreach($gurus as $index => $guru)
                <div class="teacher-attendance-card" data-teacher-id="{{ $guru->id }}">
                    <div class="teacher-card-header">
                        <div class="teacher-avatar">
                            <span>{{ strtoupper(substr($guru->nama, 0, 2)) }}</span>
                        </div>
                        <div class="teacher-info">
                            <h4 class="teacher-name">{{ $guru->nama }}</h4>
                            <span class="teacher-nip">{{ $guru->nip }}</span>
                        </div>
                        <div class="teacher-number">{{ $index + 1 }}</div>
                    </div>

                    <input type="hidden" name="attendances[{{ $index }}][guru_id]" value="{{ $guru->id }}">

                    <div class="status-pills">
                        <label class="status-pill-option">
                            <input type="radio" name="attendances[{{ $index }}][status]" value="Hadir" class="status-radio" data-status="hadir">
                            <span class="status-pill status-pill-hadir">
                                <i class="fas fa-check"></i> Hadir
                            </span>
                        </label>
                        <label class="status-pill-option">
                            <input type="radio" name="attendances[{{ $index }}][status]" value="Tidak Hadir" class="status-radio" data-status="tidak-hadir">
                            <span class="status-pill status-pill-tidak-hadir">
                                <i class="fas fa-times"></i> Tidak Hadir
                            </span>
                        </label>
                        <label class="status-pill-option">
                            <input type="radio" name="attendances[{{ $index }}][status]" value="Terlambat" class="status-radio" data-status="terlambat">
                            <span class="status-pill status-pill-terlambat">
                                <i class="fas fa-clock"></i> Terlambat
                            </span>
                        </label>
                        <label class="status-pill-option">
                            <input type="radio" name="attendances[{{ $index }}][status]" value="Izin" class="status-radio" data-status="izin">
                            <span class="status-pill status-pill-izin">
                                <i class="fas fa-envelope"></i> Izin
                            </span>
                        </label>
                    </div>

                    <div class="keterangan-input">
                        <textarea name="attendances[{{ $index }}][keterangan]" placeholder="Keterangan (opsional)..." rows="2" class="keterangan-textarea"></textarea>
                    </div>
                </div>
            @endforeach
        </div>

        <!-- Submit Section -->
        <div class="submit-section">
            <div class="submit-summary">
                <div class="summary-item summary-hadir">
                    <i class="fas fa-check-circle"></i>
                    <span class="summary-count" id="countHadir">0</span>
                    <span class="summary-label">Hadir</span>
                </div>
                <div class="summary-item summary-tidak-hadir">
                    <i class="fas fa-times-circle"></i>
                    <span class="summary-count" id="countTidakHadir">0</span>
                    <span class="summary-label">Tidak Hadir</span>
                </div>
                <div class="summary-item summary-terlambat">
                    <i class="fas fa-clock"></i>
                    <span class="summary-count" id="countTerlambat">0</span>
                    <span class="summary-label">Terlambat</span>
                </div>
                <div class="summary-item summary-izin">
                    <i class="fas fa-envelope"></i>
                    <span class="summary-count" id="countIzin">0</span>
                    <span class="summary-label">Izin</span>
                </div>
            </div>
            <button type="submit" class="btn-submit-bulk">
                <i class="fas fa-save"></i>
                <span>Simpan Semua Kehadiran</span>
            </button>
        </div>
    </form>
</div>

<style>
/* ===== Page Header ===== */
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    flex-wrap: wrap;
    gap: 20px;
}

.page-header-content {
    flex: 1;
}

.page-title {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 12px;
}

.page-title i {
    color: #6366f1;
}

.page-subtitle {
    color: #64748b;
    font-size: 14px;
    margin: 5px 0 0 0;
}

.btn-glass {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(99, 102, 241, 0.2);
    border-radius: 12px;
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
}

.btn-glass:hover {
    background: #6366f1;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

/* ===== Alert Modern ===== */
.alert-modern {
    display: flex;
    align-items: flex-start;
    gap: 15px;
    padding: 20px;
    border-radius: 16px;
    margin-bottom: 25px;
}

.alert-error {
    background: linear-gradient(135deg, #fef2f2, #fee2e2);
    border: 1px solid #fecaca;
}

.alert-icon {
    width: 45px;
    height: 45px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
}

.alert-error .alert-icon {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    color: white;
}

.alert-content ul {
    margin: 8px 0 0 20px;
    padding: 0;
}

/* ===== Date Selection Card ===== */
.date-selection-card {
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    border-radius: 24px;
    padding: 30px 40px;
    display: flex;
    align-items: center;
    gap: 30px;
    margin-bottom: 30px;
    box-shadow: 0 20px 60px rgba(99, 102, 241, 0.3);
}

.date-icon-wrapper {
    flex-shrink: 0;
}

.date-icon-bg {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36px;
    color: white;
}

.date-input-section {
    flex: 1;
}

.date-label {
    display: block;
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 10px;
}

.date-input-large {
    background: rgba(255, 255, 255, 0.95);
    border: none;
    border-radius: 12px;
    padding: 14px 20px;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    width: 100%;
    max-width: 300px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.date-input-large:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.5);
}

.date-action-section {
    flex-shrink: 0;
}

.btn-today {
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    padding: 12px 24px;
    color: white;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s ease;
}

.btn-today:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
}

/* ===== Quick Action Bar ===== */
.quick-action-bar {
    background: white;
    border-radius: 20px;
    padding: 20px 30px;
    display: flex;
    align-items: center;
    gap: 30px;
    margin-bottom: 30px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    flex-wrap: wrap;
}

.quick-action-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 600;
    color: #64748b;
}

.quick-action-title i {
    color: #8b5cf6;
}

.quick-action-buttons {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.quick-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border: none;
    border-radius: 10px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.quick-btn-hadir {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
}

.quick-btn-tidak-hadir {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    color: white;
}

.quick-btn-reset {
    background: linear-gradient(135deg, #64748b, #475569);
    color: white;
}

.quick-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

/* ===== Teachers Grid ===== */
.teachers-attendance-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.teacher-attendance-card {
    background: white;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    border: 2px solid transparent;
}

.teacher-attendance-card:hover {
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    transform: translateY(-3px);
}

.teacher-attendance-card.selected-hadir {
    border-color: #10b981;
    background: linear-gradient(135deg, #f0fdf4, #dcfce7);
}

.teacher-attendance-card.selected-tidak-hadir {
    border-color: #ef4444;
    background: linear-gradient(135deg, #fef2f2, #fee2e2);
}

.teacher-attendance-card.selected-terlambat {
    border-color: #f59e0b;
    background: linear-gradient(135deg, #fffbeb, #fef3c7);
}

.teacher-attendance-card.selected-izin {
    border-color: #6366f1;
    background: linear-gradient(135deg, #eef2ff, #e0e7ff);
}

.teacher-card-header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;
    position: relative;
}

.teacher-avatar {
    width: 50px;
    height: 50px;
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 700;
    font-size: 16px;
}

.teacher-info {
    flex: 1;
}

.teacher-name {
    font-size: 16px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 4px 0;
}

.teacher-nip {
    font-size: 13px;
    color: #64748b;
}

.teacher-number {
    position: absolute;
    top: -8px;
    right: -8px;
    width: 28px;
    height: 28px;
    background: #f1f5f9;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 700;
    color: #64748b;
}

/* ===== Status Pills ===== */
.status-pills {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    margin-bottom: 15px;
}

.status-pill-option {
    cursor: pointer;
}

.status-pill-option input {
    display: none;
}

.status-pill {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px 12px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 600;
    transition: all 0.3s ease;
    border: 2px solid transparent;
}

.status-pill-hadir {
    background: #ecfdf5;
    color: #059669;
    border-color: #a7f3d0;
}

.status-pill-option input:checked + .status-pill-hadir {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
    box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}

.status-pill-tidak-hadir {
    background: #fef2f2;
    color: #dc2626;
    border-color: #fecaca;
}

.status-pill-option input:checked + .status-pill-tidak-hadir {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    color: white;
    box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}

.status-pill-terlambat {
    background: #fffbeb;
    color: #d97706;
    border-color: #fde68a;
}

.status-pill-option input:checked + .status-pill-terlambat {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
    box-shadow: 0 4px 15px rgba(245, 158, 11, 0.4);
}

.status-pill-izin {
    background: #eef2ff;
    color: #6366f1;
    border-color: #c7d2fe;
}

.status-pill-option input:checked + .status-pill-izin {
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    color: white;
    box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

/* ===== Keterangan Input ===== */
.keterangan-textarea {
    width: 100%;
    padding: 10px 14px;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    font-size: 13px;
    resize: none;
    transition: all 0.3s ease;
    font-family: inherit;
}

.keterangan-textarea:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.keterangan-textarea::placeholder {
    color: #94a3b8;
}

/* ===== Submit Section ===== */
.submit-section {
    background: white;
    border-radius: 24px;
    padding: 30px;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 25px;
}

.submit-summary {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    justify-content: center;
}

.summary-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 5px;
    padding: 15px 25px;
    border-radius: 16px;
    min-width: 100px;
}

.summary-item i {
    font-size: 22px;
}

.summary-count {
    font-size: 28px;
    font-weight: 800;
}

.summary-label {
    font-size: 12px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.summary-hadir {
    background: linear-gradient(135deg, #ecfdf5, #d1fae5);
    color: #059669;
}

.summary-tidak-hadir {
    background: linear-gradient(135deg, #fef2f2, #fecaca);
    color: #dc2626;
}

.summary-terlambat {
    background: linear-gradient(135deg, #fffbeb, #fde68a);
    color: #d97706;
}

.summary-izin {
    background: linear-gradient(135deg, #eef2ff, #c7d2fe);
    color: #6366f1;
}

.btn-submit-bulk {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 18px 50px;
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    border: none;
    border-radius: 16px;
    color: white;
    font-size: 18px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn-submit-bulk:hover {
    transform: translateY(-3px);
    box-shadow: 0 15px 40px rgba(99, 102, 241, 0.4);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
    .date-selection-card {
        flex-direction: column;
        text-align: center;
        padding: 25px;
    }

    .quick-action-bar {
        flex-direction: column;
        text-align: center;
    }

    .teachers-attendance-grid {
        grid-template-columns: 1fr;
    }

    .submit-summary {
        flex-wrap: wrap;
    }

    .summary-item {
        flex: 1 1 calc(50% - 10px);
        min-width: 120px;
    }
}
</style>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Update card styling when status is selected
    document.querySelectorAll('.status-radio').forEach(function(radio) {
        radio.addEventListener('change', function() {
            const card = this.closest('.teacher-attendance-card');
            const status = this.dataset.status;
            
            // Remove all selected classes
            card.classList.remove('selected-hadir', 'selected-tidak-hadir', 'selected-terlambat', 'selected-izin');
            
            // Add selected class based on status
            card.classList.add('selected-' + status);
            
            // Update summary counts
            updateSummary();
        });
    });

    updateSummary();
});

function setAllStatus(status) {
    let statusMap = {
        'Hadir': 'hadir',
        'Tidak Hadir': 'tidak-hadir',
        'Terlambat': 'terlambat',
        'Izin': 'izin'
    };

    document.querySelectorAll('.teacher-attendance-card').forEach(function(card) {
        const radio = card.querySelector(`input[value="${status}"]`);
        if (radio) {
            radio.checked = true;
            card.classList.remove('selected-hadir', 'selected-tidak-hadir', 'selected-terlambat', 'selected-izin');
            card.classList.add('selected-' + statusMap[status]);
        }
    });

    updateSummary();
}

function resetAll() {
    document.querySelectorAll('.status-radio').forEach(function(radio) {
        radio.checked = false;
    });

    document.querySelectorAll('.teacher-attendance-card').forEach(function(card) {
        card.classList.remove('selected-hadir', 'selected-tidak-hadir', 'selected-terlambat', 'selected-izin');
    });

    document.querySelectorAll('.keterangan-textarea').forEach(function(textarea) {
        textarea.value = '';
    });

    updateSummary();
}

function updateSummary() {
    const hadir = document.querySelectorAll('input[value="Hadir"]:checked').length;
    const tidakHadir = document.querySelectorAll('input[value="Tidak Hadir"]:checked').length;
    const terlambat = document.querySelectorAll('input[value="Terlambat"]:checked').length;
    const izin = document.querySelectorAll('input[value="Izin"]:checked').length;

    document.getElementById('countHadir').textContent = hadir;
    document.getElementById('countTidakHadir').textContent = tidakHadir;
    document.getElementById('countTerlambat').textContent = terlambat;
    document.getElementById('countIzin').textContent = izin;
}
</script>
@endsection
