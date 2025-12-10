# 🚀 Quick Start Guide - Task Manager Full Stack App

## ✅ Laravel Backend (SUDAH SIAP!)

### File yang sudah dibuat:
1. ✅ **Migration:** `database/migrations/2024_01_01_000000_create_tasks_table.php`
2. ✅ **Model:** `app/Models/Task.php`
3. ✅ **Controller:** `app/Http/Controllers/Api/TaskController.php`
4. ✅ **Routes:** `routes/api.php` (sudah ada route `tasks`)

### Jalankan Laravel:

```powershell
# Masuk ke folder Laravel
cd "sekolah-api"

# Jalankan migrasi (jika belum)
php artisan migrate

# Start server
php artisan serve
```

Server akan berjalan di: `http://localhost:8000`

### API Endpoints yang tersedia:

| Method | URL | Deskripsi |
|--------|-----|-----------|
| GET | `/api/tasks` | Ambil semua tasks |
| POST | `/api/tasks` | Buat task baru |
| GET | `/api/tasks/{id}` | Ambil 1 task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Hapus task |

### Test API dengan PowerShell:

```powershell
# GET - Ambil semua tasks
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks" -Method GET

# POST - Buat task baru
$body = @{title="Belajar Laravel"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks" -Method POST -Body $body -ContentType "application/json"

# PUT - Update task (id=1)
$body = @{is_completed=$true} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks/1" -Method PUT -Body $body -ContentType "application/json"

# DELETE - Hapus task (id=1)
Invoke-RestMethod -Uri "http://localhost:8000/api/tasks/1" -Method DELETE
```

---

## 📱 Android App Jetpack Compose

### Semua kode ada di file: `COMPLETE_ANDROID_CODE.md`

### Langkah-langkah:

1. **Buka Android Studio** → Create New Project → Empty Activity (Compose)
   - Name: `Task Manager`
   - Package name: `com.example.taskmanager`
   - Minimum SDK: API 24
   - Language: Kotlin
   - Build configuration language: Kotlin DSL

2. **Copy semua file** dari `COMPLETE_ANDROID_CODE.md` ke project Android Studio sesuai struktur folder

3. **Update `build.gradle.kts`** (module & project level) dengan dependencies dari file tersebut

4. **Sync Gradle**

5. **Ubah BASE_URL** di `RetrofitClient.kt`:
   - Untuk Emulator: `http://10.0.2.2:8000/api/`
   - Untuk HP Fisik: `http://IP_KOMPUTER_ANDA:8000/api/`
   
   Cara cek IP komputer:
   ```powershell
   ipconfig
   # Cari "IPv4 Address" di bagian WiFi/Ethernet
   ```

6. **Update AndroidManifest.xml** (sudah ada di dokumentasi)

7. **Run App!** 🎉

---

## 📂 Struktur Project Android

```
com.example.taskmanager/
├── data/
│   ├── remote/
│   │   ├── dto/
│   │   │   └── Task.kt
│   │   ├── ApiService.kt
│   │   └── RetrofitClient.kt
│   └── repository/
│       └── TaskRepositoryImpl.kt
├── di/
│   └── AppModule.kt
├── domain/
│   └── repository/
│       └── TaskRepository.kt
├── presentation/
│   ├── TaskScreen.kt
│   ├── TaskItem.kt
│   ├── TaskViewModel.kt
│   └── TaskUiState.kt
├── util/
│   └── Resource.kt
├── MainActivity.kt
└── TaskManagerApp.kt
```

---

## 🎯 Fitur Aplikasi

### Laravel:
- ✅ REST API dengan validasi
- ✅ CRUD lengkap
- ✅ JSON response
- ✅ Model dengan casting tipe data

### Android:
- ✅ Material 3 Design
- ✅ Clean Architecture
- ✅ Hilt Dependency Injection
- ✅ StateFlow untuk state management
- ✅ Error handling
- ✅ Loading states
- ✅ Optimistic UI updates
- ✅ CRUD operations lengkap

---

## 🔧 Troubleshooting

### Laravel tidak bisa diakses dari HP:
1. Jalankan Laravel dengan IP binding:
   ```powershell
   php artisan serve --host=0.0.0.0 --port=8000
   ```
2. Matikan firewall Windows atau izinkan port 8000

### Android tidak bisa connect ke API:
1. Pastikan Laravel server running
2. Cek BASE_URL di RetrofitClient.kt
3. Untuk emulator: gunakan `10.0.2.2`
4. Untuk HP fisik: gunakan IP komputer (bukan localhost)
5. HP dan komputer harus di WiFi yang sama

### App crash saat dibuka:
1. Pastikan `@HiltAndroidApp` ada di TaskManagerApp.kt
2. Cek AndroidManifest.xml → `android:name=".TaskManagerApp"`
3. Sync Gradle dan rebuild project

---

## 📸 Tampilan App

1. **Top Bar**: "Task Manager" dengan tombol Refresh
2. **Add Section**: TextField + Save Button
3. **Task List**: Checkbox + Title + Delete Button
4. **Error Message**: Red card dengan tombol Dismiss
5. **Loading**: Circular progress indicator
6. **Empty State**: "No tasks yet. Add one above!"

---

## 💡 Tips Pengembangan

1. **Debugging Network**: Lihat Logcat di Android Studio untuk log HTTP
2. **Test API**: Gunakan Postman atau Thunder Client di VS Code
3. **Database**: Gunakan phpMyAdmin atau Laravel Tinker untuk cek data
4. **Live Reload**: Gunakan hot reload di Android Studio untuk UI changes

---

## 📚 Dokumentasi Lengkap

- **Laravel Backend**: Sudah ada di folder `sekolah-api/`
- **Android Code**: Lihat file `COMPLETE_ANDROID_CODE.md`
- **Previous Android Docs**: `ANDROID_TASK_MANAGER_APP.md`

---

**🎉 Selamat! Project Task Manager Full Stack Anda siap digunakan!**

Jika ada pertanyaan atau error, cek troubleshooting di atas atau dokumentasi lengkap.
