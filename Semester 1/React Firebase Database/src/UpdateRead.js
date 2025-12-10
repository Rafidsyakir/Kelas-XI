import React, { useState, useEffect } from 'react';
import { database } from './firebaseConfig';
import { ref, onValue, remove } from 'firebase/database';
import { useNavigate } from 'react-router-dom';

function UpdateRead() {
  // State untuk menyimpan daftar records
  const [records, setRecords] = useState([]);
  const navigate = useNavigate();

  // useEffect untuk membaca data saat komponen dimuat
  useEffect(() => {
    // Referensi ke path /fruits di database
    const fruitsRef = ref(database, 'fruits');

    // Pasang listener untuk mendengarkan perubahan data
    const unsubscribe = onValue(fruitsRef, (snapshot) => {
      const data = snapshot.val();
      
      // Konversi objek menjadi array dengan field fruitID
      if (data) {
        const recordsArray = Object.keys(data).map((key) => ({
          fruitID: key,  // Ekstrak ID unik Firebase sebagai fruitID
          ...data[key]   // Spread data record asli
        }));
        setRecords(recordsArray);
      } else {
        // Jika tidak ada data, set records sebagai array kosong
        setRecords([]);
      }
    });

    // Cleanup: lepas listener saat komponen di-unmount
    return () => unsubscribe();
  }, []);

  // Fungsi untuk menangani navigasi ke halaman update
  const handleUpdate = (fruitID) => {
    navigate(`/update-write/${fruitID}`);
  };

  // Fungsi untuk menghapus data
  const handleDelete = async (fruitID) => {
    // Konfirmasi sebelum menghapus
    const confirmDelete = window.confirm('Apakah Anda yakin ingin menghapus data ini?');
    
    if (confirmDelete) {
      try {
        // Referensi ke record spesifik yang akan dihapus
        const fruitRef = ref(database, `fruits/${fruitID}`);
        
        // Hapus data dari database
        await remove(fruitRef);
        
        console.log('Data berhasil dihapus!');
        
        // Reload halaman untuk memperbarui tampilan
        window.location.reload();
      } catch (error) {
        console.error('Error menghapus data:', error);
        alert('Gagal menghapus data. Silakan coba lagi.');
      }
    }
  };

  return (
    <div className="update-read-container" style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Daftar Data Buah (READ)</h1>
      
      {/* Menampilkan daftar records */}
      <div className="records-list">
        <h2>Total Records: {records.length}</h2>
        
        {records.length === 0 ? (
          <p style={{ color: '#666', fontStyle: 'italic' }}>
            Belum ada data. Tambahkan data buah pertama Anda!
          </p>
        ) : (
          <div>
            {records.map((item) => (
              <div
                key={item.fruitID}
                style={{
                  padding: '15px',
                  marginBottom: '15px',
                  backgroundColor: '#f9f9f9',
                  borderRadius: '8px',
                  border: '1px solid #ddd',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center'
                }}
              >
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>
                    Nama: {item.fruitName}
                  </div>
                  <div style={{ fontSize: '14px', color: '#555', marginBottom: '8px' }}>
                    Definisi: {item.fruitDefinition}
                  </div>
                  <div style={{ fontSize: '12px', color: '#999' }}>
                    ID: {item.fruitID}
                  </div>
                </div>
                
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button
                    onClick={() => handleUpdate(item.fruitID)}
                    style={{
                      padding: '8px 16px',
                      fontSize: '14px',
                      backgroundColor: '#2196F3',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontWeight: 'bold'
                    }}
                  >
                    Update
                  </button>
                  
                  <button
                    onClick={() => handleDelete(item.fruitID)}
                    style={{
                      padding: '8px 16px',
                      fontSize: '14px',
                      backgroundColor: '#f44336',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontWeight: 'bold'
                    }}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default UpdateRead;
