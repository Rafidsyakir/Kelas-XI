<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Jadwal;
use App\Models\Guru;
use App\Models\Kelas;
use App\Models\TeacherAttendance;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;

class KurikulumController extends Controller
{
    /**
     * Dashboard Overview - Statistik kehadiran guru
     */
    public function dashboard(Request $request): JsonResponse
    {
        $today = Carbon::today();

        // Total guru
        $totalGuru = Guru::count();

        // Kehadiran hari ini
        $attendanceToday = TeacherAttendance::whereDate('tanggal', $today)->get();

        $hadir = $attendanceToday->where('status', 'Hadir')->count();
        $terlambat = $attendanceToday->where('status', 'Terlambat')->count();
        $tidakHadir = $attendanceToday->where('status', 'Tidak Hadir')->count();
        $izin = $attendanceToday->where('status', 'Izin')->count();
        $belumInput = $totalGuru - $attendanceToday->count();

        // Statistik mingguan
        $startOfWeek = Carbon::now()->startOfWeek();
        $endOfWeek = Carbon::now()->endOfWeek();

        $weeklyStats = TeacherAttendance::whereBetween('tanggal', [$startOfWeek, $endOfWeek])
            ->selectRaw('status, COUNT(*) as count')
            ->groupBy('status')
            ->pluck('count', 'status')
            ->toArray();

        // Jadwal hari ini
        $hariMapping = [
            'Sunday' => 'Minggu',
            'Monday' => 'Senin',
            'Tuesday' => 'Selasa',
            'Wednesday' => 'Rabu',
            'Thursday' => 'Kamis',
            'Friday' => 'Jumat',
            'Saturday' => 'Sabtu'
        ];
        $hariIni = $hariMapping[Carbon::now()->format('l')];

        $jadwalHariIni = Jadwal::with(['guru', 'kelas'])
            ->where('hari', $hariIni)
            ->orderBy('jam_mulai')
            ->get();

        // Total jadwal dan kelas
        $totalJadwal = Jadwal::count();
        $totalKelas = Kelas::count();

        return response()->json([
            'success' => true,
            'message' => 'Dashboard data berhasil diambil',
            'data' => [
                'tanggal' => $today->format('Y-m-d'),
                'tanggal_formatted' => $today->format('d F Y'),
                'hari' => $hariIni,
                'summary' => [
                    'total_guru' => $totalGuru,
                    'total_jadwal' => $totalJadwal,
                    'total_kelas' => $totalKelas,
                ],
                'kehadiran_hari_ini' => [
                    'hadir' => $hadir,
                    'terlambat' => $terlambat,
                    'tidak_hadir' => $tidakHadir,
                    'izin' => $izin,
                    'belum_input' => $belumInput,
                ],
                'kehadiran_mingguan' => [
                    'hadir' => $weeklyStats['Hadir'] ?? 0,
                    'terlambat' => $weeklyStats['Terlambat'] ?? 0,
                    'tidak_hadir' => $weeklyStats['Tidak Hadir'] ?? 0,
                    'izin' => $weeklyStats['Izin'] ?? 0,
                ],
                'jadwal_hari_ini' => $jadwalHariIni,
            ]
        ]);
    }

    /**
     * Rekap Kehadiran Guru
     */
    public function rekapKehadiran(Request $request): JsonResponse
    {
        $tanggalMulai = $request->get('tanggal_mulai', Carbon::now()->startOfMonth()->format('Y-m-d'));
        $tanggalSelesai = $request->get('tanggal_selesai', Carbon::now()->format('Y-m-d'));
        $guruId = $request->get('guru_id');

        $query = TeacherAttendance::with('guru')
            ->whereBetween('tanggal', [$tanggalMulai, $tanggalSelesai]);

        if ($guruId) {
            $query->where('guru_id', $guruId);
        }

        $attendances = $query->orderBy('tanggal', 'desc')->get();

        // Statistik per guru
        $guruStats = Guru::all()->map(function ($guru) use ($tanggalMulai, $tanggalSelesai) {
            $attendances = TeacherAttendance::where('guru_id', $guru->id)
                ->whereBetween('tanggal', [$tanggalMulai, $tanggalSelesai])
                ->get();

            $total = $attendances->count();
            $hadir = $attendances->where('status', 'Hadir')->count();
            $terlambat = $attendances->where('status', 'Terlambat')->count();
            $tidakHadir = $attendances->where('status', 'Tidak Hadir')->count();
            $izin = $attendances->where('status', 'Izin')->count();

            $persentaseHadir = $total > 0 ? round((($hadir + $terlambat) / $total) * 100, 1) : 0;

            return [
                'guru' => $guru,
                'statistik' => [
                    'total_hari' => $total,
                    'hadir' => $hadir,
                    'terlambat' => $terlambat,
                    'tidak_hadir' => $tidakHadir,
                    'izin' => $izin,
                    'persentase_hadir' => $persentaseHadir,
                ]
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Rekap kehadiran berhasil diambil',
            'data' => [
                'periode' => [
                    'mulai' => $tanggalMulai,
                    'selesai' => $tanggalSelesai,
                ],
                'rekap_per_guru' => $guruStats,
                'detail_kehadiran' => $attendances,
            ]
        ]);
    }

    /**
     * Statistik Kehadiran Guru per Guru
     */
    public function statistikGuru(Request $request, $guruId): JsonResponse
    {
        $guru = Guru::find($guruId);

        if (!$guru) {
            return response()->json([
                'success' => false,
                'message' => 'Guru tidak ditemukan'
            ], 404);
        }

        // Kehadiran bulan ini
        $startOfMonth = Carbon::now()->startOfMonth();
        $endOfMonth = Carbon::now()->endOfMonth();

        $attendances = TeacherAttendance::where('guru_id', $guruId)
            ->whereBetween('tanggal', [$startOfMonth, $endOfMonth])
            ->orderBy('tanggal', 'desc')
            ->get();

        $total = $attendances->count();
        $hadir = $attendances->where('status', 'Hadir')->count();
        $terlambat = $attendances->where('status', 'Terlambat')->count();
        $tidakHadir = $attendances->where('status', 'Tidak Hadir')->count();
        $izin = $attendances->where('status', 'Izin')->count();

        // Jadwal mengajar
        $jadwals = Jadwal::with('kelas')
            ->where('guru_id', $guruId)
            ->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
            ->orderBy('jam_mulai')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Statistik guru berhasil diambil',
            'data' => [
                'guru' => $guru,
                'periode' => $startOfMonth->format('F Y'),
                'statistik' => [
                    'total_hari' => $total,
                    'hadir' => $hadir,
                    'terlambat' => $terlambat,
                    'tidak_hadir' => $tidakHadir,
                    'izin' => $izin,
                    'persentase_hadir' => $total > 0 ? round((($hadir + $terlambat) / $total) * 100, 1) : 0,
                ],
                'jadwal_mengajar' => $jadwals,
                'riwayat_kehadiran' => $attendances,
            ]
        ]);
    }

    /**
     * Daftar semua guru dengan statistik singkat
     */
    public function listGuru(Request $request): JsonResponse
    {
        $today = Carbon::today();

        $gurus = Guru::all()->map(function ($guru) use ($today) {
            // Kehadiran hari ini
            $attendanceToday = TeacherAttendance::where('guru_id', $guru->id)
                ->whereDate('tanggal', $today)
                ->first();

            // Statistik bulan ini
            $startOfMonth = Carbon::now()->startOfMonth();
            $monthlyAttendances = TeacherAttendance::where('guru_id', $guru->id)
                ->whereBetween('tanggal', [$startOfMonth, $today])
                ->get();

            $totalHari = $monthlyAttendances->count();
            $hadir = $monthlyAttendances->whereIn('status', ['Hadir', 'Terlambat'])->count();

            // Jumlah jadwal mengajar
            $totalJadwal = Jadwal::where('guru_id', $guru->id)->count();

            return [
                'guru' => $guru,
                'status_hari_ini' => $attendanceToday?->status ?? 'Belum Input',
                'statistik_bulan_ini' => [
                    'total_hari' => $totalHari,
                    'persentase_hadir' => $totalHari > 0 ? round(($hadir / $totalHari) * 100, 1) : 0,
                ],
                'total_jadwal' => $totalJadwal,
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Daftar guru berhasil diambil',
            'data' => $gurus
        ]);
    }

    /**
     * Daftar semua kelas dengan jadwal
     */
    public function listKelas(Request $request): JsonResponse
    {
        $kelass = Kelas::all()->map(function ($kelas) {
            $jadwals = Jadwal::with('guru')
                ->where('kelas_id', $kelas->id)
                ->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
                ->orderBy('jam_mulai')
                ->get()
                ->groupBy('hari');

            $totalJadwal = Jadwal::where('kelas_id', $kelas->id)->count();

            return [
                'kelas' => $kelas,
                'total_jadwal' => $totalJadwal,
                'jadwal_per_hari' => $jadwals,
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Daftar kelas berhasil diambil',
            'data' => $kelass
        ]);
    }

    /**
     * Semua jadwal dengan filter
     */
    public function listJadwal(Request $request): JsonResponse
    {
        $hari = $request->get('hari');
        $kelasId = $request->get('kelas_id');
        $guruId = $request->get('guru_id');

        $query = Jadwal::with(['guru', 'kelas']);

        if ($hari) {
            $query->where('hari', $hari);
        }

        if ($kelasId) {
            $query->where('kelas_id', $kelasId);
        }

        if ($guruId) {
            $query->where('guru_id', $guruId);
        }

        $jadwals = $query->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
            ->orderBy('jam_mulai')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Daftar jadwal berhasil diambil',
            'data' => $jadwals
        ]);
    }

    /**
     * Laporan kehadiran per hari
     */
    public function laporanHarian(Request $request): JsonResponse
    {
        $tanggal = $request->get('tanggal', Carbon::today()->format('Y-m-d'));

        $attendances = TeacherAttendance::with('guru')
            ->whereDate('tanggal', $tanggal)
            ->get();

        $gurus = Guru::all();

        $guruStatus = $gurus->map(function ($guru) use ($attendances) {
            $attendance = $attendances->where('guru_id', $guru->id)->first();

            return [
                'guru' => $guru,
                'status' => $attendance?->status ?? 'Belum Input',
                'jam_masuk' => $attendance?->jam_masuk,
                'keterangan' => $attendance?->keterangan,
            ];
        });

        $summary = [
            'hadir' => $attendances->where('status', 'Hadir')->count(),
            'terlambat' => $attendances->where('status', 'Terlambat')->count(),
            'tidak_hadir' => $attendances->where('status', 'Tidak Hadir')->count(),
            'izin' => $attendances->where('status', 'Izin')->count(),
            'belum_input' => $gurus->count() - $attendances->count(),
        ];

        return response()->json([
            'success' => true,
            'message' => 'Laporan harian berhasil diambil',
            'data' => [
                'tanggal' => $tanggal,
                'tanggal_formatted' => Carbon::parse($tanggal)->format('d F Y'),
                'summary' => $summary,
                'detail' => $guruStatus,
            ]
        ]);
    }
}
