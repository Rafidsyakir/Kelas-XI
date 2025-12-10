<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\GuruPengganti;
use App\Models\Guru;
use App\Models\Jadwal;
use Illuminate\Http\Request;

class GuruPenggantiApiController extends Controller
{
    /**
     * Get list of guru pengganti (untuk Kurikulum role)
     */
    public function index(Request $request)
    {
        try {
            $query = GuruPengganti::with(['jadwal.guru', 'jadwal.kelas', 'guruAsli', 'guruPengganti']);

            // Filter by status
            if ($request->filled('status')) {
                $query->where('status', $request->status);
            }

            // Filter by tanggal
            if ($request->filled('tanggal')) {
                $query->whereDate('tanggal', $request->tanggal);
            } else {
                // Default: show today's data
                $query->whereDate('tanggal', today());
            }

            $guruPengganti = $query->orderBy('tanggal', 'desc')
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
                    'disetujui_oleh' => $item->disetujui_oleh,
                    'disetujui_pada' => $item->disetujui_pada ? $item->disetujui_pada->format('Y-m-d H:i:s') : null,
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
                        'email' => $item->guruAsli->email,
                        'no_hp' => $item->guruAsli->no_hp,
                    ],
                    'guru_pengganti' => $item->guruPengganti ? [
                        'id' => $item->guruPengganti->id,
                        'nama' => $item->guruPengganti->nama,
                        'email' => $item->guruPengganti->email,
                        'no_hp' => $item->guruPengganti->no_hp,
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

    /**
     * Get available substitute teachers for a specific date
     */
    public function getAvailableSubstitutes(Request $request)
    {
        try {
            $tanggal = $request->tanggal ?? today()->format('Y-m-d');
            $jadwalId = $request->jadwal_id;

            // Get all active teachers
            $query = Guru::where('status', 'aktif');

            // Exclude teachers who are already assigned as substitutes on this date
            $assignedGuruIds = GuruPengganti::whereDate('tanggal', $tanggal)
                ->where('status', '!=', 'Ditolak')
                ->pluck('guru_pengganti_id')
                ->toArray();

            if (!empty($assignedGuruIds)) {
                $query->whereNotIn('id', $assignedGuruIds);
            }

            // Exclude the original teacher if jadwal_id is provided
            if ($jadwalId) {
                $jadwal = Jadwal::find($jadwalId);
                if ($jadwal) {
                    $query->where('id', '!=', $jadwal->guru_id);
                }
            }

            $availableGuru = $query->get()->map(function ($guru) {
                return [
                    'id' => $guru->id,
                    'nama' => $guru->nama,
                    'email' => $guru->email,
                    'no_hp' => $guru->no_hp,
                ];
            });

            return response()->json([
                'success' => true,
                'message' => 'Data guru tersedia berhasil diambil',
                'data' => $availableGuru
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil data: ' . $e->getMessage()
            ], 500);
        }
    }

    /**
     * Assign guru pengganti
     */
    public function assign(Request $request)
    {
        try {
            $request->validate([
                'jadwal_id' => 'required|exists:jadwals,id',
                'tanggal' => 'required|date',
                'guru_asli_id' => 'required|exists:gurus,id',
                'guru_pengganti_id' => 'required|exists:gurus,id',
                'alasan' => 'required|in:Sakit,Izin,Cuti,Dinas Luar,Lainnya',
                'keterangan' => 'nullable|string',
            ]);

            $guruPengganti = GuruPengganti::create([
                'jadwal_id' => $request->jadwal_id,
                'tanggal' => $request->tanggal,
                'guru_asli_id' => $request->guru_asli_id,
                'guru_pengganti_id' => $request->guru_pengganti_id,
                'alasan' => $request->alasan,
                'keterangan' => $request->keterangan,
                'status' => 'Pending',
            ]);

            $guruPengganti->load(['jadwal.kelas', 'guruAsli', 'guruPengganti']);

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil ditambahkan',
                'data' => [
                    'id' => $guruPengganti->id,
                    'jadwal_id' => $guruPengganti->jadwal_id,
                    'tanggal' => $guruPengganti->tanggal->format('Y-m-d'),
                    'guru_asli' => [
                        'id' => $guruPengganti->guruAsli->id,
                        'nama' => $guruPengganti->guruAsli->nama,
                    ],
                    'guru_pengganti' => [
                        'id' => $guruPengganti->guruPengganti->id,
                        'nama' => $guruPengganti->guruPengganti->nama,
                    ],
                    'alasan' => $guruPengganti->alasan,
                    'status' => $guruPengganti->status,
                ]
            ], 201);
        } catch (\Illuminate\Validation\ValidationException $e) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $e->errors()
            ], 422);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal menambahkan guru pengganti: ' . $e->getMessage()
            ], 500);
        }
    }

    /**
     * Update status guru pengganti (approve/reject)
     */
    public function updateStatus(Request $request, $id)
    {
        try {
            $request->validate([
                'status' => 'required|in:Disetujui,Ditolak',
            ]);

            $guruPengganti = GuruPengganti::findOrFail($id);
            $guruPengganti->status = $request->status;
            $guruPengganti->disetujui_oleh = auth('sanctum')->id();
            $guruPengganti->disetujui_pada = now();
            $guruPengganti->save();

            return response()->json([
                'success' => true,
                'message' => "Guru pengganti berhasil {$request->status}"
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengubah status: ' . $e->getMessage()
            ], 500);
        }
    }

    /**
     * Delete guru pengganti
     */
    public function destroy($id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);
            $guruPengganti->delete();

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil dihapus'
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal menghapus guru pengganti: ' . $e->getMessage()
            ], 500);
        }
    }
}
