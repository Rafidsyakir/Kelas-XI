@extends('layouts.admin')

@section('title', 'Manage Users')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-users"></i>
            Manage Users
        </h1>
        <p class="page-subtitle">Manage all system users and their permissions</p>
    </div>
    <a href="{{ route('manage-users.create') }}" class="btn-primary">
        <i class="fas fa-plus"></i> Add New User
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
            Users List
        </h3>
        <div class="search-box">
            <i class="fas fa-search"></i>
            <input type="text" id="searchInput" placeholder="Search users...">
        </div>
    </div>
    <div class="card-body">
        @if($users->count() > 0)
            <div class="table-responsive">
                <table class="data-table" id="usersTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($users as $user)
                        <tr>
                            <td><strong>#{{ $user->id }}</strong></td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 10px;">
                                    <div class="list-avatar" style="width: 35px; height: 35px; font-size: 14px;">
                                        {{ strtoupper(substr($user->email, 0, 1)) }}
                                    </div>
                                    <strong>{{ $user->nama ?? 'N/A' }}</strong>
                                </div>
                            </td>
                            <td>{{ $user->email }}</td>
                            <td>
                                @if($user->role == 'admin')
                                    <span class="badge badge-danger"><i class="fas fa-crown"></i> Admin</span>
                                @elseif($user->role == 'siswa')
                                    <span class="badge badge-primary"><i class="fas fa-graduation-cap"></i> Siswa</span>
                                @elseif($user->role == 'kurikulum')
                                    <span class="badge badge-success"><i class="fas fa-book"></i> Kurikulum</span>
                                @elseif($user->role == 'kepala-sekolah')
                                    <span class="badge badge-warning"><i class="fas fa-user-tie"></i> Kepala Sekolah</span>
                                @else
                                    <span class="badge badge-secondary"><i class="fas fa-user"></i> {{ ucfirst($user->role) }}</span>
                                @endif
                            </td>
                            <td>{{ $user->created_at->format('d M Y') }}</td>
                            <td>
                                <div class="action-buttons">
                                    <a href="{{ route('manage-users.edit', $user->id) }}" class="btn-action btn-edit" title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form action="{{ route('manage-users.destroy', $user->id) }}" method="POST" style="display: inline;">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn-action btn-delete" onclick="return confirm('Are you sure you want to delete this user?')" title="Delete">
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
                {{ $users->links() }}
            </div>
        @else
            <div class="empty-state">
                <i class="fas fa-users-slash"></i>
                <h3>No Users Found</h3>
                <p>Start by adding your first user to the system</p>
                <a href="{{ route('manage-users.create') }}" class="btn-primary">
                    <i class="fas fa-plus"></i> Add New User
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

.badge-danger {
    background: rgba(239, 68, 68, 0.1);
    color: var(--danger);
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
    let table = document.getElementById('usersTable');
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
