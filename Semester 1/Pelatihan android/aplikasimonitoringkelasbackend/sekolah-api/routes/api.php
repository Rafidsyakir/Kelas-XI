<?php

use App\Http\Controllers\Api\TaskController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\Api\SiswaController;
use App\Http\Controllers\Api\TeacherAttendanceApiController;
use App\Http\Controllers\Api\KurikulumController;
use App\Http\Controllers\Api\GuruPenggantiApiController;
use App\Http\Controllers\KepalaSekolahController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

// Authentication Routes
Route::post('/login', [AuthController::class, 'login']);
Route::post('/register', [AuthController::class, 'register']);

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/user', function (Request $request) {
        return $request->user()->load('kelas');
    });
    Route::post('/logout', [AuthController::class, 'logout']);

    // Siswa Routes - Jadwal berdasarkan kelas
    Route::get('/siswa/jadwal', [SiswaController::class, 'getJadwal']);
    Route::get('/siswa/jadwal/hari-ini', [SiswaController::class, 'getJadwalHariIni']);
    Route::get('/siswa/guru-pengganti', [SiswaController::class, 'getGuruPengganti']);

    // Teacher Attendance Routes - untuk siswa input kehadiran guru
    Route::get('/teacher-attendance', [TeacherAttendanceApiController::class, 'index']);
    Route::post('/teacher-attendance', [TeacherAttendanceApiController::class, 'store']);
    Route::get('/teacher-attendance/today', [TeacherAttendanceApiController::class, 'today']);
    Route::get('/gurus', [TeacherAttendanceApiController::class, 'getGurus']);

    // Kurikulum Routes - Dashboard dan Manajemen
    Route::prefix('kurikulum')->group(function () {
        Route::get('/dashboard', [KurikulumController::class, 'dashboard']);
        Route::get('/rekap-kehadiran', [KurikulumController::class, 'rekapKehadiran']);
        Route::get('/guru', [KurikulumController::class, 'listGuru']);
        Route::get('/guru/{id}/statistik', [KurikulumController::class, 'statistikGuru']);
        Route::get('/kelas', [KurikulumController::class, 'listKelas']);
        Route::get('/jadwal', [KurikulumController::class, 'listJadwal']);
        Route::get('/laporan-harian', [KurikulumController::class, 'laporanHarian']);
        
        // Guru Pengganti Routes
        Route::get('/guru-pengganti', [GuruPenggantiApiController::class, 'index']);
        Route::get('/guru-pengganti/available', [GuruPenggantiApiController::class, 'getAvailableSubstitutes']);
        Route::post('/guru-pengganti', [GuruPenggantiApiController::class, 'assign']);
        Route::patch('/guru-pengganti/{id}/status', [GuruPenggantiApiController::class, 'updateStatus']);
        Route::delete('/guru-pengganti/{id}', [GuruPenggantiApiController::class, 'destroy']);
    });

    // Kepala Sekolah Routes - Executive Dashboard
    Route::prefix('kepala-sekolah')->group(function () {
        Route::get('/dashboard', [KepalaSekolahController::class, 'dashboard']);
        Route::get('/tren-mingguan', [KepalaSekolahController::class, 'getTrenMingguan']);
        Route::get('/tren-bulanan', [KepalaSekolahController::class, 'getTrenBulanan']);
        Route::get('/ranking-guru', [KepalaSekolahController::class, 'getRankingGuru']);
        Route::get('/laporan', [KepalaSekolahController::class, 'getLaporan']);
    });
});

// Public API Routes (untuk development, nanti bisa ditambah auth middleware)
Route::apiResource('users', UserController::class);
Route::apiResource('tasks', TaskController::class);

// Jadwal API Routes
Route::get('/jadwals', [JadwalController::class, 'apiIndex']);
Route::post('/jadwals', [JadwalController::class, 'apiStore']);
Route::get('/jadwals/{id}', [JadwalController::class, 'apiShow']);
Route::put('/jadwals/{id}', [JadwalController::class, 'apiUpdate']);
Route::delete('/jadwals/{id}', [JadwalController::class, 'apiDestroy']);
