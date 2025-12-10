# Setup Script untuk Firebase App Distribution
# Script ini akan membantu Anda setup proyek untuk Firebase App Distribution

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "🚀 Firebase App Distribution Setup Script" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Function untuk menampilkan pesan sukses
function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

# Function untuk menampilkan pesan error
function Write-Error-Custom {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor Red
}

# Function untuk menampilkan pesan warning
function Write-Warning-Custom {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor Yellow
}

# Function untuk menampilkan pesan info
function Write-Info {
    param([string]$Message)
    Write-Host "ℹ️  $Message" -ForegroundColor Blue
}

# 1. Check Firebase CLI
Write-Host "1️⃣ Checking Firebase CLI..." -ForegroundColor Yellow
try {
    $firebaseVersion = firebase --version 2>$null
    Write-Success "Firebase CLI sudah terinstall: $firebaseVersion"
} catch {
    Write-Error-Custom "Firebase CLI belum terinstall"
    Write-Info "Install dengan: npm install -g firebase-tools"
    $install = Read-Host "Install Firebase CLI sekarang? (y/n)"
    if ($install -eq "y") {
        npm install -g firebase-tools
        Write-Success "Firebase CLI berhasil diinstall"
    } else {
        Write-Warning-Custom "Firebase CLI diperlukan untuk distribusi"
    }
}

Write-Host ""

# 2. Check Keystore
Write-Host "2️⃣ Checking Keystore..." -ForegroundColor Yellow
if (Test-Path "keystore\release-keystore.jks") {
    Write-Success "Keystore sudah ada: keystore\release-keystore.jks"
} else {
    Write-Warning-Custom "Keystore belum dibuat"
    $createKeystore = Read-Host "Buat keystore baru? (y/n)"
    if ($createKeystore -eq "y") {
        New-Item -ItemType Directory -Force -Path "keystore" | Out-Null
        
        Write-Info "Masukkan informasi untuk keystore:"
        $alias = Read-Host "Key alias (default: my-release-key)"
        if ([string]::IsNullOrWhiteSpace($alias)) { $alias = "my-release-key" }
        
        keytool -genkey -v -keystore keystore\release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias $alias
        
        if (Test-Path "keystore\release-keystore.jks") {
            Write-Success "Keystore berhasil dibuat"
        } else {
            Write-Error-Custom "Gagal membuat keystore"
        }
    }
}

Write-Host ""

# 3. Setup keystore.properties
Write-Host "3️⃣ Checking keystore.properties..." -ForegroundColor Yellow
if (Test-Path "keystore.properties") {
    Write-Success "keystore.properties sudah ada"
} else {
    Write-Warning-Custom "keystore.properties belum ada"
    $createProperties = Read-Host "Buat keystore.properties? (y/n)"
    if ($createProperties -eq "y") {
        Write-Info "Masukkan kredensial keystore:"
        $storePassword = Read-Host "Store password" -AsSecureString
        $keyPassword = Read-Host "Key password" -AsSecureString
        $keyAlias = Read-Host "Key alias (default: my-release-key)"
        if ([string]::IsNullOrWhiteSpace($keyAlias)) { $keyAlias = "my-release-key" }
        
        # Convert SecureString to plain text
        $storePasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePassword))
        $keyPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($keyPassword))
        
        $propertiesContent = @"
storePassword=$storePasswordPlain
keyPassword=$keyPasswordPlain
keyAlias=$keyAlias
storeFile=../keystore/release-keystore.jks
"@
        $propertiesContent | Out-File -FilePath "keystore.properties" -Encoding UTF8
        Write-Success "keystore.properties berhasil dibuat"
        Write-Warning-Custom "JANGAN commit file ini ke Git!"
    }
}

Write-Host ""

# 4. Setup Firebase Token
Write-Host "4️⃣ Checking Firebase Token..." -ForegroundColor Yellow
if ($env:FIREBASE_TOKEN) {
    Write-Success "Firebase token sudah di-set"
    Write-Info "Token: $($env:FIREBASE_TOKEN.Substring(0, 20))..."
} else {
    Write-Warning-Custom "Firebase token belum di-set"
    $setupToken = Read-Host "Setup Firebase token sekarang? (y/n)"
    if ($setupToken -eq "y") {
        Write-Info "Menjalankan firebase login:ci..."
        Write-Info "Browser akan terbuka, silakan login dengan akun Google Anda"
        $token = firebase login:ci
        if ($token) {
            Write-Info "Copy token yang muncul dan paste di sini:"
            $firebaseToken = Read-Host "Firebase token"
            $env:FIREBASE_TOKEN = $firebaseToken
            Write-Success "Firebase token berhasil di-set untuk session ini"
            Write-Warning-Custom "Untuk permanent, tambahkan ke system environment variables"
        }
    }
}

Write-Host ""

# 5. Check google-services.json
Write-Host "5️⃣ Checking google-services.json..." -ForegroundColor Yellow
if (Test-Path "app\google-services.json") {
    Write-Success "google-services.json sudah ada"
} else {
    Write-Error-Custom "google-services.json TIDAK ditemukan"
    Write-Info "Download file ini dari Firebase Console:"
    Write-Info "1. Buka https://console.firebase.google.com/"
    Write-Info "2. Pilih proyek Anda"
    Write-Info "3. Go to Project Settings"
    Write-Info "4. Download google-services.json"
    Write-Info "5. Letakkan di folder app\"
}

Write-Host ""

# 6. Update build.gradle.kts files
Write-Host "6️⃣ Checking build.gradle.kts configuration..." -ForegroundColor Yellow
if (Test-Path "build.gradle.kts.distribution") {
    $updateGradle = Read-Host "Update build.gradle.kts dengan konfigurasi distribution? (y/n)"
    if ($updateGradle -eq "y") {
        # Backup original files
        if (Test-Path "build.gradle.kts") {
            Copy-Item "build.gradle.kts" "build.gradle.kts.backup" -Force
            Write-Success "Backup build.gradle.kts dibuat"
        }
        if (Test-Path "app\build.gradle.kts") {
            Copy-Item "app\build.gradle.kts" "app\build.gradle.kts.backup" -Force
            Write-Success "Backup app\build.gradle.kts dibuat"
        }
        
        # Copy new files
        Copy-Item "build.gradle.kts.distribution" "build.gradle.kts" -Force
        Copy-Item "app\build.gradle.kts.distribution" "app\build.gradle.kts" -Force
        Write-Success "build.gradle.kts files berhasil diupdate"
    }
}

Write-Host ""

# 7. Setup release-notes.txt
Write-Host "7️⃣ Checking release-notes.txt..." -ForegroundColor Yellow
if (Test-Path "release-notes.txt") {
    Write-Success "release-notes.txt sudah ada"
} else {
    Write-Warning-Custom "release-notes.txt belum ada (akan dibuat dengan default content)"
    $defaultNotes = @"
Version 1.0.0 - Initial Release
================================

✨ Fitur Baru:
- Implementasi Firebase Crashlytics
- Integrasi Firebase Analytics
- Setup Firebase App Distribution

🔧 Improvement:
- Optimasi performa aplikasi

📝 Catatan:
- Build pertama untuk testing team

Tanggal Rilis: $(Get-Date -Format "dd MMMM yyyy")
"@
    $defaultNotes | Out-File -FilePath "release-notes.txt" -Encoding UTF8
    Write-Success "release-notes.txt berhasil dibuat"
}

Write-Host ""

# 8. Check .gitignore
Write-Host "8️⃣ Checking .gitignore..." -ForegroundColor Yellow
if (Test-Path ".gitignore") {
    $gitignoreContent = Get-Content ".gitignore" -Raw
    $needsUpdate = $false
    
    if (-not $gitignoreContent.Contains("*.jks")) {
        $needsUpdate = $true
    }
    
    if ($needsUpdate) {
        Write-Warning-Custom ".gitignore perlu diupdate"
        $updateGitignore = Read-Host "Update .gitignore untuk keamanan? (y/n)"
        if ($updateGitignore -eq "y") {
            $gitignoreAdditions = @"

# Keystore files
*.jks
*.keystore
keystore.properties

# Environment
.env

# Firebase
firebase-service-account.json

# Backup files
*.backup
"@
            Add-Content -Path ".gitignore" -Value $gitignoreAdditions
            Write-Success ".gitignore berhasil diupdate"
        }
    } else {
        Write-Success ".gitignore sudah dikonfigurasi dengan baik"
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "✅ Setup Selesai!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Summary
Write-Host "📋 Summary:" -ForegroundColor Cyan
Write-Host ""
Write-Host "Untuk distribusi aplikasi, gunakan salah satu command berikut:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  .\gradlew distributeApk           - Distribusi APK (recommended)" -ForegroundColor White
Write-Host "  .\gradlew distributeAab           - Distribusi AAB" -ForegroundColor White
Write-Host "  .\gradlew distributeWithAutoNotes - Distribusi dengan auto-generated notes" -ForegroundColor White
Write-Host "  .\gradlew checkDistributionConfig - Check konfigurasi" -ForegroundColor White
Write-Host ""

Write-Host "📚 Dokumentasi:" -ForegroundColor Cyan
Write-Host "  - Quick Start: QUICKSTART.md" -ForegroundColor White
Write-Host "  - Full Guide: FIREBASE_APP_DISTRIBUTION_GUIDE.md" -ForegroundColor White
Write-Host ""

Write-Host "Tekan Enter untuk keluar..." -ForegroundColor Gray
Read-Host
