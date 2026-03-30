<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>@yield('title', 'Trifhop Admin Dashboard')</title>
    
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>
    
    <script>
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    colors: {
                        "primary": "#137FEC",
                        "background-dark": "#0F172A",
                        "surface-dark": "#1E293B",
                        "border-dark": "#334155",
                    },
                    fontFamily: {
                        "display": ["Inter", "sans-serif"]
                    },
                    borderRadius: {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "2xl": "1rem",
                        "full": "9999px"
                    },
                },
            },
        }
    </script>
    
    <style type="text/tailwindcss">
        @layer base {
            body {
                @apply antialiased bg-background-dark text-slate-100 font-display;
            }
            .material-symbols-outlined {
                font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            }
        }
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        .animate-fade-in-up {
            animation: fadeInUp 0.5s ease-out;
        }
        .product-glow {
            box-shadow: 0 0 15px rgba(19, 127, 236, 0.15);
        }
        ::-webkit-scrollbar {
            width: 8px;
            height: 8px;
        }
        ::-webkit-scrollbar-track {
            background: #0F172A;
        }
        ::-webkit-scrollbar-thumb {
            background: #334155;
            border-radius: 10px;
        }
        ::-webkit-scrollbar-thumb:hover {
            background: #475569;
        }
    </style>
    
    @stack('styles')
</head>
<body class="bg-background-dark font-display text-slate-100 selection:bg-primary/30">
    <div class="flex h-screen overflow-hidden">
        <!-- Sidebar -->
        <aside class="w-64 bg-[#0B1120] border-r border-slate-800 flex flex-col shrink-0">
            <div class="p-6 flex items-center gap-3">
                <img src="{{ asset('images/logo.png') }}" alt="Trifhop Logo" class="w-10 h-10 rounded-xl shadow-lg object-contain">
                <div>
                    <h1 class="text-xl font-bold tracking-tight text-white leading-tight">Trifhop</h1>
                    <p class="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Admin Panel</p>
                </div>
            </div>
            
            <nav class="flex-1 px-4 py-4 space-y-1">
                <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg {{ request()->routeIs('admin.dashboard') ? 'bg-primary/10 text-primary border border-primary/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }} transition-all group" href="{{ route('admin.dashboard') }}">
                    <span class="material-symbols-outlined text-xl">dashboard</span>
                    <span class="text-sm font-semibold">Dashboard</span>
                </a>
                
                <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg {{ request()->routeIs('admin.products*') ? 'bg-primary/10 text-primary border border-primary/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }} transition-all group" href="{{ route('admin.products.index') }}">
                    <span class="material-symbols-outlined text-xl">inventory_2</span>
                    <span class="text-sm font-medium">Products</span>
                </a>
                
                <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg {{ request()->routeIs('admin.categories*') ? 'bg-primary/10 text-primary border border-primary/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }} transition-all group" href="{{ route('admin.categories.index') }}">
                    <span class="material-symbols-outlined text-xl">category</span>
                    <span class="text-sm font-medium">Categories</span>
                </a>
                
                <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg {{ request()->routeIs('admin.orders*') ? 'bg-primary/10 text-primary border border-primary/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }} transition-all group" href="{{ route('admin.orders.index') }}">
                    <span class="material-symbols-outlined text-xl">shopping_cart</span>
                    <span class="text-sm font-medium">Orders</span>
                </a>
                
                <a class="flex items-center gap-3 px-3 py-2.5 rounded-lg {{ request()->routeIs('admin.customers*') ? 'bg-primary/10 text-primary border border-primary/20' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }} transition-all group" href="{{ route('admin.customers.index') }}">
                    <span class="material-symbols-outlined text-xl">group</span>
                    <span class="text-sm font-medium">Customers</span>
                </a>
                
                <div class="pt-6 pb-2">
                    <p class="px-3 text-[10px] uppercase font-bold text-slate-600 tracking-widest">Settings</p>
                </div>
                
                <form action="{{ route('admin.logout') }}" method="POST">
                    @csrf
                    <button type="submit" class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-red-500/10 hover:text-red-400 transition-all group w-full text-left">
                        <span class="material-symbols-outlined text-xl">logout</span>
                        <span class="text-sm font-medium">Logout</span>
                    </button>
                </form>
            </nav>
            
            <div class="p-4 border-t border-slate-800">
                <div class="bg-gradient-to-br from-primary/10 to-blue-600/5 rounded-xl p-4 border border-primary/20 backdrop-blur-sm">
                    <div class="flex items-center gap-3 mb-3">
                        <div class="w-10 h-10 rounded-full bg-primary/20 border border-primary/30 flex items-center justify-center">
                            <span class="text-primary font-bold text-sm">{{ substr(Auth::user()->name ?? 'A', 0, 1) }}</span>
                        </div>
                        <div>
                            <p class="text-sm font-bold text-white">{{ Auth::user()->name ?? 'Admin' }}</p>
                            <p class="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Administrator</p>
                        </div>
                    </div>
                    <div class="h-px bg-slate-700/50 mb-3"></div>
                    <p class="text-xs text-slate-400 mb-3 leading-relaxed">Full system access enabled</p>
                    <a href="{{ route('admin.dashboard') }}" class="block w-full bg-primary/90 hover:bg-primary text-white text-xs font-bold py-2.5 rounded-lg text-center transition-all shadow-lg shadow-primary/20">
                        View Dashboard
                    </a>
                </div>
            </div>
        </aside>
        
        <!-- Main Content -->
        <main class="flex-1 flex flex-col overflow-y-auto bg-background-dark">
            <!-- Header -->
            <header class="h-16 bg-[#0F172A]/80 backdrop-blur-md border-b border-slate-800 flex items-center justify-between px-8 sticky top-0 z-20">
                <div class="flex items-center gap-4 flex-1 max-w-xl">
                    <div class="relative w-full group">
                        <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 text-xl group-focus-within:text-primary transition-colors">search</span>
                        <input class="w-full bg-slate-800/50 border border-slate-700/50 rounded-xl pl-10 pr-4 py-2 text-sm focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder:text-slate-500 text-white" placeholder="Search products, orders, or customers..." type="text"/>
                    </div>
                </div>
                
                <div class="flex items-center gap-4">
                    <button class="w-10 h-10 flex items-center justify-center rounded-xl bg-slate-800/50 text-slate-400 hover:text-primary hover:bg-slate-800 transition-all relative border border-slate-700/50">
                        <span class="material-symbols-outlined">notifications</span>
                        <span class="absolute top-2 right-2.5 w-2 h-2 bg-primary rounded-full ring-2 ring-[#0F172A]"></span>
                    </button>
                    
                    <button class="w-10 h-10 flex items-center justify-center rounded-xl bg-slate-800/50 text-slate-400 hover:text-primary hover:bg-slate-800 transition-all border border-slate-700/50">
                        <span class="material-symbols-outlined">forum</span>
                    </button>
                    
                    <div class="h-8 w-px bg-slate-800 mx-2"></div>
                    
                    <div class="flex items-center gap-3 pl-2">
                        <div class="text-right hidden sm:block">
                            <p class="text-sm font-bold text-white">{{ Auth::user()->name ?? 'Admin' }}</p>
                            <p class="text-[10px] text-slate-500 font-bold uppercase tracking-tighter">Administrator</p>
                        </div>
                        <div class="h-10 w-10 rounded-full bg-gradient-to-br from-primary/30 to-blue-600/20 border-2 border-primary/40 shadow-lg shadow-primary/20 flex items-center justify-center text-primary font-bold text-sm">
                            {{ substr(Auth::user()->name ?? 'A', 0, 1) }}
                        </div>
                    </div>
                </div>
            </header>

            <!-- Content -->
            <div class="p-8 space-y-8 max-w-[1600px] mx-auto w-full flex-1">
                @if(session('success'))
                    <div class="bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 px-6 py-4 rounded-xl flex items-center gap-3" >
                        <span class="material-symbols-outlined">check_circle</span>
                        <span class="font-medium">{{ session('success') }}</span>
                    </div>
                @endif

                @if(session('error'))
                    <div class="bg-red-500/10 border border-red-500/30 text-red-400 px-6 py-4 rounded-xl flex items-center gap-3">
                        <span class="material-symbols-outlined">error</span>
                        <span class="font-medium">{{ session('error') }}</span>
                    </div>
                @endif

                @yield('content')
            </div>
        </main>
    </div>
    
    @stack('scripts')
</body>
</html>
