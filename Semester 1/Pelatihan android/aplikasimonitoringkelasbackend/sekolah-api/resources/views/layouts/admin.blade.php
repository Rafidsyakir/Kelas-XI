<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>@yield('title', 'Dashboard') - Aurora Admin</title>
    
    <!-- Preconnect untuk CDN -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="preconnect" href="https://cdnjs.cloudflare.com">
    
    <!-- Fonts - Orbitron & Rajdhani for futuristic look -->
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700;800;900&family=Rajdhani:wght@300;400;500;600;700&family=Space+Grotesk:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Font Awesome dengan defer -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" media="print" onload="this.media='all'">
    
    <style>
        :root {
            /* Aurora Night Theme */
            --neon-cyan: #00fff2;
            --neon-purple: #bf00ff;
            --neon-pink: #ff00d4;
            --neon-blue: #00d4ff;
            --aurora-green: #00ff88;
            --space-black: #0a0a0f;
            --deep-purple: #1a0a2e;
            --midnight: #0d0d1a;
            --primary: #00fff2;
            --primary-dark: #00ccc2;
            --primary-light: #66fff7;
            --secondary: #bf00ff;
            --success: #00ff88;
            --danger: #ff3366;
            --warning: #ffaa00;
            --info: #00d4ff;
            --dark: #ffffff;
            --light: #0d0d1a;
            --white: #ffffff;
            --gray: #8892b0;
            --gray-light: rgba(255, 255, 255, 0.1);
            --gray-dark: #ccd6f6;
            --sidebar-width: 280px;
            --topbar-height: 70px;
            --shadow: 0 4px 30px rgba(0, 255, 242, 0.1);
            --shadow-lg: 0 20px 50px rgba(0, 255, 242, 0.2);
            --glow-cyan: 0 0 20px rgba(0, 255, 242, 0.5);
            --glow-purple: 0 0 20px rgba(191, 0, 255, 0.5);
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Space Grotesk', 'Rajdhani', -apple-system, BlinkMacSystemFont, sans-serif;
            background: var(--space-black);
            color: var(--white);
            line-height: 1.6;
            overflow-x: hidden;
        }

        /* Animated Background */
        body::before {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: 
                radial-gradient(ellipse at 20% 0%, rgba(0, 255, 242, 0.1) 0%, transparent 50%),
                radial-gradient(ellipse at 80% 100%, rgba(191, 0, 255, 0.1) 0%, transparent 50%),
                radial-gradient(ellipse at 50% 50%, rgba(0, 212, 255, 0.05) 0%, transparent 50%);
            z-index: -1;
            animation: auroraShift 15s ease-in-out infinite;
        }

        @keyframes auroraShift {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.7; }
        }

        /* Sidebar */
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            width: var(--sidebar-width);
            height: 100vh;
            background: linear-gradient(180deg, rgba(26, 10, 46, 0.95) 0%, rgba(10, 10, 15, 0.98) 100%);
            backdrop-filter: blur(20px);
            color: white;
            z-index: 1000;
            overflow-y: auto;
            border-right: 1px solid rgba(0, 255, 242, 0.2);
            box-shadow: var(--shadow-lg);
        }

        .sidebar::-webkit-scrollbar {
            width: 4px;
        }

        .sidebar::-webkit-scrollbar-track {
            background: rgba(0, 255, 242, 0.05);
        }

        .sidebar::-webkit-scrollbar-thumb {
            background: linear-gradient(180deg, var(--neon-cyan), var(--neon-purple));
            border-radius: 4px;
        }

        .sidebar-brand {
            padding: 25px 20px;
            border-bottom: 1px solid rgba(0, 255, 242, 0.2);
            background: linear-gradient(135deg, rgba(0, 255, 242, 0.05), rgba(191, 0, 255, 0.05));
        }

        .brand-content {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .brand-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            box-shadow: var(--glow-cyan);
            animation: iconPulse 2s ease-in-out infinite;
        }

        @keyframes iconPulse {
            0%, 100% { box-shadow: 0 0 15px rgba(0, 255, 242, 0.5); }
            50% { box-shadow: 0 0 30px rgba(0, 255, 242, 0.8); }
        }

        .brand-text h2 {
            font-family: 'Orbitron', sans-serif;
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 2px;
            background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            text-transform: uppercase;
            letter-spacing: 2px;
        }

        .brand-text p {
            font-family: 'Rajdhani', sans-serif;
            font-size: 12px;
            color: var(--gray);
            text-transform: uppercase;
            letter-spacing: 3px;
        }

        .sidebar-menu {
            padding: 20px 0;
        }

        .menu-section {
            margin-bottom: 25px;
        }

        .menu-label {
            padding: 0 20px 10px;
            font-family: 'Orbitron', sans-serif;
            font-size: 10px;
            text-transform: uppercase;
            letter-spacing: 3px;
            color: var(--neon-cyan);
            opacity: 0.7;
            font-weight: 600;
        }

        .menu-item {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 14px 20px;
            color: var(--gray);
            text-decoration: none;
            transition: var(--transition);
            position: relative;
            font-family: 'Rajdhani', sans-serif;
            font-weight: 500;
            letter-spacing: 1px;
            border-left: 3px solid transparent;
        }

        .menu-item:hover {
            background: linear-gradient(90deg, rgba(0, 255, 242, 0.1), transparent);
            color: var(--neon-cyan);
            border-left-color: var(--neon-cyan);
        }

        .menu-item:hover i {
            text-shadow: var(--glow-cyan);
        }

        .menu-item.active {
            background: linear-gradient(90deg, rgba(0, 255, 242, 0.15), transparent);
            color: var(--neon-cyan);
            border-left: 3px solid var(--neon-cyan);
        }

        .menu-item.active::after {
            content: '';
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            width: 6px;
            height: 6px;
            background: var(--neon-cyan);
            border-radius: 50%;
            box-shadow: var(--glow-cyan);
        }

        .menu-item i {
            font-size: 18px;
            width: 26px;
            text-align: center;
            transition: var(--transition);
        }

        .menu-item span {
            font-size: 15px;
            font-weight: 600;
            text-transform: uppercase;
        }

        /* Main Content */
        .main-content {
            margin-left: var(--sidebar-width);
            min-height: 100vh;
            background: linear-gradient(135deg, var(--space-black) 0%, var(--midnight) 100%);
        }

        /* Topbar */
        .topbar {
            height: var(--topbar-height);
            background: rgba(10, 10, 15, 0.9);
            backdrop-filter: blur(20px);
            border-bottom: 1px solid rgba(0, 255, 242, 0.15);
            padding: 0 30px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 999;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .breadcrumb {
            display: flex;
            align-items: center;
            gap: 10px;
            font-family: 'Rajdhani', sans-serif;
            font-size: 14px;
            color: var(--gray);
            font-weight: 500;
            letter-spacing: 1px;
        }

        .breadcrumb i {
            font-size: 14px;
            color: var(--neon-cyan);
        }

        .topbar-right {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .topbar-user {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .user-avatar {
            width: 42px;
            height: 42px;
            background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--space-black);
            font-family: 'Orbitron', sans-serif;
            font-weight: 700;
            font-size: 16px;
            box-shadow: var(--glow-cyan);
        }

        .user-info {
            text-align: right;
        }

        .user-info strong {
            display: block;
            font-family: 'Rajdhani', sans-serif;
            font-size: 15px;
            font-weight: 600;
            color: var(--white);
            letter-spacing: 1px;
        }

        .user-info small {
            font-family: 'Rajdhani', sans-serif;
            font-size: 12px;
            color: var(--neon-cyan);
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 5px;
        }

        .btn-logout {
            padding: 10px 18px;
            background: linear-gradient(135deg, rgba(255, 51, 102, 0.2), rgba(255, 51, 102, 0.1));
            color: var(--danger);
            border: 1px solid rgba(255, 51, 102, 0.3);
            border-radius: 10px;
            font-family: 'Rajdhani', sans-serif;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 8px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-logout:hover {
            background: linear-gradient(135deg, var(--danger), #cc2952);
            color: white;
            box-shadow: 0 0 20px rgba(255, 51, 102, 0.4);
            transform: translateY(-2px);
        }

        /* Page Content */
        .page-content {
            padding: 30px;
        }

        .dashboard-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .page-title {
            font-family: 'Orbitron', sans-serif;
            font-size: 26px;
            font-weight: 700;
            background: linear-gradient(90deg, var(--neon-cyan), var(--neon-purple));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 5px;
            text-transform: uppercase;
            letter-spacing: 2px;
        }

        .page-title i {
            color: var(--neon-cyan);
            -webkit-text-fill-color: var(--neon-cyan);
            text-shadow: var(--glow-cyan);
        }

        .page-subtitle {
            font-family: 'Rajdhani', sans-serif;
            color: var(--gray);
            font-size: 15px;
            letter-spacing: 1px;
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: linear-gradient(135deg, rgba(26, 10, 46, 0.8), rgba(13, 13, 26, 0.9));
            backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            display: flex;
            align-items: center;
            gap: 20px;
            border: 1px solid rgba(0, 255, 242, 0.15);
            transition: var(--transition);
            position: relative;
            overflow: hidden;
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, transparent, rgba(0, 255, 242, 0.05));
            opacity: 0;
            transition: var(--transition);
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-lg);
            border-color: var(--neon-cyan);
        }

        .stat-card:hover::before {
            opacity: 1;
        }

        .stat-card.blue { border-left: 4px solid var(--neon-blue); }
        .stat-card.green { border-left: 4px solid var(--aurora-green); }
        .stat-card.orange { border-left: 4px solid var(--warning); }
        .stat-card.purple { border-left: 4px solid var(--neon-purple); }

        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            color: white;
        }

        .stat-card.blue .stat-icon { 
            background: linear-gradient(135deg, var(--neon-blue), #0099cc);
            box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
        }
        .stat-card.green .stat-icon { 
            background: linear-gradient(135deg, var(--aurora-green), #00cc6a);
            box-shadow: 0 0 20px rgba(0, 255, 136, 0.4);
        }
        .stat-card.orange .stat-icon { 
            background: linear-gradient(135deg, var(--warning), #cc8800);
            box-shadow: 0 0 20px rgba(255, 170, 0, 0.4);
        }
        .stat-card.purple .stat-icon { 
            background: linear-gradient(135deg, var(--neon-purple), #9900cc);
            box-shadow: 0 0 20px rgba(191, 0, 255, 0.4);
        }

        .stat-details {
            flex: 1;
        }

        .stat-number {
            font-family: 'Orbitron', sans-serif;
            font-size: 32px;
            font-weight: 800;
            color: var(--white);
            line-height: 1;
            margin-bottom: 5px;
        }

        .stat-label {
            font-family: 'Rajdhani', sans-serif;
            font-size: 14px;
            color: var(--gray);
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        /* Content Grid */
        .content-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
            gap: 20px;
        }

        .data-card {
            background: linear-gradient(135deg, rgba(26, 10, 46, 0.8), rgba(13, 13, 26, 0.9));
            backdrop-filter: blur(20px);
            border-radius: 16px;
            border: 1px solid rgba(0, 255, 242, 0.15);
            overflow: hidden;
        }

        .card-header {
            padding: 20px 25px;
            border-bottom: 1px solid rgba(0, 255, 242, 0.15);
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: rgba(0, 255, 242, 0.03);
        }

        .card-title {
            font-family: 'Orbitron', sans-serif;
            font-size: 16px;
            font-weight: 600;
            color: var(--white);
            display: flex;
            align-items: center;
            gap: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .card-title i {
            color: var(--neon-cyan);
            text-shadow: var(--glow-cyan);
        }

        .btn-link {
            font-family: 'Rajdhani', sans-serif;
            color: var(--neon-cyan);
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            transition: var(--transition);
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-link:hover {
            color: var(--neon-purple);
            text-shadow: var(--glow-purple);
        }

        .card-body {
            padding: 25px;
        }

        /* Table */
        .table-responsive {
            overflow-x: auto;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .data-table thead {
            background: rgba(0, 255, 242, 0.08);
        }

        .data-table th {
            padding: 14px 12px;
            text-align: left;
            font-family: 'Orbitron', sans-serif;
            font-size: 11px;
            font-weight: 600;
            color: var(--neon-cyan);
            text-transform: uppercase;
            letter-spacing: 2px;
        }

        .data-table td {
            padding: 14px 12px;
            border-top: 1px solid rgba(0, 255, 242, 0.1);
            font-family: 'Rajdhani', sans-serif;
            font-size: 15px;
            color: var(--gray-dark);
            font-weight: 500;
        }

        .data-table tbody tr {
            transition: var(--transition);
        }

        .data-table tbody tr:hover {
            background: rgba(0, 255, 242, 0.05);
        }

        /* Badge */
        .badge {
            display: inline-block;
            padding: 5px 14px;
            border-radius: 6px;
            font-family: 'Rajdhani', sans-serif;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .badge-primary { background: rgba(0, 255, 242, 0.15); color: var(--neon-cyan); border: 1px solid rgba(0, 255, 242, 0.3); }
        .badge-blue { background: rgba(0, 212, 255, 0.15); color: var(--neon-blue); border: 1px solid rgba(0, 212, 255, 0.3); }
        .badge-pink { background: rgba(255, 0, 212, 0.15); color: var(--neon-pink); border: 1px solid rgba(255, 0, 212, 0.3); }
        .badge-danger { background: rgba(255, 51, 102, 0.15); color: var(--danger); border: 1px solid rgba(255, 51, 102, 0.3); }
        .badge-success { background: rgba(0, 255, 136, 0.15); color: var(--aurora-green); border: 1px solid rgba(0, 255, 136, 0.3); }
        .badge-warning { background: rgba(255, 170, 0, 0.15); color: var(--warning); border: 1px solid rgba(255, 170, 0, 0.3); }
        .badge-secondary { background: rgba(136, 146, 176, 0.15); color: var(--gray); border: 1px solid rgba(136, 146, 176, 0.3); }

        /* List Group */
        .list-group {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .list-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 15px;
            background: rgba(0, 255, 242, 0.05);
            border: 1px solid rgba(0, 255, 242, 0.1);
            border-radius: 12px;
            transition: var(--transition);
        }

        .list-item:hover {
            background: rgba(0, 255, 242, 0.1);
            border-color: rgba(0, 255, 242, 0.3);
        }

        .list-avatar {
            width: 45px;
            height: 45px;
            background: linear-gradient(135deg, var(--neon-cyan), var(--neon-purple));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--space-black);
            font-family: 'Orbitron', sans-serif;
            font-weight: 700;
            font-size: 16px;
            box-shadow: var(--glow-cyan);
        }

        .list-details {
            flex: 1;
        }

        .list-details h4 {
            font-family: 'Rajdhani', sans-serif;
            font-size: 16px;
            font-weight: 600;
            color: var(--white);
            margin-bottom: 3px;
        }

        .list-details p {
            font-family: 'Rajdhani', sans-serif;
            font-size: 13px;
            color: var(--gray);
        }

        /* Empty State */
        .empty-state-small {
            text-align: center;
            padding: 40px 20px;
            color: var(--gray);
        }

        .empty-state-small i {
            font-size: 48px;
            margin-bottom: 15px;
            color: var(--neon-cyan);
            opacity: 0.3;
        }

        .empty-state-small p {
            font-family: 'Rajdhani', sans-serif;
            font-size: 14px;
        }

        /* Buttons */
        .btn {
            font-family: 'Rajdhani', sans-serif;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--neon-cyan), var(--neon-blue));
            color: var(--space-black);
            box-shadow: 0 0 20px rgba(0, 255, 242, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 30px rgba(0, 255, 242, 0.5);
        }

        .btn-secondary {
            background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink));
            color: white;
            box-shadow: 0 0 20px rgba(191, 0, 255, 0.3);
        }

        .btn-secondary:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 30px rgba(191, 0, 255, 0.5);
        }

        /* Form Controls */
        .form-control {
            font-family: 'Rajdhani', sans-serif;
            background: rgba(13, 13, 26, 0.8);
            border: 1px solid rgba(0, 255, 242, 0.2);
            border-radius: 10px;
            padding: 12px 16px;
            color: var(--white);
            font-size: 15px;
            transition: var(--transition);
        }

        .form-control:focus {
            outline: none;
            border-color: var(--neon-cyan);
            box-shadow: 0 0 15px rgba(0, 255, 242, 0.2);
        }

        .form-control::placeholder {
            color: var(--gray);
        }

        /* Responsive */
        @media (max-width: 1024px) {
            .content-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
            }

            .sidebar.active {
                transform: translateX(0);
            }

            .main-content {
                margin-left: 0;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .topbar {
                padding: 0 15px;
            }

            .page-content {
                padding: 20px 15px;
            }
        }
    </style>

    @yield('styles')
</head>
<body>
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-content">
                <div class="brand-icon">
                    <i class="fas fa-bolt"></i>
                </div>
                <div class="brand-text">
                    <h2>Aurora Admin</h2>
                    <p>Control Center</p>
                </div>
            </div>
        </div>

        <nav class="sidebar-menu">
            <div class="menu-section">
                <div class="menu-label">Navigation</div>
                <a href="{{ route('dashboard') }}" class="menu-item {{ request()->routeIs('dashboard') ? 'active' : '' }}">
                    <i class="fas fa-rocket"></i>
                    <span>Dashboard</span>
                </a>
            </div>

            <div class="menu-section">
                <div class="menu-label">Data Core</div>
                <a href="{{ route('manage-users.index') }}" class="menu-item {{ request()->routeIs('manage-users.*') ? 'active' : '' }}">
                    <i class="fas fa-user-astronaut"></i>
                    <span>Users</span>
                </a>
                <a href="{{ route('guru.index') }}" class="menu-item {{ request()->routeIs('guru.*') ? 'active' : '' }}">
                    <i class="fas fa-user-tie"></i>
                    <span>Teachers</span>
                </a>
                <a href="{{ route('kelas.index') }}" class="menu-item {{ request()->routeIs('kelas.*') ? 'active' : '' }}">
                    <i class="fas fa-cubes"></i>
                    <span>Classes</span>
                </a>
            </div>

            <div class="menu-section">
                <div class="menu-label">Academic</div>
                <a href="{{ route('jadwal.index') }}" class="menu-item {{ request()->routeIs('jadwal.*') ? 'active' : '' }}">
                    <i class="fas fa-calendar-week"></i>
                    <span>Schedule</span>
                </a>
                <a href="{{ route('teacher-attendance.index') }}" class="menu-item {{ request()->routeIs('teacher-attendance.*') ? 'active' : '' }}">
                    <i class="fas fa-fingerprint"></i>
                    <span>Attendance</span>
                </a>
                <a href="{{ route('guru-pengganti.index') }}" class="menu-item {{ request()->routeIs('guru-pengganti.*') ? 'active' : '' }}">
                    <i class="fas fa-sync-alt"></i>
                    <span>Substitutes</span>
                </a>
            </div>
        </nav>
    </aside>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Topbar -->
        <div class="topbar">
            <div class="topbar-left">
                <div class="breadcrumb">
                    <i class="fas fa-terminal"></i>
                    <span>// @yield('title', 'Dashboard')</span>
                </div>
            </div>

            <div class="topbar-right">
                <div class="topbar-user">
                    <div class="user-avatar">
                        {{ strtoupper(substr(Auth::user()->email, 0, 1)) }}
                    </div>
                    <div class="user-info">
                        <strong>{{ Auth::user()->nama ?? Auth::user()->email }}</strong>
                        <small><i class="fas fa-shield-alt"></i> Admin</small>
                    </div>
                </div>
                <form action="{{ route('logout') }}" method="POST">
                    @csrf
                    <button type="submit" class="btn-logout">
                        <i class="fas fa-power-off"></i>
                        Exit
                    </button>
                </form>
            </div>
        </div>

        <!-- Page Content -->
        <div class="page-content">
            @yield('content')
        </div>
    </div>

    @yield('scripts')
</body>
</html>
