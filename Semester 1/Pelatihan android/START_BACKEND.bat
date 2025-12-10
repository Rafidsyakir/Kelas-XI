@echo off
echo ========================================
echo   SETUP BACKEND LARAVEL
echo ========================================
echo.

cd "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"

echo [1/4] Checking composer dependencies...
if not exist "vendor" (
    echo Installing composer dependencies...
    composer install
) else (
    echo Composer dependencies already installed.
)
echo.

echo [2/4] Running migrations...
php artisan migrate
echo.

echo [3/4] Seeding database with test data...
php artisan db:seed --class=DevelopmentSeeder
echo.

echo [4/4] Getting your IP Address...
ipconfig | findstr /i "IPv4"
echo.

echo ========================================
echo   STARTING LARAVEL SERVER
echo ========================================
echo Server will run at: http://localhost:8000
echo.
echo IMPORTANT: Update RetrofitClient.kt dengan IP address Anda!
echo - Emulator: http://10.0.2.2:8000/api/
echo - Device Fisik: http://[IP_ADDRESS]:8000/api/
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

php artisan serve
