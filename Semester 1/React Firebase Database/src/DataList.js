import React, { useState, useEffect } from 'react';
import { database } from './firebaseConfig';
import { ref, onValue, push, set } from 'firebase/database';

function DataList() {
  // State untuk menyimpan daftar item
  const [items, setItems] = useState([]);
  
  // State untuk input pengguna
  const [newItemText, setNewItemText] = useState('');

  // useEffect untuk membaca data saat komponen dimuat
  useEffect(() => {
    // Referensi ke path /items di database
    const itemsRef = ref(database, 'items');

    // Pasang listener untuk mendengarkan perubahan data
    const unsubscribe = onValue(itemsRef, (snapshot) => {
      const data = snapshot.val();
      
      // Konversi objek menjadi array
      if (data) {
        const itemsArray = Object.keys(data).map((key) => ({
          id: key,
          ...data[key]
        }));
        setItems(itemsArray);
      } else {
        // Jika tidak ada data, set items sebagai array kosong
        setItems([]);
      }
    });

    // Cleanup: lepas listener saat komponen di-unmount
    return () => unsubscribe();
  }, []);

  // Fungsi untuk menambahkan item baru
  const handleAddItem = () => {
    // Validasi input tidak kosong
    if (newItemText.trim() === '') {
      alert('Mohon masukkan teks item!');
      return;
    }

    // Referensi ke path /items
    const itemsRef = ref(database, 'items');
    
    // Buat item baru dengan push untuk mendapatkan ID unik
    const newItemRef = push(itemsRef);
    
    // Data yang akan disimpan
    const newItem = {
      text: newItemText,
      timestamp: Date.now(),
      createdAt: new Date().toISOString()
    };

    // Simpan data ke database
    set(newItemRef, newItem)
      .then(() => {
        console.log('Item berhasil ditambahkan!');
        // Kosongkan input setelah berhasil
        setNewItemText('');
      })
      .catch((error) => {
        console.error('Error menambahkan item:', error);
        alert('Gagal menambahkan item. Silakan coba lagi.');
      });
  };

  // Fungsi untuk menangani perubahan input
  const handleInputChange = (e) => {
    setNewItemText(e.target.value);
  };

  // Fungsi untuk menangani Enter key
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleAddItem();
    }
  };

  return (
    <div className="data-list-container" style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <h1>Firebase Realtime Database - Data List</h1>
      
      {/* Form untuk menambahkan item baru */}
      <div className="add-item-form" style={{ marginBottom: '20px' }}>
        <input
          type="text"
          value={newItemText}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          placeholder="Masukkan teks item baru..."
          style={{
            padding: '10px',
            width: '70%',
            marginRight: '10px',
            fontSize: '16px',
            border: '1px solid #ccc',
            borderRadius: '4px'
          }}
        />
        <button
          onClick={handleAddItem}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            backgroundColor: '#4CAF50',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Tambah Item
        </button>
      </div>

      {/* Menampilkan daftar item */}
      <div className="items-list">
        <h2>Daftar Item ({items.length})</h2>
        {items.length === 0 ? (
          <p style={{ color: '#666', fontStyle: 'italic' }}>
            Belum ada data. Tambahkan item pertama Anda!
          </p>
        ) : (
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {items.map((item) => (
              <li
                key={item.id}
                style={{
                  padding: '15px',
                  marginBottom: '10px',
                  backgroundColor: '#f5f5f5',
                  borderRadius: '4px',
                  borderLeft: '4px solid #4CAF50'
                }}
              >
                <div style={{ fontSize: '16px', marginBottom: '5px' }}>
                  <strong>{item.text}</strong>
                </div>
                <div style={{ fontSize: '12px', color: '#666' }}>
                  Dibuat: {new Date(item.timestamp).toLocaleString('id-ID')}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default DataList;
