<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\TeacherAttendance;
use App\Models\Guru;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Validator;
use Carbon\Carbon;

class TeacherAttendanceApiController extends Controller
{
    /**
     * Get all teacher attendance records
     */
    public function index(Request $request): JsonResponse
    {
        $query = TeacherAttendance::with('guru');

        // Filter by date if provided
        if ($request->has('tanggal')) {
            $query->where('tanggal', $request->tanggal);
        }

        // Filter by guru if provided
        if ($request->has('guru_id')) {
            $query->where('guru_id', $request->guru_id);
        }

        $attendances = $query->orderBy('tanggal', 'desc')
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Data kehadiran guru berhasil diambil',
            'data' => $attendances
        ]);
    }

    /**
     * Get today's attendance
     */
    public function today(): JsonResponse
    {
        $today = Carbon::now()->format('Y-m-d');

        $attendances = TeacherAttendance::with('guru')
            ->where('tanggal', $today)
            ->get();

        // Get all gurus and mark which ones have attendance
        $allGurus = Guru::all();
        $attendedGuruIds = $attendances->pluck('guru_id')->toArray();

        $guruStatus = $allGurus->map(function ($guru) use ($attendances, $attendedGuruIds) {
            $attendance = $attendances->firstWhere('guru_id', $guru->id);
            return [
                'guru' => $guru,
                'has_attendance' => in_array($guru->id, $attendedGuruIds),
                'attendance' => $attendance
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data kehadiran hari ini berhasil diambil',
            'data' => [
                'tanggal' => $today,
                'tanggal_formatted' => Carbon::now()->format('d F Y'),
                'summary' => [
                    'total_guru' => $allGurus->count(),
                    'hadir' => $attendances->where('status', 'Hadir')->count(),
                    'terlambat' => $attendances->where('status', 'Terlambat')->count(),
                    'tidak_hadir' => $attendances->where('status', 'Tidak Hadir')->count(),
                    'izin' => $attendances->where('status', 'Izin')->count(),
                    'belum_input' => $allGurus->count() - $attendances->count()
                ],
                'guru_status' => $guruStatus,
                'attendances' => $attendances
            ]
        ]);
    }

    /**
     * Store a new teacher attendance
     */
    public function store(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'guru_id' => 'required|exists:guru,id',
            'status' => 'required|in:Hadir,Terlambat,Tidak Hadir,Izin,Present,Late,Absent,Excused',
            'tanggal' => 'nullable|date',
            'jam_masuk' => 'nullable|date_format:H:i',
            'keterangan' => 'nullable|string|max:500'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        // Convert English status to Indonesian
        $statusMap = [
            'Present' => 'Hadir',
            'Late' => 'Terlambat',
            'Absent' => 'Tidak Hadir',
            'Excused' => 'Izin'
        ];
        
        $status = $statusMap[$request->status] ?? $request->status;
        $tanggal = $request->tanggal ?? Carbon::now()->format('Y-m-d');

        // Check if attendance already exists for this guru on this date
        $existing = TeacherAttendance::where('guru_id', $request->guru_id)
            ->where('tanggal', $tanggal)
            ->first();

        if ($existing) {
            // Update existing
            $existing->update([
                'status' => $status,
                'jam_masuk' => $request->jam_masuk ?? ($status === 'Hadir' || $status === 'Terlambat' ? Carbon::now()->format('H:i') : null),
                'keterangan' => $request->keterangan
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Kehadiran guru berhasil diupdate',
                'data' => $existing->load('guru')
            ]);
        }

        // Create new
        $attendance = TeacherAttendance::create([
            'guru_id' => $request->guru_id,
            'tanggal' => $tanggal,
            'status' => $status,
            'jam_masuk' => $request->jam_masuk ?? ($status === 'Hadir' || $status === 'Terlambat' ? Carbon::now()->format('H:i') : null),
            'keterangan' => $request->keterangan
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Kehadiran guru berhasil dicatat',
            'data' => $attendance->load('guru')
        ], 201);
    }

    /**
     * Get all gurus
     */
    public function getGurus(): JsonResponse
    {
        $gurus = Guru::orderBy('nama')->get();

        return response()->json([
            'success' => true,
            'message' => 'Data guru berhasil diambil',
            'data' => $gurus
        ]);
    }
}
