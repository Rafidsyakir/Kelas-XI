# Panduan Integrasi Firebase App Distribution dengan Gradle

Dokumentasi lengkap untuk mengintegrasikan dan mengotomatisasi proses rilis aplikasi Android (APK/AAB) ke Firebase App Distribution melalui Gradle.

---

## 📋 Daftar Isi
1. [Prasyarat](#prasyarat)
2. [Konfigurasi Dasar Proyek](#konfigurasi-dasar-proyek)
3. [Otentikasi Firebase](#otentikasi-firebase)
4. [Konfigurasi Signing untuk Release Build](#konfigurasi-signing-untuk-release-build)
5. [Konfigurasi Firebase App Distribution](#konfigurasi-firebase-app-distribution)
6. [Menjalankan Distribusi](#menjalankan-distribusi)
7. [Integrasi CI/CD](#integrasi-cicd)
8. [Troubleshooting](#troubleshooting)

---

## Prasyarat

Sebelum memulai, pastikan Anda telah:

1. ✅ Membuat proyek Firebase di [Firebase Console](https://console.firebase.google.com/)
2. ✅ Menghubungkan aplikasi Android ke proyek Firebase
3. ✅ Mengunduh file `google-services.json` dan meletakkannya di folder `app/`
4. ✅ Menginstal Firebase CLI:
   ```bash
   npm install -g firebase-tools
   ```

---

## Konfigurasi Dasar Proyek

### 1. Update `build.gradle.kts` (Project Level)

Tambahkan plugin Firebase App Distribution di file root proyek:

```kotlin
// build.gradle.kts (Project Level)
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    // Tambahkan plugin Firebase App Distribution
    id("com.google.firebase.appdistribution") version "5.0.0" apply false
}
```

### 2. Update `build.gradle.kts` (Module: app)

Terapkan plugin dan konfigurasi Firebase App Distribution:

```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    // Tambahkan plugin App Distribution
    id("com.google.firebase.appdistribution")
}

android {
    namespace = "com.example.firebasetutorial"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.firebasetutorial"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Konfigurasi signing (lihat bagian selanjutnya)
            storeFile = file("../keystore/release-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "your-password"
            keyAlias = System.getenv("KEY_ALIAS") ?: "your-alias"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "your-key-password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Gunakan signing config untuk release
            signingConfig = signingConfigs.getByName("release")
            
            // Konfigurasi Firebase App Distribution
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                artifactType = "APK" // atau "AAB"
                releaseNotesFile = "./release-notes.txt"
                groups = "qa-team, management"
                // atau gunakan testers individual:
                // testers = "tester1@example.com, tester2@example.com"
            }
        }
        
        debug {
            // Konfigurasi opsional untuk debug build
            configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
                artifactType = "APK"
                groups = "internal-testers"
            }
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.measurement.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
}
```

---

## Otentikasi Firebase

### Mendapatkan Firebase Token untuk CI/CD

1. **Login ke Firebase CLI:**
   ```bash
   firebase login:ci
   ```

2. Perintah ini akan membuka browser untuk login. Setelah berhasil, Anda akan mendapatkan token seperti:
   ```
   1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

3. **Simpan token sebagai environment variable:**

   **Windows (PowerShell):**
   ```powershell
   $env:FIREBASE_TOKEN="1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
   ```

   **Windows (Command Prompt):**
   ```cmd
   set FIREBASE_TOKEN=1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

   **Linux/Mac:**
   ```bash
   export FIREBASE_TOKEN="1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
   ```

4. **Atau simpan di gradle.properties (lokal):**
   ```properties
   # gradle.properties (JANGAN commit ke Git!)
   firebaseToken=1//0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

---

## Konfigurasi Signing untuk Release Build

### 1. Buat Keystore

Jalankan perintah berikut untuk membuat keystore baru:

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-release-key
```

**Simpan keystore di folder `keystore/` di root proyek:**
```
Firebase Tutorial/
├── keystore/
│   └── release-keystore.jks
├── app/
├── build.gradle.kts
└── ...
```

### 2. Amankan Kredensial Signing

**Buat file `keystore.properties` di root proyek:**

```properties
# keystore.properties (JANGAN commit ke Git!)
storePassword=your-store-password
keyPassword=your-key-password
keyAlias=my-release-key
storeFile=../keystore/release-keystore.jks
```

**Update `.gitignore`:**
```gitignore
# Keystore files
*.jks
*.keystore
keystore.properties
gradle.properties
```

### 3. Load Signing Config dari Properties

Update `app/build.gradle.kts`:

```kotlin
android {
    // ...
    
    // Load keystore properties
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = java.util.Properties()
    
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String? ?: "../keystore/release-keystore.jks")
            storePassword = keystoreProperties["storePassword"] as String? ?: System.getenv("KEYSTORE_PASSWORD")
            keyAlias = keystoreProperties["keyAlias"] as String? ?: System.getenv("KEY_ALIAS")
            keyPassword = keystoreProperties["keyPassword"] as String? ?: System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

---

## Konfigurasi Firebase App Distribution

### Opsi Konfigurasi Lengkap

```kotlin
// app/build.gradle.kts
buildTypes {
    release {
        // ...
        configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
            // Tipe artifact: "APK" atau "AAB"
            artifactType = "APK"
            
            // Path ke file release notes
            releaseNotesFile = "./release-notes.txt"
            
            // Atau release notes langsung sebagai string
            // releaseNotes = "Bug fixes and performance improvements"
            
            // Grup tester (pisahkan dengan koma)
            groups = "qa-team, management, beta-testers"
            
            // Atau tester individual
            // testers = "user1@example.com, user2@example.com"
            
            // Service account credentials (opsional, untuk CI/CD)
            // serviceCredentialsFile = "./firebase-service-account.json"
            
            // App ID Firebase (opsional, biasanya auto-detect dari google-services.json)
            // appId = "1:1234567890:android:abcdef"
        }
    }
}
```

### Membuat File Release Notes

Buat file `release-notes.txt` di root proyek:

```text
# release-notes.txt

Version 1.0.0 - Release Notes
=============================

✨ New Features:
- Fitur login dengan Google
- Dashboard analytics baru
- Dark mode support

🐛 Bug Fixes:
- Fixed crash on startup
- Improved performance pada loading data

🔧 Improvements:
- Update Firebase SDK ke versi terbaru
- Optimasi ukuran APK
```

---

## Menjalankan Distribusi

### Gradle Tasks yang Tersedia

Setelah konfigurasi selesai, Gradle akan generate tasks berikut:

#### 1. Upload APK/AAB ke Firebase App Distribution

**Release Build:**
```bash
# Windows (PowerShell/CMD)
gradlew appDistributionUploadRelease

# Linux/Mac
./gradlew appDistributionUploadRelease
```

**Debug Build:**
```bash
gradlew appDistributionUploadDebug
```

#### 2. Build dan Upload Sekaligus

Untuk memastikan build terbaru:

```bash
# Clean, build, dan upload
gradlew clean assembleRelease appDistributionUploadRelease
```

#### 3. Upload AAB (Android App Bundle)

Jika menggunakan AAB, ubah `artifactType` menjadi `"AAB"` dan gunakan:

```bash
gradlew bundleRelease appDistributionUploadRelease
```

### Custom Gradle Task untuk Otomatisasi Penuh

Tambahkan custom task di `app/build.gradle.kts`:

```kotlin
// app/build.gradle.kts

// Task untuk distribusi APK otomatis
tasks.register("distributeApk") {
    group = "distribution"
    description = "Build dan upload APK release ke Firebase App Distribution"
    
    dependsOn("clean", "assembleRelease", "appDistributionUploadRelease")
    
    doLast {
        println("✅ APK berhasil didistribusikan ke Firebase App Distribution!")
    }
}

// Task untuk distribusi AAB otomatis
tasks.register("distributeAab") {
    group = "distribution"
    description = "Build dan upload AAB release ke Firebase App Distribution"
    
    dependsOn("clean", "bundleRelease", "appDistributionUploadRelease")
    
    doLast {
        println("✅ AAB berhasil didistribusikan ke Firebase App Distribution!")
    }
}

// Task dengan release notes dinamis
tasks.register("distributeWithNotes") {
    group = "distribution"
    description = "Distribusi dengan release notes yang di-generate otomatis"
    
    doFirst {
        val releaseNotes = """
            Version ${android.defaultConfig.versionName} (Build ${android.defaultConfig.versionCode})
            Released: ${java.time.LocalDateTime.now()}
            
            Build dari commit: ${getGitCommitHash()}
            Branch: ${getGitBranch()}
        """.trimIndent()
        
        file("release-notes.txt").writeText(releaseNotes)
    }
    
    dependsOn("assembleRelease", "appDistributionUploadRelease")
}

// Helper functions untuk Git info
fun getGitCommitHash(): String {
    return try {
        val process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) {
        "unknown"
    }
}

fun getGitBranch(): String {
    return try {
        val process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD")
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) {
        "unknown"
    }
}
```

**Jalankan custom task:**
```bash
# Distribusi APK otomatis
gradlew distributeApk

# Distribusi AAB otomatis
gradlew distributeAab

# Distribusi dengan release notes otomatis
gradlew distributeWithNotes
```

---

## Integrasi CI/CD

### GitHub Actions

Buat file `.github/workflows/firebase-distribution.yml`:

```yaml
name: Firebase App Distribution

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  distribute:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Decode keystore
      run: |
        echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore/release-keystore.jks
    
    - name: Build and Distribute
      env:
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        FIREBASE_TOKEN: ${{ secrets.FIREBASE_TOKEN }}
      run: ./gradlew assembleRelease appDistributionUploadRelease
```

**Setup Secrets di GitHub:**
1. Go to Repository Settings → Secrets and variables → Actions
2. Tambahkan secrets berikut:
   - `KEYSTORE_BASE64`: Base64 encoded keystore file
   - `KEYSTORE_PASSWORD`: Password keystore
   - `KEY_ALIAS`: Alias key
   - `KEY_PASSWORD`: Password key
   - `FIREBASE_TOKEN`: Token dari `firebase login:ci`

**Encode keystore ke Base64:**
```bash
# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore/release-keystore.jks"))

# Linux/Mac
base64 -i keystore/release-keystore.jks
```

### GitLab CI

Buat file `.gitlab-ci.yml`:

```yaml
image: openjdk:11-jdk

variables:
  ANDROID_COMPILE_SDK: "36"
  ANDROID_BUILD_TOOLS: "34.0.0"

before_script:
  - apt-get --quiet update --yes
  - apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1
  - export ANDROID_HOME=$PWD/android-sdk-linux
  - wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
  - unzip -q android-sdk.zip -d $ANDROID_HOME
  - echo y | $ANDROID_HOME/cmdline-tools/bin/sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "platforms;android-${ANDROID_COMPILE_SDK}" "build-tools;${ANDROID_BUILD_TOOLS}"
  - chmod +x ./gradlew

distribute:
  stage: deploy
  script:
    - echo "$KEYSTORE_BASE64" | base64 -d > keystore/release-keystore.jks
    - ./gradlew assembleRelease appDistributionUploadRelease
  only:
    - main
    - develop
```

---

## Troubleshooting

### Error: "App Distribution plugin requires authentication"

**Solusi:**
1. Pastikan Firebase token sudah di-set:
   ```bash
   export FIREBASE_TOKEN="your-token"
   ```
2. Atau gunakan service account credentials:
   ```kotlin
   configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
       serviceCredentialsFile = "./firebase-service-account.json"
   }
   ```

### Error: "No testers or groups specified"

**Solusi:**
Tambahkan `groups` atau `testers` di konfigurasi:
```kotlin
configure<com.google.firebase.appdistribution.gradle.AppDistributionExtension> {
    groups = "qa-team"
}
```

### Error: "Signing config is missing"

**Solusi:**
Pastikan signing config sudah dikonfigurasi dengan benar di `buildTypes`:
```kotlin
buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### Build berhasil tapi upload gagal

**Solusi:**
1. Cek koneksi internet
2. Verifikasi Firebase token masih valid
3. Pastikan App Distribution enabled di Firebase Console
4. Cek log detail dengan flag `--info`:
   ```bash
   gradlew appDistributionUploadRelease --info
   ```

---

## 📚 Referensi

- [Firebase App Distribution Documentation](https://firebase.google.com/docs/app-distribution)
- [Firebase App Distribution Gradle Plugin](https://firebase.google.com/docs/app-distribution/android/distribute-gradle)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)

---

## ✅ Checklist Implementasi

- [ ] Install Firebase CLI
- [ ] Tambahkan plugin Firebase App Distribution di `build.gradle.kts`
- [ ] Konfigurasi signing untuk release build
- [ ] Setup Firebase token atau service account
- [ ] Konfigurasi tester groups di Firebase Console
- [ ] Buat file `release-notes.txt`
- [ ] Test upload manual dengan `gradlew appDistributionUploadRelease`
- [ ] Setup CI/CD pipeline (opsional)
- [ ] Dokumentasi workflow untuk tim

---

**Selamat! Anda telah berhasil mengintegrasikan Firebase App Distribution dengan Gradle! 🎉**
