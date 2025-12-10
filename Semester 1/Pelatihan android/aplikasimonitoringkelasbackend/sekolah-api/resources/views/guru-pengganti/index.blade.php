@extends('layouts.admin')

@section('title', 'Guru Pengganti')

@section('styles')
<style>
    .filter-card {
        background: #1e293b;
        border-radius: 16px;
        padding: 20px;
        margin-bottom: 20px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
    }
    
    .filter-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 15px;
        align-items: end;
    }
    
    .filter-group label {
        display: block;
        font-size: 12px;
        font-weight: 600;
        color: #94a3b8;
        margin-bottom: 6px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    
    .filter-group input,
    .filter-group select {
        width: 100%;
        padding: 10px 14px;
        border: 2px solid #334155;
        border-radius: 10px;
        font-size: 14px;
        transition: var(--transition);
        background: #0f172a;
        color: #e2e8f0;
    }
    
    .filter-group input:focus,
    .filter-group select:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
    }

    .stats-row {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 15px;
        margin-bottom: 25px;
    }

    .mini-stat {
        background: #1e293b;
        border-radius: 12px;
        padding: 18px;
        text-align: center;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        border-left: 4px solid;
        transition: var(--transition);
    }

    .mini-stat:hover {
        transform: translateY(-3px);
    }

    .mini-stat.primary { border-left-color: #6366f1; }
    .mini-stat.success { border-left-color: #10b981; }
    .mini-stat.warning { border-left-color: #f59e0b; }
    .mini-stat.danger { border-left-color: #ef4444; }
    .mini-stat.info { border-left-color: #3b82f6; }
    .mini-stat.purple { border-left-color: #8b5cf6; }

    .mini-stat .stat-value {
        font-size: 28px;
        font-weight: 800;
        margin-bottom: 4px;
    }

    .mini-stat.primary .stat-value { color: #6366f1; }
    .mini-stat.success .stat-value { color: #10b981; }
    .mini-stat.warning .stat-value { color: #f59e0b; }
    .mini-stat.danger .stat-value { color: #ef4444; }
    .mini-stat.info .stat-value { color: #3b82f6; }
    .mini-stat.purple .stat-value { color: #8b5cf6; }

    .mini-stat .stat-label {
        font-size: 12px;
        color: #94a3b8;
        font-weight: 500;
    }

    .data-table-wrapper {
        background: #1e293b;
        border-radius: 16px;
        padding: 25px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
    }

    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .table-title {
        font-size: 18px;
        font-weight: 700;
        color: #e2e8f0;
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .table-title i {
        color: var(--primary);
    }

    .data-table {
        width: 100%;
        border-collapse: collapse;
    }

    .data-table th {
        text-align: left;
        padding: 14px 16px;
        background: linear-gradient(135deg, #0f172a, #1e293b);
        font-size: 12px;
        font-weight: 700;
        color: #94a3b8;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        border-bottom: 2px solid #334155;
    }

    .data-table td {
        padding: 16px;
        border-bottom: 1px solid #334155;
        font-size: 14px;
        vertical-align: middle;
        color: #e2e8f0;
    }

    .data-table tbody tr {
        transition: var(--transition);
    }

    .data-table tbody tr:hover {
        background: rgba(99, 102, 241, 0.15);
    }

    .badge {
        display: inline-flex;
        align-items: center;
        gap: 5px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
    }

    .badge-pending {
        background: rgba(245, 158, 11, 0.15);
        color: #d97706;
    }

    .badge-disetujui {
        background: rgba(16, 185, 129, 0.15);
        color: #059669;
    }

    .badge-ditolak {
        background: rgba(239, 68, 68, 0.15);
        color: #dc2626;
    }

    .badge-selesai {
        background: rgba(100, 116, 139, 0.15);
        color: #475569;
    }

    .badge-sakit {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
    }

    .badge-izin {
        background: rgba(245, 158, 11, 0.1);
        color: #f59e0b;
    }

    .badge-cuti {
        background: rgba(59, 130, 246, 0.1);
        color: #3b82f6;
    }

    .badge-dinas {
        background: rgba(99, 102, 241, 0.1);
        color: #6366f1;
    }

    .badge-lainnya {
        background: rgba(100, 116, 139, 0.1);
        color: #64748b;
    }

    .guru-info {
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .guru-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 700;
        font-size: 14px;
        color: white;
    }

    .guru-avatar.asli {
        background: linear-gradient(135deg, #ef4444, #dc2626);
    }

    .guru-avatar.pengganti {
        background: linear-gradient(135deg, #10b981, #059669);
    }

    .guru-name {
        font-weight: 600;
        color: #e2e8f0;
    }

    .action-buttons {
        display: flex;
        gap: 8px;
    }

    .btn-action {
        width: 34px;
        height: 34px;
        border-radius: 8px;
        border: none;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        transition: var(--transition);
    }

    .btn-action.approve {
        background: rgba(16, 185, 129, 0.1);
        color: #10b981;
    }

    .btn-action.approve:hover {
        background: #10b981;
        color: white;
    }

    .btn-action.reject {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
    }

    .btn-action.reject:hover {
        background: #ef4444;
        color: white;
    }

    .btn-action.edit {
        background: rgba(59, 130, 246, 0.1);
        color: #3b82f6;
    }

    .btn-action.edit:hover {
        background: #3b82f6;
        color: white;
    }

    .btn-action.delete {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
    }

    .btn-action.delete:hover {
        background: #ef4444;
        color: white;
    }

    .btn-action.complete {
        background: rgba(100, 116, 139, 0.1);
        color: #64748b;
    }

    .btn-action.complete:hover {
        background: #64748b;
        color: white;
    }

    .btn-primary {
        background: linear-gradient(135deg, var(--primary), var(--primary-dark));
        color: white;
        border: none;
        padding: 12px 24px;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
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
        padding: 10px 20px;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: var(--transition);
        text-decoration: none;
    }

    .btn-secondary:hover {
        background: #475569;
        color: white;
    }

    .arrow-icon {
        color: #94a3b8;
        font-size: 16px;
    }

    .empty-state {
        text-align: center;
        padding: 60px 20px;
    }

    .empty-state i {
        font-size: 64px;
        color: #475569;
        margin-bottom: 20px;
    }

    .empty-state h3 {
        font-size: 20px;
        color: #e2e8f0;
        margin-bottom: 10px;
    }

    .empty-state p {
        color: #94a3b8;
        margin-bottom: 20px;
    }

    .pagination-wrapper {
        display: flex;
        justify-content: center;
        margin-top: 25px;
    }

    .alert {
        padding: 16px 20px;
        border-radius: 12px;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .alert-success {
        background: rgba(16, 185, 129, 0.15);
        border-left: 4px solid #10b981;
        color: #6ee7b7;
    }

    .alert-danger {
        background: rgba(239, 68, 68, 0.15);
        border-left: 4px solid #ef4444;
        color: #fca5a5;
    }
</style>
@endsection

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-exchange-alt"></i>
            Guru Pengganti
        </h1>
        <p class="page-subtitle">Kelola penggantian guru yang tidak hadir</p>
    </div>
    <a href="{{ route('guru-pengganti.create') }}" class="btn-primary">
        <i class="fas fa-plus"></i>
        Tambah Pengganti
    </a>
</div>

@if(session('success'))
<div class="alert alert-success">
    <i class="fas fa-check-circle"></i>
    {{ session('success') }}
</div>
@endif

<!-- Statistics -->
<div class="stats-row">
    <div class="mini-stat primary">
        <div class="stat-value">{{ $stats['total'] }}</div>
        <div class="stat-label">Total Data</div>
    </div>
    <div class="mini-stat info">
        <div class="stat-value">{{ $stats['hari_ini'] }}</div>
        <div class="stat-label">Hari Ini</div>
    </div>
    <div class="mini-stat warning">
        <div class="stat-value">{{ $stats['pending'] }}</div>
        <div class="stat-label">Pending</div>
    </div>
    <div class="mini-stat success">
        <div class="stat-value">{{ $stats['disetujui'] }}</div>
        <div class="stat-label">Disetujui</div>
    </div>
    <div class="mini-stat danger">
        <div class="stat-value">{{ $stats['ditolak'] }}</div>
        <div class="stat-label">Ditolak</div>
    </div>
    <div class="mini-stat purple">
        <div class="stat-value">{{ $stats['bulan_ini'] }}</div>
        <div class="stat-label">Bulan Ini</div>
    </div>
</div>

<!-- Filter -->
<div class="filter-card">
    <form action="{{ route('guru-pengganti.index') }}" method="GET">
        <div class="filter-grid">
            <div class="filter-group">
                <label>Status</label>
                <select name="status">
                    <option value="">Semua Status</option>
                    <option value="Pending" {{ request('status') == 'Pending' ? 'selected' : '' }}>Pending</option>
                    <option value="Disetujui" {{ request('status') == 'Disetujui' ? 'selected' : '' }}>Disetujui</option>
                    <option value="Ditolak" {{ request('status') == 'Ditolak' ? 'selected' : '' }}>Ditolak</option>
                    <option value="Selesai" {{ request('status') == 'Selesai' ? 'selected' : '' }}>Selesai</option>
                </select>
            </div>
            <div class="filter-group">
                <label>Alasan</label>
                <select name="alasan">
                    <option value="">Semua Alasan</option>
                    <option value="Sakit" {{ request('alasan') == 'Sakit' ? 'selected' : '' }}>Sakit</option>
                    <option value="Izin" {{ request('alasan') == 'Izin' ? 'selected' : '' }}>Izin</option>
                    <option value="Cuti" {{ request('alasan') == 'Cuti' ? 'selected' : '' }}>Cuti</option>
                    <option value="Dinas Luar" {{ request('alasan') == 'Dinas Luar' ? 'selected' : '' }}>Dinas Luar</option>
                    <option value="Lainnya" {{ request('alasan') == 'Lainnya' ? 'selected' : '' }}>Lainnya</option>
                </select>
            </div>
            <div class="filter-group">
                <label>Dari Tanggal</label>
                <input type="date" name="tanggal_dari" value="{{ request('tanggal_dari') }}">
            </div>
            <div class="filter-group">
                <label>Sampai Tanggal</label>
                <input type="date" name="tanggal_sampai" value="{{ request('tanggal_sampai') }}">
            </div>
            <div class="filter-group">
                <button type="submit" class="btn-primary" style="width: 100%;">
                    <i class="fas fa-search"></i> Filter
                </button>
            </div>
            <div class="filter-group">
                <a href="{{ route('guru-pengganti.index') }}" class="btn-secondary" style="width: 100%; justify-content: center;">
                    <i class="fas fa-redo"></i> Reset
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Data Table -->
<div class="data-table-wrapper">
    <div class="table-header">
        <h3 class="table-title">
            <i class="fas fa-list"></i>
            Daftar Guru Pengganti
        </h3>
        <a href="{{ route('guru-pengganti.index', ['hari_ini' => 1]) }}" class="btn-secondary">
            <i class="fas fa-calendar-day"></i> Hari Ini
        </a>
    </div>

    @if($guruPengganti->count() > 0)
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Tanggal</th>
                    <th>Jadwal</th>
                    <th>Guru Asli</th>
                    <th><i class="fas fa-arrow-right arrow-icon"></i></th>
                    <th>Guru Pengganti</th>
                    <th>Alasan</th>
                    <th>Status</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                @foreach($guruPengganti as $item)
                <tr>
                    <td>
                        <strong>{{ $item->tanggal->format('d M Y') }}</strong>
                        <br>
                        <small style="color: #94a3b8;">{{ $item->tanggal->translatedFormat('l') }}</small>
                    </td>
                    <td>
                        <strong>{{ $item->jadwal->mata_pelajaran ?? 'N/A' }}</strong>
                        <br>
                        <small style="color: #94a3b8;">
                            {{ $item->jadwal->kelas->nama_kelas ?? 'N/A' }} |
                            {{ substr($item->jadwal->jam_mulai ?? '00:00', 0, 5) }}
                        </small>
                    </td>
                    <td>
                        <div class="guru-info">
                            <div class="guru-avatar asli">
                                {{ strtoupper(substr($item->guruAsli->nama ?? 'N', 0, 1)) }}
                            </div>
                            <span class="guru-name">{{ $item->guruAsli->nama ?? 'N/A' }}</span>
                        </div>
                    </td>
                    <td>
                        <i class="fas fa-arrow-right arrow-icon"></i>
                    </td>
                    <td>
                        <div class="guru-info">
                            <div class="guru-avatar pengganti">
                                {{ strtoupper(substr($item->guruPengganti->nama ?? 'N', 0, 1)) }}
                            </div>
                            <span class="guru-name">{{ $item->guruPengganti->nama ?? 'N/A' }}</span>
                        </div>
                    </td>
                    <td>
                        @php
                            $alasanClass = match($item->alasan) {
                                'Sakit' => 'badge-sakit',
                                'Izin' => 'badge-izin',
                                'Cuti' => 'badge-cuti',
                                'Dinas Luar' => 'badge-dinas',
                                default => 'badge-lainnya',
                            };
                            $alasanIcon = match($item->alasan) {
                                'Sakit' => 'fa-thermometer-half',
                                'Izin' => 'fa-file-alt',
                                'Cuti' => 'fa-umbrella-beach',
                                'Dinas Luar' => 'fa-car',
                                default => 'fa-question-circle',
                            };
                        @endphp
                        <span class="badge {{ $alasanClass }}">
                            <i class="fas {{ $alasanIcon }}"></i>
                            {{ $item->alasan }}
                        </span>
                    </td>
                    <td>
                        @php
                            $statusClass = match($item->status) {
                                'Pending' => 'badge-pending',
                                'Disetujui' => 'badge-disetujui',
                                'Ditolak' => 'badge-ditolak',
                                'Selesai' => 'badge-selesai',
                                default => 'badge-pending',
                            };
                            $statusIcon = match($item->status) {
                                'Pending' => 'fa-clock',
                                'Disetujui' => 'fa-check-circle',
                                'Ditolak' => 'fa-times-circle',
                                'Selesai' => 'fa-flag-checkered',
                                default => 'fa-clock',
                            };
                        @endphp
                        <span class="badge {{ $statusClass }}">
                            <i class="fas {{ $statusIcon }}"></i>
                            {{ $item->status }}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            @if($item->status === 'Pending')
                                <form action="{{ route('guru-pengganti.approve', $item) }}" method="POST" style="display: inline;">
                                    @csrf
                                    @method('PATCH')
                                    <button type="submit" class="btn-action approve" title="Setujui" onclick="return confirm('Setujui penggantian ini?')">
                                        <i class="fas fa-check"></i>
                                    </button>
                                </form>
                                <form action="{{ route('guru-pengganti.reject', $item) }}" method="POST" style="display: inline;">
                                    @csrf
                                    @method('PATCH')
                                    <button type="submit" class="btn-action reject" title="Tolak" onclick="return confirm('Tolak penggantian ini?')">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </form>
                            @endif
                            @if($item->status === 'Disetujui')
                                <form action="{{ route('guru-pengganti.complete', $item) }}" method="POST" style="display: inline;">
                                    @csrf
                                    @method('PATCH')
                                    <button type="submit" class="btn-action complete" title="Selesai" onclick="return confirm('Tandai selesai?')">
                                        <i class="fas fa-flag-checkered"></i>
                                    </button>
                                </form>
                            @endif
                            <a href="{{ route('guru-pengganti.edit', $item) }}" class="btn-action edit" title="Edit">
                                <i class="fas fa-edit"></i>
                            </a>
                            <form action="{{ route('guru-pengganti.destroy', $item) }}" method="POST" style="display: inline;">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="btn-action delete" title="Hapus" onclick="return confirm('Hapus data ini?')">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
                @endforeach
            </tbody>
        </table>
    </div>

    <div class="pagination-wrapper">
        {{ $guruPengganti->withQueryString()->links() }}
    </div>
    @else
    <div class="empty-state">
        <i class="fas fa-exchange-alt"></i>
        <h3>Belum Ada Data Guru Pengganti</h3>
        <p>Mulai dengan menambahkan data penggantian guru baru.</p>
        <a href="{{ route('guru-pengganti.create') }}" class="btn-primary">
            <i class="fas fa-plus"></i>
            Tambah Pengganti
        </a>
    </div>
    @endif
</div>
@endsection
