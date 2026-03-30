<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Trifhop</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap');
        
        * {
            font-family: 'Inter', sans-serif;
        }
        
        @keyframes gradientShift {
            0%, 100% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
        }
        
        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-20px); }
        }
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        @keyframes shimmer {
            0% { background-position: -1000px 0; }
            100% { background-position: 1000px 0; }
        }
        
        .gradient-bg {
            background: linear-gradient(-45deg, #0f172a, #1e293b, #0c4a6e, #1e3a8a);
            background-size: 400% 400%;
            animation: gradientShift 15s ease infinite;
        }
        
        .glass-effect {
            background: rgba(15, 23, 42, 0.7);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(148, 163, 184, 0.1);
        }
        
        .float-animation {
            animation: float 6s ease-in-out infinite;
        }
        
        .animate-fade-in-up {
            animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1);
        }
        
        .input-glow:focus {
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1),
                        0 0 20px rgba(59, 130, 246, 0.2);
        }
        
        .shimmer-button {
            background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 50%, #3b82f6 100%);
            background-size: 200% 100%;
            animation: shimmer 3s linear infinite;
        }
        
        .shape {
            position: absolute;
            border-radius: 50%;
            filter: blur(60px);
            opacity: 0.3;
        }
    </style>
</head>
<body class="gradient-bg min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
    <!-- Floating Shapes -->
    <div class="shape w-96 h-96 bg-blue-500 top-0 left-0 float-animation"></div>
    <div class="shape w-80 h-80 bg-purple-500 bottom-0 right-0 float-animation" style="animation-delay: 2s"></div>
    <div class="shape w-64 h-64 bg-cyan-500 top-1/2 left-1/2 float-animation" style="animation-delay: 4s"></div>
    
    <div class="w-full max-w-md relative z-10">
        <!-- Logo & Title -->
        <div class="text-center mb-8 animate-fade-in-up">
            <div class="inline-flex items-center justify-center w-24 h-24 bg-white rounded-3xl shadow-2xl shadow-blue-500/40 mb-6 p-3">
                <img src="{{ asset('images/logo.png') }}" alt="Trifhop Logo" class="w-full h-full object-contain">
            </div>
            <h1 class="text-4xl font-bold text-white mb-2">Trifhop Admin</h1>
            <p class="text-slate-300">Sign in to your dashboard</p>
        </div>

        <!-- Login Form -->
        <div class="glass-effect rounded-3xl p-8 shadow-2xl animate-fade-in-up" style="animation-delay: 0.2s">
            @if ($errors->any())
                <div class="mb-6 bg-red-500/20 border border-red-500/30 text-red-300 px-5 py-4 rounded-xl flex items-center">
                    <i class="fas fa-exclamation-circle text-xl mr-3"></i>
                    <span class="font-medium">{{ $errors->first() }}</span>
                </div>
            @endif

            <form method="POST" action="{{ route('admin.login.post') }}" class="space-y-6">
                @csrf
                
                <!-- Email -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        <i class="fas fa-envelope mr-2 text-blue-400"></i>Email Address
                    </label>
                    <input 
                        type="email" 
                        name="email" 
                        required 
                        value="{{ old('email') }}"
                        class="input-glow w-full px-5 py-4 bg-slate-900/50 border-2 border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-all duration-300"
                        placeholder="admin@trifhop.com"
                        autocomplete="email">
                </div>

                <!-- Password -->
                <div>
                    <label class="block text-sm font-semibold text-slate-300 mb-2">
                        <i class="fas fa-lock mr-2 text-blue-400"></i>Password
                    </label>
                    <div class="relative">
                        <input 
                            type="password" 
                            name="password" 
                            required
                            id="passwordInput"
                            class="input-glow w-full px-5 py-4 bg-slate-900/50 border-2 border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-all duration-300"
                            placeholder="Enter your password"
                            autocomplete="current-password">
                        <button 
                            type="button"
                            onclick="togglePassword()"
                            class="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white transition-colors">
                            <i class="fas fa-eye" id="toggleIcon"></i>
                        </button>
                    </div>
                </div>

                <!-- Remember Me -->
                <div class="flex items-center justify-between">
                    <label class="flex items-center cursor-pointer group">
                        <input type="checkbox" name="remember" class="w-5 h-5 bg-slate-900/50 border-2 border-slate-700 rounded-lg text-blue-500 focus:ring-2 focus:ring-blue-500/20">
                        <span class="ml-3 text-sm text-slate-300 group-hover:text-white transition-colors">Remember me</span>
                    </label>
                    <a href="#" class="text-sm text-blue-400 hover:text-blue-300 transition-colors font-medium">
                        Forgot password?
                    </a>
                </div>

                <!-- Submit Button -->
                <button 
                    type="submit" 
                    class="shimmer-button w-full py-4 text-white font-bold rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-blue-500/50 transform hover:scale-[1.02] transition-all duration-300">
                    <i class="fas fa-sign-in-alt mr-2"></i>Sign In
                </button>
            </form>

            <!-- Divider -->
            <div class="relative my-8">
                <div class="absolute inset-0 flex items-center">
                    <div class="w-full border-t border-slate-700"></div>
                </div>
                <div class="relative flex justify-center text-sm">
                    <span class="px-4 bg-slate-900/50 text-slate-400 rounded-full">Secure Login</span>
                </div>
            </div>

            <!-- Security Badge -->
            <div class="flex items-center justify-center space-x-2 text-slate-400 text-sm">
                <i class="fas fa-shield-alt text-green-400"></i>
                <span>Protected by 256-bit SSL encryption</span>
            </div>
        </div>

        <!-- Footer -->
        <div class="text-center mt-8 animate-fade-in-up" style="animation-delay: 0.4s">
            <p class="text-slate-400 text-sm">
                &copy; {{ date('Y') }} Trifhop. All rights reserved.
            </p>
        </div>
    </div>

    <script>
        function togglePassword() {
            const input = document.getElementById('passwordInput');
            const icon = document.getElementById('toggleIcon');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }
    </script>
</body>
</html>
