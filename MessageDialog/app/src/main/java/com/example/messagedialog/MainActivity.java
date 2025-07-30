package com.example.messagedialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnToast(View view) {
        Toast.makeText(this, "Ini adalah Toast!", Toast.LENGTH_SHORT).show();
    }

    public void btnAlert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert Dialog");
        builder.setMessage("Ini adalah alert dialog sederhana.");
        builder.setCancelable(true);
        builder.show();
    }

    public void btnAlertDialogButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert dengan Tombol");
        builder.setMessage("Apakah kamu yakin?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", (dialog, which) -> {
            Toast.makeText(this, "Kamu memilih Ya", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            Toast.makeText(this, "Kamu memilih Tidak", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
}
