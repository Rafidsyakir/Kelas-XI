@extends('layouts.admin')

@section('title', 'Add New Class')

@section('content')
<div class="dashboard-header">
    <div>
        <h1 class="page-title">
            <i class="fas fa-plus-square"></i>
            Add New Class
        </h1>
        <p class="page-subtitle">Register a new class to the system</p>
    </div>
    <a href="{{ route('kelas.index') }}" class="btn-secondary">
        <i class="fas fa-arrow-left"></i> Back to Classes
    </a>
</div>

<div class="form-card">
    <form action="{{ route('kelas.store') }}" method="POST">
        @csrf
        
        <div class="form-section">
            <h3 class="section-title"><i class="fas fa-info-circle"></i> Class Information</h3>
            <div class="form-grid">
                <div class="form-group">
                    <label for="nama_kelas">
                        <i class="fas fa-tag"></i> Class Name <span class="required">*</span>
                    </label>
                    <input type="text" id="nama_kelas" name="nama_kelas" class="form-control @error('nama_kelas') is-invalid @enderror" 
                           value="{{ old('nama_kelas') }}" placeholder="e.g., X RPL 1, XI IPA 2" required>
                    @error('nama_kelas')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="tingkat">
                        <i class="fas fa-layer-group"></i> Grade Level <span class="required">*</span>
                    </label>
                    <input type="number" id="tingkat" name="tingkat" class="form-control @error('tingkat') is-invalid @enderror" 
                           value="{{ old('tingkat') }}" placeholder="10, 11, or 12" min="10" max="12" required>
                    @error('tingkat')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="jurusan">
                        <i class="fas fa-graduation-cap"></i> Major <span class="required">*</span>
                    </label>
                    <input type="text" id="jurusan" name="jurusan" class="form-control @error('jurusan') is-invalid @enderror" 
                           value="{{ old('jurusan') }}" placeholder="e.g., RPL, IPA, IPS" required>
                    @error('jurusan')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="wali_kelas_id">
                        <i class="fas fa-user-tie"></i> Homeroom Teacher <span class="required">*</span>
                    </label>
                    <select id="wali_kelas_id" name="wali_kelas_id" class="form-control @error('wali_kelas_id') is-invalid @enderror" required>
                        <option value="">Select Homeroom Teacher</option>
                        @foreach($gurus as $guru)
                            <option value="{{ $guru->id }}" {{ old('wali_kelas_id') == $guru->id ? 'selected' : '' }}>
                                {{ $guru->nama }} - {{ $guru->mata_pelajaran }}
                            </option>
                        @endforeach
                    </select>
                    @error('wali_kelas_id')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="ruangan">
                        <i class="fas fa-door-open"></i> Room <span class="required">*</span>
                    </label>
                    <input type="text" id="ruangan" name="ruangan" class="form-control @error('ruangan') is-invalid @enderror" 
                           value="{{ old('ruangan') }}" placeholder="e.g., Room 101, Lab 2" required>
                    @error('ruangan')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>

                <div class="form-group">
                    <label for="kapasitas">
                        <i class="fas fa-users"></i> Capacity <span class="required">*</span>
                    </label>
                    <input type="number" id="kapasitas" name="kapasitas" class="form-control @error('kapasitas') is-invalid @enderror" 
                           value="{{ old('kapasitas') }}" placeholder="e.g., 30, 35" min="1" required>
                    @error('kapasitas')
                        <div class="invalid-feedback">{{ $message }}</div>
                    @enderror
                </div>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-primary">
                <i class="fas fa-save"></i> Save Class
            </button>
            <a href="{{ route('kelas.index') }}" class="btn-secondary">
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
