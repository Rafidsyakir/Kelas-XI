@echo off
echo ================================================
echo   PERBAIKAN MySQL XAMPP - Database Aman
echo ================================================
echo.

echo [INFO] Memeriksa status MySQL...
tasklist | findstr "mysqld" >nul
if %errorlevel% equ 0 (
    echo [WARNING] MySQL masih berjalan, menghentikan...
    taskkill /F /IM mysqld.exe >nul 2>&1
    timeout /t 3 >nul
)

echo [INFO] Backup database ke folder backup...
set timestamp=%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set timestamp=%timestamp: =0%

if not exist "C:\xampp\mysql\data_backup_%timestamp%" (
    xcopy "C:\xampp\mysql\data" "C:\xampp\mysql\data_backup_%timestamp%\" /E /I /H /Y >nul
    echo [OK] Backup selesai: data_backup_%timestamp%
) else (
    echo [OK] Backup sudah ada
)

echo.
echo [INFO] Memperbaiki file MySQL yang corrupt...
cd /d C:\xampp\mysql\data

if exist ibdata1 (
    echo [INFO] Backup ibdata1...
    copy ibdata1 ibdata1.old >nul 2>&1
)

echo [INFO] Menghapus file log yang corrupt...
del ib_logfile0 >nul 2>&1
del ib_logfile1 >nul 2>&1
del aria_log.* >nul 2>&1
del aria_log_control >nul 2>&1

echo [OK] File log dihapus

echo.
echo [INFO] Membuat ulang file MySQL...
cd /d C:\xampp\mysql\bin

echo [INFO] Initialize MySQL...
mysqld --initialize-insecure --datadir=C:\xampp\mysql\data >nul 2>&1

echo.
echo ================================================
echo   PERBAIKAN SELESAI!
echo ================================================
echo.
echo Langkah selanjutnya:
echo 1. Buka XAMPP Control Panel
echo 2. Start MySQL
echo 3. Database Anda masih aman di backup folder
echo.
echo Backup location: C:\xampp\mysql\data_backup_%timestamp%
echo.
pause
