@extends('layouts.admin')

@section('title', 'Kehadiran Guru')

@section('content')
<div class="page-header">
    <div class="page-header-content">
        <h1 class="page-title">
            <i class="fas fa-clipboard-check"></i>
            Kehadiran Guru
        </h1>
        <p class="page-subtitle">Kelola data kehadiran guru sekolah dengan mudah</p>
    </div>
    <div class="page-actions">
        <a href="{{ route('teacher-attendance.bulk-create') }}" class="btn btn-glass">
            <i class="fas fa-users"></i> Input Massal
        </a>
        <a href="{{ route('teacher-attendance.report') }}" class="btn btn-glass">
            <i class="fas fa-chart-bar"></i> Laporan
        </a>
        <a href="{{ route('teacher-attendance.create') }}" class="btn btn-primary btn-glow">
            <i class="fas fa-plus"></i> Tambah Kehadiran
        </a>
    </div>
</div>

<!-- Animated Statistics Cards -->
<div class="attendance-stats">
    <div class="stat-card-animated stat-total">
        <div class="stat-bg-icon">
            <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
            <div class="stat-number" data-count="{{ $stats['total_guru'] }}">{{ $stats['total_guru'] }}</div>
            <div class="stat-label">Total Guru</div>
            <div class="stat-bar">
                <div class="stat-bar-fill" style="width: 100%;"></div>
            </div>
        </div>
        <div class="stat-icon-wrapper">
            <i class="fas fa-users"></i>
        </div>
    </div>

    <div class="stat-card-animated stat-hadir">
        <div class="stat-bg-icon">
            <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
            <div class="stat-number" data-count="{{ $stats['hadir'] }}">{{ $stats['hadir'] }}</div>
            <div class="stat-label">Hadir Hari Ini</div>
            <div class="stat-bar">
                <div class="stat-bar-fill" style="width: {{ $stats['total_guru'] > 0 ? ($stats['hadir'] / $stats['total_guru']) * 100 : 0 }}%;"></div>
            </div>
        </div>
        <div class="stat-icon-wrapper pulse-green">
            <i class="fas fa-check-circle"></i>
        </div>
    </div>

    <div class="stat-card-animated stat-tidak-hadir">
        <div class="stat-bg-icon">
            <i class="fas fa-times-circle"></i>
        </div>
        <div class="stat-content">
            <div class="stat-number" data-count="{{ $stats['tidak_hadir'] }}">{{ $stats['tidak_hadir'] }}</div>
            <div class="stat-label">Tidak Hadir</div>
            <div class="stat-bar">
                <div class="stat-bar-fill" style="width: {{ $stats['total_guru'] > 0 ? ($stats['tidak_hadir'] / $stats['total_guru']) * 100 : 0 }}%;"></div>
            </div>
        </div>
        <div class="stat-icon-wrapper pulse-red">
            <i class="fas fa-times-circle"></i>
        </div>
    </div>

    <div class="stat-card-animated stat-terlambat">
        <div class="stat-bg-icon">
            <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
            <div class="stat-number" data-count="{{ $stats['terlambat'] }}">{{ $stats['terlambat'] }}</div>
            <div class="stat-label">Terlambat</div>
            <div class="stat-bar">
                <div class="stat-bar-fill" style="width: {{ $stats['total_guru'] > 0 ? ($stats['terlambat'] / $stats['total_guru']) * 100 : 0 }}%;"></div>
            </div>
        </div>
        <div class="stat-icon-wrapper pulse-orange">
            <i class="fas fa-clock"></i>
        </div>
    </div>

    <div class="stat-card-animated stat-izin">
        <div class="stat-bg-icon">
            <i class="fas fa-envelope-open-text"></i>
        </div>
        <div class="stat-content">
            <div class="stat-number" data-count="{{ $stats['izin'] }}">{{ $stats['izin'] }}</div>
            <div class="stat-label">Izin</div>
            <div class="stat-bar">
                <div class="stat-bar-fill" style="width: {{ $stats['total_guru'] > 0 ? ($stats['izin'] / $stats['total_guru']) * 100 : 0 }}%;"></div>
            </div>
        </div>
        <div class="stat-icon-wrapper pulse-purple">
            <i class="fas fa-envelope-open-text"></i>
        </div>
    </div>
</div>

@if(session('success'))
    <div class="alert alert-success alert-dismissible fade-in">
        <div class="alert-icon">
            <i class="fas fa-check-circle"></i>
        </div>
        <div class="alert-content">
            <strong>Berhasil!</strong>
            <p>{{ session('success') }}</p>
        </div>
        <button class="alert-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    </div>
@endif

<!-- Modern Filter Section -->
<div class="filter-card">
    <div class="filter-header">
        <div class="filter-title">
            <i class="fas fa-filter"></i>
            <span>Filter & Pencarian</span>
        </div>
        <button class="filter-toggle" onclick="toggleFilter()">
            <i class="fas fa-chevron-down"></i>
        </button>
    </div>
    <div class="filter-body" id="filterBody">
        <form action="{{ route('teacher-attendance.index') }}" method="GET" class="filter-form">
            <div class="filter-grid">
                <div class="filter-item">
                    <label class="filter-label">
                        <i class="fas fa-calendar-day"></i> Tanggal
                    </label>
                    <input type="date" name="tanggal" class="filter-input" value="{{ request('tanggal') }}">
                </div>
                <div class="filter-item">
                    <label class="filter-label">
                        <i class="fas fa-chalkboard-teacher"></i> Guru
                    </label>
                    <select name="guru_id" class="filter-input">
                        <option value="">🔍 Semua Guru</option>
                        @foreach($gurus as $guru)
                            <option value="{{ $guru->id }}" {{ request('guru_id') == $guru->id ? 'selected' : '' }}>
                                {{ $guru->nama }}
                            </option>
                        @endforeach
                    </select>
                </div>
                <div class="filter-item">
                    <label class="filter-label">
                        <i class="fas fa-clipboard-list"></i> Status
                    </label>
                    <select name="status" class="filter-input">
                        <option value="">📋 Semua Status</option>
                        <option value="Hadir" {{ request('status') == 'Hadir' ? 'selected' : '' }}>✅ Hadir</option>
                        <option value="Tidak Hadir" {{ request('status') == 'Tidak Hadir' ? 'selected' : '' }}>❌ Tidak Hadir</option>
                        <option value="Terlambat" {{ request('status') == 'Terlambat' ? 'selected' : '' }}>⏰ Terlambat</option>
                        <option value="Izin" {{ request('status') == 'Izin' ? 'selected' : '' }}>📧 Izin</option>
                    </select>
                </div>
                <div class="filter-item filter-actions">
                    <button type="submit" class="btn-filter-search">
                        <i class="fas fa-search"></i> Cari
                    </button>
                    <a href="{{ route('teacher-attendance.index') }}" class="btn-filter-reset">
                        <i class="fas fa-redo"></i>
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Modern Data Table -->
<div class="table-card">
    <div class="table-header">
        <div class="table-title">
            <i class="fas fa-list-alt"></i>
            <span>Data Kehadiran Guru</span>
        </div>
        <div class="table-meta">
            <span class="record-count">
                <i class="fas fa-database"></i>
                {{ $attendances->total() }} Data
            </span>
            <span class="today-date">
                <i class="fas fa-calendar-alt"></i>
                {{ now()->isoFormat('dddd, D MMMM Y') }}
            </span>
        </div>
    </div>
    <div class="table-body">
        @if($attendances->count() > 0)
            <div class="table-responsive">
                <table class="modern-table">
                    <thead>
                        <tr>
                            <th class="th-center" width="60">
                                <span class="th-content">No</span>
                            </th>
                            <th>
                                <span class="th-content"><i class="fas fa-user-tie"></i> Guru</span>
                            </th>
                            <th>
                                <span class="th-content"><i class="fas fa-calendar"></i> Tanggal</span>
                            </th>
                            <th class="th-center">
                                <span class="th-content"><i class="fas fa-info-circle"></i> Status</span>
                            </th>
                            <th class="th-center">
                                <span class="th-content"><i class="fas fa-sign-in-alt"></i> Masuk</span>
                            </th>
                            <th class="th-center">
                                <span class="th-content"><i class="fas fa-sign-out-alt"></i> Keluar</span>
                            </th>
                            <th>
                                <span class="th-content"><i class="fas fa-comment-alt"></i> Keterangan</span>
                            </th>
                            <th class="th-center" width="120">
                                <span class="th-content"><i class="fas fa-cogs"></i> Aksi</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($attendances as $index => $attendance)
                            <tr class="table-row fade-in-row" style="animation-delay: {{ $index * 0.05 }}s">
                                <td class="td-center">
                                    <span class="row-number">{{ $attendances->firstItem() + $index }}</span>
                                </td>
                                <td>
                                    <div class="teacher-info">
                                        <div class="teacher-avatar" style="background: {{ ['#6366f1', '#8b5cf6', '#ec4899', '#14b8a6', '#f59e0b'][($attendance->guru->id ?? 0) % 5] }};">
                                            {{ strtoupper(substr($attendance->guru->nama ?? 'G', 0, 1)) }}
                                        </div>
                                        <div class="teacher-details">
                                            <span class="teacher-name">{{ $attendance->guru->nama ?? 'N/A' }}</span>
                                            <span class="teacher-nip">
                                                <i class="fas fa-id-badge"></i> {{ $attendance->guru->nip ?? '-' }}
                                            </span>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <div class="date-info">
                                        <span class="date-main">{{ $attendance->tanggal->format('d M Y') }}</span>
                                        <span class="date-day">{{ $attendance->tanggal->isoFormat('dddd') }}</span>
                                    </div>
                                </td>
                                <td class="td-center">
                                    @if($attendance->status == 'Hadir')
                                        <span class="status-badge status-hadir">
                                            <i class="fas fa-check-circle"></i> Hadir
                                        </span>
                                    @elseif($attendance->status == 'Tidak Hadir')
                                        <span class="status-badge status-tidak-hadir">
                                            <i class="fas fa-times-circle"></i> Tidak Hadir
                                        </span>
                                    @elseif($attendance->status == 'Terlambat')
                                        <span class="status-badge status-terlambat">
                                            <i class="fas fa-clock"></i> Terlambat
                                        </span>
                                    @elseif($attendance->status == 'Izin')
                                        <span class="status-badge status-izin">
                                            <i class="fas fa-envelope"></i> Izin
                                        </span>
                                    @endif
                                </td>
                                <td class="td-center">
                                    @if($attendance->jam_masuk)
                                        <span class="time-badge time-in">
                                            <i class="fas fa-arrow-right"></i>
                                            {{ \Carbon\Carbon::parse($attendance->jam_masuk)->format('H:i') }}
                                        </span>
                                    @else
                                        <span class="time-empty">—</span>
                                    @endif
                                </td>
                                <td class="td-center">
                                    @if($attendance->jam_keluar)
                                        <span class="time-badge time-out">
                                            <i class="fas fa-arrow-left"></i>
                                            {{ \Carbon\Carbon::parse($attendance->jam_keluar)->format('H:i') }}
                                        </span>
                                    @else
                                        <span class="time-empty">—</span>
                                    @endif
                                </td>
                                <td>
                                    @if($attendance->keterangan)
                                        <div class="keterangan-text" title="{{ $attendance->keterangan }}">
                                            <i class="fas fa-quote-left"></i>
                                            {{ Str::limit($attendance->keterangan, 25) }}
                                        </div>
                                    @else
                                        <span class="no-keterangan">—</span>
                                    @endif
                                </td>
                                <td class="td-center">
                                    <div class="action-group">
                                        <a href="{{ route('teacher-attendance.edit', $attendance->id) }}" class="action-btn action-edit" title="Edit">
                                            <i class="fas fa-pen"></i>
                                        </a>
                                        <form action="{{ route('teacher-attendance.destroy', $attendance->id) }}" method="POST" class="inline-form">
                                            @csrf
                                            @method('DELETE')
                                            <button type="submit" class="action-btn action-delete" onclick="return confirm('Yakin ingin menghapus data ini?')" title="Hapus">
                                                <i class="fas fa-trash-alt"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>

            <!-- Modern Pagination -->
            <div class="pagination-wrapper">
                <div class="pagination-info">
                    Menampilkan {{ $attendances->firstItem() ?? 0 }} - {{ $attendances->lastItem() ?? 0 }} dari {{ $attendances->total() }} data
                </div>
                <div class="pagination-links">
                    {{ $attendances->appends(request()->query())->links() }}
                </div>
            </div>
        @else
            <div class="empty-state-modern">
                <div class="empty-illustration">
                    <div class="empty-icon-wrapper">
                        <i class="fas fa-clipboard-list"></i>
                    </div>
                    <div class="empty-circles">
                        <span class="circle c1"></span>
                        <span class="circle c2"></span>
                        <span class="circle c3"></span>
                    </div>
                </div>
                <h3 class="empty-title">Belum Ada Data Kehadiran</h3>
                <p class="empty-desc">Mulai catat kehadiran guru dengan mengklik tombol di bawah ini</p>
                <div class="empty-actions">
                    <a href="{{ route('teacher-attendance.create') }}" class="btn btn-primary btn-glow">
                        <i class="fas fa-plus"></i> Tambah Kehadiran
                    </a>
                    <a href="{{ route('teacher-attendance.bulk-create') }}" class="btn btn-glass">
                        <i class="fas fa-users"></i> Input Massal
                    </a>
                </div>
            </div>
        @endif
    </div>
</div>

<style>
/* ============================================
   TEACHER ATTENDANCE - PREMIUM UI/UX STYLES
   ============================================ */

/* Page Actions */
.page-actions {
    display: flex;
    gap: 0.75rem;
}

.btn-glass {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: var(--text);
    padding: 0.75rem 1.25rem;
    border-radius: 12px;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.3s ease;
    text-decoration: none;
}

.btn-glass:hover {
    background: rgba(99, 102, 241, 0.1);
    border-color: var(--primary);
    color: var(--primary);
    transform: translateY(-2px);
}

.btn-glow {
    box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.btn-glow:hover {
    box-shadow: 0 6px 25px rgba(99, 102, 241, 0.5);
}

/* ============================================
   ANIMATED STATISTICS CARDS
   ============================================ */
.attendance-stats {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 1.25rem;
    margin-bottom: 2rem;
}

.stat-card-animated {
    position: relative;
    border-radius: 20px;
    padding: 1.5rem;
    overflow: hidden;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    cursor: pointer;
}

.stat-card-animated:hover {
    transform: translateY(-8px) scale(1.02);
}

.stat-card-animated::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 100%);
    pointer-events: none;
}

.stat-bg-icon {
    position: absolute;
    right: -20px;
    bottom: -20px;
    font-size: 6rem;
    opacity: 0.1;
    color: white;
}

.stat-total {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    color: white;
}

.stat-hadir {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
}

.stat-tidak-hadir {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: white;
}

.stat-terlambat {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    color: white;
}

.stat-izin {
    background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
    color: white;
}

.stat-content {
    position: relative;
    z-index: 1;
}

.stat-number {
    font-size: 2.5rem;
    font-weight: 800;
    line-height: 1;
    margin-bottom: 0.25rem;
}

.stat-label {
    font-size: 0.9rem;
    opacity: 0.9;
    font-weight: 500;
}

.stat-bar {
    height: 4px;
    background: rgba(255,255,255,0.3);
    border-radius: 2px;
    margin-top: 1rem;
    overflow: hidden;
}

.stat-bar-fill {
    height: 100%;
    background: rgba(255,255,255,0.8);
    border-radius: 2px;
    transition: width 1s ease-out;
}

.stat-icon-wrapper {
    position: absolute;
    top: 1rem;
    right: 1rem;
    width: 45px;
    height: 45px;
    background: rgba(255,255,255,0.2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.25rem;
}

.pulse-green { animation: pulse-green 2s infinite; }
.pulse-red { animation: pulse-red 2s infinite; }
.pulse-orange { animation: pulse-orange 2s infinite; }
.pulse-purple { animation: pulse-purple 2s infinite; }

@keyframes pulse-green {
    0%, 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); }
    50% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
}

@keyframes pulse-red {
    0%, 100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.4); }
    50% { box-shadow: 0 0 0 10px rgba(239, 68, 68, 0); }
}

@keyframes pulse-orange {
    0%, 100% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.4); }
    50% { box-shadow: 0 0 0 10px rgba(245, 158, 11, 0); }
}

@keyframes pulse-purple {
    0%, 100% { box-shadow: 0 0 0 0 rgba(139, 92, 246, 0.4); }
    50% { box-shadow: 0 0 0 10px rgba(139, 92, 246, 0); }
}

/* ============================================
   MODERN ALERT
   ============================================ */
.alert-dismissible {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem 1.5rem;
    border-radius: 16px;
    margin-bottom: 1.5rem;
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(5, 150, 105, 0.1) 100%);
    border: 1px solid rgba(16, 185, 129, 0.3);
}

.alert-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: #10b981;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.1rem;
}

.alert-content {
    flex: 1;
}

.alert-content strong {
    color: #059669;
    display: block;
    margin-bottom: 0.25rem;
}

.alert-content p {
    margin: 0;
    color: var(--text);
    font-size: 0.9rem;
}

.alert-close {
    background: none;
    border: none;
    color: #64748b;
    cursor: pointer;
    padding: 0.5rem;
    transition: all 0.2s;
}

.alert-close:hover {
    color: #ef4444;
}

/* ============================================
   FILTER CARD
   ============================================ */
.filter-card {
    background: var(--card-bg);
    border-radius: 20px;
    box-shadow: 0 4px 20px var(--shadow);
    margin-bottom: 1.5rem;
    overflow: hidden;
    border: 1px solid var(--border);
}

.filter-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1rem 1.5rem;
    background: linear-gradient(135deg, var(--primary) 0%, #4f46e5 100%);
    color: white;
}

.filter-title {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-weight: 600;
    font-size: 1rem;
}

.filter-toggle {
    background: rgba(255,255,255,0.2);
    border: none;
    color: white;
    width: 32px;
    height: 32px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
}

.filter-toggle:hover {
    background: rgba(255,255,255,0.3);
}

.filter-body {
    padding: 1.5rem;
}

.filter-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
    align-items: end;
}

.filter-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--text);
    margin-bottom: 0.5rem;
}

.filter-input {
    width: 100%;
    padding: 0.75rem 1rem;
    border: 2px solid var(--border);
    border-radius: 12px;
    font-size: 0.9rem;
    transition: all 0.3s;
    background: var(--card-bg);
    color: var(--text);
}

.filter-input:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    outline: none;
}

.filter-actions {
    display: flex;
    gap: 0.5rem;
}

.btn-filter-search {
    flex: 1;
    padding: 0.75rem 1.5rem;
    background: linear-gradient(135deg, var(--primary) 0%, #4f46e5 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: all 0.3s;
}

.btn-filter-search:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.btn-filter-reset {
    padding: 0.75rem;
    background: var(--border);
    color: var(--text);
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s;
    text-decoration: none;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-filter-reset:hover {
    background: #e2e8f0;
    color: var(--primary);
}

/* ============================================
   TABLE CARD
   ============================================ */
.table-card {
    background: var(--card-bg);
    border-radius: 20px;
    box-shadow: 0 4px 20px var(--shadow);
    overflow: hidden;
    border: 1px solid var(--border);
}

.table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.25rem 1.5rem;
    border-bottom: 1px solid var(--border);
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, transparent 100%);
}

.table-title {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 1.1rem;
    font-weight: 700;
    color: var(--text);
}

.table-title i {
    color: var(--primary);
}

.table-meta {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.record-count, .today-date {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.85rem;
    color: #64748b;
    padding: 0.5rem 1rem;
    background: var(--border);
    border-radius: 20px;
}

.table-body {
    padding: 0;
}

/* ============================================
   MODERN TABLE
   ============================================ */
.modern-table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
}

.modern-table thead {
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.modern-table th {
    padding: 1rem 1.25rem;
    font-weight: 700;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #475569;
    border-bottom: 2px solid var(--border);
}

.th-content {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.th-center {
    text-align: center;
}

.th-center .th-content {
    justify-content: center;
}

.modern-table tbody tr {
    transition: all 0.3s ease;
}

.modern-table tbody tr:hover {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, transparent 100%);
}

.modern-table td {
    padding: 1rem 1.25rem;
    border-bottom: 1px solid var(--border);
    vertical-align: middle;
}

.td-center {
    text-align: center;
}

.row-number {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, var(--primary) 0%, #4f46e5 100%);
    color: white;
    border-radius: 8px;
    font-weight: 700;
    font-size: 0.85rem;
}

/* Teacher Info */
.teacher-info {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.teacher-avatar {
    width: 45px;
    height: 45px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 700;
    font-size: 1.1rem;
    box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}

.teacher-details {
    display: flex;
    flex-direction: column;
}

.teacher-name {
    font-weight: 600;
    color: var(--text);
    font-size: 0.95rem;
}

.teacher-nip {
    font-size: 0.8rem;
    color: #64748b;
    display: flex;
    align-items: center;
    gap: 0.35rem;
    margin-top: 0.15rem;
}

/* Date Info */
.date-info {
    display: flex;
    flex-direction: column;
}

.date-main {
    font-weight: 600;
    color: var(--text);
}

.date-day {
    font-size: 0.8rem;
    color: #64748b;
    text-transform: capitalize;
}

/* Status Badges */
.status-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.5rem 1rem;
    border-radius: 25px;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.3px;
}

.status-hadir {
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.15) 100%);
    color: #059669;
    border: 1px solid rgba(16, 185, 129, 0.3);
}

.status-tidak-hadir {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.15) 0%, rgba(220, 38, 38, 0.15) 100%);
    color: #dc2626;
    border: 1px solid rgba(239, 68, 68, 0.3);
}

.status-terlambat {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.15) 0%, rgba(217, 119, 6, 0.15) 100%);
    color: #d97706;
    border: 1px solid rgba(245, 158, 11, 0.3);
}

.status-izin {
    background: linear-gradient(135deg, rgba(139, 92, 246, 0.15) 0%, rgba(124, 58, 237, 0.15) 100%);
    color: #7c3aed;
    border: 1px solid rgba(139, 92, 246, 0.3);
}

/* Time Badges */
.time-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.35rem;
    padding: 0.4rem 0.75rem;
    border-radius: 8px;
    font-size: 0.85rem;
    font-weight: 600;
}

.time-in {
    background: rgba(16, 185, 129, 0.1);
    color: #059669;
}

.time-out {
    background: rgba(239, 68, 68, 0.1);
    color: #dc2626;
}

.time-empty, .no-keterangan {
    color: #cbd5e1;
    font-size: 1.25rem;
}

/* Keterangan */
.keterangan-text {
    font-size: 0.85rem;
    color: #64748b;
    display: flex;
    align-items: flex-start;
    gap: 0.5rem;
}

.keterangan-text i {
    color: #cbd5e1;
    font-size: 0.75rem;
    margin-top: 2px;
}

/* Action Buttons */
.action-group {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
}

.action-btn {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    border: none;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s;
    text-decoration: none;
}

.action-edit {
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(37, 99, 235, 0.1) 100%);
    color: #3b82f6;
    border: 1px solid rgba(59, 130, 246, 0.3);
}

.action-edit:hover {
    background: #3b82f6;
    color: white;
    transform: scale(1.1);
}

.action-delete {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
    color: #ef4444;
    border: 1px solid rgba(239, 68, 68, 0.3);
}

.action-delete:hover {
    background: #ef4444;
    color: white;
    transform: scale(1.1);
}

.inline-form {
    display: inline;
}

/* ============================================
   PAGINATION
   ============================================ */
.pagination-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.25rem 1.5rem;
    border-top: 1px solid var(--border);
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.02) 0%, transparent 100%);
}

.pagination-info {
    font-size: 0.9rem;
    color: #64748b;
}

/* ============================================
   EMPTY STATE
   ============================================ */
.empty-state-modern {
    padding: 4rem 2rem;
    text-align: center;
}

.empty-illustration {
    position: relative;
    display: inline-block;
    margin-bottom: 2rem;
}

.empty-icon-wrapper {
    width: 120px;
    height: 120px;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
    border-radius: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
    color: var(--primary);
    position: relative;
    z-index: 1;
}

.empty-circles {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

.circle {
    position: absolute;
    border-radius: 50%;
    border: 2px dashed rgba(99, 102, 241, 0.2);
}

.c1 { width: 150px; height: 150px; top: -75px; left: -75px; animation: rotate 20s linear infinite; }
.c2 { width: 180px; height: 180px; top: -90px; left: -90px; animation: rotate 25s linear infinite reverse; }
.c3 { width: 210px; height: 210px; top: -105px; left: -105px; animation: rotate 30s linear infinite; }

@keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.empty-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--text);
    margin-bottom: 0.5rem;
}

.empty-desc {
    color: #64748b;
    margin-bottom: 2rem;
    max-width: 400px;
    margin-left: auto;
    margin-right: auto;
}

.empty-actions {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 1rem;
}

/* ============================================
   ANIMATIONS
   ============================================ */
.fade-in {
    animation: fadeIn 0.5s ease-out;
}

.fade-in-row {
    animation: fadeInUp 0.5s ease-out forwards;
    opacity: 0;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}

/* ============================================
   RESPONSIVE
   ============================================ */
@media (max-width: 1200px) {
    .attendance-stats {
        grid-template-columns: repeat(3, 1fr);
    }
}

@media (max-width: 992px) {
    .attendance-stats {
        grid-template-columns: repeat(2, 1fr);
    }
    
    .filter-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .attendance-stats {
        grid-template-columns: 1fr;
    }
    
    .filter-grid {
        grid-template-columns: 1fr;
    }
    
    .page-actions {
        flex-direction: column;
        width: 100%;
    }
    
    .table-header {
        flex-direction: column;
        gap: 1rem;
    }
    
    .table-meta {
        width: 100%;
        justify-content: space-between;
    }
    
    .pagination-wrapper {
        flex-direction: column;
        gap: 1rem;
    }
}
</style>

<script>
function toggleFilter() {
    const body = document.getElementById('filterBody');
    const toggle = document.querySelector('.filter-toggle i');
    
    if (body.style.display === 'none') {
        body.style.display = 'block';
        toggle.classList.remove('fa-chevron-down');
        toggle.classList.add('fa-chevron-up');
    } else {
        body.style.display = 'none';
        toggle.classList.remove('fa-chevron-up');
        toggle.classList.add('fa-chevron-down');
    }
}

// Animate numbers on load
document.addEventListener('DOMContentLoaded', function() {
    const numbers = document.querySelectorAll('.stat-number');
    numbers.forEach(num => {
        const target = parseInt(num.getAttribute('data-count'));
        let current = 0;
        const increment = target / 30;
        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                num.textContent = target;
                clearInterval(timer);
            } else {
                num.textContent = Math.floor(current);
            }
        }, 30);
    });
});
</script>
@endsection
