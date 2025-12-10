@extends('layouts.admin')

@section('title', 'Add New User')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-user-plus"></i>
            Add New User
        </h1>
        <p class="page-subtitle">Create a new user account</p>
    </div>
    <a href="{{ route('manage-users.index') }}" class="btn-secondary">
        <i class="fas fa-arrow-left"></i> Back to Users
    </a>
</div>

<div class="form-card">
    <form action="{{ route('manage-users.store') }}" method="POST">
        @csrf
        
        <div class="form-grid">
            <div class="form-group">
                <label for="nama">
                    <i class="fas fa-user"></i> Full Name
                </label>
                <input type="text" id="nama" name="nama" class="form-control @error('nama') is-invalid @enderror" 
                       value="{{ old('nama') }}" placeholder="Enter full name">
                @error('nama')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="form-group">
                <label for="email">
                    <i class="fas fa-envelope"></i> Email Address <span class="required">*</span>
                </label>
                <input type="email" id="email" name="email" class="form-control @error('email') is-invalid @enderror" 
                       value="{{ old('email') }}" placeholder="Enter email address" required>
                @error('email')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="form-group">
                <label for="password">
                    <i class="fas fa-lock"></i> Password <span class="required">*</span>
                </label>
                <input type="password" id="password" name="password" class="form-control @error('password') is-invalid @enderror" 
                       placeholder="Enter password" required>
                @error('password')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="form-group">
                <label for="role">
                    <i class="fas fa-user-tag"></i> Role <span class="required">*</span>
                </label>
                <select id="role" name="role" class="form-control @error('role') is-invalid @enderror" required>
                    <option value="">Select Role</option>
                    <option value="admin" {{ old('role') == 'admin' ? 'selected' : '' }}>Admin</option>
                    <option value="siswa" {{ old('role') == 'siswa' ? 'selected' : '' }}>Siswa</option>
                    <option value="kurikulum" {{ old('role') == 'kurikulum' ? 'selected' : '' }}>Kurikulum</option>
                    <option value="kepala-sekolah" {{ old('role') == 'kepala-sekolah' ? 'selected' : '' }}>Kepala Sekolah</option>
                </select>
                @error('role')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i> Create User
            </button>
            <a href="{{ route('manage-users.index') }}" class="btn-secondary">
                <i class="fas fa-times"></i> Cancel
            </a>
        </div>
    </form>
</div>

<style>
.btn-secondary {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: white;
    color: var(--dark);
    border: 2px solid var(--gray-light);
    border-radius: 10px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    text-decoration: none;
    transition: var(--transition);
}

.btn-secondary:hover {
    border-color: var(--primary);
    color: var(--primary);
    transform: translateY(-2px);
}

.form-card {
    background: white;
    border-radius: 16px;
    padding: 30px;
    box-shadow: var(--shadow);
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-group label {
    font-size: 14px;
    font-weight: 600;
    color: var(--dark);
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.form-group label i {
    color: var(--primary);
}

.required {
    color: var(--danger);
}

.form-control {
    padding: 12px 15px;
    border: 2px solid var(--gray-light);
    border-radius: 8px;
    font-size: 14px;
    transition: var(--transition);
}

.form-control:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.form-control.is-invalid {
    border-color: var(--danger);
}

.invalid-feedback {
    color: var(--danger);
    font-size: 13px;
    margin-top: 5px;
}

.form-actions {
    display: flex;
    gap: 12px;
    padding-top: 20px;
    border-top: 1px solid var(--gray-light);
}
</style>
@endsection
