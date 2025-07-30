package com.example.intentactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText etBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etBarang = findViewById(R.id.etBarang);
    }

    public void goToBarang(View view) {
        String namaBarang = etBarang.getText().toString();
        Intent intent = new Intent(this, Barang.class);
        intent.putExtra("nama_barang", namaBarang);
        startActivity(intent);
    }

    public void goToPenjualan(View view) {
        startActivity(new Intent(this, Penjualan.class));
    }

    public void goToPembelian(View view) {
        startActivity(new Intent(this, Pembelian.class));
    }
}
