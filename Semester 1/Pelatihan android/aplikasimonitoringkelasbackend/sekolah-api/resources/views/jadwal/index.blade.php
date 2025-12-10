@extends('layouts.admin')

@section('title', 'Jadwal Pelajaran')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-calendar-alt"></i>
            Jadwal Pelajaran (Schedules)
        </h1>
        <p class="page-subtitle">Manage class schedules and timetables</p>
    </div>
    <a href="{{ route('jadwal.create') }}" class="btn-primary">
        <i class="fas fa-plus"></i> Add New Schedule
    </a>
</div>

@if(session('success'))
<div class="alert alert-success">
    <i class="fas fa-check-circle"></i>
    {{ session('success') }}
</div>
@endif

<div class="data-card">
    <div class="card-header">
        <h3 class="card-title">
            <i class="fas fa-list"></i>
            Schedules List
        </h3>
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Search schedules...">
        </div>
    </div>
    <div class="card-body">
        @if($jadwals->count() > 0)
            <div class="table-responsive">
                <table class="data-table" id="jadwalTable">
                    <thead>
                        <tr>
                            <th>Day</th>
                            <th>Subject</th>
                            <th>Class</th>
                            <th>Teacher</th>
                            <th>Time</th>
                            <th>Room</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($jadwals as $jadwal)
                        <tr>
                            <td><span class="badge badge-day">{{ $jadwal->hari }}</span></td>
                            <td><strong><i class="fas fa-book"></i> {{ $jadwal->mata_pelajaran }}</strong></td>
                            <td><span class="badge badge-primary">{{ $jadwal->kelas->nama_kelas }}</span></td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 8px;">
                                    <div class="list-avatar" style="width: 30px; height: 30px; font-size: 12px;">
                                        {{ strtoupper(substr($jadwal->guru->nama, 0, 1)) }}
                                    </div>
                                    <span>{{ $jadwal->guru->nama }}</span>
                                </div>
                            </td>
                            <td>
                                <span class="badge badge-time">
                                    <i class="fas fa-clock"></i> {{ substr($jadwal->jam_mulai, 0, 5) }} - {{ substr($jadwal->jam_selesai, 0, 5) }}
                                </span>
                            </td>
                            <td><i class="fas fa-door-open"></i> {{ $jadwal->ruangan }}</td>
                            <td>
                                <div class="action-buttons">
                                    <a href="{{ route('jadwal.edit', $jadwal->id) }}" class="btn-action btn-edit" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form action="{{ route('jadwal.destroy', $jadwal->id) }}" method="POST" style="display: inline;">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn-action btn-delete" onclick="return confirm('Are you sure you want to delete this schedule?')" title="Delete">
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
                {{ $jadwals->links() }}
            </div>
        @else
            <div class="empty-state">
                <i class="fas fa-calendar-times"></i>
                <h3>No Schedules Found</h3>
                <p>Start by adding your first schedule to the system</p>
                <a href="{{ route('jadwal.create') }}" class="btn-primary">
                    <i class="fas fa-plus"></i> Add New Schedule
                </a>
            </div>
        @endif
    </div>
</div>

<style>
.btn-primary {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: linear-gradient(135deg, var(--primary), var(--primary-dark));
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    transition: var(--transition);
    box-shadow: 0 4px 6px rgba(79, 70, 229, 0.2);
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(79, 70, 229, 0.3);
}

.alert {
    padding: 15px 20px;
    border-radius: 10px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 500;
}

.alert-success {
    background: rgba(16, 185, 129, 0.1);
    color: var(--success);
    border-left: 4px solid var(--success);
}

.search-box {
    position: relative;
    display: flex;
    align-items: center;
}

.search-box i {
    position: absolute;
    left: 15px;
    color: var(--gray);
}

.search-box input {
    padding: 10px 15px 10px 40px;
    border: 2px solid var(--gray-light);
    border-radius: 8px;
    font-size: 14px;
    transition: var(--transition);
    width: 300px;
}

.search-box input:focus {
    outline: none;
    border-color: var(--primary);
}

.action-buttons {
    display: flex;
    gap: 8px;
}

.btn-action {
    width: 35px;
    height: 35px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: var(--transition);
    font-size: 14px;
}

.btn-edit {
    background: rgba(59, 130, 246, 0.1);
    color: var(--info);
}

.btn-edit:hover {
    background: var(--info);
    color: white;
    transform: translateY(-2px);
}

.btn-delete {
    background: rgba(239, 68, 68, 0.1);
    color: var(--danger);
}

.btn-delete:hover {
    background: var(--danger);
    color: white;
    transform: translateY(-2px);
}

.badge-day {
    background: linear-gradient(135deg, #8b5cf6, #7c3aed);
    color: white;
    padding: 6px 12px;
    font-size: 12px;
}

.badge-time {
    background: rgba(16, 185, 129, 0.1);
    color: var(--success);
    padding: 6px 12px;
    font-size: 12px;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
}

.empty-state i {
    font-size: 80px;
    color: var(--gray-light);
    margin-bottom: 20px;
}

.empty-state h3 {
    font-size: 24px;
    color: var(--dark);
    margin-bottom: 10px;
}

.empty-state p {
    color: var(--gray);
    margin-bottom: 25px;
}

.pagination-wrapper {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid var(--gray-light);
}
</style>

<script>
document.getElementById('searchInput').addEventListener('keyup', function() {
    let filter = this.value.toUpperCase();
    let table = document.getElementById('jadwalTable');
    let tr = table.getElementsByTagName('tr');
    
    for (let i = 1; i < tr.length; i++) {
        let td = tr[i].getElementsByTagName('td');
        let found = false;
        
        for (let j = 0; j < td.length - 1; j++) {
            if (td[j]) {
                if (td[j].innerHTML.toUpperCase().indexOf(filter) > -1) {
                    found = true;
                    break;
                }
            }
        }
        
        tr[i].style.display = found ? '' : 'none';
    }
});
</script>
@endsection
