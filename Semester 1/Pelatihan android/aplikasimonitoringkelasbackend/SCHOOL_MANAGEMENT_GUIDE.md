# 🎓 School Management System - Dokumentasi Lengkap

## 📋 Deskripsi
School Management System adalah aplikasi web profesional untuk manajemen sekolah yang dibangun dengan Laravel 9. Aplikasi ini memiliki fitur CRUD lengkap untuk mengelola Users, Guru (Teachers), Kelas (Classes), dan Jadwal Pelajaran (Schedules) dengan UI/UX yang modern dan profesional.

## ✨ Fitur Utama

### 1. **Authentication System**
- ✅ Login dengan email dan password
- ✅ Role-based access (Admin/User)
- ✅ Protected routes dengan middleware
- ✅ Session management

### 2. **Dashboard**
- 📊 Statistics cards (Total Users, Teachers, Classes, Schedules)
- 📅 Recent Schedules table
- 👨‍🏫 Recent Teachers list
- 🎨 Modern UI dengan gradient dan glass-morphism

### 3. **Manage Users**
- ✅ Create, Read, Update, Delete users
- ✅ Set user roles (Admin/User)
- ✅ Password management
- 🔍 Real-time search functionality

### 4. **Data Guru (Teachers)**
- ✅ CRUD operations untuk guru
- 📝 Data: NIP, Nama, Gender, Email, Phone, Address, Subject
- 🔍 Search dan filter
- 📊 Pagination

### 5. **Data Kelas (Classes)**
- ✅ CRUD operations untuk kelas
- 📝 Data: Class Name, Grade Level, Major, Homeroom Teacher, Room, Capacity
- 🔗 Relasi dengan Guru (Wali Kelas)
- 📊 Capacity management

### 6. **Jadwal Pelajaran (Schedules)**
- ✅ CRUD operations untuk jadwal
- 📝 Data: Subject, Class, Teacher, Day, Start Time, End Time, Room
- 🔗 Relasi dengan Kelas dan Guru
- 📅 Time management (7 days a week)

## 🎨 Design Features

### UI/UX yang Profesional
- 🎨 **Color Scheme**: Modern gradient dengan primary color #4f46e5 (Indigo)
- 🖼️ **Layout**: Sidebar navigation + Top bar dengan user info
- 💳 **Cards**: Shadow dan hover effects untuk interaktif
- 🔘 **Buttons**: Gradient background dengan smooth transitions
- 📱 **Responsive**: Mobile-friendly design
- 🔍 **Search**: Real-time search di setiap table
- ⚡ **Animations**: Smooth transitions dan hover effects

### Icons & Typography
- 🎯 Font Awesome 6.4.0 untuk icons
- 📝 Google Font "Inter" untuk typography
- 🎨 Professional badge system untuk status

## 🚀 Installation & Setup

### Prerequisites
- PHP 8.0+
- Composer
- MySQL
- Laravel 9

### Step 1: Database Setup
```sql
CREATE DATABASE sekolah;
```

### Step 2: Configure Environment
Edit file `.env`:
```env
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=sekolah
DB_USERNAME=root
DB_PASSWORD=
```

### Step 3: Run Migrations
```bash
cd "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"
php artisan migrate
```

### Step 4: Seed Sample Data (Optional)
```bash
php artisan db:seed --class=SchoolDataSeeder
```

Ini akan membuat:
- 2 users (admin@admin.com / admin123, user@example.com / password)
- 5 teachers
- 4 classes
- 8 schedules

### Step 5: Start Server
```bash
php artisan serve
```

Aplikasi berjalan di: **http://127.0.0.1:8000**

## 🔐 Login Credentials

### Admin Account
- **Email**: admin@admin.com
- **Password**: admin123
- **Role**: Administrator (Full Access)

### Regular User
- **Email**: user@example.com
- **Password**: password
- **Role**: User

## 📁 Project Structure

```
sekolah-api/
├── app/
│   ├── Http/
│   │   ├── Controllers/
│   │   │   ├── AuthController.php
│   │   │   ├── DashboardController.php
│   │   │   ├── WebUserController.php
│   │   │   ├── GuruController.php
│   │   │   ├── KelasController.php
│   │   │   └── JadwalController.php
│   │   └── Middleware/
│   │       └── AdminMiddleware.php
│   └── Models/
│       ├── User.php
│       ├── Guru.php
│       ├── Kelas.php
│       └── Jadwal.php
├── database/
│   ├── migrations/
│   │   ├── 2024_10_29_100001_create_guru_table.php
│   │   ├── 2024_10_29_100002_create_kelas_table.php
│   │   └── 2024_10_29_100003_create_jadwal_table.php
│   └── seeders/
│       └── SchoolDataSeeder.php
├── resources/
│   └── views/
│       ├── layouts/
│       │   └── admin.blade.php
│       ├── auth/
│       │   └── login.blade.php
│       ├── dashboard.blade.php
│       ├── users/
│       │   ├── index.blade.php
│       │   ├── create.blade.php
│       │   └── edit.blade.php
│       ├── guru/
│       │   ├── index.blade.php
│       │   ├── create.blade.php
│       │   └── edit.blade.php
│       ├── kelas/
│       │   ├── index.blade.php
│       │   ├── create.blade.php
│       │   └── edit.blade.php
│       └── jadwal/
│           ├── index.blade.php
│           ├── create.blade.php
│           └── edit.blade.php
└── routes/
    └── web.php
```

## 🗄️ Database Schema

### Users Table
| Field | Type | Description |
|-------|------|-------------|
| id | bigint | Primary Key |
| name | string | User's name |
| email | string | Unique email |
| password | string | Hashed password |
| role | enum | 'admin' or 'user' |
| created_at | timestamp | Created timestamp |
| updated_at | timestamp | Updated timestamp |

### Guru Table
| Field | Type | Description |
|-------|------|-------------|
| id | bigint | Primary Key |
| nip | string | Teacher ID (Unique) |
| nama | string | Full name |
| jenis_kelamin | enum | 'Laki-laki' or 'Perempuan' |
| email | string | Unique email |
| telepon | string | Phone number |
| alamat | text | Address |
| mata_pelajaran | string | Subject taught |

### Kelas Table
| Field | Type | Description |
|-------|------|-------------|
| id | bigint | Primary Key |
| nama_kelas | string | Class name |
| tingkat | integer | Grade level (10-12) |
| jurusan | string | Major |
| wali_kelas_id | bigint | Foreign Key to Guru |
| ruangan | string | Room number |
| kapasitas | integer | Student capacity |

### Jadwal Table
| Field | Type | Description |
|-------|------|-------------|
| id | bigint | Primary Key |
| kelas_id | bigint | Foreign Key to Kelas |
| guru_id | bigint | Foreign Key to Guru |
| mata_pelajaran | string | Subject name |
| hari | enum | Day (Senin-Sabtu) |
| jam_mulai | time | Start time |
| jam_selesai | time | End time |
| ruangan | string | Room number |

## 🔗 Routes

### Authentication
- `GET /` - Login page
- `POST /login` - Process login
- `POST /logout` - Logout

### Dashboard
- `GET /dashboard` - Main dashboard (protected)

### Users Management
- `GET /manage-users` - List all users
- `GET /manage-users/create` - Create user form
- `POST /manage-users` - Store new user
- `GET /manage-users/{id}/edit` - Edit user form
- `PUT /manage-users/{id}` - Update user
- `DELETE /manage-users/{id}` - Delete user

### Guru (Teachers)
- `GET /guru` - List all teachers
- `GET /guru/create` - Create teacher form
- `POST /guru` - Store new teacher
- `GET /guru/{id}/edit` - Edit teacher form
- `PUT /guru/{id}` - Update teacher
- `DELETE /guru/{id}` - Delete teacher

### Kelas (Classes)
- `GET /kelas` - List all classes
- `GET /kelas/create` - Create class form
- `POST /kelas` - Store new class
- `GET /kelas/{id}/edit` - Edit class form
- `PUT /kelas/{id}` - Update class
- `DELETE /kelas/{id}` - Delete class

### Jadwal (Schedules)
- `GET /jadwal` - List all schedules
- `GET /jadwal/create` - Create schedule form
- `POST /jadwal` - Store new schedule
- `GET /jadwal/{id}/edit` - Edit schedule form
- `PUT /jadwal/{id}` - Update schedule
- `DELETE /jadwal/{id}` - Delete schedule

## 🎯 Usage Guide

### 1. Login ke Aplikasi
1. Buka browser dan akses `http://127.0.0.1:8000`
2. Masukkan email: `admin@admin.com`
3. Masukkan password: `admin123`
4. Klik "Sign In"

### 2. Dashboard Overview
Setelah login, Anda akan melihat:
- **Statistics Cards**: Menampilkan total users, guru, kelas, dan jadwal
- **Recent Schedules**: Tabel 5 jadwal terbaru
- **Recent Teachers**: List 5 guru terbaru

### 3. Mengelola Users
**Menambah User Baru:**
1. Klik "Manage Users" di sidebar
2. Klik tombol "Add New User"
3. Isi form: Name, Email, Password, Role
4. Klik "Create User"

**Edit User:**
1. Di halaman Users, klik icon Edit (✏️)
2. Update data yang diperlukan
3. Klik "Update User"

**Hapus User:**
1. Di halaman Users, klik icon Delete (🗑️)
2. Konfirmasi penghapusan

### 4. Mengelola Guru
**Menambah Guru Baru:**
1. Klik "Data Guru" di sidebar
2. Klik "Add New Teacher"
3. Isi form lengkap:
   - NIP (unique)
   - Nama lengkap
   - Jenis Kelamin
   - Mata Pelajaran
   - Email
   - Telepon
   - Alamat
4. Klik "Save Teacher"

### 5. Mengelola Kelas
**Menambah Kelas Baru:**
1. Klik "Data Kelas" di sidebar
2. Klik "Add New Class"
3. Isi form:
   - Nama Kelas (e.g., "X RPL 1")
   - Tingkat (10, 11, atau 12)
   - Jurusan (e.g., "RPL", "IPA", "IPS")
   - Wali Kelas (pilih dari dropdown guru)
   - Ruangan
   - Kapasitas
4. Klik "Save Class"

### 6. Mengelola Jadwal
**Menambah Jadwal Baru:**
1. Klik "Jadwal Pelajaran" di sidebar
2. Klik "Add New Schedule"
3. Isi form:
   - Mata Pelajaran
   - Kelas (pilih dari dropdown)
   - Guru (pilih dari dropdown)
   - Hari (Senin-Sabtu)
   - Jam Mulai
   - Jam Selesai
   - Ruangan
4. Klik "Save Schedule"

### 7. Search & Filter
Setiap halaman list (Users, Guru, Kelas, Jadwal) memiliki search box:
- Ketik kata kunci di search box
- Table akan otomatis filter sesuai keyword
- Search bekerja real-time tanpa perlu klik

## 🎨 Customization

### Mengubah Warna Theme
Edit file `resources/views/layouts/admin.blade.php`:
```css
:root {
    --primary: #4f46e5; /* Ubah warna primary */
    --primary-dark: #4338ca;
    --secondary: #8b5cf6;
    /* ... */
}
```

### Menambah Menu Sidebar
Edit file `resources/views/layouts/admin.blade.php`:
```html
<div class="menu-section">
    <div class="menu-label">Custom Menu</div>
    <a href="/custom-route" class="menu-item">
        <i class="fas fa-custom-icon"></i>
        <span>Custom Menu</span>
    </a>
</div>
```

## 🐛 Troubleshooting

### Problem: "Class not found" error
**Solution**: Run `composer dump-autoload`

### Problem: Migration error
**Solution**: 
```bash
php artisan migrate:fresh
php artisan db:seed --class=SchoolDataSeeder
```

### Problem: 404 on routes
**Solution**: Clear route cache
```bash
php artisan route:clear
php artisan route:cache
```

### Problem: Login redirect tidak work
**Solution**: Check `.env` file, pastikan `APP_URL=http://127.0.0.1:8000`

## 📊 Sample Data

Setelah running seeder, Anda akan memiliki:

**5 Guru:**
1. Budi Santoso - Matematika
2. Siti Nurhaliza - Bahasa Indonesia
3. Ahmad Hidayat - Fisika
4. Dewi Lestari - Kimia
5. Hendra Wijaya - Bahasa Inggris

**4 Kelas:**
1. X RPL 1 (Wali: Budi Santoso)
2. X RPL 2 (Wali: Siti Nurhaliza)
3. XI IPA 1 (Wali: Ahmad Hidayat)
4. XI IPS 1 (Wali: Dewi Lestari)

**8 Jadwal:**
- Berbagai mata pelajaran untuk setiap kelas
- Senin - Jumat
- Jam 07:00 - 10:00

## 🚀 Deployment

### Production Checklist
- [ ] Set `APP_ENV=production` di `.env`
- [ ] Set `APP_DEBUG=false`
- [ ] Generate app key: `php artisan key:generate`
- [ ] Optimize: `php artisan optimize`
- [ ] Cache config: `php artisan config:cache`
- [ ] Cache routes: `php artisan route:cache`
- [ ] Cache views: `php artisan view:cache`

## 📞 Support

Untuk pertanyaan atau bantuan, silakan hubungi:
- **Email**: support@schoolmanagement.com
- **Documentation**: README.md

## 📝 License

This project is open-source and available under the MIT License.

---

## ✅ Checklist Fitur

- ✅ Authentication System (Login/Logout)
- ✅ Role-based Access Control
- ✅ Professional Dashboard dengan Statistics
- ✅ CRUD Users (Create, Read, Update, Delete)
- ✅ CRUD Guru dengan validasi
- ✅ CRUD Kelas dengan relasi ke Guru
- ✅ CRUD Jadwal dengan relasi ke Kelas & Guru
- ✅ Real-time Search di semua table
- ✅ Pagination untuk data banyak
- ✅ Responsive Design (Mobile-friendly)
- ✅ Modern UI/UX dengan gradient & animations
- ✅ Form validation & error handling
- ✅ Database relationships (One-to-Many, Many-to-One)
- ✅ Sample data seeder
- ✅ Professional sidebar navigation

## 🎉 Selamat!

Aplikasi School Management System Anda sudah siap digunakan! Semua fitur CRUD sudah berfungsi dengan normal dan desain UI/UX sangat profesional.

**Akses Aplikasi**: http://127.0.0.1:8000
**Login**: admin@admin.com / admin123

Enjoy managing your school! 🎓
