<style>
/* ============================================
   MODERN FORM STYLES - SHARED PARTIAL
   ============================================ */
.btn-glass {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: var(--text);
    padding: 0.75rem 1.25rem;
    border-radius: 12px;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    transition: all 0.3s ease;
    text-decoration: none;
}

.btn-glass:hover {
    background: rgba(99, 102, 241, 0.1);
    border-color: var(--primary);
    color: var(--primary);
    transform: translateY(-2px);
}

.form-card {
    background: var(--card-bg);
    border-radius: 24px;
    box-shadow: 0 10px 40px var(--shadow);
    overflow: hidden;
    border: 1px solid var(--border);
}

.form-header {
    background: linear-gradient(135deg, var(--primary) 0%, #7c3aed 100%);
    padding: 2rem;
    display: flex;
    align-items: center;
    gap: 1.5rem;
    color: white;
}

.form-icon {
    width: 70px;
    height: 70px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
}

.form-header-text h2 {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 700;
}

.form-header-text p {
    margin: 0.25rem 0 0;
    opacity: 0.9;
    font-size: 0.95rem;
}

.form-body {
    padding: 2rem;
}

/* Alert Modern */
.alert-modern {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
    padding: 1.25rem;
    border-radius: 16px;
    margin-bottom: 2rem;
}

.alert-error {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
    border: 1px solid rgba(239, 68, 68, 0.3);
}

.alert-modern .alert-icon {
    width: 40px;
    height: 40px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.1rem;
    flex-shrink: 0;
}

.alert-error .alert-icon {
    background: #ef4444;
    color: white;
}

.alert-modern .alert-content strong {
    display: block;
    margin-bottom: 0.5rem;
    color: #dc2626;
}

.alert-modern .alert-content ul {
    margin: 0;
    padding-left: 1.25rem;
    color: #64748b;
}

/* Form Sections */
.form-section {
    margin-bottom: 2rem;
    padding-bottom: 2rem;
    border-bottom: 1px dashed var(--border);
}

.form-section:last-of-type {
    border-bottom: none;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-size: 1.1rem;
    font-weight: 700;
    color: var(--text);
    margin-bottom: 1.5rem;
}

.section-title i {
    color: var(--primary);
    font-size: 1.25rem;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
}

/* Form Group Modern */
.form-group-modern {
    margin-bottom: 0;
}

.form-label-modern {
    display: block;
    font-weight: 600;
    color: var(--text);
    margin-bottom: 0.75rem;
    font-size: 0.95rem;
}

.required {
    color: #ef4444;
}

.input-wrapper, .select-wrapper {
    position: relative;
}

.input-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
    font-size: 1rem;
}

.text-success { color: #10b981 !important; }
.text-danger { color: #ef4444 !important; }

.form-input-modern, .form-select-modern, .form-textarea-modern {
    width: 100%;
    padding: 1rem 1rem 1rem 3rem;
    border: 2px solid var(--border);
    border-radius: 14px;
    font-size: 1rem;
    transition: all 0.3s ease;
    background: var(--card-bg);
    color: var(--text);
}

.form-select-modern {
    appearance: none;
    cursor: pointer;
}

.select-arrow {
    position: absolute;
    right: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #94a3b8;
    pointer-events: none;
}

.form-textarea-modern {
    padding: 1rem;
    min-height: 120px;
    resize: vertical;
}

.form-input-modern:focus, .form-select-modern:focus, .form-textarea-modern:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15);
    outline: none;
}

/* Status Selector */
.status-selector {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
}

.status-option-card {
    cursor: pointer;
}

.status-option-card input[type="radio"] {
    display: none;
}

.status-card-content {
    padding: 1.5rem 1rem;
    border-radius: 16px;
    text-align: center;
    border: 2px solid var(--border);
    transition: all 0.3s ease;
    background: var(--card-bg);
}

.status-icon-large {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.75rem;
    margin: 0 auto 1rem;
    transition: all 0.3s ease;
}

.status-text {
    display: block;
    font-weight: 700;
    font-size: 1rem;
    margin-bottom: 0.25rem;
}

.status-desc {
    display: block;
    font-size: 0.75rem;
    opacity: 0.7;
}

/* Status Colors */
.status-hadir .status-icon-large {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
}

.status-tidak-hadir .status-icon-large {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
}

.status-terlambat .status-icon-large {
    background: rgba(245, 158, 11, 0.1);
    color: #f59e0b;
}

.status-izin .status-icon-large {
    background: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
}

/* Selected States */
.status-option-card input[type="radio"]:checked + .status-hadir {
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(5, 150, 105, 0.1) 100%);
    border-color: #10b981;
    color: #059669;
}

.status-option-card input[type="radio"]:checked + .status-hadir .status-icon-large {
    background: #10b981;
    color: white;
    transform: scale(1.1);
}

.status-option-card input[type="radio"]:checked + .status-tidak-hadir {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
    border-color: #ef4444;
    color: #dc2626;
}

.status-option-card input[type="radio"]:checked + .status-tidak-hadir .status-icon-large {
    background: #ef4444;
    color: white;
    transform: scale(1.1);
}

.status-option-card input[type="radio"]:checked + .status-terlambat {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(217, 119, 6, 0.1) 100%);
    border-color: #f59e0b;
    color: #d97706;
}

.status-option-card input[type="radio"]:checked + .status-terlambat .status-icon-large {
    background: #f59e0b;
    color: white;
    transform: scale(1.1);
}

.status-option-card input[type="radio"]:checked + .status-izin {
    background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
    border-color: #8b5cf6;
    color: #7c3aed;
}

.status-option-card input[type="radio"]:checked + .status-izin .status-icon-large {
    background: #8b5cf6;
    color: white;
    transform: scale(1.1);
}

/* Form Actions */
.form-actions-modern {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 1px solid var(--border);
}

.btn-submit {
    flex: 1;
    padding: 1rem 2rem;
    background: linear-gradient(135deg, var(--primary) 0%, #7c3aed 100%);
    color: white;
    border: none;
    border-radius: 14px;
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.btn-submit:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(99, 102, 241, 0.5);
}

.btn-cancel {
    padding: 1rem 2rem;
    background: var(--border);
    color: var(--text);
    border: none;
    border-radius: 14px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
    transition: all 0.3s ease;
    text-decoration: none;
}

.btn-cancel:hover {
    background: #e2e8f0;
    color: #ef4444;
}

/* Responsive */
@media (max-width: 992px) {
    .status-selector {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
    
    .status-selector {
        grid-template-columns: 1fr;
    }
    
    .form-actions-modern {
        flex-direction: column;
    }
    
    .form-header {
        flex-direction: column;
        text-align: center;
    }
}
</style>
