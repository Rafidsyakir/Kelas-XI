package com.example.aplikasimonitoringkelas3.data.api

import com.example.aplikasimonitoringkelas3.data.model.*
import com.example.aplikasimonitoringkelas3.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("register")
    suspend fun register(@Body user: User): Response<LoginResponse>
    
    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Any>>
    
    // Users
    @GET("users")
    suspend fun getUsers(): Response<ApiResponse<List<User>>>
    
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<ApiResponse<User>>
    
    @POST("users")
    suspend fun createUser(@Body user: User): Response<ApiResponse<User>>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<ApiResponse<User>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<Any>>
    
    // Jadwal
    @GET("jadwals")
    suspend fun getJadwals(): Response<ApiResponse<List<Jadwal>>>
    
    @GET("jadwals/{id}")
    suspend fun getJadwal(@Path("id") id: Int): Response<ApiResponse<Jadwal>>
    
    @POST("jadwals")
    suspend fun createJadwal(@Body jadwal: JadwalRequest): Response<ApiResponse<Jadwal>>
    
    @PUT("jadwals/{id}")
    suspend fun updateJadwal(@Path("id") id: Int, @Body jadwal: JadwalRequest): Response<ApiResponse<Jadwal>>
    
    @DELETE("jadwals/{id}")
    suspend fun deleteJadwal(@Path("id") id: Int): Response<ApiResponse<Any>>
    
    // Siswa - Jadwal berdasarkan kelas
    @GET("siswa/jadwal")
    suspend fun getSiswaJadwal(): Response<SiswaJadwalResponse>
    
    @GET("siswa/jadwal/hari-ini")
    suspend fun getSiswaJadwalHariIni(): Response<SiswaJadwalHariIniResponse>
    
    @GET("siswa/guru-pengganti")
    suspend fun getSiswaGuruPengganti(@Query("tanggal") tanggal: String? = null): Response<GuruPenggantiListResponse>
    
    // Teacher Attendance - Kehadiran Guru
    @GET("gurus")
    suspend fun getGurus(): Response<ApiResponse<List<Guru>>>
    
    @GET("teacher-attendance/today")
    suspend fun getTeacherAttendanceToday(): Response<TeacherAttendanceTodayResponse>
    
    @POST("teacher-attendance")
    suspend fun createTeacherAttendance(@Body request: TeacherAttendanceRequest): Response<ApiResponse<TeacherAttendance>>
    
    // Kurikulum - Dashboard dan Manajemen
    @GET("kurikulum/dashboard")
    suspend fun getKurikulumDashboard(): Response<KurikulumDashboardResponse>
    
    @GET("kurikulum/rekap-kehadiran")
    suspend fun getRekapKehadiran(
        @Query("tanggal_mulai") tanggalMulai: String? = null,
        @Query("tanggal_selesai") tanggalSelesai: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<RekapKehadiranResponse>
    
    @GET("kurikulum/guru")
    suspend fun getListGuru(): Response<ListGuruResponse>
    
    @GET("kurikulum/guru/{id}/statistik")
    suspend fun getStatistikGuru(@Path("id") guruId: Int): Response<StatistikGuruResponse>
    
    @GET("kurikulum/kelas")
    suspend fun getListKelas(): Response<ListKelasResponse>
    
    @GET("kurikulum/jadwal")
    suspend fun getKurikulumJadwal(
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<ListJadwalResponse>
    
    @GET("kurikulum/laporan-harian")
    suspend fun getLaporanHarian(@Query("tanggal") tanggal: String? = null): Response<LaporanHarianResponse>
    
    // Guru Pengganti
    @GET("kurikulum/guru-pengganti")
    suspend fun getGuruPengganti(@Query("tanggal") tanggal: String? = null): Response<GuruPenggantiListResponse>
    
    @GET("kurikulum/guru-pengganti/available")
    suspend fun getAvailableSubstitutes(
        @Query("tanggal") tanggal: String? = null,
        @Query("jadwal_id") jadwalId: Int? = null
    ): Response<AvailableSubstitutesResponse>
    
    @POST("kurikulum/guru-pengganti")
    suspend fun assignGuruPengganti(@Body request: AssignGuruPenggantiRequest): Response<GuruPenggantiActionResponse>
    
    @PATCH("kurikulum/guru-pengganti/{id}/status")
    suspend fun updateGuruPenggantiStatus(@Path("id") id: Int, @Body request: Map<String, String>): Response<GuruPenggantiActionResponse>
    
    @DELETE("kurikulum/guru-pengganti/{id}")
    suspend fun deleteGuruPengganti(@Path("id") id: Int): Response<GuruPenggantiActionResponse>

    // ==================== KEPALA SEKOLAH ====================
    
    // Dashboard Executive
    @GET("kepala-sekolah/dashboard")
    suspend fun getKepsekDashboard(): Response<KepsekDashboardResponse>
    
    // Tren Mingguan
    @GET("kepala-sekolah/tren-mingguan")
    suspend fun getTrenMingguan(): Response<TrenMingguanResponse>
    
    // Tren Bulanan
    @GET("kepala-sekolah/tren-bulanan")
    suspend fun getTrenBulanan(@Query("tahun") tahun: Int? = null): Response<TrenBulananResponse>
    
    // Ranking Guru
    @GET("kepala-sekolah/ranking-guru")
    suspend fun getRankingGuru(
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<RankingGuruResponse>
    
    // Laporan
    @GET("kepala-sekolah/laporan")
    suspend fun getLaporanKepsek(
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null
    ): Response<LaporanKepsekResponse>
}
