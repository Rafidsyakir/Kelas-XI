# 🔧 PANDUAN LENGKAP PERBAIKAN MySQL XAMPP
## Database Anda AMAN - Tidak Akan Hilang!

---

## 🚨 JANGAN PANIC!

**DATABASE ANDA SUDAH DI-BACKUP OTOMATIS KE:**
- `C:\xampp\mysql\data_backup_20251126_071753`
- `C:\xampp\mysql\data_backup_20251126_072012`

Semua database (termasuk database `sekolah`) masih ada dan aman!

---

## 🛠️ METODE PERBAIKAN

### **METODE 1: Perbaikan Cepat (Coba Ini Dulu)** ⚡

1. **Tutup XAMPP Control Panel** jika terbuka
2. **Buka Command Prompt/PowerShell sebagai Administrator**
3. Jalankan:
   ```bash
   cd "c:\Kelas XI\PelatihanAndroid"
   .\FIX_MYSQL.ps1
   ```
4. **Buka XAMPP Control Panel**
5. **Klik Start pada MySQL**

**Jika berhasil:** MySQL akan jalan normal ✅

---

### **METODE 2: Recovery Lengkap** 🔄

Jika Metode 1 gagal, gunakan ini:

1. **Stop semua proses MySQL:**
   ```powershell
   taskkill /F /IM mysqld.exe
   ```

2. **Jalankan script recovery:**
   ```powershell
   cd "c:\Kelas XI\PelatihanAndroid"
   .\RECOVERY_MYSQL_LENGKAP.ps1
   ```

3. **Buka XAMPP dan Start MySQL**

---

### **METODE 3: Manual (Jika Script Gagal)** 🔨

#### Langkah-langkah:

1. **Pastikan MySQL tidak berjalan:**
   - Buka Task Manager (Ctrl+Shift+Esc)
   - Cari `mysqld.exe`, end process jika ada

2. **Backup folder data:**
   ```
   C:\xampp\mysql\data → Copy ke desktop
   ```

3. **Hapus file yang corrupt:**
   Masuk ke `C:\xampp\mysql\data\` dan hapus:
   - `ibdata1`
   - `ib_logfile0`
   - `ib_logfile1`
   - `aria_log.00000001`
   - `aria_log_control`
   - Semua file `.err`

4. **JANGAN HAPUS folder database user!**
   Folder seperti:
   - `sekolah` ← DATABASE ANDA
   - `phpmyadmin`
   - Folder lain yang Anda buat

5. **Copy file MySQL system dari backup:**
   - Dari `C:\xampp\mysql\data_backup_[timestamp]\`
   - Copy folder `mysql` dan `performance_schema`
   - Paste ke `C:\xampp\mysql\data\`

6. **Start MySQL dari XAMPP**

---

## 🔍 PENYEBAB ERROR

Error ini biasanya terjadi karena:
1. ❌ MySQL tidak di-shutdown dengan benar (listrik mati, crash, dll)
2. ❌ File `ibdata1` atau `ib_logfile` corrupt
3. ❌ Disk penuh
4. ❌ Port 3306 bentrok dengan aplikasi lain
5. ❌ Antivirus memblok MySQL

---

## ✅ CEK APAKAH MYSQL SUDAH JALAN

Setelah repair, test dengan:

```powershell
# Test 1: Cek port
netstat -ano | findstr :3306

# Test 2: Cek process
tasklist | findstr mysqld

# Test 3: Test koneksi
cd C:\xampp\mysql\bin
mysql -uroot -e "SHOW DATABASES;"
```

**Jika berhasil, Anda akan melihat:**
```
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| phpmyadmin         |
| sekolah           | ← DATABASE ANDA
| test               |
+--------------------+
```

---

## 🎯 RESTORE DATABASE (Jika Diperlukan)

Jika database hilang setelah repair:

```powershell
# 1. Stop MySQL
net stop mysql

# 2. Copy folder database dari backup
Copy-Item "C:\xampp\mysql\data_backup_20251126_072012\sekolah" "C:\xampp\mysql\data\sekolah" -Recurse -Force

# 3. Start MySQL lagi
net start mysql
```

---

## 🚀 JALANKAN LARAVEL SETELAH MYSQL JALAN

Setelah MySQL berhasil:

```powershell
cd "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"

# Clear cache
php artisan config:clear
php artisan cache:clear

# Test koneksi database
php artisan db:show

# Jalankan server
php artisan serve --host=0.0.0.0 --port=8000
```

---

## 💾 BACKUP DATABASE SECARA MANUAL

**Untuk mencegah kehilangan data di masa depan:**

### Via Command Line:
```bash
cd C:\xampp\mysql\bin
mysqldump -uroot sekolah > "C:\backup\sekolah_backup.sql"
```

### Via phpMyAdmin:
1. Buka http://localhost/phpmyadmin
2. Klik database `sekolah`
3. Klik tab "Export"
4. Klik "Go"
5. Save file `.sql`

**Lakukan backup minimal 1x seminggu!**

---

## 🔐 TIPS MENCEGAH ERROR LAGI

1. **Selalu shutdown MySQL dengan benar:**
   - Klik "Stop" di XAMPP sebelum tutup laptop
   - Jangan langsung cabut kabel listrik

2. **Gunakan UPS** jika sering mati listrik

3. **Backup database rutin:**
   - Otomatis via script
   - Manual via phpMyAdmin

4. **Monitor disk space:**
   - Pastikan drive C: masih ada space

5. **Update XAMPP secara berkala**

---

## 📞 TROUBLESHOOTING

### Error: "Port 3306 is in use"
```powershell
# Cari aplikasi yang pakai port 3306
netstat -ano | findstr :3306
# Kill process
taskkill /PID [PID_NUMBER] /F
```

### Error: "Access denied for user 'root'"
```bash
# Reset password MySQL
cd C:\xampp\mysql\bin
mysqladmin -u root password "newpassword"
```

### Error: "Table doesn't exist"
```sql
-- Repair table
REPAIR TABLE nama_table;
-- Atau
mysqlcheck -uroot --auto-repair --all-databases
```

---

## 📊 STATUS BACKUP ANDA

✅ **Backup 1:** `data_backup_20251126_071753` (Lengkap)
✅ **Backup 2:** `data_backup_20251126_072012` (Lengkap)
✅ **Database sekolah:** AMAN di backup
✅ **Total backup:** 2 copies

**Anda memiliki 2 backup lengkap. Data tidak akan hilang!**

---

## 🎬 LANGKAH BERIKUTNYA

**Pilih salah satu:**

### Opsi A: Perbaikan Otomatis (Rekomendasi)
```powershell
cd "c:\Kelas XI\PelatihanAndroid"
.\FIX_MYSQL.ps1
```

### Opsi B: Recovery Lengkap
```powershell
cd "c:\Kelas XI\PelatihanAndroid"
.\RECOVERY_MYSQL_LENGKAP.ps1
```

### Opsi C: Manual (Ikuti Metode 3 di atas)

---

## ❓ KAPAN PERLU INSTALL ULANG?

**TIDAK PERLU install ulang jika:**
- ✅ Backup masih ada
- ✅ Folder database masih ada di `C:\xampp\mysql\data\`
- ✅ MySQL bisa start setelah hapus file log

**Baru install ulang jika:**
- ❌ Semua metode gagal
- ❌ Backup hilang/corrupt semua
- ❌ XAMPP tidak bisa diakses sama sekali

**Tapi tetap backup folder data dulu sebelum install ulang!**

---

## 📧 BUTUH BANTUAN?

Jika masih error setelah coba semua metode:
1. Screenshot error message dari XAMPP
2. Copy isi file: `C:\xampp\mysql\data\[nama-komputer].err`
3. Kirim untuk analisa

---

**Dibuat:** 26 November 2025
**Status:** ✅ Database AMAN (2 backup)
**Next:** Jalankan script perbaikan
