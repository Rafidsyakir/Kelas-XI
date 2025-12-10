# 🎨 Visual Guide - Firebase App Distribution Flow

Diagram visual untuk memahami alur kerja Firebase App Distribution.

---

## 📊 Overall Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     FIREBASE APP DISTRIBUTION                    │
│                         Complete System                          │
└─────────────────────────────────────────────────────────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
                    ▼             ▼             ▼
        ┌──────────────┐  ┌─────────────┐  ┌──────────────┐
        │              │  │             │  │              │
        │  DEVELOPER   │  │   CI/CD     │  │   TESTER     │
        │  WORKFLOW    │  │  PIPELINE   │  │  WORKFLOW    │
        │              │  │             │  │              │
        └──────────────┘  └─────────────┘  └──────────────┘
```

---

## 🔄 Developer Workflow

```
┌─────────────┐
│ 1. Code     │
│    Changes  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 2. Update   │
│    Release  │
│    Notes    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────────┐
│ 3. Build & Sign                         │
│                                          │
│  .\gradlew distributeApk                │
│      │                                   │
│      ├─ Clean project                   │
│      ├─ Build APK with signing          │
│      └─ Prepare artifacts                │
└──────┬──────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────┐
│ 4. Upload to Firebase                   │
│                                          │
│  appDistributionUploadRelease           │
│      │                                   │
│      ├─ Authenticate with token         │
│      ├─ Upload APK/AAB                  │
│      └─ Attach release notes            │
└──────┬──────────────────────────────────┘
       │
       ▼
┌─────────────┐
│ 5. Firebase │
│    Notifies │
│    Testers  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 6. Monitor  │
│    Console  │
└─────────────┘
```

---

## 🤖 CI/CD Pipeline Flow

```
┌──────────────────────────────────────────────────────────────┐
│  CONTINUOUS INTEGRATION & DEPLOYMENT                          │
└──────────────────────────────────────────────────────────────┘

    Developer Push              Automated Process
         │
         ▼
    ┌─────────┐
    │  Git    │
    │  Push   │
    └────┬────┘
         │
         ▼
    ┌─────────────────────┐
    │  CI/CD Triggered    │
    │  (GitHub/GitLab)    │
    └─────┬───────────────┘
          │
          ├─────────────────────┐
          │                     │
          ▼                     ▼
    ┌──────────┐          ┌──────────┐
    │ Checkout │          │  Setup   │
    │   Code   │          │   JDK    │
    └────┬─────┘          └────┬─────┘
         │                     │
         └──────────┬──────────┘
                    ▼
         ┌─────────────────────┐
         │  Decode Keystore    │
         │  from Base64        │
         └──────────┬──────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │  Auto Generate      │
         │  Release Notes      │
         │  (from Git info)    │
         └──────────┬──────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │  Build APK/AAB      │
         │  with Signing       │
         └──────────┬──────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │  Upload to          │
         │  Firebase           │
         └──────────┬──────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │  Save as            │
         │  Artifact           │
         └─────────────────────┘
```

---

## 🎯 Gradle Task Execution Flow

```
Command: .\gradlew distributeApk

┌─────────────────────────────────────────┐
│  distributeApk (Custom Task)            │
└────────────┬────────────────────────────┘
             │
             ├──► Depends on: clean
             │
             ├──► Depends on: assembleRelease
             │         │
             │         ├─ preBuild
             │         ├─ compileReleaseJava
             │         ├─ processReleaseResources
             │         ├─ packageRelease
             │         └─ Sign with keystore
             │
             └──► Depends on: appDistributionUploadRelease
                       │
                       ├─ Read google-services.json
                       ├─ Authenticate with Firebase
                       ├─ Read release-notes.txt
                       ├─ Upload APK to Firebase
                       ├─ Configure tester groups
                       └─ Send notifications
```

---

## 🔐 Authentication & Security Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY LAYERS                           │
└─────────────────────────────────────────────────────────────┘

Layer 1: Local Development
┌────────────────────────┐
│ keystore.properties    │  ◄─── NOT in Git
│  - storePassword       │
│  - keyPassword         │
│  - keyAlias            │
└────────────────────────┘

Layer 2: Firebase Authentication
┌────────────────────────┐
│ Firebase Token         │  ◄─── Environment Variable
│  (from firebase        │       $env:FIREBASE_TOKEN
│   login:ci)            │
└────────────────────────┘

Layer 3: CI/CD Secrets
┌────────────────────────┐
│ GitHub/GitLab Secrets  │  ◄─── Encrypted in CI/CD
│  - KEYSTORE_BASE64     │
│  - FIREBASE_TOKEN      │
│  - Credentials         │
└────────────────────────┘

Layer 4: .gitignore Protection
┌────────────────────────┐
│ Ignored Files:         │  ◄─── Never committed
│  - *.jks               │
│  - keystore.properties │
│  - .env                │
└────────────────────────┘
```

---

## 📱 Tester Experience Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    TESTER WORKFLOW                           │
└─────────────────────────────────────────────────────────────┘

Step 1: Invitation
┌────────────────┐
│  Tester added  │
│  to Firebase   │
│  Console       │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Email invite  │
│  received      │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Accept invite │
│  & setup app   │
└────────────────┘

Step 2: Distribution
┌────────────────┐
│  New build     │
│  distributed   │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Email/Push    │
│  notification  │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  View release  │
│  notes         │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Download      │
│  APK/AAB       │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Install &     │
│  Test          │
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Provide       │
│  Feedback      │
└────────────────┘
```

---

## 🏗️ Build Configuration Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                     PROJECT STRUCTURE                        │
└─────────────────────────────────────────────────────────────┘

Root Project
│
├── build.gradle.kts (Project Level)
│   └── plugins {
│       ├── android.application
│       ├── google-services
│       ├── firebase.crashlytics
│       └── firebase.appdistribution ◄─── Add this!
│       }
│
├── app/
│   │
│   ├── build.gradle.kts (App Level)
│   │   ├── plugins { ... }
│   │   │
│   │   ├── android {
│   │   │   ├── signingConfigs {
│   │   │   │   └── release {
│   │   │   │       ├── storeFile
│   │   │   │       ├── storePassword
│   │   │   │       ├── keyAlias
│   │   │   │       └── keyPassword
│   │   │   │       }
│   │   │   │   }
│   │   │   │
│   │   │   └── buildTypes {
│   │   │       └── release {
│   │   │           ├── signingConfig
│   │   │           │
│   │   │           └── configure<AppDistributionExtension> {
│   │   │               ├── artifactType = "APK"
│   │   │               ├── releaseNotesFile
│   │   │               └── groups = "qa-team"
│   │   │               }
│   │   │           }
│   │   │       }
│   │   │   }
│   │   │
│   │   └── tasks {
│   │       ├── distributeApk
│   │       ├── distributeAab
│   │       ├── distributeWithAutoNotes
│   │       └── checkDistributionConfig
│   │       }
│   │
│   └── google-services.json ◄─── From Firebase Console
│
├── keystore/
│   └── release-keystore.jks ◄─── Your signing key
│
├── keystore.properties ◄─── Credentials (NOT in Git)
│
└── release-notes.txt ◄─── Release information
```

---

## 🔄 File Dependencies Map

```
┌─────────────────────────────────────────────────────────────┐
│                   FILE DEPENDENCIES                          │
└─────────────────────────────────────────────────────────────┘

build.gradle.kts (Project)
    │
    └──► Declares plugins for all modules
         ├─ com.google.firebase.appdistribution
         └─ Must be applied in app module

app/build.gradle.kts
    │
    ├──► Reads: google-services.json
    │    └─ For Firebase configuration
    │
    ├──► Reads: keystore.properties
    │    └─ For signing configuration
    │
    ├──► Reads: release-notes.txt
    │    └─ For distribution notes
    │
    └──► Uses: Environment Variables
         ├─ FIREBASE_TOKEN
         ├─ KEYSTORE_PASSWORD (optional)
         └─ KEY_PASSWORD (optional)

Custom Gradle Tasks
    │
    ├──► distributeApk
    │    ├─ Depends: clean
    │    ├─ Depends: assembleRelease
    │    └─ Depends: appDistributionUploadRelease
    │
    ├──► distributeAab
    │    ├─ Depends: clean
    │    ├─ Depends: bundleRelease
    │    └─ Depends: appDistributionUploadRelease
    │
    └──► distributeWithAutoNotes
         ├─ Generate: release-notes.txt (dynamic)
         ├─ Depends: assembleRelease
         └─ Depends: appDistributionUploadRelease
```

---

## 📊 Setup Process Flowchart

```
┌─────────────────────────────────────────────────────────────┐
│                    SETUP FLOWCHART                           │
└─────────────────────────────────────────────────────────────┘

START
  │
  ▼
┌──────────────────┐      YES    ┌──────────────────┐
│ Firebase CLI     ├────────────► │ Skip Firebase    │
│ installed?       │              │ CLI install      │
└────────┬─────────┘              └────────┬─────────┘
         │ NO                              │
         ▼                                 │
┌──────────────────┐                       │
│ Install          │                       │
│ firebase-tools   │                       │
└────────┬─────────┘                       │
         │                                 │
         └────────────┬────────────────────┘
                      ▼
         ┌──────────────────┐      YES    ┌──────────────────┐
         │ Keystore exists? ├────────────► │ Skip keystore    │
         └────────┬─────────┘              │ creation         │
                  │ NO                     └────────┬─────────┘
                  ▼                                 │
         ┌──────────────────┐                      │
         │ Create keystore  │                      │
         │ with keytool     │                      │
         └────────┬─────────┘                      │
                  │                                │
                  └───────────┬────────────────────┘
                              ▼
                  ┌──────────────────┐      YES    ┌──────────────────┐
                  │ keystore.        ├────────────► │ Skip properties  │
                  │ properties       │              │ creation         │
                  │ exists?          │              └────────┬─────────┘
                  └────────┬─────────┘                       │
                           │ NO                              │
                           ▼                                 │
                  ┌──────────────────┐                       │
                  │ Create from      │                       │
                  │ template         │                       │
                  └────────┬─────────┘                       │
                           │                                 │
                           └──────────┬──────────────────────┘
                                      ▼
                           ┌──────────────────┐      YES    ┌──────────────────┐
                           │ Firebase token   ├────────────► │ Skip token       │
                           │ set?             │              │ setup            │
                           └────────┬─────────┘              └────────┬─────────┘
                                    │ NO                              │
                                    ▼                                 │
                           ┌──────────────────┐                       │
                           │ Run firebase     │                       │
                           │ login:ci         │                       │
                           └────────┬─────────┘                       │
                                    │                                 │
                                    └──────────┬──────────────────────┘
                                               ▼
                                    ┌──────────────────┐
                                    │ Update           │
                                    │ build.gradle.kts │
                                    └────────┬─────────┘
                                             ▼
                                    ┌──────────────────┐
                                    │ Setup complete!  │
                                    │ Ready to         │
                                    │ distribute       │
                                    └────────┬─────────┘
                                             ▼
                                           END
```

---

## 🎮 Command Execution Tree

```
.\gradlew [command]
│
├── distributeApk ◄─── Recommended
│   ├── clean
│   ├── assembleRelease
│   │   ├── preBuild
│   │   ├── compile
│   │   ├── package
│   │   └── sign
│   └── appDistributionUploadRelease
│       ├── authenticate
│       ├── upload
│       └── notify
│
├── distributeAab
│   ├── clean
│   ├── bundleRelease
│   │   ├── preBuild
│   │   ├── compile
│   │   ├── bundle
│   │   └── sign
│   └── appDistributionUploadRelease
│
├── distributeWithAutoNotes
│   ├── generate release-notes.txt
│   │   ├── get git info
│   │   ├── format notes
│   │   └── write file
│   ├── assembleRelease
│   └── appDistributionUploadRelease
│
├── quickDistribute
│   ├── assembleRelease (no clean)
│   └── appDistributionUploadRelease
│
└── checkDistributionConfig ◄─── Validation
    ├── check keystore
    ├── check release-notes
    ├── check google-services.json
    └── check firebase token
```

---

## 🌐 Multi-Environment Setup

```
┌─────────────────────────────────────────────────────────────┐
│              MULTI-ENVIRONMENT CONFIGURATION                 │
└─────────────────────────────────────────────────────────────┘

Development
├── Build Variant: debug
├── Tester Group: internal-testers
├── Auto-sign: debug keystore
└── Firebase: auto-distribute on commit

Staging
├── Build Variant: staging
├── Tester Group: qa-team
├── Auto-sign: release keystore
└── Firebase: manual or on tag

Production
├── Build Variant: release
├── Tester Group: management, beta-testers
├── Auto-sign: release keystore
└── Firebase: manual distribution

Configuration in build.gradle.kts:
buildTypes {
    debug   { groups = "internal-testers" }
    staging { groups = "qa-team" }
    release { groups = "management, beta-testers" }
}
```

---

## 📈 Monitoring & Feedback Loop

```
┌─────────────────────────────────────────────────────────────┐
│                  MONITORING & FEEDBACK                       │
└─────────────────────────────────────────────────────────────┘

    Developer                Firebase Console              Tester
        │                           │                        │
        │  1. Distribute            │                        │
        ├──────────────────────────►│                        │
        │                           │                        │
        │                           │  2. Notify             │
        │                           ├───────────────────────►│
        │                           │                        │
        │                           │  3. Download           │
        │                           │◄───────────────────────┤
        │                           │                        │
        │  4. Monitor Stats         │                        │
        │◄──────────────────────────┤                        │
        │  - Downloads              │                        │
        │  - Installs               │                        │
        │  - Crashes                │                        │
        │                           │                        │
        │                           │  5. Feedback           │
        │◄──────────────────────────┼────────────────────────┤
        │                           │                        │
        │  6. Iterate & Improve     │                        │
        │                           │                        │
        ▼                           ▼                        ▼
```

---

## 🎯 Success Metrics Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│              FIREBASE CONSOLE METRICS                        │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   RELEASES   │  │   TESTERS    │  │   ACTIVITY   │
│              │  │              │  │              │
│  Total: 15   │  │  Active: 25  │  │  Today: 45   │
│  Today: 2    │  │  Groups: 3   │  │  Week: 120   │
└──────────────┘  └──────────────┘  └──────────────┘

┌──────────────────────────────────────────────────────┐
│  DOWNLOAD STATISTICS                                  │
│  ████████████████████░░░░░  75%                      │
│  18 of 24 testers downloaded                         │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│  INSTALLATION SUCCESS                                 │
│  ███████████████████████░  92%                       │
│  16 of 18 successfully installed                     │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│  CRASH REPORTS                                        │
│  ██░░░░░░░░░░░░░░░░░░░░░  2 crashes (12%)           │
│  View details in Crashlytics                         │
└──────────────────────────────────────────────────────┘
```

---

**Use these diagrams untuk memahami flow dan architecture! 🎨**

*Untuk implementasi detail, lihat dokumentasi lengkap di INDEX.md*
