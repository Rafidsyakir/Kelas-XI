@extends('layouts.admin')

@section('title', 'Dashboard')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-chart-line"></i>
            Dashboard Overview
        </h1>
        <p class="page-subtitle">Welcome back! Here's what's happening in your school today.</p>
    </div>
</div>

<!-- Stats Cards -->
<div class="stats-grid">
    <div class="stat-card blue">
        <div class="stat-icon">
            <i class="fas fa-users"></i>
        </div>
        <div class="stat-details">
            <div class="stat-number">{{ $total_users }}</div>
            <div class="stat-label">Total Users</div>
        </div>
    </div>

    <div class="stat-card green">
        <div class="stat-icon">
            <i class="fas fa-chalkboard-teacher"></i>
        </div>
        <div class="stat-details">
            <div class="stat-number">{{ $total_guru }}</div>
            <div class="stat-label">Total Teachers</div>
        </div>
    </div>

    <div class="stat-card orange">
        <div class="stat-icon">
            <i class="fas fa-school"></i>
        </div>
        <div class="stat-details">
            <div class="stat-number">{{ $total_kelas }}</div>
            <div class="stat-label">Total Classes</div>
        </div>
    </div>

    <div class="stat-card purple">
        <div class="stat-icon">
            <i class="fas fa-calendar-alt"></i>
        </div>
        <div class="stat-details">
            <div class="stat-number">{{ $total_jadwal }}</div>
            <div class="stat-label">Scheduled Lessons</div>
        </div>
    </div>
</div>

<!-- Teacher Attendance Today -->
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header">
        <h2 class="card-title">
            <i class="fas fa-clipboard-check"></i>
            Kehadiran Guru Hari Ini
        </h2>
        <a href="{{ route('teacher-attendance.index') }}" class="btn btn-sm btn-primary">
            <i class="fas fa-arrow-right"></i> Lihat Detail
        </a>
    </div>
    <div class="card-body">
        <div class="attendance-stats" style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem;">
            <div class="attendance-stat-card" style="background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(5, 150, 105, 0.1)); border-left: 4px solid #10b981; padding: 1rem; border-radius: 8px;">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="width: 40px; height: 40px; background: #10b981; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white;">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: #10b981;">{{ $attendance_stats['hadir'] }}</div>
                        <div style="font-size: 0.8rem; color: #64748b;">Hadir</div>
                    </div>
                </div>
            </div>
            <div class="attendance-stat-card" style="background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1)); border-left: 4px solid #ef4444; padding: 1rem; border-radius: 8px;">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="width: 40px; height: 40px; background: #ef4444; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white;">
                        <i class="fas fa-times-circle"></i>
                    </div>
                    <div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: #ef4444;">{{ $attendance_stats['tidak_hadir'] }}</div>
                        <div style="font-size: 0.8rem; color: #64748b;">Tidak Hadir</div>
                    </div>
                </div>
            </div>
            <div class="attendance-stat-card" style="background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.1)); border-left: 4px solid #f59e0b; padding: 1rem; border-radius: 8px;">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="width: 40px; height: 40px; background: #f59e0b; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white;">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: #f59e0b;">{{ $attendance_stats['terlambat'] }}</div>
                        <div style="font-size: 0.8rem; color: #64748b;">Terlambat</div>
                    </div>
                </div>
            </div>
            <div class="attendance-stat-card" style="background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(79, 70, 229, 0.1)); border-left: 4px solid #6366f1; padding: 1rem; border-radius: 8px;">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <div style="width: 40px; height: 40px; background: #6366f1; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white;">
                        <i class="fas fa-envelope"></i>
                    </div>
                    <div>
                        <div style="font-size: 1.5rem; font-weight: 700; color: #6366f1;">{{ $attendance_stats['izin'] }}</div>
                        <div style="font-size: 0.8rem; color: #64748b;">Izin</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Data Overview -->
<div class="content-grid">
    <!-- Recent Schedules -->
    <div class="data-card">
        <div class="card-header">
            <h3 class="card-title">
                <i class="fas fa-calendar-week"></i>
                Recent Schedules
            </h3>
            <a href="{{ route('jadwal.index') }}" class="btn-link">View All <i class="fas fa-arrow-right"></i></a>
        </div>
        <div class="card-body">
            @if($recent_jadwal->count() > 0)
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Subject</th>
                                <th>Teacher</th>
                                <th>Class</th>
                                <th>Day</th>
                                <th>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            @foreach($recent_jadwal as $jadwal)
                            <tr>
                                <td><strong>{{ $jadwal->mata_pelajaran }}</strong></td>
                                <td>{{ $jadwal->guru->nama }}</td>
                                <td><span class="badge badge-primary">{{ $jadwal->kelas->nama_kelas }}</span></td>
                                <td><span class="badge badge-blue">{{ $jadwal->hari }}</span></td>
                                <td>{{ substr($jadwal->jam_mulai, 0, 5) }} - {{ substr($jadwal->jam_selesai, 0, 5) }}</td>
                            </tr>
                            @endforeach
                        </tbody>
                    </table>
                </div>
            @else
                <div class="empty-state-small">
                    <i class="fas fa-calendar-times"></i>
                    <p>No schedules found. Start by adding a new schedule!</p>
                </div>
            @endif
        </div>
    </div>

    <!-- Recent Teachers -->
    <div class="data-card">
        <div class="card-header">
            <h3 class="card-title">
                <i class="fas fa-user-tie"></i>
                Recent Teachers
            </h3>
            <a href="{{ route('guru.index') }}" class="btn-link">View All <i class="fas fa-arrow-right"></i></a>
        </div>
        <div class="card-body">
            @if($recent_guru->count() > 0)
                <div class="list-group">
                    @foreach($recent_guru as $guru)
                    <div class="list-item">
                        <div class="list-avatar">
                            {{ strtoupper(substr($guru->nama, 0, 1)) }}
                        </div>
                        <div class="list-details">
                            <h4>{{ $guru->nama }}</h4>
                            <p><i class="fas fa-id-card"></i> NIP: {{ $guru->nip }} | <i class="fas fa-book"></i> {{ $guru->mata_pelajaran }}</p>
                        </div>
                        <span class="badge badge-pink">{{ $guru->jenis_kelamin }}</span>
                    </div>
                    @endforeach
                </div>
            @else
                <div class="empty-state-small">
                    <i class="fas fa-user-times"></i>
                    <p>No teachers found. Start by adding a new teacher!</p>
                </div>
            @endif
        </div>
    </div>
</div>
@endsection
