package com.example.intentactivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Barang extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        String namaBarang = getIntent().getStringExtra("nama_barang");
        TextView tvNamaBarang = findViewById(R.id.tvNamaBarang);
        tvNamaBarang.setText("Nama Barang: " + namaBarang);
    }
}
