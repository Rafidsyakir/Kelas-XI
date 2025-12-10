# 🚀 Quick Start - Firebase App Distribution

Panduan cepat untuk memulai distribusi aplikasi ke Firebase App Distribution.

## 📦 Instalasi & Setup

### 1. Install Firebase CLI
```powershell
npm install -g firebase-tools
```

### 2. Login ke Firebase dan Dapatkan Token
```powershell
firebase login:ci
```

Simpan token yang didapat ke environment variable:
```powershell
$env:FIREBASE_TOKEN="1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

### 3. Buat Keystore
```powershell
keytool -genkey -v -keystore keystore\release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-release-key
```

### 4. Setup Keystore Properties
Copy file `keystore.properties.example` menjadi `keystore.properties` dan isi dengan kredensial Anda:
```properties
storePassword=your-store-password
keyPassword=your-key-password
keyAlias=my-release-key
storeFile=../keystore/release-keystore.jks
```

### 5. Update File Build Gradle
Ganti file `build.gradle.kts` dengan `build.gradle.kts.distribution`:
```powershell
# Backup file lama
Copy-Item build.gradle.kts build.gradle.kts.backup

# Gunakan konfigurasi baru
Copy-Item build.gradle.kts.distribution build.gradle.kts
Copy-Item app\build.gradle.kts app\build.gradle.kts.backup
Copy-Item app\build.gradle.kts.distribution app\build.gradle.kts
```

### 6. Setup Tester Groups di Firebase Console
1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Pilih proyek Anda
3. Go to **App Distribution** → **Testers & Groups**
4. Buat grup tester (contoh: `qa-team`, `management`, `internal-testers`)
5. Tambahkan email tester ke grup

## 🎯 Cara Menggunakan

### Distribusi APK (Cara Paling Mudah)
```powershell
.\gradlew distributeApk
```

### Distribusi AAB
```powershell
.\gradlew distributeAab
```

### Distribusi dengan Release Notes Otomatis
```powershell
.\gradlew distributeWithAutoNotes
```

### Distribusi Cepat (Tanpa Clean)
```powershell
.\gradlew quickDistribute
```

### Check Konfigurasi
```powershell
.\gradlew checkDistributionConfig
```

## 📋 Gradle Tasks yang Tersedia

| Task | Deskripsi |
|------|-----------|
| `distributeApk` | Clean, build APK, dan upload ke Firebase |
| `distributeAab` | Clean, build AAB, dan upload ke Firebase |
| `distributeWithAutoNotes` | Distribusi dengan release notes auto-generated dari Git |
| `quickDistribute` | Distribusi cepat tanpa clean build |
| `checkDistributionConfig` | Validasi konfigurasi Firebase App Distribution |
| `assembleRelease` | Build APK release saja |
| `bundleRelease` | Build AAB release saja |
| `appDistributionUploadRelease` | Upload build yang sudah ada ke Firebase |

## 🔧 Environment Variables

| Variable | Deskripsi | Required |
|----------|-----------|----------|
| `FIREBASE_TOKEN` | Token dari `firebase login:ci` | Yes |
| `KEYSTORE_PASSWORD` | Password keystore | Yes (jika tidak ada keystore.properties) |
| `KEY_ALIAS` | Alias key | Yes (jika tidak ada keystore.properties) |
| `KEY_PASSWORD` | Password key | Yes (jika tidak ada keystore.properties) |

## 📱 Workflow Lengkap

1. **Persiapan**
   - Pastikan semua environment variable sudah di-set
   - Pastikan release notes sudah diupdate (atau akan auto-generated)

2. **Build & Distribusi**
   ```powershell
   .\gradlew distributeApk
   ```

3. **Monitoring**
   - Cek Firebase Console untuk melihat status distribusi
   - Tester akan menerima email notifikasi

4. **Testing**
   - Tester download dan install aplikasi dari email/Firebase App
   - Tester memberikan feedback

## 🔐 Security Best Practices

1. **JANGAN commit file berikut ke Git:**
   - `keystore/*.jks`
   - `keystore.properties`
   - `.env`
   - `firebase-service-account.json`

2. **Gunakan .gitignore:**
   ```gitignore
   # Keystore files
   *.jks
   *.keystore
   keystore.properties
   
   # Environment
   .env
   
   # Firebase
   firebase-service-account.json
   ```

3. **Untuk CI/CD:**
   - Gunakan secrets/environment variables
   - Jangan hardcode credentials di script

## 📚 Resources

- [Dokumentasi Lengkap](./FIREBASE_APP_DISTRIBUTION_GUIDE.md)
- [Firebase App Distribution Docs](https://firebase.google.com/docs/app-distribution)
- [Gradle Plugin Docs](https://firebase.google.com/docs/app-distribution/android/distribute-gradle)

## ❓ Troubleshooting

### Error: "No testers or groups specified"
```kotlin
// Tambahkan di build.gradle.kts
configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
    groups = "qa-team"
}
```

### Error: "Signing config is missing"
Pastikan keystore.properties sudah dibuat dan diisi dengan benar.

### Build berhasil tapi upload gagal
1. Cek FIREBASE_TOKEN: `echo $env:FIREBASE_TOKEN`
2. Verifikasi Firebase token masih valid: `firebase login:ci`
3. Pastikan App Distribution enabled di Firebase Console

---

**Need help?** Baca [dokumentasi lengkap](./FIREBASE_APP_DISTRIBUTION_GUIDE.md) untuk panduan detail.
