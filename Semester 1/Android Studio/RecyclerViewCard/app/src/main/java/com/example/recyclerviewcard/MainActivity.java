package com.example.recyclerviewcard;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSiswa;
    private SiswaAdapter adapterSiswa;
    private SiswaAdapter adapterBuah;

    private ArrayList<Siswa> listSiswa;
    private ArrayList<Siswa> listBuah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvSiswa = findViewById(R.id.rvSiswa);
        rvSiswa.setLayoutManager(new LinearLayoutManager(this));

        // List untuk siswa
        listSiswa = new ArrayList<>();
        adapterSiswa = new SiswaAdapter(this, listSiswa);

        // List untuk buah
        listBuah = new ArrayList<>();
        adapterBuah = new SiswaAdapter(this, listBuah);

        // Default: tampilkan siswa
        rvSiswa.setAdapter(adapterSiswa);
    }

    // Tombol tambah siswa
    public void btnTambah(View view) {
        listSiswa.add(new Siswa(
                "Nama Siswa " + (listSiswa.size() + 1),
                "Alamat Siswa " + (listSiswa.size() + 1)
        ));
        adapterSiswa.notifyItemInserted(listSiswa.size() - 1);

        // Pastikan yang tampil adalah list siswa
        rvSiswa.setAdapter(adapterSiswa);
    }

    // Tombol tambah buah
    public void btnTambahBuah(View view) {
        String[] daftarBuah = {"Apel", "Jeruk", "Mangga", "Pisang", "Anggur", "Semangka", "Pepaya"};
        String[] kategori = {"Buah Segar", "Buah Citrus", "Buah Tropis", "Buah Manis", "Buah Anggur", "Buah Air", "Buah Tropis"};

        int index = listBuah.size() % daftarBuah.length;

        listBuah.add(new Siswa(
                daftarBuah[index],
                kategori[index]
        ));
        adapterBuah.notifyItemInserted(listBuah.size() - 1);

        // Pastikan yang tampil adalah list buah
        rvSiswa.setAdapter(adapterBuah);
    }
}
 