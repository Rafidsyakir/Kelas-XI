package com.example.recyclerviewcard;

public class Siswa {
    // 1. Definisikan atribut untuk siswa
    private String nama;
    private String alamat;

    // 2. Buat constructor untuk inisialisasi data
    public Siswa(String nama, String alamat) {
        this.nama = nama;
        this.alamat = alamat;
    }

    // 3. Buat getter untuk mengambil data
    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }
}