<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title') - Trifhop Admin</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');
        
        * {
            font-family: 'Inter', sans-serif;
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
        
        @keyframes slideInLeft {
            from {
                transform: translateX(-100%);
            }
            to {
                transform: translateX(0);
            }
        }
        
        .animate-fade-in-up {
            animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }
        
        .animate-slide-in-left {
            animation: slideInLeft 0.5s cubic-bezier(0.16, 1, 0.3, 1);
        }
        
        .card-hover {
            transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
        }
        
        .card-hover:hover {
            transform: translateY(-4px);
        }
        
        .sidebar-link {
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
        
        .sidebar-link:hover {
            transform: translateX(8px);
        }
        
        .glass-effect {
            background: rgba(15, 23, 42, 0.7);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(148, 163, 184, 0.1);
        }
        
        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
                transition: transform 0.3s;
            }
            .sidebar.active {
                transform: translateX(0);
            }
        }
    </style>
    @yield('styles')
</head>
<body class="bg-slate-950 text-slate-100">
    <div class="flex h-screen overflow-hidden">
        <!-- Sidebar -->
        <aside class="sidebar animate-slide-in-left w-72 bg-gradient-to-b from-slate-900 to-slate-950 border-r border-slate-800 shadow-2xl flex-shrink-0 hidden md:flex flex-col">
            <!-- Logo -->
            <div class="flex items-center justify-between px-6 h-20 border-b border-slate-800">
                <div class="flex items-center space-x-3">
                    <img src="{{ asset('images/logo.png') }}" alt="Trifhop Logo" class="w-12 h-12 rounded-xl shadow-lg object-contain">
                    <div>
                        <h1 class="text-2xl font-bold text-white">Trifhop</h1>
                        <p class="text-xs text-slate-400">Admin Panel</p>
                    </div>
                </div>
            </div>

            <!-- Navigation -->
            <nav class="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
                <a href="{{ route('admin.dashboard') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.dashboard') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-home w-5 text-center"></i>
                    <span class="font-semibold">Dashboard</span>
                    @if(request()->routeIs('admin.dashboard'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.products.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.products.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-box w-5 text-center"></i>
                    <span class="font-medium">Inventory</span>
                    @if(request()->routeIs('admin.products.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.categories.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.categories.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-tags w-5 text-center"></i>
                    <span class="font-medium">Categories</span>
                    @if(request()->routeIs('admin.categories.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.customers.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.customers.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-users w-5 text-center"></i>
                    <span class="font-medium">Customers</span>
                    @if(request()->routeIs('admin.customers.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.orders.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.orders.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-shopping-cart w-5 text-center"></i>
                    <span class="font-medium">Orders</span>
                    @if(request()->routeIs('admin.orders.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.reports.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.reports.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-chart-bar w-5 text-center"></i>
                    <span class="font-medium">Reports</span>
                    @if(request()->routeIs('admin.reports.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
                <a href="{{ route('admin.settings.index') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3.5 rounded-xl {{ request()->routeIs('admin.settings.*') ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' : 'text-slate-400 hover:bg-slate-800/50 hover:text-white' }}">
                    <i class="fas fa-cog w-5 text-center"></i>
                    <span class="font-medium">Pengaturan</span>
                    @if(request()->routeIs('admin.settings.*'))
                        <i class="fas fa-chevron-right ml-auto text-xs"></i>
                    @endif
                </a>
            </nav>

            <!-- User Profile -->
            <div class="p-4 border-t border-slate-800">
                <div class="flex items-center space-x-3 px-4 py-3 rounded-xl bg-slate-800/50 mb-3">
                    <div class="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center shadow-md">
                        <span class="text-white font-bold text-sm">{{ substr(Auth::user()->name, 0, 1) }}</span>
                    </div>
                    <div class="flex-1 min-w-0">
                        <p class="text-white font-semibold text-sm truncate">{{ Auth::user()->name }}</p>
                        <p class="text-slate-400 text-xs flex items-center">
                            <span class="w-2 h-2 bg-green-400 rounded-full mr-1.5 animate-pulse"></span>
                            Administrator
                        </p>
                    </div>
                </div>
                <form action="{{ route('admin.logout') }}" method="POST">
                    @csrf
                    <button type="submit" class="w-full px-4 py-2.5 rounded-xl bg-red-500/10 text-red-400 hover:bg-red-500/20 border border-red-500/20 hover:border-red-500/30 transition-all flex items-center justify-center space-x-2 font-medium">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Logout</span>
                    </button>
                </form>
            </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1 overflow-x-hidden overflow-y-auto bg-slate-950">
            <!-- Top Bar -->
            <header class="bg-slate-900/50 backdrop-blur-xl border-b border-slate-800 sticky top-0 z-40">
                <div class="flex items-center justify-between px-8 py-5">
                    <div>
                        <h1 class="text-3xl font-bold text-white mb-1">@yield('page-title')</h1>
                        <p class="text-slate-400 text-sm">@yield('page-description')</p>
                    </div>
                    <div class="flex items-center space-x-4">
                        @yield('header-actions')
                        <div class="text-right">
                            <p class="text-sm font-medium text-slate-300">{{ date('l, F j, Y') }}</p>
                            <p class="text-xs text-slate-500">{{ date('h:i A') }}</p>
                        </div>
                    </div>
                </div>
            </header>

            <!-- Page Content -->
            <div class="p-8">
                @if(session('success'))
                    <div class="animate-fade-in-up bg-green-500/20 border border-green-500/30 text-green-400 px-6 py-4 rounded-xl mb-6 flex items-center">
                        <i class="fas fa-check-circle text-xl mr-3"></i>
                        <span class="font-medium">{{ session('success') }}</span>
                    </div>
                @endif

                @if(session('error'))
                    <div class="animate-fade-in-up bg-red-500/20 border border-red-500/30 text-red-400 px-6 py-4 rounded-xl mb-6 flex items-center">
                        <i class="fas fa-exclamation-circle text-xl mr-3"></i>
                        <span class="font-medium">{{ session('error') }}</span>
                    </div>
                @endif

                @yield('content')
            </div>
        </main>
    </div>

    @yield('scripts')
</body>
</html>
