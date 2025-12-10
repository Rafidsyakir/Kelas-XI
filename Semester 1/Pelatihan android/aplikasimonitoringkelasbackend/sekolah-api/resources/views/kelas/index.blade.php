@extends('layouts.admin')

@section('title', 'Data Kelas')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-school"></i>
            Data Kelas (Classes)
        </h1>
        <p class="page-subtitle">Manage class information and homeroom teachers</p>
    </div>
    <a href="{{ route('kelas.create') }}" class="btn-primary">
        <i class="fas fa-plus"></i> Add New Class
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
            Classes List
        </h3>
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Search classes...">
        </div>
    </div>
    <div class="card-body">
        @if($kelas->count() > 0)
            <div class="table-responsive">
                <table class="data-table" id="kelasTable">
                    <thead>
                        <tr>
                            <th>Class Name</th>
                            <th>Level</th>
                            <th>Major</th>
                            <th>Homeroom Teacher</th>
                            <th>Room</th>
                            <th>Capacity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($kelas as $k)
                        <tr>
                            <td><strong><span class="badge badge-lg badge-primary">{{ $k->nama_kelas }}</span></strong></td>
                            <td><span class="badge badge-blue">Grade {{ $k->tingkat }}</span></td>
                            <td><span class="badge badge-purple">{{ $k->jurusan }}</span></td>
                            <td>
                                @if($k->waliKelas)
                                    <div style="display: flex; align-items: center; gap: 8px;">
                                        <div class="list-avatar" style="width: 30px; height: 30px; font-size: 12px;">
                                            {{ strtoupper(substr($k->waliKelas->nama, 0, 1)) }}
                                        </div>
                                        <span>{{ $k->waliKelas->nama }}</span>
                                    </div>
                                @else
                                    <span class="text-muted">-</span>
                                @endif
                            </td>
                            <td><i class="fas fa-door-open"></i> {{ $k->ruangan }}</td>
                            <td><i class="fas fa-users"></i> {{ $k->kapasitas }} students</td>
                            <td>
                                <div class="action-buttons">
                                    <a href="{{ route('kelas.edit', $k->id) }}" class="btn-action btn-edit" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form action="{{ route('kelas.destroy', $k->id) }}" method="POST" style="display: inline;">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn-action btn-delete" onclick="return confirm('Are you sure you want to delete this class?')" title="Delete">
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
                {{ $kelas->links() }}
            </div>
        @else
            <div class="empty-state">
                <i class="fas fa-school"></i>
                <h3>No Classes Found</h3>
                <p>Start by adding your first class to the system</p>
                <a href="{{ route('kelas.create') }}" class="btn-primary">
                    <i class="fas fa-plus"></i> Add New Class
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

.badge-lg {
    padding: 6px 14px;
    font-size: 13px;
}

.badge-purple {
    background: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
}

.text-muted {
    color: var(--gray);
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
    let table = document.getElementById('kelasTable');
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
