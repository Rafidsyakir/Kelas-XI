<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\GuruController;
use App\Http\Controllers\KelasController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\WebUserController;
use App\Http\Controllers\TeacherAttendanceController;
use App\Http\Controllers\GuruPenggantiController;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

// Redirect root to login
Route::get('/', function () {
    return redirect()->route('login');
});

// Authentication Routes
Route::get('/login', [AuthController::class, 'showLogin'])->name('login');
Route::post('/login', [AuthController::class, 'login'])->name('login.post');
Route::post('/logout', [AuthController::class, 'logout'])->name('logout');

// Protected Admin Routes
Route::middleware(['admin'])->group(function () {
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');

    // Resource Routes for CRUD
    Route::resource('guru', GuruController::class);
    Route::resource('kelas', KelasController::class);
    Route::resource('jadwal', JadwalController::class);
    Route::resource('manage-users', WebUserController::class);

    // Teacher Attendance Routes
    Route::get('teacher-attendance/bulk-create', [TeacherAttendanceController::class, 'bulkCreate'])->name('teacher-attendance.bulk-create');
    Route::post('teacher-attendance/bulk-store', [TeacherAttendanceController::class, 'bulkStore'])->name('teacher-attendance.bulk-store');
    Route::get('teacher-attendance/report', [TeacherAttendanceController::class, 'report'])->name('teacher-attendance.report');
    Route::resource('teacher-attendance', TeacherAttendanceController::class);

    // Guru Pengganti Routes
    Route::patch('guru-pengganti/{guruPengganti}/approve', [GuruPenggantiController::class, 'approve'])->name('guru-pengganti.approve');
    Route::patch('guru-pengganti/{guruPengganti}/reject', [GuruPenggantiController::class, 'reject'])->name('guru-pengganti.reject');
    Route::patch('guru-pengganti/{guruPengganti}/complete', [GuruPenggantiController::class, 'complete'])->name('guru-pengganti.complete');
    Route::get('guru-pengganti/get-guru/{jadwal}', [GuruPenggantiController::class, 'getGuruFromJadwal'])->name('guru-pengganti.get-guru');
    Route::resource('guru-pengganti', GuruPenggantiController::class);
});
