@extends('layouts.admin')

@section('title', 'Add New Teacher')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-user-plus"></i>
            Add New Teacher
        </h1>
        <p class="page-subtitle">Register a new teacher to the system</p>
    </div>
    <a href="{{ route('guru.index') }}" class="btn-secondary">
        <i class="fas fa-arrow-left"></i> Back to Teachers
    </a>
</div>

<div class="form-card">
    <form action="{{ route('guru.store') }}" method="POST">
        @csrf
        
        <div class="form-section">
            <h3 class="section-title"><i class="fas fa-user-circle"></i> Personal Information</h3>
            <div class="form-grid">
                <div class="form-group">
                    <label for="nip">
                        <i class="fas fa-id-card"></i> NIP (Teacher ID) <span class="required">*</span>
                    </label>
                    <input type="text" id="nip" name="nip" class="form-control @error('nip') is-invalid @enderror" 
                           value="{{ old('nip') }}" placeholder="Enter NIP" required>
                    @error('nip')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="nama">
                        <i class="fas fa-user"></i> Full Name <span class="required">*</span>
                    </label>
                    <input type="text" id="nama" name="nama" class="form-control @error('nama') is-invalid @enderror" 
                           value="{{ old('nama') }}" placeholder="Enter full name" required>
                    @error('nama')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="jenis_kelamin">
                        <i class="fas fa-venus-mars"></i> Gender <span class="required">*</span>
                    </label>
                    <select id="jenis_kelamin" name="jenis_kelamin" class="form-control @error('jenis_kelamin') is-invalid @enderror" required>
                        <option value="">Select Gender</option>
                        <option value="Laki-laki" {{ old('jenis_kelamin') == 'Laki-laki' ? 'selected' : '' }}>Laki-laki</option>
                        <option value="Perempuan" {{ old('jenis_kelamin') == 'Perempuan' ? 'selected' : '' }}>Perempuan</option>
                    </select>
                    @error('jenis_kelamin')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="mata_pelajaran">
                        <i class="fas fa-book"></i> Subject <span class="required">*</span>
                    </label>
                    <input type="text" id="mata_pelajaran" name="mata_pelajaran" class="form-control @error('mata_pelajaran') is-invalid @enderror" 
                           value="{{ old('mata_pelajaran') }}" placeholder="e.g., Mathematics, Physics" required>
                    @error('mata_pelajaran')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3 class="section-title"><i class="fas fa-address-card"></i> Contact Information</h3>
            <div class="form-grid">
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
                    <label for="telepon">
                        <i class="fas fa-phone"></i> Phone Number <span class="required">*</span>
                    </label>
                    <input type="text" id="telepon" name="telepon" class="form-control @error('telepon') is-invalid @enderror" 
                           value="{{ old('telepon') }}" placeholder="e.g., 081234567890" required>
                    @error('telepon')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group full-width">
                    <label for="alamat">
                        <i class="fas fa-map-marker-alt"></i> Address <span class="required">*</span>
                    </label>
                    <textarea id="alamat" name="alamat" class="form-control @error('alamat') is-invalid @enderror" 
                              rows="3" placeholder="Enter complete address" required>{{ old('alamat') }}</textarea>
                    @error('alamat')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i> Save Teacher
            </button>
            <a href="{{ route('guru.index') }}" class="btn-secondary">
                <i class="fas fa-times"></i> Cancel
            </a>
        </div>
    </form>
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

.form-section {
    margin-bottom: 30px;
}

.section-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--dark);
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--gray-light);
    display: flex;
    align-items: center;
    gap: 10px;
}

.section-title i {
    color: var(--primary);
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-group.full-width {
    grid-column: 1 / -1;
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
    font-family: inherit;
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
