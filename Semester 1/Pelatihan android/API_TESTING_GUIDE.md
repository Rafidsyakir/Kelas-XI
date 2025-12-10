# API TESTING GUIDE

## Test API dengan cURL atau Postman

### 1. Test Login
```bash
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@smkn2.sch.id\",\"password\":\"password\"}"
```

**Response:**
```json
{
  "success": true,
  "message": "Login berhasil",
  "data": {
    "user": {
      "id": 1,
      "nama": "Admin User",
      "email": "admin@smkn2.sch.id",
      "role": "Admin"
    },
    "token": "1|xxxxxxxxxxxxxxxxxx"
  }
}
```

### 2. Test Get Users
```bash
curl http://localhost:8000/api/users
```

### 3. Test Create User
```bash
curl -X POST http://localhost:8000/api/users \
  -H "Content-Type: application/json" \
  -d "{\"nama\":\"Test User\",\"email\":\"test@smkn2.sch.id\",\"password\":\"password\",\"role\":\"Siswa\"}"
```

### 4. Test Get Jadwals
```bash
curl http://localhost:8000/api/jadwals
```

### 5. Test Create Jadwal
```bash
curl -X POST http://localhost:8000/api/jadwals \
  -H "Content-Type: application/json" \
  -d "{\"kelas\":\"10 RPL\",\"guru\":\"Siti\",\"mata_pelajaran\":\"Matematika\",\"hari\":\"Senin\",\"jam_ke\":\"1\",\"ruangan\":\"Lab 1\"}"
```

## Default Users untuk Login

| Email | Password | Role |
|-------|----------|------|
| admin@smkn2.sch.id | password | Admin |
| siswa@smkn2.sch.id | password | Siswa |
| kurikulum@smkn2.sch.id | password | Kurikulum |
| kepsek@smkn2.sch.id | password | Kepala Sekolah |

## Test di Android App

1. **Login sebagai Admin:**
   - Email: `admin@smkn2.sch.id`
   - Password: `password`
   - Role: Admin

2. **Test Entry User:**
   - Masuk ke tab "Entry User"
   - Isi form dan klik Simpan
   - Cek list user yang muncul

3. **Test Entry Jadwal:**
   - Masuk ke tab "Entry Jadwal"
   - Pilih hari, kelas, mapel, guru
   - Isi jam ke- dan ruangan
   - Klik Simpan

4. **Test List Jadwal:**
   - Masuk ke tab "List Jadwal"
   - Lihat semua jadwal yang sudah dibuat
   - Coba hapus jadwal dengan klik icon delete

## Troubleshooting API

### Check if server is running:
```bash
curl http://localhost:8000/api/users
```

### Check Laravel logs:
```bash
tail -f "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api\storage\logs\laravel.log"
```

### Reset database:
```bash
cd "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"
php artisan migrate:fresh --seed --seeder=DevelopmentSeeder
```
