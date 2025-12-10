@extends('layouts.admin')

@section('title', 'Data Guru')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-chalkboard-teacher"></i>
            Data Guru (Teachers)
        </h1>
        <p class="page-subtitle">Manage teachers information and assignments</p>
    </div>
    <a href="{{ route('guru.create') }}" class="btn-primary">
        <i class="fas fa-plus"></i> Add New Teacher
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
            Teachers List
        </h3>
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Search teachers...">
        </div>
    </div>
    <div class="card-body">
        @if($gurus->count() > 0)
            <div class="table-responsive">
                <table class="data-table" id="guruTable">
                    <thead>
                        <tr>
                            <th>NIP</th>
                            <th>Name</th>
                            <th>Subject</th>
                            <th>Gender</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($gurus as $guru)
                        <tr>
                            <td><span class="badge badge-primary">{{ $guru->nip }}</span></td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 10px;">
                                    <div class="list-avatar" style="width: 35px; height: 35px; font-size: 14px;">
                                        {{ strtoupper(substr($guru->nama, 0, 1)) }}
                                    </div>
                                    <strong>{{ $guru->nama }}</strong>
                                </div>
                            </td>
                            <td><span class="badge badge-info"><i class="fas fa-book"></i> {{ $guru->mata_pelajaran }}</span></td>
                            <td>
                                @if($guru->jenis_kelamin == 'Laki-laki')
                                    <span class="badge badge-blue"><i class="fas fa-mars"></i> {{ $guru->jenis_kelamin }}</span>
                                @else
                                    <span class="badge badge-pink"><i class="fas fa-venus"></i> {{ $guru->jenis_kelamin }}</span>
                                @endif
                            </td>
                            <td>{{ $guru->email }}</td>
                            <td><i class="fas fa-phone"></i> {{ $guru->telepon }}</td>
                            <td>
                                <div class="action-buttons">
                                    <a href="{{ route('guru.edit', $guru->id) }}" class="btn-action btn-edit" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form action="{{ route('guru.destroy', $guru->id) }}" method="POST" style="display: inline;">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn-action btn-delete" onclick="return confirm('Are you sure you want to delete this teacher?')" title="Delete">
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
                {{ $gurus->links() }}
            </div>
        @else
            <div class="empty-state">
                <i class="fas fa-user-tie"></i>
                <h3>No Teachers Found</h3>
                <p>Start by adding your first teacher to the system</p>
                <a href="{{ route('guru.create') }}" class="btn-primary">
                    <i class="fas fa-plus"></i> Add New Teacher
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

.badge-info {
    background: rgba(99, 102, 241, 0.1);
    color: var(--primary);
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
    let table = document.getElementById('guruTable');
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
