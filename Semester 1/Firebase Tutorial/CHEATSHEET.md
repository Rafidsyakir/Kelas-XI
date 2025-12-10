# 🚀 Firebase App Distribution - Cheat Sheet

Quick reference untuk command dan konfigurasi Firebase App Distribution.

## ⚡ Quick Commands

```powershell
# Setup awal
.\setup-distribution.ps1              # Setup otomatis (Windows)
./setup-distribution.sh               # Setup otomatis (Linux/Mac)

# Distribusi
.\gradlew distributeApk               # Distribusi APK (recommended)
.\gradlew distributeAab               # Distribusi AAB
.\gradlew distributeWithAutoNotes     # Dengan auto release notes
.\gradlew quickDistribute             # Distribusi cepat (no clean)

# Build saja (tanpa upload)
.\gradlew assembleRelease             # Build APK
.\gradlew bundleRelease               # Build AAB

# Utilities
.\gradlew checkDistributionConfig     # Check konfigurasi
.\gradlew clean                       # Clean build
.\gradlew --refresh-dependencies      # Refresh dependencies
```

## 🔧 Setup Firebase Token

```powershell
# 1. Login dan dapatkan token
firebase login:ci

# 2. Set environment variable
# Windows (PowerShell)
$env:FIREBASE_TOKEN="your-token"

# Linux/Mac
export FIREBASE_TOKEN="your-token"

# 3. Permanent (tambahkan ke profile)
# PowerShell: $PROFILE
# Bash: ~/.bashrc
# Zsh: ~/.zshrc
```

## 🔐 Setup Keystore

```powershell
# 1. Buat keystore
keytool -genkey -v -keystore keystore/release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-release-key

# 2. Buat keystore.properties
storePassword=your-password
keyPassword=your-password
keyAlias=my-release-key
storeFile=../keystore/release-keystore.jks

# 3. Encode keystore untuk CI/CD (Base64)
# Windows
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore/release-keystore.jks"))

# Linux/Mac
base64 -i keystore/release-keystore.jks
```

## 📝 Konfigurasi build.gradle.kts

### Project Level
```kotlin
plugins {
    id("com.google.firebase.appdistribution") version "5.0.0" apply false
}
```

### App Level
```kotlin
plugins {
    id("com.google.firebase.appdistribution")
}

android {
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                artifactType = "APK"  // atau "AAB"
                releaseNotesFile = "./release-notes.txt"
                groups = "qa-team, management"
                // atau: testers = "user1@example.com, user2@example.com"
            }
        }
    }
}
```

## 🤖 CI/CD Environment Variables

### GitHub Actions / GitLab CI
```yaml
FIREBASE_TOKEN         # Token dari firebase login:ci
KEYSTORE_BASE64        # Base64 encoded keystore
KEYSTORE_PASSWORD      # Password keystore
KEY_ALIAS              # Alias key
KEY_PASSWORD           # Password key
```

### Set di GitHub
```
Repository → Settings → Secrets and variables → Actions → New secret
```

### Set di GitLab
```
Project → Settings → CI/CD → Variables → Add variable
```

## 📋 File Structure Checklist

```
✅ keystore/release-keystore.jks       # Keystore file
✅ keystore.properties                 # Kredensial keystore
✅ release-notes.txt                   # Release notes
✅ app/google-services.json            # Firebase config
✅ build.gradle.kts                    # Project config
✅ app/build.gradle.kts                # App config
✅ .gitignore                          # Security
```

## 🚫 .gitignore Essentials

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

## 📊 Gradle Tasks Generated

| Task | Variant | Description |
|------|---------|-------------|
| `appDistributionUploadRelease` | Release | Upload release build |
| `appDistributionUploadDebug` | Debug | Upload debug build |
| `distributeApk` | Custom | Clean + build + upload APK |
| `distributeAab` | Custom | Clean + build + upload AAB |
| `distributeWithAutoNotes` | Custom | Auto-generate notes + upload |
| `quickDistribute` | Custom | Upload tanpa clean |
| `checkDistributionConfig` | Custom | Validasi konfigurasi |

## 🔍 Debug & Troubleshooting

```powershell
# Check konfigurasi
.\gradlew checkDistributionConfig

# Verbose output
.\gradlew distributeApk --info

# Stack trace untuk error
.\gradlew distributeApk --stacktrace

# Debug mode
.\gradlew distributeApk --debug

# Refresh dependencies
.\gradlew --refresh-dependencies

# Clean build
.\gradlew clean build
```

## 📱 Firebase Console URLs

```
Main Console:     https://console.firebase.google.com/
App Distribution: https://console.firebase.google.com/u/0/project/YOUR_PROJECT/appdistribution
Testers & Groups: https://console.firebase.google.com/u/0/project/YOUR_PROJECT/appdistribution/testers
Settings:         https://console.firebase.google.com/u/0/project/YOUR_PROJECT/settings
```

## ⚙️ Common Configurations

### Multiple Tester Groups
```kotlin
groups = "qa-team, management, beta-testers"
```

### Individual Testers
```kotlin
testers = "user1@example.com, user2@example.com, user3@example.com"
```

### Custom Release Notes (Inline)
```kotlin
releaseNotes = """
    Version 1.0.0
    - Feature A
    - Bug fix B
""".trimIndent()
```

### Service Account (CI/CD)
```kotlin
serviceCredentialsFile = "./firebase-service-account.json"
```

### Custom App ID
```kotlin
appId = "1:1234567890:android:abcdef"
```

## 🎯 Workflow Shortcuts

### Quick Release Flow
```powershell
# Edit code → Commit → Build & Distribute
git add .
git commit -m "Release v1.0.1"
.\gradlew distributeApk
```

### Update Release Notes & Distribute
```powershell
# Edit release-notes.txt
notepad release-notes.txt
.\gradlew distributeApk
```

### Check Then Distribute
```powershell
.\gradlew checkDistributionConfig
.\gradlew distributeApk
```

## 📚 Quick Links

| Resource | URL |
|----------|-----|
| Firebase Docs | https://firebase.google.com/docs/app-distribution |
| Gradle Plugin | https://firebase.google.com/docs/app-distribution/android/distribute-gradle |
| Android Signing | https://developer.android.com/studio/publish/app-signing |
| Firebase CLI | https://firebase.google.com/docs/cli |

## 🆘 Emergency Fixes

### Token expired
```powershell
firebase login:ci
$env:FIREBASE_TOKEN="new-token"
```

### Build gagal
```powershell
.\gradlew clean
.\gradlew --refresh-dependencies
.\gradlew distributeApk
```

### Upload gagal
```powershell
# Check internet
Test-Connection google.com

# Check token
echo $env:FIREBASE_TOKEN

# Try manual upload
.\gradlew assembleRelease
.\gradlew appDistributionUploadRelease --info
```

---

**Simpan file ini untuk referensi cepat! 📌**

Untuk panduan lengkap: [FIREBASE_APP_DISTRIBUTION_GUIDE.md](./FIREBASE_APP_DISTRIBUTION_GUIDE.md)
