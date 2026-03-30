#!/bin/bash

# Build Script untuk Trifhop Android App
# ======================================

echo "🚀 Building Trifhop Android App..."
echo ""

# Clean previous build
echo "🧹 Cleaning previous build..."
./gradlew clean

# Build Debug APK
echo "📦 Building Debug APK..."
./gradlew assembleDebug

# Check if build successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build Successful!"
    echo ""
    echo "📱 APK Location:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📊 APK Size:"
    ls -lh app/build/outputs/apk/debug/app-debug.apk | awk '{print "   " $5}'
    echo ""
    echo "🎉 Ready to install on device!"
    echo ""
    echo "To install:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo ""
    echo "❌ Build Failed!"
    echo "Check the error messages above."
    exit 1
fi
