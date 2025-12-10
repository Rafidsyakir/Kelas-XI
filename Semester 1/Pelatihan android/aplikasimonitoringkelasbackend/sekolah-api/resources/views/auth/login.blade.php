@extends('layouts.app')

@section('title', 'Admin Login')

@section('styles')
<link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700;800;900&family=Rajdhani:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<style>
    :root {
        --neon-cyan: #00fff2;
        --neon-purple: #bf00ff;
        --neon-pink: #ff00d4;
        --dark-bg: #0a0a0f;
        --card-bg: #12121a;
        --text-primary: #ffffff;
        --text-secondary: #8892a0;
    }

    body {
        font-family: 'Rajdhani', sans-serif;
        background: var(--dark-bg);
        min-height: 100vh;
        overflow-x: hidden;
    }

    .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
        position: relative;
        z-index: 1;
    }

    /* Animated background */
    .bg-animation {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 0;
        overflow: hidden;
    }

    .bg-animation::before {
        content: '';
        position: absolute;
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, var(--neon-purple) 0%, transparent 70%);
        top: -100px;
        right: -100px;
        animation: float1 8s ease-in-out infinite;
        opacity: 0.3;
    }

    .bg-animation::after {
        content: '';
        position: absolute;
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, var(--neon-cyan) 0%, transparent 70%);
        bottom: -100px;
        left: -100px;
        animation: float2 10s ease-in-out infinite;
        opacity: 0.3;
    }

    @keyframes float1 {
        0%, 100% { transform: translate(0, 0) rotate(0deg); }
        50% { transform: translate(50px, 50px) rotate(180deg); }
    }

    @keyframes float2 {
        0%, 100% { transform: translate(0, 0) rotate(0deg); }
        50% { transform: translate(-30px, -30px) rotate(-180deg); }
    }

    .particles {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 0;
        pointer-events: none;
    }

    .particle {
        position: absolute;
        width: 4px;
        height: 4px;
        background: var(--neon-cyan);
        border-radius: 50%;
        animation: rise 10s infinite;
        opacity: 0;
    }

    @keyframes rise {
        0% { opacity: 0; transform: translateY(100vh) scale(0); }
        10% { opacity: 1; }
        90% { opacity: 1; }
        100% { opacity: 0; transform: translateY(-100vh) scale(1); }
    }

    .login-card {
        background: rgba(18, 18, 26, 0.9);
        backdrop-filter: blur(20px);
        border-radius: 24px;
        padding: 50px;
        max-width: 480px;
        width: 100%;
        border: 1px solid rgba(0, 255, 242, 0.2);
        box-shadow: 
            0 0 40px rgba(0, 255, 242, 0.1),
            inset 0 0 60px rgba(0, 255, 242, 0.05);
        animation: cardGlow 3s ease-in-out infinite alternate;
        position: relative;
        z-index: 10;
    }

    @keyframes cardGlow {
        0% { box-shadow: 0 0 40px rgba(0, 255, 242, 0.1), inset 0 0 60px rgba(0, 255, 242, 0.05); }
        100% { box-shadow: 0 0 60px rgba(191, 0, 255, 0.2), inset 0 0 80px rgba(191, 0, 255, 0.05); }
    }

    .login-header {
        text-align: center;
        margin-bottom: 40px;
    }

    .login-icon {
        width: 100px;
        height: 100px;
        background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 24px;
        animation: iconPulse 2s ease-in-out infinite;
        position: relative;
    }

    .login-icon::before {
        content: '';
        position: absolute;
        width: 120%;
        height: 120%;
        border: 2px solid var(--neon-cyan);
        border-radius: 50%;
        animation: ring 3s linear infinite;
        opacity: 0.5;
    }

    @keyframes iconPulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.05); }
    }

    @keyframes ring {
        0% { transform: scale(1); opacity: 0.5; }
        100% { transform: scale(1.5); opacity: 0; }
    }

    .login-icon i {
        font-size: 42px;
        color: var(--dark-bg);
    }

    .login-title {
        font-family: 'Orbitron', sans-serif;
        font-size: 36px;
        font-weight: 800;
        background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple), var(--neon-pink));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin-bottom: 10px;
        letter-spacing: 3px;
        text-transform: uppercase;
    }

    .login-subtitle {
        color: var(--text-secondary);
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 1px;
    }

    .form-group {
        margin-bottom: 24px;
    }

    .form-label {
        display: block;
        margin-bottom: 10px;
        font-weight: 600;
        color: var(--neon-cyan);
        font-size: 14px;
        letter-spacing: 1px;
        text-transform: uppercase;
        font-family: 'Orbitron', sans-serif;
    }

    .form-label .required {
        color: var(--neon-pink);
    }

    .input-group {
        position: relative;
    }

    .input-icon {
        position: absolute;
        left: 18px;
        top: 50%;
        transform: translateY(-50%);
        color: var(--neon-cyan);
        font-size: 18px;
    }

    .form-input {
        width: 100%;
        padding: 16px 18px 16px 50px;
        border: 2px solid rgba(0, 255, 242, 0.3);
        border-radius: 12px;
        font-size: 16px;
        font-family: 'Rajdhani', sans-serif;
        font-weight: 500;
        transition: all 0.3s ease;
        background: rgba(0, 0, 0, 0.3);
        color: var(--text-primary);
        letter-spacing: 1px;
    }

    .form-input::placeholder {
        color: var(--text-secondary);
    }

    .form-input:focus {
        outline: none;
        border-color: var(--neon-cyan);
        box-shadow: 0 0 20px rgba(0, 255, 242, 0.3);
        background: rgba(0, 255, 242, 0.05);
    }

    .form-input.is-invalid {
        border-color: var(--neon-pink);
    }

    .form-input.is-invalid:focus {
        box-shadow: 0 0 20px rgba(255, 0, 212, 0.3);
    }

    .invalid-feedback {
        display: block;
        margin-top: 8px;
        color: var(--neon-pink);
        font-size: 13px;
        font-weight: 600;
    }

    .form-check {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 28px;
    }

    .form-check-input {
        width: 20px;
        height: 20px;
        cursor: pointer;
        accent-color: var(--neon-cyan);
        border-radius: 4px;
    }

    .form-check-label {
        font-size: 14px;
        color: var(--text-secondary);
        cursor: pointer;
        user-select: none;
        font-weight: 500;
    }

    .btn {
        width: 100%;
        padding: 18px 28px;
        border: none;
        border-radius: 12px;
        font-size: 18px;
        font-weight: 700;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        font-family: 'Orbitron', sans-serif;
        letter-spacing: 2px;
        text-transform: uppercase;
        position: relative;
        overflow: hidden;
    }

    .btn-primary {
        background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
        color: var(--dark-bg);
        box-shadow: 0 0 30px rgba(0, 255, 242, 0.4);
    }

    .btn-primary::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
        transition: 0.5s;
    }

    .btn-primary:hover::before {
        left: 100%;
    }

    .btn-primary:hover {
        transform: translateY(-3px);
        box-shadow: 0 0 50px rgba(0, 255, 242, 0.6);
    }

    .btn-primary:active {
        transform: translateY(0);
    }

    .alert {
        padding: 16px 20px;
        border-radius: 12px;
        margin-bottom: 24px;
        display: flex;
        align-items: center;
        gap: 12px;
        animation: shake 0.5s ease-out;
        font-size: 14px;
        font-weight: 600;
    }

    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(-10px); }
        75% { transform: translateX(10px); }
    }

    .alert-danger {
        background: rgba(255, 0, 212, 0.1);
        color: var(--neon-pink);
        border: 2px solid var(--neon-pink);
    }

    .alert i {
        font-size: 20px;
    }

    .login-footer {
        text-align: center;
        margin-top: 32px;
        padding-top: 24px;
        border-top: 1px solid rgba(0, 255, 242, 0.2);
        color: var(--text-secondary);
        font-size: 14px;
        font-weight: 500;
    }

    .admin-badge {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: linear-gradient(135deg, rgba(0, 255, 242, 0.1), rgba(191, 0, 255, 0.1));
        color: var(--neon-cyan);
        padding: 10px 20px;
        border-radius: 30px;
        font-size: 13px;
        font-weight: 700;
        margin-bottom: 24px;
        border: 1px solid rgba(0, 255, 242, 0.3);
        font-family: 'Orbitron', sans-serif;
        letter-spacing: 2px;
        text-transform: uppercase;
    }

    @media (max-width: 576px) {
        .login-card {
            padding: 30px 20px;
        }

        .login-title {
            font-size: 24px;
        }
    }
</style>
@endsection

@section('content')
<div class="bg-animation"></div>
<div class="particles">
    @for($i = 0; $i < 20; $i++)
    <div class="particle" style="left: {{ rand(0, 100) }}%; animation-delay: {{ rand(0, 10) }}s; animation-duration: {{ rand(8, 15) }}s;"></div>
    @endfor
</div>
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <div class="login-icon">
                <i class="fas fa-fingerprint"></i>
            </div>
            <div class="admin-badge">
                <i class="fas fa-shield-alt"></i>
                Secure Access
            </div>
            <h1 class="login-title">AURORA</h1>
            <p class="login-subtitle">School Management System</p>
        </div>

        @if ($errors->any())
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i>
                <span>{{ $errors->first() }}</span>
            </div>
        @endif

        <form method="POST" action="{{ route('login.post') }}">
            @csrf
            
            <div class="form-group">
                <label class="form-label" for="email">
                    Email <span class="required">*</span>
                </label>
                <div class="input-group">
                    <i class="input-icon fas fa-at"></i>
                    <input 
                        type="email" 
                        id="email" 
                        name="email"
                        class="form-input @error('email') is-invalid @enderror" 
                        placeholder="admin@school.edu"
                        value="{{ old('email') }}"
                        required
                        autofocus
                        autocomplete="email"
                    >
                </div>
                @error('email')
                    <span class="invalid-feedback">{{ $message }}</span>
                @enderror
            </div>

            <div class="form-group">
                <label class="form-label" for="password">
                    Password <span class="required">*</span>
                </label>
                <div class="input-group">
                    <i class="input-icon fas fa-key"></i>
                    <input 
                        type="password" 
                        id="password" 
                        name="password"
                        class="form-input @error('password') is-invalid @enderror" 
                        placeholder="••••••••••"
                        required
                        autocomplete="current-password"
                    >
                </div>
                @error('password')
                    <span class="invalid-feedback">{{ $message }}</span>
                @enderror
            </div>

            <div class="form-check">
                <input 
                    type="checkbox" 
                    id="remember" 
                    name="remember"
                    class="form-check-input"
                >
                <label class="form-check-label" for="remember">
                    Keep me signed in
                </label>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fas fa-bolt"></i>
                Access System
            </button>
        </form>

        <div class="login-footer">
            <i class="fas fa-lock"></i>
            AURORA Portal &copy; {{ date('Y') }}
        </div>
    </div>
</div>
@endsection
