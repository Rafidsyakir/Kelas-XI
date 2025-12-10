package com.example.aplikasimonitoringkelas3.data.repository

import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import retrofit2.Response

class KurikulumRepository {
    private val apiService = RetrofitClient.apiService

    // Dashboard
    suspend fun getDashboard(): Response<KurikulumDashboardResponse> {
        return apiService.getKurikulumDashboard()
    }
    
    // Rekap Kehadiran
    suspend fun getRekapKehadiran(
        tanggalMulai: String? = null,
        tanggalSelesai: String? = null,
        guruId: Int? = null
    ): Response<RekapKehadiranResponse> {
        return apiService.getRekapKehadiran(tanggalMulai, tanggalSelesai, guruId)
    }
    
    // List Guru
    suspend fun getListGuru(): Response<ListGuruResponse> {
        return apiService.getListGuru()
    }
    
    // Statistik Guru Detail
    suspend fun getStatistikGuru(guruId: Int): Response<StatistikGuruResponse> {
        return apiService.getStatistikGuru(guruId)
    }
    
    // List Kelas
    suspend fun getListKelas(): Response<ListKelasResponse> {
        return apiService.getListKelas()
    }
    
    // List Jadwal dengan Filter
    suspend fun getJadwal(
        hari: String? = null,
        kelasId: Int? = null,
        guruId: Int? = null
    ): Response<ListJadwalResponse> {
        return apiService.getKurikulumJadwal(hari, kelasId, guruId)
    }
    
    // Laporan Harian
    suspend fun getLaporanHarian(tanggal: String? = null): Response<LaporanHarianResponse> {
        return apiService.getLaporanHarian(tanggal)
    }
    
    // ==================== GURU PENGGANTI ====================
    
    suspend fun getGuruPengganti(tanggal: String? = null): Response<GuruPenggantiListResponse> {
        return apiService.getGuruPengganti(tanggal)
    }
    
    suspend fun getAvailableSubstitutes(tanggal: String? = null, jadwalId: Int? = null): Response<AvailableSubstitutesResponse> {
        return apiService.getAvailableSubstitutes(tanggal, jadwalId)
    }
    
    suspend fun assignGuruPengganti(request: AssignGuruPenggantiRequest): Response<GuruPenggantiActionResponse> {
        return apiService.assignGuruPengganti(request)
    }
    
    suspend fun updateGuruPenggantiStatus(id: Int, status: String): Response<GuruPenggantiActionResponse> {
        return apiService.updateGuruPenggantiStatus(id, mapOf("status" to status))
    }
    
    suspend fun deleteGuruPengganti(id: Int): Response<GuruPenggantiActionResponse> {
        return apiService.deleteGuruPengganti(id)
    }
}
