#!/bin/bash

# Setup Script untuk Firebase App Distribution
# Script ini akan membantu Anda setup proyek untuk Firebase App Distribution

echo "============================================"
echo "🚀 Firebase App Distribution Setup Script"
echo "============================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 1. Check Firebase CLI
echo -e "${YELLOW}1️⃣ Checking Firebase CLI...${NC}"
if command -v firebase &> /dev/null; then
    version=$(firebase --version)
    echo -e "${GREEN}✅ Firebase CLI sudah terinstall: $version${NC}"
else
    echo -e "${RED}❌ Firebase CLI belum terinstall${NC}"
    echo -e "${BLUE}ℹ️  Install dengan: npm install -g firebase-tools${NC}"
    read -p "Install Firebase CLI sekarang? (y/n): " install
    if [ "$install" = "y" ]; then
        npm install -g firebase-tools
        echo -e "${GREEN}✅ Firebase CLI berhasil diinstall${NC}"
    else
        echo -e "${YELLOW}⚠️  Firebase CLI diperlukan untuk distribusi${NC}"
    fi
fi

echo ""

# 2. Check Keystore
echo -e "${YELLOW}2️⃣ Checking Keystore...${NC}"
if [ -f "keystore/release-keystore.jks" ]; then
    echo -e "${GREEN}✅ Keystore sudah ada: keystore/release-keystore.jks${NC}"
else
    echo -e "${YELLOW}⚠️  Keystore belum dibuat${NC}"
    read -p "Buat keystore baru? (y/n): " create_keystore
    if [ "$create_keystore" = "y" ]; then
        mkdir -p keystore
        
        echo -e "${BLUE}ℹ️  Masukkan informasi untuk keystore:${NC}"
        read -p "Key alias (default: my-release-key): " alias
        alias=${alias:-my-release-key}
        
        keytool -genkey -v -keystore keystore/release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias "$alias"
        
        if [ -f "keystore/release-keystore.jks" ]; then
            echo -e "${GREEN}✅ Keystore berhasil dibuat${NC}"
        else
            echo -e "${RED}❌ Gagal membuat keystore${NC}"
        fi
    fi
fi

echo ""

# 3. Setup keystore.properties
echo -e "${YELLOW}3️⃣ Checking keystore.properties...${NC}"
if [ -f "keystore.properties" ]; then
    echo -e "${GREEN}✅ keystore.properties sudah ada${NC}"
else
    echo -e "${YELLOW}⚠️  keystore.properties belum ada${NC}"
    read -p "Buat keystore.properties? (y/n): " create_properties
    if [ "$create_properties" = "y" ]; then
        echo -e "${BLUE}ℹ️  Masukkan kredensial keystore:${NC}"
        read -sp "Store password: " store_password
        echo ""
        read -sp "Key password: " key_password
        echo ""
        read -p "Key alias (default: my-release-key): " key_alias
        key_alias=${key_alias:-my-release-key}
        
        cat > keystore.properties << EOF
storePassword=$store_password
keyPassword=$key_password
keyAlias=$key_alias
storeFile=../keystore/release-keystore.jks
EOF
        echo -e "${GREEN}✅ keystore.properties berhasil dibuat${NC}"
        echo -e "${YELLOW}⚠️  JANGAN commit file ini ke Git!${NC}"
    fi
fi

echo ""

# 4. Setup Firebase Token
echo -e "${YELLOW}4️⃣ Checking Firebase Token...${NC}"
if [ -n "$FIREBASE_TOKEN" ]; then
    echo -e "${GREEN}✅ Firebase token sudah di-set${NC}"
    echo -e "${BLUE}ℹ️  Token: ${FIREBASE_TOKEN:0:20}...${NC}"
else
    echo -e "${YELLOW}⚠️  Firebase token belum di-set${NC}"
    read -p "Setup Firebase token sekarang? (y/n): " setup_token
    if [ "$setup_token" = "y" ]; then
        echo -e "${BLUE}ℹ️  Menjalankan firebase login:ci...${NC}"
        echo -e "${BLUE}ℹ️  Browser akan terbuka, silakan login dengan akun Google Anda${NC}"
        firebase login:ci
        echo ""
        read -p "Paste Firebase token di sini: " firebase_token
        export FIREBASE_TOKEN="$firebase_token"
        echo -e "${GREEN}✅ Firebase token berhasil di-set untuk session ini${NC}"
        echo -e "${YELLOW}⚠️  Untuk permanent, tambahkan ke ~/.bashrc atau ~/.zshrc:${NC}"
        echo -e "${BLUE}   export FIREBASE_TOKEN=\"$firebase_token\"${NC}"
    fi
fi

echo ""

# 5. Check google-services.json
echo -e "${YELLOW}5️⃣ Checking google-services.json...${NC}"
if [ -f "app/google-services.json" ]; then
    echo -e "${GREEN}✅ google-services.json sudah ada${NC}"
else
    echo -e "${RED}❌ google-services.json TIDAK ditemukan${NC}"
    echo -e "${BLUE}ℹ️  Download file ini dari Firebase Console:${NC}"
    echo -e "${BLUE}   1. Buka https://console.firebase.google.com/${NC}"
    echo -e "${BLUE}   2. Pilih proyek Anda${NC}"
    echo -e "${BLUE}   3. Go to Project Settings${NC}"
    echo -e "${BLUE}   4. Download google-services.json${NC}"
    echo -e "${BLUE}   5. Letakkan di folder app/${NC}"
fi

echo ""

# 6. Update build.gradle.kts files
echo -e "${YELLOW}6️⃣ Checking build.gradle.kts configuration...${NC}"
if [ -f "build.gradle.kts.distribution" ]; then
    read -p "Update build.gradle.kts dengan konfigurasi distribution? (y/n): " update_gradle
    if [ "$update_gradle" = "y" ]; then
        # Backup original files
        if [ -f "build.gradle.kts" ]; then
            cp build.gradle.kts build.gradle.kts.backup
            echo -e "${GREEN}✅ Backup build.gradle.kts dibuat${NC}"
        fi
        if [ -f "app/build.gradle.kts" ]; then
            cp app/build.gradle.kts app/build.gradle.kts.backup
            echo -e "${GREEN}✅ Backup app/build.gradle.kts dibuat${NC}"
        fi
        
        # Copy new files
        cp build.gradle.kts.distribution build.gradle.kts
        cp app/build.gradle.kts.distribution app/build.gradle.kts
        echo -e "${GREEN}✅ build.gradle.kts files berhasil diupdate${NC}"
    fi
fi

echo ""

# 7. Setup release-notes.txt
echo -e "${YELLOW}7️⃣ Checking release-notes.txt...${NC}"
if [ -f "release-notes.txt" ]; then
    echo -e "${GREEN}✅ release-notes.txt sudah ada${NC}"
else
    echo -e "${YELLOW}⚠️  release-notes.txt belum ada (akan dibuat dengan default content)${NC}"
    cat > release-notes.txt << EOF
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

Tanggal Rilis: $(date '+%d %B %Y')
EOF
    echo -e "${GREEN}✅ release-notes.txt berhasil dibuat${NC}"
fi

echo ""

# 8. Check .gitignore
echo -e "${YELLOW}8️⃣ Checking .gitignore...${NC}"
if [ -f ".gitignore" ]; then
    if ! grep -q "*.jks" .gitignore; then
        echo -e "${YELLOW}⚠️  .gitignore perlu diupdate${NC}"
        read -p "Update .gitignore untuk keamanan? (y/n): " update_gitignore
        if [ "$update_gitignore" = "y" ]; then
            cat >> .gitignore << EOF

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
EOF
            echo -e "${GREEN}✅ .gitignore berhasil diupdate${NC}"
        fi
    else
        echo -e "${GREEN}✅ .gitignore sudah dikonfigurasi dengan baik${NC}"
    fi
fi

echo ""
echo "============================================"
echo -e "${GREEN}✅ Setup Selesai!${NC}"
echo "============================================"
echo ""

# Summary
echo -e "${CYAN}📋 Summary:${NC}"
echo ""
echo -e "${YELLOW}Untuk distribusi aplikasi, gunakan salah satu command berikut:${NC}"
echo ""
echo "  ./gradlew distributeApk           - Distribusi APK (recommended)"
echo "  ./gradlew distributeAab           - Distribusi AAB"
echo "  ./gradlew distributeWithAutoNotes - Distribusi dengan auto-generated notes"
echo "  ./gradlew checkDistributionConfig - Check konfigurasi"
echo ""

echo -e "${CYAN}📚 Dokumentasi:${NC}"
echo "  - Quick Start: QUICKSTART.md"
echo "  - Full Guide: FIREBASE_APP_DISTRIBUTION_GUIDE.md"
echo ""

read -p "Tekan Enter untuk keluar..."
