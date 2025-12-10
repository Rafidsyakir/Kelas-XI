package com.example.aplikasimonitoringkelas3.data.model

import com.google.gson.annotations.SerializedName

// Dashboard Response
data class KurikulumDashboardResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: KurikulumDashboardData?
)

data class KurikulumDashboardData(
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("tanggal_formatted")
    val tanggalFormatted: String,
    
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("summary")
    val summary: DashboardSummary,
    
    @SerializedName("kehadiran_hari_ini")
    val kehadiranHariIni: KehadiranSummary,
    
    @SerializedName("kehadiran_mingguan")
    val kehadiranMingguan: KehadiranMingguan,
    
    @SerializedName("jadwal_hari_ini")
    val jadwalHariIni: List<Jadwal>
)

data class DashboardSummary(
    @SerializedName("total_guru")
    val totalGuru: Int,
    
    @SerializedName("total_jadwal")
    val totalJadwal: Int,
    
    @SerializedName("total_kelas")
    val totalKelas: Int
)

data class KehadiranSummary(
    @SerializedName("hadir")
    val hadir: Int,
    
    @SerializedName("terlambat")
    val terlambat: Int,
    
    @SerializedName("tidak_hadir")
    val tidakHadir: Int,
    
    @SerializedName("izin")
    val izin: Int,
    
    @SerializedName("belum_input")
    val belumInput: Int
)

data class KehadiranMingguan(
    @SerializedName("hadir")
    val hadir: Int,
    
    @SerializedName("terlambat")
    val terlambat: Int,
    
    @SerializedName("tidak_hadir")
    val tidakHadir: Int,
    
    @SerializedName("izin")
    val izin: Int
)

// Rekap Kehadiran Response
data class RekapKehadiranResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: RekapKehadiranData?
)

data class RekapKehadiranData(
    @SerializedName("periode")
    val periode: Periode,
    
    @SerializedName("rekap_per_guru")
    val rekapPerGuru: List<GuruRekap>,
    
    @SerializedName("detail_kehadiran")
    val detailKehadiran: List<TeacherAttendance>
)

data class Periode(
    @SerializedName("mulai")
    val mulai: String,
    
    @SerializedName("selesai")
    val selesai: String
)

data class GuruRekap(
    @SerializedName("guru")
    val guru: Guru,
    
    @SerializedName("statistik")
    val statistik: StatistikKehadiran
)

data class StatistikKehadiran(
    @SerializedName("total_hari")
    val totalHari: Int,
    
    @SerializedName("hadir")
    val hadir: Int,
    
    @SerializedName("terlambat")
    val terlambat: Int,
    
    @SerializedName("tidak_hadir")
    val tidakHadir: Int,
    
    @SerializedName("izin")
    val izin: Int,
    
    @SerializedName("persentase_hadir")
    val persentaseHadir: Float
)

// List Guru Response
data class ListGuruResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<GuruWithStats>?
)

data class GuruWithStats(
    @SerializedName("guru")
    val guru: Guru,
    
    @SerializedName("status_hari_ini")
    val statusHariIni: String,
    
    @SerializedName("statistik_bulan_ini")
    val statistikBulanIni: StatistikBulan,
    
    @SerializedName("total_jadwal")
    val totalJadwal: Int
)

data class StatistikBulan(
    @SerializedName("total_hari")
    val totalHari: Int,
    
    @SerializedName("persentase_hadir")
    val persentaseHadir: Float
)

// Statistik Guru Detail Response
data class StatistikGuruResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: StatistikGuruData?
)

data class StatistikGuruData(
    @SerializedName("guru")
    val guru: Guru,
    
    @SerializedName("periode")
    val periode: String,
    
    @SerializedName("statistik")
    val statistik: StatistikKehadiran,
    
    @SerializedName("jadwal_mengajar")
    val jadwalMengajar: List<Jadwal>,
    
    @SerializedName("riwayat_kehadiran")
    val riwayatKehadiran: List<TeacherAttendance>
)

// List Kelas Response
data class ListKelasResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<KelasWithJadwal>?
)

data class KelasWithJadwal(
    @SerializedName("kelas")
    val kelas: Kelas,
    
    @SerializedName("total_jadwal")
    val totalJadwal: Int,
    
    @SerializedName("jadwal_per_hari")
    val jadwalPerHari: Map<String, List<Jadwal>>
)

// List Jadwal Response
data class ListJadwalResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<Jadwal>?
)

// Laporan Harian Response
data class LaporanHarianResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: LaporanHarianData?
)

data class LaporanHarianData(
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("tanggal_formatted")
    val tanggalFormatted: String,
    
    @SerializedName("summary")
    val summary: KehadiranSummary,
    
    @SerializedName("detail")
    val detail: List<GuruStatusHarian>
)

data class GuruStatusHarian(
    @SerializedName("guru")
    val guru: Guru,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("jam_masuk")
    val jamMasuk: String?,
    
    @SerializedName("keterangan")
    val keterangan: String?
)

// Guru Pengganti Models
data class GuruPenggantiListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<GuruPenggantiItem>?
)

data class GuruPenggantiItem(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    
    @SerializedName("guru_asli_id")
    val guruAsliId: Int,
    
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int?,
    
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("tanggal_formatted")
    val tanggalFormatted: String,
    
    @SerializedName("alasan")
    val alasan: String,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("jadwal")
    val jadwal: Jadwal,
    
    @SerializedName("guru_asli")
    val guruAsli: Guru,
    
    @SerializedName("guru_pengganti")
    val guruPengganti: Guru?
)

data class AvailableSubstitutesResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<Guru>?
)

data class AssignGuruPenggantiRequest(
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("guru_asli_id")
    val guruAsliId: Int,
    
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int,
    
    @SerializedName("alasan")
    val alasan: String,
    
    @SerializedName("keterangan")
    val keterangan: String? = null
)

data class GuruPenggantiActionResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String
)