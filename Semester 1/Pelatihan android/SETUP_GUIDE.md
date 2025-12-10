# Setup Backend dan Frontend - Aplikasi Monitoring Kelas

## 📱 Aplikasi sudah terhubung antara Backend Laravel dan Frontend Android!

### 🚀 Cara Menjalankan

#### 1. Setup Backend Laravel

1. **Masuk ke folder backend:**
   ```bash
   cd "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"
   ```

2. **Install dependencies (jika belum):**
   ```bash
   composer install
   ```

3. **Setup database:**
   - Buat database MySQL bernama `sekolah`
   - Update file `.env` jika perlu (DB_DATABASE, DB_USERNAME, DB_PASSWORD)

4. **Jalankan migration:**
   ```bash
   php artisan migrate
   ```

5. **Jalankan server Laravel:**
   ```bash
   php artisan serve
   ```
   
   Server akan berjalan di `http://localhost:8000` atau `http://127.0.0.1:8000`

6. **Dapatkan IP Address komputer Anda:**
   - Buka Command Prompt/PowerShell
   - Ketik: `ipconfig`
   - Cari IPv4 Address (contoh: 192.168.1.100)

#### 2. Setup Frontend Android

1. **Update IP Address di RetrofitClient.kt:**
   - Buka file: `AplikasiMonitoringKelas3\app\src\main\java\com\example\aplikasimonitoringkelas3\data\api\RetrofitClient.kt`
   - Ubah `BASE_URL`:
     - Jika menggunakan **Emulator Android Studio**: `http://10.0.2.2:8000/api/`
     - Jika menggunakan **Device Fisik**: `http://[IP_ADDRESS_KOMPUTER]:8000/api/`
       (contoh: `http://192.168.1.100:8000/api/`)

2. **Sync Gradle:**
   - Buka project di Android Studio
   - Klik "Sync Now" jika muncul notifikasi
   - Tunggu sampai proses sync selesai

3. **Build dan Run:**
   - Pilih device/emulator
   - Klik Run (▶️)

### 📝 Data untuk Testing

#### User Default (buat manual via API atau database):
```sql
INSERT INTO users (nama, name, email, password, role, created_at, updated_at) 
VALUES 
('Admin User', 'Admin User', 'admin@smkn2.sch.id', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', NOW(), NOW()),
('Siswa Test', 'Siswa Test', 'siswa@smkn2.sch.id', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Siswa', NOW(), NOW());
```
Password: `password`

#### Atau buat user baru melalui aplikasi:
- Pilih role yang sesuai
- Masukkan email dan password
- Klik Login (akan mendaftar otomatis jika belum ada)

### 🔧 Struktur API Endpoints

#### Authentication
- `POST /api/login` - Login user
- `POST /api/register` - Register user baru
- `POST /api/logout` - Logout user

#### Users
- `GET /api/users` - Get semua users
- `POST /api/users` - Create user baru
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

#### Jadwal
- `GET /api/jadwals` - Get semua jadwal
- `POST /api/jadwals` - Create jadwal baru
- `GET /api/jadwals/{id}` - Get jadwal by ID
- `PUT /api/jadwals/{id}` - Update jadwal
- `DELETE /api/jadwals/{id}` - Delete jadwal

### 🛠️ Troubleshooting

#### 1. Error "Connection refused" atau "Unable to resolve host"
   - Pastikan Laravel server sudah berjalan
   - Periksa IP address di RetrofitClient.kt
   - Pastikan komputer dan device di jaringan yang sama (untuk device fisik)
   - Nonaktifkan firewall jika perlu

#### 2. Error "CLEARTEXT communication not permitted"
   - Sudah diatasi dengan `android:usesCleartextTraffic="true"` di AndroidManifest.xml
   - Dan `network_security_config.xml`

#### 3. Error database di Laravel
   - Jalankan: `php artisan migrate:fresh`
   - Periksa koneksi database di file `.env`

#### 4. CORS Error
   - Sudah dikonfigurasi di `config/cors.php`
   - Pastikan `'allowed_origins' => ['*']` untuk development

#### 5. Error Build Android
   - Clean project: `Build > Clean Project`
   - Rebuild: `Build > Rebuild Project`
   - Invalidate Caches: `File > Invalidate Caches / Restart`

### 📱 Fitur yang Sudah Terintegrasi

✅ Login dengan email dan password (API)
✅ Role-based authentication (Siswa, Kurikulum, Kepala Sekolah, Admin)
✅ Entry User (Admin) dengan save ke database
✅ Entry Jadwal (Admin) dengan save ke database
✅ List Jadwal dengan data dari API
✅ Delete User dan Jadwal
✅ Loading indicators
✅ Error handling
✅ Success messages

### 🎯 Next Steps (Opsional)

1. Tambahkan validasi form yang lebih lengkap
2. Implementasi refresh token
3. Tambahkan SharedPreferences untuk menyimpan session
4. Implementasi fitur edit user dan jadwal
5. Tambahkan pagination untuk list yang panjang
6. Implementasi search/filter
7. Tambahkan logout functionality
8. Implementasi fitur untuk role Siswa, Kurikulum, Kepala Sekolah

### 📞 Support

Jika ada error, periksa:
1. **Android Studio Logcat** - untuk error Android
2. **Laravel Log** - `storage/logs/laravel.log`
3. **Chrome DevTools** - Network tab untuk melihat request/response

---

**Selamat! Aplikasi sudah berhasil terhubung! 🎉**
