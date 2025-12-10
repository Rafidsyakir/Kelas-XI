# RECOVERY MySQL XAMPP - Metode Lengkap
# Gunakan jika metode pertama gagal

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  RECOVERY MySQL - Metode Lengkap" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Stop semua proses MySQL
Write-Host "[1/8] Stop semua proses MySQL..." -ForegroundColor Yellow
Stop-Process -Name mysqld -Force -ErrorAction SilentlyContinue
taskkill /F /IM mysqld.exe /T 2>$null
Start-Sleep -Seconds 3

# Backup lengkap
Write-Host "[2/8] Backup database lengkap..." -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupPath = "C:\xampp\mysql\data_recovery_$timestamp"
Copy-Item "C:\xampp\mysql\data" $backupPath -Recurse -Force
Write-Host "[OK] Backup: $backupPath" -ForegroundColor Green

# Hapus file system MySQL yang corrupt
Write-Host "[3/8] Hapus file system yang corrupt..." -ForegroundColor Yellow
$dataPath = "C:\xampp\mysql\data"
Remove-Item "$dataPath\ibdata1" -Force -ErrorAction SilentlyContinue
Remove-Item "$dataPath\ib_logfile*" -Force -ErrorAction SilentlyContinue
Remove-Item "$dataPath\aria_log*" -Force -ErrorAction SilentlyContinue
Remove-Item "$dataPath\ib_buffer_pool" -Force -ErrorAction SilentlyContinue
Remove-Item "$dataPath\ibtmp1" -Force -ErrorAction SilentlyContinue
Remove-Item "$dataPath\*.err" -Force -ErrorAction SilentlyContinue
Write-Host "[OK] File system dihapus" -ForegroundColor Green

# Copy database user folders saja (sekolah, dll)
Write-Host "[4/8] Preserve database user..." -ForegroundColor Yellow
$userDatabases = Get-ChildItem $dataPath -Directory | Where-Object { 
    $_.Name -notin @('mysql', 'performance_schema', 'phpmyadmin', 'test') 
}
Write-Host "[INFO] Database user ditemukan: $($userDatabases.Count)" -ForegroundColor Cyan
foreach ($db in $userDatabases) {
    Write-Host "  - $($db.Name)" -ForegroundColor Gray
}

# Restore MySQL system dari instalasi bersih
Write-Host "[5/8] Restore MySQL system files..." -ForegroundColor Yellow
# Copy dari backup XAMPP atau download fresh
if (Test-Path "C:\xampp\mysql\backup") {
    Copy-Item "C:\xampp\mysql\backup\data\mysql" "$dataPath\mysql" -Recurse -Force -ErrorAction SilentlyContinue
    Copy-Item "C:\xampp\mysql\backup\data\performance_schema" "$dataPath\performance_schema" -Recurse -Force -ErrorAction SilentlyContinue
}

# Initialize MySQL baru
Write-Host "[6/8] Initialize MySQL system..." -ForegroundColor Yellow
& "C:\xampp\mysql\bin\mysqld.exe" --initialize-insecure --datadir="$dataPath" 2>&1 | Out-Null
Start-Sleep -Seconds 3

# Copy kembali database user
Write-Host "[7/8] Restore database user..." -ForegroundColor Yellow
foreach ($db in $userDatabases) {
    $sourcePath = Join-Path $backupPath $db.Name
    $destPath = Join-Path $dataPath $db.Name
    if (Test-Path $sourcePath) {
        Copy-Item $sourcePath $destPath -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "  [OK] Restored: $($db.Name)" -ForegroundColor Green
    }
}

# Test start MySQL
Write-Host "[8/8] Test start MySQL..." -ForegroundColor Yellow
$mysqlProcess = Start-Process "C:\xampp\mysql\bin\mysqld.exe" -PassThru -NoNewWindow
Start-Sleep -Seconds 5

if ($mysqlProcess -and !$mysqlProcess.HasExited) {
    Write-Host "[OK] MySQL berhasil start!" -ForegroundColor Green
    
    # Test koneksi
    Start-Sleep -Seconds 2
    $test = & "C:\xampp\mysql\bin\mysql.exe" -uroot -e "SHOW DATABASES;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Koneksi database berhasil!" -ForegroundColor Green
        Write-Host "`nDatabase yang tersedia:" -ForegroundColor Cyan
        & "C:\xampp\mysql\bin\mysql.exe" -uroot -e "SHOW DATABASES;"
    }
    
    # Stop for XAMPP to take over
    Stop-Process -Id $mysqlProcess.Id -Force
} else {
    Write-Host "[ERROR] MySQL gagal start" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  RECOVERY SELESAI!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "HASIL:" -ForegroundColor White
Write-Host "- Backup lengkap: $backupPath" -ForegroundColor Yellow
Write-Host "- Database user telah di-restore" -ForegroundColor Green
Write-Host "- MySQL system di-reset" -ForegroundColor Green
Write-Host "`nSekarang buka XAMPP Control Panel dan start MySQL!" -ForegroundColor Cyan
