<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Carbon\Carbon;

class SiswaController extends Controller
{
    /**
     * Get jadwal berdasarkan kelas user yang login
     */
    public function getJadwal(Request $request): JsonResponse
    {
        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        $jadwals = Jadwal::with(['guru', 'kelas'])
            ->where('kelas_id', $user->kelas_id)
            ->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
            ->orderBy('jam_mulai')
            ->get()
            ->groupBy('hari');

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diambil',
            'data' => [
                'kelas' => $user->kelas,
                'jadwal' => $jadwals
            ]
        ]);
    }

    /**
     * Get jadwal hari ini
     */
    public function getJadwalHariIni(Request $request): JsonResponse
    {
        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        // Get hari dalam bahasa Indonesia
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

        $jadwals = Jadwal::with(['guru', 'kelas'])
            ->where('kelas_id', $user->kelas_id)
            ->where('hari', $hariIni)
            ->orderBy('jam_mulai')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Jadwal hari ini berhasil diambil',
            'data' => [
                'hari' => $hariIni,
                'tanggal' => Carbon::now()->format('d F Y'),
                'kelas' => $user->kelas,
                'jadwal' => $jadwals
            ]
        ]);
    }

    /**
     * Get guru pengganti untuk siswa (berdasarkan kelas siswa)
     */
    public function getGuruPengganti(Request $request): JsonResponse
    {
        try {
            $user = $request->user();

            if (!$user->kelas_id) {
                return response()->json([
                    'success' => false,
                    'message' => 'Anda belum terdaftar di kelas manapun'
                ], 400);
            }

            $tanggal = $request->tanggal ?? today()->format('Y-m-d');

            // Get jadwal kelas siswa pada tanggal tersebut
            $hariMapping = [
                'Sunday' => 'Minggu',
                'Monday' => 'Senin',
                'Tuesday' => 'Selasa',
                'Wednesday' => 'Rabu',
                'Thursday' => 'Kamis',
                'Friday' => 'Jumat',
                'Saturday' => 'Sabtu'
            ];
            
            $hari = $hariMapping[Carbon::parse($tanggal)->format('l')];

            // Get guru pengganti yang jadwalnya sesuai dengan kelas siswa
            $guruPengganti = \App\Models\GuruPengganti::with(['jadwal.guru', 'jadwal.kelas', 'guruAsli', 'guruPengganti'])
                ->whereHas('jadwal', function($query) use ($user, $hari) {
                    $query->where('kelas_id', $user->kelas_id)
                          ->where('hari', $hari);
                })
                ->whereDate('tanggal', $tanggal)
                ->where('status', '!=', 'Ditolak')
                ->orderBy('created_at', 'desc')
                ->get();

            $data = $guruPengganti->map(function ($item) {
                return [
                    'id' => $item->id,
                    'jadwal_id' => $item->jadwal_id,
                    'guru_asli_id' => $item->guru_asli_id,
                    'guru_pengganti_id' => $item->guru_pengganti_id,
                    'tanggal' => $item->tanggal->format('Y-m-d'),
                    'tanggal_formatted' => $item->tanggal->translatedFormat('l, d F Y'),
                    'alasan' => $item->alasan,
                    'keterangan' => $item->keterangan,
                    'status' => $item->status,
                    'jadwal' => [
                        'id' => $item->jadwal->id,
                        'hari' => $item->jadwal->hari,
                        'mata_pelajaran' => $item->jadwal->mata_pelajaran,
                        'jam_mulai' => substr($item->jadwal->jam_mulai, 0, 5),
                        'jam_selesai' => substr($item->jadwal->jam_selesai, 0, 5),
                        'kelas' => [
                            'id' => $item->jadwal->kelas->id ?? null,
                            'nama_kelas' => $item->jadwal->kelas->nama_kelas ?? 'N/A',
                        ]
                    ],
                    'guru_asli' => [
                        'id' => $item->guruAsli->id,
                        'nama' => $item->guruAsli->nama,
                    ],
                    'guru_pengganti' => $item->guruPengganti ? [
                        'id' => $item->guruPengganti->id,
                        'nama' => $item->guruPengganti->nama,
                    ] : null,
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data guru pengganti berhasil diambil',
                'data' => $data
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil data: ' . $e->getMessage()
            ], 500);
        }
    }
}
