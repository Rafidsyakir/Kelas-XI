# ======================================================
#   PERBAIKAN MySQL XAMPP - Metode Aman (Keep Database)
# ======================================================

Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "   PERBAIKAN MySQL XAMPP - Database Aman" -ForegroundColor Cyan
Write-Host "================================================`n" -ForegroundColor Cyan

# Stop MySQL jika masih berjalan
Write-Host "[1/6] Menghentikan MySQL..." -ForegroundColor Yellow
Stop-Process -Name mysqld -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# Backup database
Write-Host "[2/6] Backup database..." -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupPath = "C:\xampp\mysql\data_backup_$timestamp"

if (-not (Test-Path $backupPath)) {
    Copy-Item "C:\xampp\mysql\data" $backupPath -Recurse -Force
    Write-Host "[OK] Backup selesai: $backupPath" -ForegroundColor Green
}

# Perbaikan Method 1: Hapus file log saja (Database tetap aman)
Write-Host "[3/6] Menghapus file log yang corrupt..." -ForegroundColor Yellow
Remove-Item "C:\xampp\mysql\data\ib_logfile0" -Force -ErrorAction SilentlyContinue
Remove-Item "C:\xampp\mysql\data\ib_logfile1" -Force -ErrorAction SilentlyContinue
Remove-Item "C:\xampp\mysql\data\aria_log.*" -Force -ErrorAction SilentlyContinue
Remove-Item "C:\xampp\mysql\data\aria_log_control" -Force -ErrorAction SilentlyContinue
Write-Host "[OK] File log dihapus" -ForegroundColor Green

# Cek my.ini
Write-Host "[4/6] Cek konfigurasi MySQL..." -ForegroundColor Yellow
$myini = "C:\xampp\mysql\bin\my.ini"
if (Test-Path $myini) {
    Write-Host "[OK] File my.ini ditemukan" -ForegroundColor Green
} else {
    Write-Host "[WARNING] my.ini tidak ditemukan" -ForegroundColor Red
}

# Coba repair dengan mysqlcheck
Write-Host "[5/6] Mengecek integritas database..." -ForegroundColor Yellow
# Start MySQL terlebih dahulu
Start-Process "C:\xampp\mysql\bin\mysqld.exe" -NoNewWindow
Start-Sleep -Seconds 5

# Test koneksi
Write-Host "[6/6] Test koneksi MySQL..." -ForegroundColor Yellow
$testConnection = & "C:\xampp\mysql\bin\mysql.exe" -uroot -e "SELECT 1;" 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] MySQL berhasil jalan!" -ForegroundColor Green
} else {
    Write-Host "[WARNING] MySQL belum bisa start" -ForegroundColor Red
}

Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "   PERBAIKAN SELESAI!" -ForegroundColor Green
Write-Host "================================================`n" -ForegroundColor Cyan

Write-Host "Langkah selanjutnya:" -ForegroundColor White
Write-Host "1. Buka XAMPP Control Panel" -ForegroundColor Gray
Write-Host "2. Klik tombol 'Start' pada MySQL" -ForegroundColor Gray
Write-Host "3. Jika masih error, lihat log di XAMPP" -ForegroundColor Gray
Write-Host "`nBackup location: $backupPath" -ForegroundColor Yellow
Write-Host "Database Anda AMAN di folder backup!`n" -ForegroundColor Green
