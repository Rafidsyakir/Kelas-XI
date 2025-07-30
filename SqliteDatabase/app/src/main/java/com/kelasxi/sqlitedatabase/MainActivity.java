package com.kelasxi.sqlitedatabase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    Database db;
    EditText etBarang, etStok, etHarga, etSearch;
    TextView tvPilihan, tvEmptyState;
    RecyclerView rcvBarang; // RecyclerView renamed to rcvBarang
    BarangAdapter adapter;
    List<Barang> dataBarang; // List renamed to dataBarang (ArrayList)
    
    // SharedPreferences object for saving/retrieving app settings
    SharedPreferences sharedPreferences;
    
    // Variable to track update mode
    private String updateId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "Setting content view");
            setContentView(R.layout.activity_main);
            
            Log.d(TAG, "Initializing components");
            initializeComponents();
            
            Log.d(TAG, "Loading initial data");
            selectData();
            
            // Load last saved preferences if available
            loadLastPreferences();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void initializeComponents() {
        try {
            // Initialize database
            Log.d(TAG, "Creating database");
            db = new Database(this);
            
            // Initialize SharedPreferences
            Log.d(TAG, "Initializing SharedPreferences");
            sharedPreferences = getSharedPreferences("barang", MODE_PRIVATE);
            
            // Initialize views
            Log.d(TAG, "Finding views");
            etBarang = findViewById(R.id.etBarang);
            etStok = findViewById(R.id.etStok);
            etHarga = findViewById(R.id.etHarga);
            etSearch = findViewById(R.id.etSearch);
            tvPilihan = findViewById(R.id.tvPilihan);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            rcvBarang = findViewById(R.id.recyclerView); // Initialize rcvBarang
            
            // Check if all views are found
            if (etBarang == null || etStok == null || etHarga == null || 
                etSearch == null || tvPilihan == null || rcvBarang == null || tvEmptyState == null) {
                throw new RuntimeException("One or more views not found in layout");
            }
            
            Log.d(TAG, "All views found successfully");
            
            // CRITICAL: Initialize data list FIRST
            dataBarang = new ArrayList<>();
            Log.d(TAG, "DataBarang list initialized, size: " + dataBarang.size());
            
            // CRITICAL: Initialize adapter with data list
            adapter = new BarangAdapter(this, dataBarang);
            Log.d(TAG, "Adapter created with data list");
            
            // CRITICAL: Setup RecyclerView properties BEFORE setting adapter
            Log.d(TAG, "Setting up RecyclerView");
            rcvBarang.setLayoutManager(new LinearLayoutManager(this));
            rcvBarang.setHasFixedSize(true);
            
            // CRITICAL: Set adapter to RecyclerView
            rcvBarang.setAdapter(adapter);
            Log.d(TAG, "Adapter set to RecyclerView successfully");
            
            // Verify setup
            Log.d(TAG, "Verification - RecyclerView: " + (rcvBarang != null));
            Log.d(TAG, "Verification - Adapter: " + (adapter != null));
            Log.d(TAG, "Verification - DataBarang: " + (dataBarang != null) + ", size: " + dataBarang.size());
            Log.d(TAG, "Verification - RecyclerView has adapter: " + (rcvBarang.getAdapter() != null));
            
            // Initially show empty state
            if (tvEmptyState != null) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rcvBarang.setVisibility(View.GONE);
                Log.d(TAG, "Initial UI state: showing empty state");
            }
            
            Log.d(TAG, "RecyclerView setup complete - all components verified");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in initializeComponents", e);
            throw e;
        }
    }
    
    // Method to load last saved preferences on app startup
    private void loadLastPreferences() {
        try {
            // Check if there's saved data
            String barang = sharedPreferences.getString("key_for_barang", "");
            
            if (!barang.isEmpty()) {
                // Auto-load last saved preferences (optional - you can enable/disable this)
                // Uncomment the next line if you want to auto-populate form on startup
                // tampilPreferences(null);
                
                Log.d(TAG, "Previous preferences data found for: " + barang);
                pesan("Tip: Click 'Tampil Preferences' to load last saved data");
            } else {
                Log.d(TAG, "No previous preferences data found");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading last preferences", e);
        }
    }
    
    // Method triggered by SIMPAN button click
    public void simpan(View v) {
        try {
            // Retrieve text from EditText fields
            String barang = etBarang.getText().toString().trim();
            String stok = etStok.getText().toString().trim();
            String harga = etHarga.getText().toString().trim();
            
            // Input validation
            if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
                pesan("Data Kosong");
                return;
            }
            
            // Validate numeric inputs
            try {
                Integer.parseInt(stok);
                Double.parseDouble(harga);
            } catch (NumberFormatException e) {
                pesan("Stok harus berupa angka bulat dan Harga harus berupa angka");
                return;
            }
            
            // Check if it's update mode or insert mode
            if (updateId != null) {
                // Update mode - call updateData method
                updateData(updateId, barang, stok, harga);
                
                // Reset to insert mode
                updateId = null;
                tvPilihan.setText("📝 Insert Mode");
            } else {
                // Insert mode - construct SQL INSERT statement
                String sql = "INSERT INTO tblbarang (barang, stok, harga) VALUES ('" 
                        + barang + "', " + stok + ", " + harga + ")";
                
                // Execute SQL query
                boolean result = db.runSQL(sql);
                
                if (result) {
                    pesan("Insert berhasil");
                    
                    // Clear EditText fields
                    etBarang.setText("");
                    etStok.setText("");
                    etHarga.setText("");
                    tvPilihan.setText("📝 Insert Mode");
                    
                    // Refresh data - Call selectData() after a successful insert operation to refresh the RecyclerView
                    selectData();
                } else {
                    pesan("Insert gagal");
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in simpan", e);
            pesan("Error: " + e.getMessage());
        }
    }
    
    // Method triggered by BACA button click
    public void bacaData(View v) {
        selectData();
    }
    
    // Method triggered by BATAL/CLEAR button click
    public void batalData(View v) {
        clearForm();
        pesan("Form cleared");
    }
    
    // Method to save form data to SharedPreferences
    public void simpanPreferences(View v) {
        try {
            // Retrieve text from EditText fields
            String barang = etBarang.getText().toString().trim();
            String stok = etStok.getText().toString().trim();
            String harga = etHarga.getText().toString().trim();
            
            // Input validation
            if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
                pesan("Data Kosong - Tidak dapat menyimpan ke preferences");
                return;
            }
            
            // Validate numeric inputs
            try {
                Integer.parseInt(stok);
                Float.parseFloat(harga);
            } catch (NumberFormatException e) {
                pesan("Format data salah - Stok dan Harga harus berupa angka");
                return;
            }
            
            // Get Editor from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            
            // Use editor.putString and editor.putFloat to store data
            editor.putString("key_for_barang", barang);
            editor.putString("key_for_stok", stok); // Store as string for consistency
            editor.putString("key_for_harga", harga); // Store as string for consistency
            
            // Save additional metadata
            editor.putLong("last_saved_time", System.currentTimeMillis());
            editor.putString("last_saved_mode", tvPilihan.getText().toString());
            
            // Call editor.apply() to save the changes
            editor.apply();
            
            // Display "Data Sudah Disimpan" Toast message
            pesan("Data Sudah Disimpan ke Preferences");
            Log.d(TAG, "Form data saved to SharedPreferences: " + barang + ", " + stok + ", " + harga);
            
            // Clear the input fields after saving
            clearForm();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in simpanPreferences", e);
            pesan("Error saving to preferences: " + e.getMessage());
        }
    }
    
    // Method to retrieve and display data from SharedPreferences
    public void tampilPreferences(View v) {
        try {
            // Retrieve data using sharedPreferences.getString with default values
            String barang = sharedPreferences.getString("key_for_barang", "");
            String stok = sharedPreferences.getString("key_for_stok", "");
            String harga = sharedPreferences.getString("key_for_harga", "");
            long lastSavedTime = sharedPreferences.getLong("last_saved_time", 0);
            String lastSavedMode = sharedPreferences.getString("last_saved_mode", "Unknown");
            
            // Input validation: Check if retrieved data is empty or default
            if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()) {
                pesan("Data Kosong - Tidak ada data tersimpan di preferences");
                tvEmptyState.setVisibility(View.VISIBLE);
                rcvBarang.setVisibility(View.GONE);
                return;
            }
            
            // Populate form fields with retrieved data
            etBarang.setText(barang);
            etStok.setText(stok);
            etHarga.setText(harga);
            tvPilihan.setText("💾 From Preferences");
            
            // Create timestamp string
            String timeString = "";
            if (lastSavedTime > 0) {
                timeString = " (Saved: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date(lastSavedTime)) + ")";
            }
            
            // Display the retrieved data with details
            String message = "Data berhasil dimuat dari Preferences:\n" +
                    "Barang: " + barang + "\n" +
                    "Stok: " + stok + "\n" +
                    "Harga: " + harga + "\n" +
                    "Mode: " + lastSavedMode + timeString;
            
            // Show AlertDialog with retrieved data details
            new AlertDialog.Builder(this)
                    .setTitle("Data dari Preferences")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
            
            Log.d(TAG, "Data retrieved from SharedPreferences: " + barang + ", " + stok + ", " + harga);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in tampilPreferences", e);
            pesan("Error retrieving from preferences: " + e.getMessage());
        }
    }
    
    // Method to clear SharedPreferences data
    public void clearPreferences(View v) {
        try {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Yakin akan menghapus semua data preferences?")
                    .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Clear all SharedPreferences data
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            
                            pesan("Data Preferences berhasil dihapus");
                            clearForm();
                            Log.d(TAG, "SharedPreferences cleared");
                        }
                    })
                    .setNegativeButton("TIDAK", null)
                    .show();
                    
        } catch (Exception e) {
            Log.e(TAG, "Error in clearPreferences", e);
            pesan("Error clearing preferences: " + e.getMessage());
        }
    }
    
    // Method to delete data from database
    public void deleteData(String id) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            // Create AlertDialog.Builder instance for delete confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            
            // Set the dialog title
            builder.setTitle("PERINGATAN");
            
            // Set the dialog message
            builder.setMessage("Yakin akan menghapus data ini?");
            
            // Add Positive Button (YA - Yes)
            builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Move the existing SQL delete command and related logic inside this button's onClick listener
                    try {
                        // Construct SQL DELETE statement
                        String sql = "DELETE FROM tblbarang WHERE id = '" + id + "'";
                        
                        Log.d(TAG, "Executing DELETE SQL: " + sql);
                        
                        // Call db.runSQL(sql) to execute the delete query
                        boolean result = db.runSQL(sql);
                        
                        // Display Toast message indicating success or failure of the deletion
                        if (result) {
                            pesan("Data berhasil dihapus");
                            Log.d(TAG, "Delete successful for ID: " + id);
                            
                            // Refresh data after successful deletion
                            selectData();
                        } else {
                            pesan("Gagal menghapus data");
                            Log.e(TAG, "Delete failed for ID: " + id);
                        }
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error in delete operation", e);
                        pesan("Error deleting data: " + e.getMessage());
                    }
                }
            });
            
            // Add Negative Button (TIDAK - No)
            builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Simply dismiss the dialog
                    dialog.cancel();
                }
            });
            
            // Show the dialog
            builder.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteData", e);
            pesan("Error showing delete confirmation: " + e.getMessage());
        }
    }
    
    // Overloaded method to delete data with item name for better user feedback
    public void deleteData(String id, String itemName) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            // Create AlertDialog.Builder instance for delete confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            
            // Set the dialog title
            builder.setTitle("PERINGATAN");
            
            // Set the dialog message with item name
            builder.setMessage("Yakin akan menghapus item '" + itemName + "'?");
            
            // Add Positive Button (YA - Yes)
            builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Move the existing SQL delete command and related logic inside this button's onClick listener
                    try {
                        // Construct SQL DELETE statement
                        String sql = "DELETE FROM tblbarang WHERE id = '" + id + "'";
                        
                        Log.d(TAG, "Executing DELETE SQL: " + sql);
                        
                        // Call db.runSQL(sql) to execute the delete query
                        boolean result = db.runSQL(sql);
                        
                        // Display Toast message indicating success or failure of the deletion
                        if (result) {
                            pesan("Data '" + itemName + "' berhasil dihapus");
                            Log.d(TAG, "Delete successful for ID: " + id);
                            
                            // Refresh data after successful deletion
                            selectData();
                        } else {
                            pesan("Gagal menghapus data '" + itemName + "'");
                            Log.e(TAG, "Delete failed for ID: " + id);
                        }
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error in delete operation", e);
                        pesan("Error deleting data: " + e.getMessage());
                    }
                }
            });
            
            // Add Negative Button (TIDAK - No)
            builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Simply dismiss the dialog
                    dialog.cancel();
                }
            });
            
            // Show the dialog
            builder.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteData", e);
            pesan("Error showing delete confirmation: " + e.getMessage());
        }
    }
    
    // Method to update data in database
    public void updateData(String id, String newBarang, String newStok, String newHarga) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            // Input validation
            if (newBarang.trim().isEmpty() || newStok.trim().isEmpty() || newHarga.trim().isEmpty()) {
                pesan("Data tidak boleh kosong");
                return;
            }
            
            // Validate numeric inputs
            try {
                Integer.parseInt(newStok);
                Double.parseDouble(newHarga);
            } catch (NumberFormatException e) {
                pesan("Stok harus berupa angka bulat dan Harga harus berupa angka");
                return;
            }
            
            // Construct SQL UPDATE statement
            String sql = "UPDATE tblbarang SET barang = '" + newBarang + "', stok = " + newStok + ", harga = " + newHarga + " WHERE id = '" + id + "'";
            
            Log.d(TAG, "Executing UPDATE SQL: " + sql);
            
            // Call db.runSQL(sql) to execute the update query
            boolean result = db.runSQL(sql);
            
            // Implement if-else block to check the result of the update
            if (result) {
                pesan("Data sudah diubah");
                Log.d(TAG, "Update successful for ID: " + id);
                
                // Refresh data after successful update
                selectData();
            } else {
                pesan("Data tidak bisa diubah");
                Log.e(TAG, "Update failed for ID: " + id);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in updateData", e);
            pesan("Error updating data: " + e.getMessage());
        }
    }
    
    // Method to populate form for editing
    public void populateFormForUpdate(String id, String barang, String stok, String harga) {
        try {
            // Set the update mode
            updateId = id;
            
            // Populate EditText fields with current data
            etBarang.setText(barang);
            etStok.setText(stok);
            etHarga.setText(harga);
            
            // Change the label to indicate update mode
            tvPilihan.setText("✏️ Update Mode");
            
            pesan("Form ready for update. Click SIMPAN to save changes.");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in populateFormForUpdate", e);
            pesan("Error preparing form for update: " + e.getMessage());
        }
    }
    
    // Method to clear form and reset to insert mode
    public void clearForm() {
        try {
            // Reset to insert mode
            updateId = null;
            
            // Clear EditText fields
            etBarang.setText("");
            etStok.setText("");
            etHarga.setText("");
            
            // Reset label
            tvPilihan.setText("📝 Insert Mode");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in clearForm", e);
        }
    }
    
    // Method to execute SELECT with WHERE clause - Generic approach
    public void selectDataWhere(String whereClause) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            Log.d(TAG, "Selecting data with WHERE: " + whereClause);
            Cursor cursor = db.selectWhere(whereClause);
            
            processSelectResults(cursor, "Filtered data");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in selectDataWhere", e);
            pesan("Error filtering data: " + e.getMessage());
        }
    }
    
    // Method to search by item name - Practical example 1
    public void searchByItemName(String itemName) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            Log.d(TAG, "Searching items by name: " + itemName);
            Cursor cursor = db.searchByName(itemName);
            
            processSelectResults(cursor, "Search results for: " + itemName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in searchByItemName", e);
            pesan("Error searching data: " + e.getMessage());
        }
    }
    
    // Method to filter by stock range - Practical example 2
    public void filterByStock(int minStock, int maxStock) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            Log.d(TAG, "Filtering by stock range: " + minStock + " - " + maxStock);
            Cursor cursor = db.filterByStockRange(minStock, maxStock);
            
            processSelectResults(cursor, "Items with stock " + minStock + " - " + maxStock);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in filterByStock", e);
            pesan("Error filtering by stock: " + e.getMessage());
        }
    }
    
    // Method to filter by price range - Practical example 3
    public void filterByPrice(double minPrice, double maxPrice) {
        try {
            if (db == null) {
                Log.e(TAG, "Database is null");
                pesan("Database not initialized");
                return;
            }
            
            Log.d(TAG, "Filtering by price range: " + minPrice + " - " + maxPrice);
            Cursor cursor = db.filterByPriceRange(minPrice, maxPrice);
            
            processSelectResults(cursor, "Items with price " + minPrice + " - " + maxPrice);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in filterByPrice", e);
            pesan("Error filtering by price: " + e.getMessage());
        }
    }
    
    // Helper method to process SELECT results and display in RecyclerView
    private void processSelectResults(Cursor cursor, String operationDescription) {
        try {
            if (cursor != null) {
                Log.d(TAG, "Cursor obtained, count: " + cursor.getCount());
                
                if (dataBarang == null) {
                    dataBarang = new ArrayList<>();
                }
                
                if (adapter == null) {
                    adapter = new BarangAdapter(this, dataBarang);
                    if (rcvBarang != null) {
                        rcvBarang.setAdapter(adapter);
                    }
                }
                
                // Clear dataBarang before fetching new data
                dataBarang.clear();
                
                // Check if cursor count > 0; if not, display "Data Kosong"
                if (cursor.getCount() > 0) {
                    // Loop through the cursor using cursor.moveToNext()
                    cursor.moveToFirst();
                    do {
                        // Extract data from cursor using getColumnIndex with null checks
                        int idIndex = cursor.getColumnIndex("id");
                        int barangIndex = cursor.getColumnIndex("barang");
                        int stokIndex = cursor.getColumnIndex("stok");
                        int hargaIndex = cursor.getColumnIndex("harga");
                        
                        if (idIndex >= 0 && barangIndex >= 0 && stokIndex >= 0 && hargaIndex >= 0) {
                            String idBarang = cursor.getString(idIndex);
                            String barang = cursor.getString(barangIndex);
                            String stok = cursor.getString(stokIndex);
                            String harga = cursor.getString(hargaIndex);
                            
                            // Create new Barang object and add to dataBarang
                            Barang barangItem = new Barang(idBarang, barang, stok, harga);
                            dataBarang.add(barangItem);
                        }
                        
                    } while (cursor.moveToNext());
                    
                    // Notify adapter of data changes
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    
                    // Show RecyclerView and hide empty state
                    if (tvEmptyState != null && rcvBarang != null) {
                        tvEmptyState.setVisibility(View.GONE);
                        rcvBarang.setVisibility(View.VISIBLE);
                    }
                    pesan(operationDescription + ": " + dataBarang.size() + " items found");
                    
                } else {
                    // Display "Data Kosong" if cursor count is 0
                    if (tvEmptyState != null && rcvBarang != null) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rcvBarang.setVisibility(View.GONE);
                    }
                    pesan("No data found for: " + operationDescription);
                }
                
                cursor.close();
                
            } else {
                Log.e(TAG, "Cursor is null");
                pesan("Failed to retrieve filtered data");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in processSelectResults", e);
            pesan("Error processing results: " + e.getMessage());
        }
    }
    
    // Method to select data from database
    public void selectData() {
        try {
            Log.d(TAG, "=== SELECT DATA START ===");
            
            // STEP 1: Validate Database
            if (db == null) {
                Log.e(TAG, "Database is null - CRITICAL ERROR");
                pesan("Database not initialized");
                return;
            }
            Log.d(TAG, "✅ Database check: OK");
            
            // STEP 2: Validate and Fix Data List
            if (dataBarang == null) {
                Log.e(TAG, "DataBarang list is null - REINITIALIZING");
                dataBarang = new ArrayList<>();
            }
            dataBarang.clear(); // Always clear first
            Log.d(TAG, "✅ DataBarang list: READY, size after clear: " + dataBarang.size());
            
            // STEP 3: Validate and Fix RecyclerView
            if (rcvBarang == null) {
                Log.e(TAG, "RecyclerView is NULL - CRITICAL ERROR");
                rcvBarang = findViewById(R.id.recyclerView);
                if (rcvBarang == null) {
                    Log.e(TAG, "Cannot find RecyclerView even after findViewById");
                    pesan("UI Error: RecyclerView not found");
                    return;
                }
            }
            Log.d(TAG, "✅ RecyclerView check: OK");
            
            // STEP 4: Validate and Fix Adapter
            if (adapter == null) {
                Log.e(TAG, "Adapter is null - RECREATING");
                adapter = new BarangAdapter(this, dataBarang);
                rcvBarang.setAdapter(adapter);
                Log.d(TAG, "✅ Adapter RECREATED and set to RecyclerView");
            } else {
                Log.d(TAG, "✅ Adapter check: OK, current item count: " + adapter.getItemCount());
            }
            
            // STEP 5: Execute Database Query
            Log.d(TAG, "🔄 Executing SQL query");
            String sql = "SELECT * FROM tblbarang ORDER BY barang ASC";
            Cursor cursor = db.select(sql);
            
            if (cursor == null) {
                Log.e(TAG, "❌ Cursor is null - SQL query failed");
                pesan("Failed to retrieve data from database");
                showEmptyState();
                return;
            }
            
            int cursorCount = cursor.getCount();
            Log.d(TAG, "✅ Cursor obtained successfully, count: " + cursorCount);
            
            // STEP 6: Process Data
            if (cursorCount == 0) {
                Log.d(TAG, "📭 No records found in database");
                cursor.close();
                showEmptyState();
                pesan("Data Kosong");
                return;
            }
            
            // STEP 7: Process Records
            Log.d(TAG, "🔄 Processing " + cursorCount + " records");
            cursor.moveToFirst();
            int processedCount = 0;
            
            do {
                try {
                    // Get column indices
                    int idIndex = cursor.getColumnIndex("id");
                    int barangIndex = cursor.getColumnIndex("barang");
                    int stokIndex = cursor.getColumnIndex("stok");
                    int hargaIndex = cursor.getColumnIndex("harga");
                    
                    if (idIndex < 0 || barangIndex < 0 || stokIndex < 0 || hargaIndex < 0) {
                        Log.e(TAG, "❌ Invalid column indices - skipping record");
                        continue;
                    }
                    
                    // Extract data
                    String idBarang = cursor.getString(idIndex);
                    String barang = cursor.getString(barangIndex);
                    String stok = cursor.getString(stokIndex);
                    String harga = cursor.getString(hargaIndex);
                    
                    Log.d(TAG, "📦 Record " + processedCount + ": ID=" + idBarang + 
                           ", Barang=" + barang + ", Stok=" + stok + ", Harga=" + harga);
                    
                    // Create and add Barang object
                    Barang barangItem = new Barang(idBarang, barang, stok, harga);
                    dataBarang.add(barangItem);
                    processedCount++;
                    
                } catch (Exception e) {
                    Log.e(TAG, "❌ Error processing record " + processedCount, e);
                }
                
            } while (cursor.moveToNext());
            
            cursor.close();
            Log.d(TAG, "✅ Finished processing. Total records added: " + dataBarang.size());
            
            // STEP 8: Update UI
            if (dataBarang.size() > 0) {
                showRecyclerViewWithData();
            } else {
                showEmptyState();
            }
            
            Log.d(TAG, "=== SELECT DATA END ===");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ CRITICAL ERROR in selectData", e);
            pesan("Error loading data: " + e.getMessage());
            showEmptyState();
        }
    }
    
    // Helper method to show RecyclerView with data
    private void showRecyclerViewWithData() {
        try {
            Log.d(TAG, "🎯 Showing RecyclerView with " + dataBarang.size() + " items");
            
            // Ensure adapter is set and notify changes
            if (adapter == null) {
                Log.e(TAG, "❌ Adapter is still null - recreating");
                adapter = new BarangAdapter(this, dataBarang);
                rcvBarang.setAdapter(adapter);
            }
            
            // Force adapter refresh
            adapter.notifyDataSetChanged();
            Log.d(TAG, "✅ Adapter notified, item count: " + adapter.getItemCount());
            
            // Update UI visibility
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
            if (rcvBarang != null) rcvBarang.setVisibility(View.VISIBLE);
            
            // Force RecyclerView to refresh
            rcvBarang.post(new Runnable() {
                @Override
                public void run() {
                    rcvBarang.requestLayout();
                }
            });
            
            pesan("✅ Data loaded: " + dataBarang.size() + " items");
            Log.d(TAG, "✅ UI updated - RecyclerView visible with data");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in showRecyclerViewWithData", e);
        }
    }
    
    // Helper method to show empty state
    private void showEmptyState() {
        try {
            Log.d(TAG, "📭 Showing empty state");
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
            if (rcvBarang != null) rcvBarang.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in showEmptyState", e);
        }
    }
    
    // Method onClick untuk Button Search
    public void searchItems(View view) {
        try {
            String searchTerm = etSearch.getText().toString().trim();
            
            if (searchTerm.isEmpty()) {
                // Jika search kosong, tampilkan semua data
                selectData();
                pesan("Showing all items");
            } else {
                // Gunakan search by name
                searchByItemName(searchTerm);
                pesan("Searching for: " + searchTerm);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in searchItems", e);
            pesan("Search error: " + e.getMessage());
        }
    }
    
    // Method onClick untuk Button Filter Low Stock
    public void filterLowStock(View view) {
        try {
            // Filter barang dengan stok kurang dari 10
            filterByStock(0, 9);
            pesan("Showing low stock items (≤ 9)");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in filterLowStock", e);
            pesan("Filter error: " + e.getMessage());
        }
    }
    
    // Method onClick untuk Button Filter Expensive
    public void filterExpensive(View view) {
        try {
            // Filter barang dengan harga lebih dari 50000
            filterByPrice(50000, Integer.MAX_VALUE);
            pesan("Showing expensive items (> 50,000)");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in filterExpensive", e);
            pesan("Filter error: " + e.getMessage());
        }
    }
    
    // Method onClick untuk Button Show All
    public void showAllItems(View view) {
        try {
            // Tampilkan semua data
            selectData();
            // Clear search field
            etSearch.setText("");
            pesan("Showing all items");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in showAllItems", e);
            pesan("Error loading all items: " + e.getMessage());
        }
    }
    
    // Helper method to display messages
    private void pesan(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    // Method to open Debug Preferences Activity
    public void openDebugPreferences(View v) {
        try {
            Log.d(TAG, "Opening Debug Preferences Activity");
            Intent intent = new Intent(this, DebugPreferencesActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Debug Preferences Activity", e);
            pesan("Error opening debug activity: " + e.getMessage());
        }
    }
    
    // Debug method to test database directly
    public void testDatabase(View v) {
        try {
            StringBuilder results = new StringBuilder("🧪 COMPLETE DATABASE TEST:\n\n");
            
            // Test 1: Database connection
            results.append("1️⃣ DATABASE CONNECTION:\n");
            if (db == null) {
                results.append("❌ Database is NULL\n\n");
                pesan(results.toString());
                return;
            } else {
                results.append("✅ Database connected\n\n");
            }
            
            // Test 2: Table and data check
            results.append("2️⃣ TABLE & DATA CHECK:\n");
            try {
                String sql = "SELECT COUNT(*) FROM tblbarang";
                Cursor cursor = db.select(sql);
                if (cursor != null && cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    results.append("✅ Table exists, records: ").append(count).append("\n");
                    cursor.close();
                } else {
                    results.append("❌ Table query failed\n");
                }
            } catch (Exception e) {
                results.append("❌ Table error: ").append(e.getMessage()).append("\n");
            }
            results.append("\n");
            
            // Test 3: UI Components
            results.append("3️⃣ UI COMPONENTS:\n");
            results.append("RecyclerView: ").append(rcvBarang != null ? "✅ EXISTS" : "❌ NULL").append("\n");
            results.append("EmptyState: ").append(tvEmptyState != null ? "✅ EXISTS" : "❌ NULL").append("\n");
            results.append("DataBarang List: ").append(dataBarang != null ? "✅ EXISTS" : "❌ NULL");
            if (dataBarang != null) {
                results.append(" (size: ").append(dataBarang.size()).append(")");
            }
            results.append("\n");
            results.append("Adapter: ").append(adapter != null ? "✅ EXISTS" : "❌ NULL");
            if (adapter != null) {
                results.append(" (items: ").append(adapter.getItemCount()).append(")");
            }
            results.append("\n\n");
            
            // Test 4: RecyclerView Setup
            results.append("4️⃣ RECYCLERVIEW SETUP:\n");
            if (rcvBarang != null) {
                results.append("Has LayoutManager: ").append(rcvBarang.getLayoutManager() != null ? "✅ YES" : "❌ NO").append("\n");
                results.append("Has Adapter: ").append(rcvBarang.getAdapter() != null ? "✅ YES" : "❌ NO").append("\n");
                results.append("Visibility: ").append(rcvBarang.getVisibility() == View.VISIBLE ? "✅ VISIBLE" : "❌ HIDDEN").append("\n");
            } else {
                results.append("❌ RecyclerView is NULL - cannot check setup\n");
            }
            results.append("\n");
            
            // Test 5: Sample Data Query
            results.append("5️⃣ SAMPLE DATA:\n");
            try {
                String sql = "SELECT * FROM tblbarang LIMIT 3";
                Cursor cursor = db.select(sql);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        results.append("Sample records found:\n");
                        cursor.moveToFirst();
                        int count = 0;
                        do {
                            String id = cursor.getString(cursor.getColumnIndex("id"));
                            String barang = cursor.getString(cursor.getColumnIndex("barang"));
                            String stok = cursor.getString(cursor.getColumnIndex("stok"));
                            String harga = cursor.getString(cursor.getColumnIndex("harga"));
                            
                            results.append("  ").append(++count).append(". ").append(barang)
                                   .append(" (Stock: ").append(stok)
                                   .append(", Price: ").append(harga).append(")\n");
                        } while (cursor.moveToNext() && count < 3);
                    } else {
                        results.append("❌ No sample data found\n");
                    }
                    cursor.close();
                } else {
                    results.append("❌ Cannot query sample data\n");
                }
            } catch (Exception e) {
                results.append("❌ Sample data error: ").append(e.getMessage()).append("\n");
            }
            
            // Show results
            String resultString = results.toString();
            Log.d(TAG, resultString);
            
            // Show in AlertDialog for better readability
            new android.app.AlertDialog.Builder(this)
                    .setTitle("🧪 Database Test Results")
                    .setMessage(resultString)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Force Refresh", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            // Force complete refresh
                            adapter = null;
                            dataBarang = null;
                            selectData();
                        }
                    })
                    .show();
            
        } catch (Exception e) {
            Log.e(TAG, "Database test error", e);
            pesan("Test error: " + e.getMessage());
        }
    }
    
    // Method to force add sample data for testing
    public void addSampleData(View v) {
        try {
            // Add some sample data
            String[] sampleData = {
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('Laptop Gaming', 5, 15000000)",
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('Mouse Wireless', 25, 350000)",
                "INSERT INTO tblbarang (barang, stok, harga) VALUES ('Keyboard Mechanical', 15, 750000)"
            };
            
            int successCount = 0;
            for (String sql : sampleData) {
                if (db.runSQL(sql)) {
                    successCount++;
                }
            }
            
            pesan("Added " + successCount + " sample items");
            
            // Refresh data
            selectData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error adding sample data", e);
            pesan("Error adding sample data: " + e.getMessage());
        }
    }
}