package com.example.recyclerviewcard;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SiswaAdapter adapter;
    private List<Siswa> siswaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Opsional, bisa dinonaktifkan jika menyebabkan masalah layout
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        // 1. Inisialisasi list data
        siswaList = new ArrayList<>();

        // 2. Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.rvSiswa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Buat dan pasang adapter
        adapter = new SiswaAdapter(this, siswaList);
        recyclerView.setAdapter(adapter);

        // 4. Muat data awal (dummy data)
        loadDataSiswa();
    }

    private void loadDataSiswa() {
        siswaList.add(new Siswa("Budi Santoso", "Jl. Merdeka No. 10, Jakarta"));
        siswaList.add(new Siswa("Ani Yudhoyono", "Jl. Pahlawan No. 22, Surabaya"));
        siswaList.add(new Siswa("Charlie van Houten", "Jl. Kembang No. 5, Bandung"));
        siswaList.add(new Siswa("Dewi Persik", "Jl. Mawar No. 1, Jember"));
        adapter.notifyDataSetChanged(); // Memberi tahu adapter bahwa data telah berubah
    }

    // Method ini akan dipanggil saat tombol "Tambah" diklik (dari XML android:onClick)
    public void btnTambah(View view) {
        int nomorSiswaBaru = siswaList.size() + 1;
        siswaList.add(new Siswa("Siswa Baru " + nomorSiswaBaru, "Alamat Default"));
        // Memberi tahu adapter ada item baru di posisi terakhir
        adapter.notifyItemInserted(siswaList.size() - 1);
        // Scroll ke item yang baru ditambahkan
        recyclerView.scrollToPosition(siswaList.size() - 1);
    }
}