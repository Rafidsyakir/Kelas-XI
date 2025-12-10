@echo off
echo ====================================
echo   Setup ADB Port Forwarding
echo ====================================
echo.

echo Checking connected devices...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" devices

echo.
echo Setting up port forwarding...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8000 tcp:8000

echo.
echo Verifying...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse --list

echo.
echo ====================================
echo   DONE! Port 8000 forwarded
echo ====================================
echo.
echo Sekarang aplikasi Android bisa akses
echo http://localhost:8000 via USB
echo.
pause
