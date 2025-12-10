# START BACKEND - PowerShell Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   SETUP BACKEND LARAVEL" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "c:\Kelas XI\PelatihanAndroid\api-aplikasimonitoringkelas\sekolah-api"

Write-Host "[1/4] Checking composer dependencies..." -ForegroundColor Yellow
if (!(Test-Path "vendor")) {
    Write-Host "Installing composer dependencies..." -ForegroundColor Yellow
    composer install
} else {
    Write-Host "Composer dependencies already installed." -ForegroundColor Green
}
Write-Host ""

Write-Host "[2/4] Running migrations..." -ForegroundColor Yellow
php artisan migrate
Write-Host ""

Write-Host "[3/4] Seeding database with test data..." -ForegroundColor Yellow
php artisan db:seed --class=DevelopmentSeeder
Write-Host ""

Write-Host "[4/4] Getting your IP Address..." -ForegroundColor Yellow
Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like "192.168.*"} | Select-Object IPAddress
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   STARTING LARAVEL SERVER" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Server will run at: http://localhost:8000" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANT: Update RetrofitClient.kt dengan IP address Anda!" -ForegroundColor Red
Write-Host "- Emulator: http://10.0.2.2:8000/api/" -ForegroundColor Yellow
Write-Host "- Device Fisik: http://[IP_ADDRESS]:8000/api/" -ForegroundColor Yellow
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

php artisan serve
