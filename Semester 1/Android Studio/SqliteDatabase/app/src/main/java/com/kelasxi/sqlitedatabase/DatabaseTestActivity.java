package com.kelasxi.sqlitedatabase;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DatabaseTestActivity extends AppCompatActivity {
    
    private static final String TAG = "DatabaseTest";
    
    Database db;
    TextView tvTestResults;
    EditText etTestBarang, etTestStok, etTestHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple layout programmatically for testing
        TextView title = new TextView(this);
        title.setText("Database Test Activity");
        title.setTextSize(20);
        title.setPadding(16, 16, 16, 16);
        
        tvTestResults = new TextView(this);
        tvTestResults.setText("Test Results:\n");
        tvTestResults.setPadding(16, 16, 16, 16);
        
        etTestBarang = new EditText(this);
        etTestBarang.setHint("Test Product Name");
        etTestBarang.setPadding(16, 16, 16, 16);
        
        etTestStok = new EditText(this);
        etTestStok.setHint("Test Stock");
        etTestStok.setPadding(16, 16, 16, 16);
        
        etTestHarga = new EditText(this);
        etTestHarga.setHint("Test Price");
        etTestHarga.setPadding(16, 16, 16, 16);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(title);
        layout.addView(etTestBarang);
        layout.addView(etTestStok);
        layout.addView(etTestHarga);
        
        // Add test button
        android.widget.Button btnTest = new android.widget.Button(this);
        btnTest.setText("Run Database Test");
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDatabaseTest();
            }
        });
        layout.addView(btnTest);
        
        // Add insert test button
        android.widget.Button btnInsert = new android.widget.Button(this);
        btnInsert.setText("Insert Test Data");
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTestData();
            }
        });
        layout.addView(btnInsert);
        
        layout.addView(tvTestResults);
        
        setContentView(layout);
        
        // Initialize database
        db = new Database(this);
        
        // Run initial test
        runDatabaseTest();
    }
    
    private void runDatabaseTest() {
        StringBuilder results = new StringBuilder("🧪 Database Test Results:\n\n");
        
        try {
            // Test 1: Check if database is initialized
            results.append("1. Database Initialization: ");
            if (db != null) {
                results.append("✅ SUCCESS\n");
            } else {
                results.append("❌ FAILED - Database is null\n");
                return;
            }
            
            // Test 2: Check table structure
            results.append("2. Table Structure Check: ");
            try {
                String sql = "PRAGMA table_info(tblbarang)";
                Cursor cursor = db.select(sql);
                if (cursor != null && cursor.getCount() > 0) {
                    results.append("✅ SUCCESS - Table exists\n");
                    results.append("   Columns: ");
                    cursor.moveToFirst();
                    do {
                        String columnName = cursor.getString(1); // column name is at index 1
                        results.append(columnName).append(" ");
                    } while (cursor.moveToNext());
                    results.append("\n");
                    cursor.close();
                } else {
                    results.append("❌ FAILED - Table doesn't exist\n");
                }
            } catch (Exception e) {
                results.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            }
            
            // Test 3: Check data count
            results.append("3. Data Count Check: ");
            try {
                String sql = "SELECT COUNT(*) FROM tblbarang";
                Cursor cursor = db.select(sql);
                if (cursor != null && cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    results.append("✅ SUCCESS - Found ").append(count).append(" records\n");
                    cursor.close();
                } else {
                    results.append("❌ FAILED - Cannot count records\n");
                }
            } catch (Exception e) {
                results.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            }
            
            // Test 4: Check sample data
            results.append("4. Sample Data Check: ");
            try {
                String sql = "SELECT * FROM tblbarang LIMIT 5";
                Cursor cursor = db.select(sql);
                if (cursor != null) {
                    results.append("✅ SUCCESS\n");
                    if (cursor.getCount() > 0) {
                        results.append("   Sample records:\n");
                        cursor.moveToFirst();
                        do {
                            int idIndex = cursor.getColumnIndex("id");
                            int barangIndex = cursor.getColumnIndex("barang");
                            int stokIndex = cursor.getColumnIndex("stok");
                            int hargaIndex = cursor.getColumnIndex("harga");
                            
                            if (idIndex >= 0 && barangIndex >= 0 && stokIndex >= 0 && hargaIndex >= 0) {
                                String id = cursor.getString(idIndex);
                                String barang = cursor.getString(barangIndex);
                                String stok = cursor.getString(stokIndex);
                                String harga = cursor.getString(hargaIndex);
                                
                                results.append("   ID:").append(id)
                                       .append(" | Barang:").append(barang)
                                       .append(" | Stok:").append(stok)
                                       .append(" | Harga:").append(harga).append("\n");
                            }
                        } while (cursor.moveToNext());
                    } else {
                        results.append("   No data found - Database is empty\n");
                    }
                    cursor.close();
                } else {
                    results.append("❌ FAILED - Cannot retrieve data\n");
                }
            } catch (Exception e) {
                results.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            }
            
            // Test 5: Barang class test
            results.append("5. Barang Class Test: ");
            try {
                Barang testBarang = new Barang("1", "Test Product", "10", "50000");
                results.append("✅ SUCCESS\n");
                results.append("   Test object: ").append(testBarang.getBarang())
                       .append(" (Stock: ").append(testBarang.getStock())
                       .append(", Price: ").append(testBarang.getHarga()).append(")\n");
            } catch (Exception e) {
                results.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            }
            
        } catch (Exception e) {
            results.append("❌ GENERAL ERROR: ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Database test error", e);
        }
        
        tvTestResults.setText(results.toString());
        Log.d(TAG, results.toString());
    }
    
    private void insertTestData() {
        try {
            String barang = etTestBarang.getText().toString().trim();
            String stok = etTestStok.getText().toString().trim();
            String harga = etTestHarga.getText().toString().trim();
            
            if (barang.isEmpty()) barang = "Test Product " + System.currentTimeMillis();
            if (stok.isEmpty()) stok = "10";
            if (harga.isEmpty()) harga = "25000";
            
            String sql = "INSERT INTO tblbarang (barang, stok, harga) VALUES ('" 
                    + barang + "', " + stok + ", " + harga + ")";
            
            boolean result = db.runSQL(sql);
            
            if (result) {
                Toast.makeText(this, "✅ Test data inserted successfully!", Toast.LENGTH_SHORT).show();
                runDatabaseTest(); // Refresh results
            } else {
                Toast.makeText(this, "❌ Failed to insert test data", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Insert test data error", e);
        }
    }
}
