@extends('layouts.admin')

@section('title', 'Laporan Kehadiran Guru')

@section('content')
<div class="page-header">
    <div class="page-header-content">
        <h1 class="page-title">
            <i class="fas fa-chart-bar"></i>
            Laporan Kehadiran Guru
        </h1>
        <p class="page-subtitle">Analisis dan statistik kehadiran guru</p>
    </div>
    <a href="{{ route('teacher-attendance.index') }}" class="btn btn-glass">
        <i class="fas fa-arrow-left"></i> Kembali
    </a>
</div>

<!-- Filter Section -->
<div class="report-filter-card">
    <form action="{{ route('teacher-attendance.report') }}" method="GET" class="filter-form">
        <div class="filter-grid">
            <div class="filter-item">
                <label class="filter-label">
                    <i class="fas fa-calendar-alt"></i> Dari Tanggal
                </label>
                <input type="date" name="start_date" class="filter-input" 
                       value="{{ request('start_date', now()->startOfMonth()->format('Y-m-d')) }}">
            </div>
            <div class="filter-item">
                <label class="filter-label">
                    <i class="fas fa-calendar-alt"></i> Sampai Tanggal
                </label>
                <input type="date" name="end_date" class="filter-input" 
                       value="{{ request('end_date', now()->format('Y-m-d')) }}">
            </div>
            <div class="filter-item">
                <label class="filter-label">
                    <i class="fas fa-user"></i> Guru
                </label>
                <select name="guru_id" class="filter-select">
                    <option value="">Semua Guru</option>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ request('guru_id') == $guru->id ? 'selected' : '' }}>
                            {{ $guru->nama }}
                        </option>
                    @endforeach
                </select>
            </div>
            <div class="filter-actions">
                <button type="submit" class="btn-filter">
                    <i class="fas fa-search"></i> Tampilkan
                </button>
                <a href="{{ route('teacher-attendance.report') }}" class="btn-filter-reset">
                    <i class="fas fa-undo"></i> Reset
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Summary Stats -->
<div class="stats-overview">
    <div class="stat-card stat-total">
        <div class="stat-icon-wrapper">
            <div class="stat-icon">
                <i class="fas fa-calendar-check"></i>
            </div>
        </div>
        <div class="stat-content">
            <span class="stat-value">{{ $summary['total'] ?? 0 }}</span>
            <span class="stat-label">Total Entri</span>
        </div>
        <div class="stat-decoration"></div>
    </div>

    <div class="stat-card stat-hadir">
        <div class="stat-icon-wrapper">
            <div class="stat-icon">
                <i class="fas fa-check-circle"></i>
            </div>
        </div>
        <div class="stat-content">
            <span class="stat-value">{{ $summary['hadir'] ?? 0 }}</span>
            <span class="stat-label">Hadir</span>
            <span class="stat-percentage">{{ $summary['total'] > 0 ? round(($summary['hadir'] / $summary['total']) * 100, 1) : 0 }}%</span>
        </div>
        <div class="stat-decoration"></div>
    </div>

    <div class="stat-card stat-tidak-hadir">
        <div class="stat-icon-wrapper">
            <div class="stat-icon">
                <i class="fas fa-times-circle"></i>
            </div>
        </div>
        <div class="stat-content">
            <span class="stat-value">{{ $summary['tidak_hadir'] ?? 0 }}</span>
            <span class="stat-label">Tidak Hadir</span>
            <span class="stat-percentage">{{ $summary['total'] > 0 ? round(($summary['tidak_hadir'] / $summary['total']) * 100, 1) : 0 }}%</span>
        </div>
        <div class="stat-decoration"></div>
    </div>

    <div class="stat-card stat-terlambat">
        <div class="stat-icon-wrapper">
            <div class="stat-icon">
                <i class="fas fa-clock"></i>
            </div>
        </div>
        <div class="stat-content">
            <span class="stat-value">{{ $summary['terlambat'] ?? 0 }}</span>
            <span class="stat-label">Terlambat</span>
            <span class="stat-percentage">{{ $summary['total'] > 0 ? round(($summary['terlambat'] / $summary['total']) * 100, 1) : 0 }}%</span>
        </div>
        <div class="stat-decoration"></div>
    </div>

    <div class="stat-card stat-izin">
        <div class="stat-icon-wrapper">
            <div class="stat-icon">
                <i class="fas fa-envelope-open-text"></i>
            </div>
        </div>
        <div class="stat-content">
            <span class="stat-value">{{ $summary['izin'] ?? 0 }}</span>
            <span class="stat-label">Izin</span>
            <span class="stat-percentage">{{ $summary['total'] > 0 ? round(($summary['izin'] / $summary['total']) * 100, 1) : 0 }}%</span>
        </div>
        <div class="stat-decoration"></div>
    </div>
</div>

<!-- Chart Section -->
<div class="charts-section">
    <div class="chart-card">
        <div class="chart-header">
            <h3><i class="fas fa-chart-pie"></i> Distribusi Kehadiran</h3>
        </div>
        <div class="chart-body">
            <div class="pie-chart-container">
                <div class="pie-chart">
                    @php
                        $total = $summary['total'] ?? 1;
                        $hadirPercent = ($summary['hadir'] / max($total, 1)) * 100;
                        $tidakHadirPercent = ($summary['tidak_hadir'] / max($total, 1)) * 100;
                        $terlambatPercent = ($summary['terlambat'] / max($total, 1)) * 100;
                        $izinPercent = ($summary['izin'] / max($total, 1)) * 100;
                        
                        $gradientStops = [];
                        $currentStop = 0;
                        
                        if ($hadirPercent > 0) {
                            $gradientStops[] = "#10b981 {$currentStop}%";
                            $currentStop += $hadirPercent;
                            $gradientStops[] = "#10b981 {$currentStop}%";
                        }
                        if ($tidakHadirPercent > 0) {
                            $gradientStops[] = "#ef4444 {$currentStop}%";
                            $currentStop += $tidakHadirPercent;
                            $gradientStops[] = "#ef4444 {$currentStop}%";
                        }
                        if ($terlambatPercent > 0) {
                            $gradientStops[] = "#f59e0b {$currentStop}%";
                            $currentStop += $terlambatPercent;
                            $gradientStops[] = "#f59e0b {$currentStop}%";
                        }
                        if ($izinPercent > 0) {
                            $gradientStops[] = "#6366f1 {$currentStop}%";
                            $currentStop += $izinPercent;
                            $gradientStops[] = "#6366f1 {$currentStop}%";
                        }
                    @endphp
                    <div class="pie-visual" style="background: conic-gradient({{ implode(', ', $gradientStops) ?: '#e2e8f0 0% 100%' }});">
                        <div class="pie-center">
                            <span class="pie-total">{{ $summary['total'] ?? 0 }}</span>
                            <span class="pie-label">Total</span>
                        </div>
                    </div>
                </div>
                <div class="pie-legend">
                    <div class="legend-item">
                        <span class="legend-color" style="background: #10b981;"></span>
                        <span class="legend-text">Hadir</span>
                        <span class="legend-value">{{ $summary['hadir'] ?? 0 }}</span>
                    </div>
                    <div class="legend-item">
                        <span class="legend-color" style="background: #ef4444;"></span>
                        <span class="legend-text">Tidak Hadir</span>
                        <span class="legend-value">{{ $summary['tidak_hadir'] ?? 0 }}</span>
                    </div>
                    <div class="legend-item">
                        <span class="legend-color" style="background: #f59e0b;"></span>
                        <span class="legend-text">Terlambat</span>
                        <span class="legend-value">{{ $summary['terlambat'] ?? 0 }}</span>
                    </div>
                    <div class="legend-item">
                        <span class="legend-color" style="background: #6366f1;"></span>
                        <span class="legend-text">Izin</span>
                        <span class="legend-value">{{ $summary['izin'] ?? 0 }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="chart-card">
        <div class="chart-header">
            <h3><i class="fas fa-chart-bar"></i> Progress Bar Kehadiran</h3>
        </div>
        <div class="chart-body">
            <div class="progress-bars">
                <div class="progress-item">
                    <div class="progress-header">
                        <span class="progress-label">
                            <i class="fas fa-check-circle text-success"></i> Hadir
                        </span>
                        <span class="progress-value">{{ $summary['total'] > 0 ? round(($summary['hadir'] / $summary['total']) * 100, 1) : 0 }}%</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill progress-hadir" style="width: {{ $summary['total'] > 0 ? ($summary['hadir'] / $summary['total']) * 100 : 0 }}%;"></div>
                    </div>
                </div>

                <div class="progress-item">
                    <div class="progress-header">
                        <span class="progress-label">
                            <i class="fas fa-times-circle text-danger"></i> Tidak Hadir
                        </span>
                        <span class="progress-value">{{ $summary['total'] > 0 ? round(($summary['tidak_hadir'] / $summary['total']) * 100, 1) : 0 }}%</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill progress-tidak-hadir" style="width: {{ $summary['total'] > 0 ? ($summary['tidak_hadir'] / $summary['total']) * 100 : 0 }}%;"></div>
                    </div>
                </div>

                <div class="progress-item">
                    <div class="progress-header">
                        <span class="progress-label">
                            <i class="fas fa-clock text-warning"></i> Terlambat
                        </span>
                        <span class="progress-value">{{ $summary['total'] > 0 ? round(($summary['terlambat'] / $summary['total']) * 100, 1) : 0 }}%</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill progress-terlambat" style="width: {{ $summary['total'] > 0 ? ($summary['terlambat'] / $summary['total']) * 100 : 0 }}%;"></div>
                    </div>
                </div>

                <div class="progress-item">
                    <div class="progress-header">
                        <span class="progress-label">
                            <i class="fas fa-envelope text-primary"></i> Izin
                        </span>
                        <span class="progress-value">{{ $summary['total'] > 0 ? round(($summary['izin'] / $summary['total']) * 100, 1) : 0 }}%</span>
                    </div>
                    <div class="progress-track">
                        <div class="progress-fill progress-izin" style="width: {{ $summary['total'] > 0 ? ($summary['izin'] / $summary['total']) * 100 : 0 }}%;"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Per Teacher Stats -->
@if(isset($guruStats) && count($guruStats) > 0)
<div class="teacher-stats-section">
    <div class="section-header">
        <h2><i class="fas fa-users"></i> Statistik Per Guru</h2>
    </div>
    <div class="teacher-stats-grid">
        @foreach($guruStats as $stat)
            <div class="teacher-stat-card">
                <div class="teacher-stat-header">
                    <div class="teacher-avatar-small">
                        {{ strtoupper(substr($stat['nama'], 0, 2)) }}
                    </div>
                    <div class="teacher-stat-info">
                        <h4>{{ $stat['nama'] }}</h4>
                        <span>{{ $stat['nip'] }}</span>
                    </div>
                </div>
                <div class="teacher-stat-body">
                    <div class="mini-stat mini-hadir">
                        <i class="fas fa-check"></i>
                        <span>{{ $stat['hadir'] }}</span>
                    </div>
                    <div class="mini-stat mini-tidak-hadir">
                        <i class="fas fa-times"></i>
                        <span>{{ $stat['tidak_hadir'] }}</span>
                    </div>
                    <div class="mini-stat mini-terlambat">
                        <i class="fas fa-clock"></i>
                        <span>{{ $stat['terlambat'] }}</span>
                    </div>
                    <div class="mini-stat mini-izin">
                        <i class="fas fa-envelope"></i>
                        <span>{{ $stat['izin'] }}</span>
                    </div>
                </div>
                <div class="teacher-attendance-rate">
                    @php
                        $totalGuru = $stat['hadir'] + $stat['tidak_hadir'] + $stat['terlambat'] + $stat['izin'];
                        $ratePercent = $totalGuru > 0 ? round((($stat['hadir'] + $stat['terlambat']) / $totalGuru) * 100, 1) : 0;
                    @endphp
                    <div class="rate-label">Tingkat Kehadiran</div>
                    <div class="rate-bar">
                        <div class="rate-fill" style="width: {{ $ratePercent }}%;"></div>
                    </div>
                    <div class="rate-value">{{ $ratePercent }}%</div>
                </div>
            </div>
        @endforeach
    </div>
</div>
@endif

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
    color: #8b5cf6;
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

/* ===== Filter Section ===== */
.report-filter-card {
    background: white;
    border-radius: 20px;
    padding: 25px 30px;
    margin-bottom: 30px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.filter-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    align-items: flex-end;
}

.filter-item {
    flex: 1;
    min-width: 180px;
}

.filter-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    font-weight: 600;
    color: #64748b;
    margin-bottom: 8px;
}

.filter-label i {
    color: #8b5cf6;
}

.filter-input, .filter-select {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 14px;
    transition: all 0.3s ease;
}

.filter-input:focus, .filter-select:focus {
    outline: none;
    border-color: #8b5cf6;
    box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.filter-actions {
    display: flex;
    gap: 10px;
}

.btn-filter {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    border: none;
    border-radius: 12px;
    color: white;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn-filter:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.btn-filter-reset {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    background: #f1f5f9;
    border: none;
    border-radius: 12px;
    color: #64748b;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    transition: all 0.3s ease;
}

.btn-filter-reset:hover {
    background: #e2e8f0;
    color: #1e293b;
}

/* ===== Stats Overview ===== */
.stats-overview {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.stat-card {
    background: white;
    border-radius: 20px;
    padding: 25px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    display: flex;
    align-items: center;
    gap: 20px;
}

.stat-icon-wrapper {
    flex-shrink: 0;
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
}

.stat-total .stat-icon {
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    color: white;
}

.stat-hadir .stat-icon {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
}

.stat-tidak-hadir .stat-icon {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    color: white;
}

.stat-terlambat .stat-icon {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
}

.stat-izin .stat-icon {
    background: linear-gradient(135deg, #8b5cf6, #7c3aed);
    color: white;
}

.stat-content {
    display: flex;
    flex-direction: column;
}

.stat-value {
    font-size: 32px;
    font-weight: 800;
    color: #1e293b;
    line-height: 1;
}

.stat-label {
    font-size: 14px;
    color: #64748b;
    margin-top: 4px;
}

.stat-percentage {
    font-size: 13px;
    font-weight: 600;
    color: #8b5cf6;
    margin-top: 4px;
}

.stat-decoration {
    position: absolute;
    right: -20px;
    bottom: -20px;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    opacity: 0.1;
}

.stat-hadir .stat-decoration { background: #10b981; }
.stat-tidak-hadir .stat-decoration { background: #ef4444; }
.stat-terlambat .stat-decoration { background: #f59e0b; }
.stat-izin .stat-decoration { background: #8b5cf6; }

/* ===== Charts Section ===== */
.charts-section {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 25px;
    margin-bottom: 30px;
}

.chart-card {
    background: white;
    border-radius: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    overflow: hidden;
}

.chart-header {
    padding: 20px 25px;
    border-bottom: 1px solid #f1f5f9;
}

.chart-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 10px;
}

.chart-header i {
    color: #8b5cf6;
}

.chart-body {
    padding: 25px;
}

/* ===== Pie Chart ===== */
.pie-chart-container {
    display: flex;
    align-items: center;
    gap: 40px;
    flex-wrap: wrap;
    justify-content: center;
}

.pie-visual {
    width: 200px;
    height: 200px;
    border-radius: 50%;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pie-center {
    width: 120px;
    height: 120px;
    background: white;
    border-radius: 50%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.pie-total {
    font-size: 36px;
    font-weight: 800;
    color: #1e293b;
}

.pie-label {
    font-size: 12px;
    color: #64748b;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.pie-legend {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 12px;
}

.legend-color {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    flex-shrink: 0;
}

.legend-text {
    font-size: 14px;
    color: #64748b;
    flex: 1;
}

.legend-value {
    font-size: 16px;
    font-weight: 700;
    color: #1e293b;
}

/* ===== Progress Bars ===== */
.progress-bars {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.progress-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.progress-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.progress-label {
    font-size: 14px;
    font-weight: 600;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 8px;
}

.progress-value {
    font-size: 14px;
    font-weight: 700;
    color: #8b5cf6;
}

.progress-track {
    height: 12px;
    background: #f1f5f9;
    border-radius: 6px;
    overflow: hidden;
}

.progress-fill {
    height: 100%;
    border-radius: 6px;
    transition: width 0.8s ease;
}

.progress-hadir { background: linear-gradient(135deg, #10b981, #059669); }
.progress-tidak-hadir { background: linear-gradient(135deg, #ef4444, #dc2626); }
.progress-terlambat { background: linear-gradient(135deg, #f59e0b, #d97706); }
.progress-izin { background: linear-gradient(135deg, #6366f1, #8b5cf6); }

.text-success { color: #10b981; }
.text-danger { color: #ef4444; }
.text-warning { color: #f59e0b; }
.text-primary { color: #6366f1; }

/* ===== Teacher Stats Section ===== */
.teacher-stats-section {
    margin-top: 30px;
}

.section-header {
    margin-bottom: 25px;
}

.section-header h2 {
    font-size: 22px;
    font-weight: 700;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0;
}

.section-header i {
    color: #8b5cf6;
}

.teacher-stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.teacher-stat-card {
    background: white;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.teacher-stat-header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #f1f5f9;
}

.teacher-avatar-small {
    width: 45px;
    height: 45px;
    background: linear-gradient(135deg, #6366f1, #8b5cf6);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 700;
    font-size: 14px;
}

.teacher-stat-info h4 {
    margin: 0 0 2px 0;
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
}

.teacher-stat-info span {
    font-size: 12px;
    color: #64748b;
}

.teacher-stat-body {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    margin-bottom: 15px;
}

.mini-stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 10px;
    border-radius: 10px;
}

.mini-stat i {
    font-size: 14px;
}

.mini-stat span {
    font-size: 16px;
    font-weight: 700;
}

.mini-hadir {
    background: #ecfdf5;
    color: #059669;
}

.mini-tidak-hadir {
    background: #fef2f2;
    color: #dc2626;
}

.mini-terlambat {
    background: #fffbeb;
    color: #d97706;
}

.mini-izin {
    background: #eef2ff;
    color: #6366f1;
}

.teacher-attendance-rate {
    display: flex;
    align-items: center;
    gap: 10px;
}

.rate-label {
    font-size: 12px;
    color: #64748b;
    flex-shrink: 0;
}

.rate-bar {
    flex: 1;
    height: 8px;
    background: #f1f5f9;
    border-radius: 4px;
    overflow: hidden;
}

.rate-fill {
    height: 100%;
    background: linear-gradient(135deg, #10b981, #059669);
    border-radius: 4px;
    transition: width 0.8s ease;
}

.rate-value {
    font-size: 14px;
    font-weight: 700;
    color: #10b981;
    min-width: 50px;
    text-align: right;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
    .filter-grid {
        flex-direction: column;
    }

    .filter-item {
        min-width: 100%;
    }

    .charts-section {
        grid-template-columns: 1fr;
    }

    .pie-chart-container {
        flex-direction: column;
    }

    .stats-overview {
        grid-template-columns: repeat(2, 1fr);
    }
}
</style>
@endsection
