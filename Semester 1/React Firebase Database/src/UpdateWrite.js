import React, { useState, useEffect } from 'react';
import { database } from './firebaseConfig';
import { ref, get, set } from 'firebase/database';
import { useParams, useNavigate } from 'react-router-dom';

function UpdateWrite() {
  // Ambil firebaseID dari URL parameter
  const { firebaseID } = useParams();
  const navigate = useNavigate();
  
  // State untuk input form
  const [input, setInput] = useState({
    fruitName: '',
    fruitDefinition: ''
  });
  
  // State untuk loading
  const [loading, setLoading] = useState(true);

  // useEffect untuk pre-fill data saat komponen dimuat
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Referensi ke record spesifik berdasarkan firebaseID
        const fruitRef = ref(database, `fruits/${firebaseID}`);
        
        // Ambil data spesifik
        const snapshot = await get(fruitRef);
        
        if (snapshot.exists()) {
          // Isi form dengan data yang ada
          const data = snapshot.val();
          setInput({
            fruitName: data.fruitName || '',
            fruitDefinition: data.fruitDefinition || ''
          });
        } else {
          alert('Data tidak ditemukan!');
          navigate('/update-read');
        }
      } catch (error) {
        console.error('Error mengambil data:', error);
        alert('Gagal mengambil data. Silakan coba lagi.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [firebaseID, navigate]);

  // Fungsi untuk menangani perubahan input
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setInput((prevInput) => ({
      ...prevInput,
      [name]: value
    }));
  };

  // Fungsi untuk menangani update data
  const handleUpdate = async (e) => {
    e.preventDefault();
    
    // Validasi input
    if (input.fruitName.trim() === '' || input.fruitDefinition.trim() === '') {
      alert('Semua field harus diisi!');
      return;
    }

    try {
      // Referensi ke record spesifik yang akan diupdate
      const fruitRef = ref(database, `fruits/${firebaseID}`);
      
      // Update data dengan set (menimpa data lama)
      await set(fruitRef, {
        fruitName: input.fruitName,
        fruitDefinition: input.fruitDefinition,
        updatedAt: new Date().toISOString()
      });
      
      alert('Data berhasil diperbarui!');
      console.log('Data berhasil diperbarui!');
      
      // Navigasi kembali ke halaman daftar
      navigate('/update-read');
    } catch (error) {
      console.error('Error memperbarui data:', error);
      alert('Gagal memperbarui data. Silakan coba lagi.');
    }
  };

  // Fungsi untuk kembali ke halaman daftar
  const handleCancel = () => {
    navigate('/update-read');
  };

  if (loading) {
    return (
      <div style={{ padding: '20px', textAlign: 'center' }}>
        <p>Memuat data...</p>
      </div>
    );
  }

  return (
    <div className="update-write-container" style={{ padding: '20px', maxWidth: '600px', margin: '0 auto' }}>
      <h1>Form Update Data Buah</h1>
      <p style={{ color: '#666', marginBottom: '20px' }}>
        ID Record: <strong>{firebaseID}</strong>
      </p>
      
      <form onSubmit={handleUpdate}>
        {/* Input Nama Buah */}
        <div style={{ marginBottom: '20px' }}>
          <label
            htmlFor="fruitName"
            style={{
              display: 'block',
              marginBottom: '8px',
              fontSize: '16px',
              fontWeight: 'bold'
            }}
          >
            Nama Buah:
          </label>
          <input
            type="text"
            id="fruitName"
            name="fruitName"
            value={input.fruitName}
            onChange={handleInputChange}
            placeholder="Masukkan nama buah..."
            style={{
              width: '100%',
              padding: '10px',
              fontSize: '16px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              boxSizing: 'border-box'
            }}
          />
        </div>

        {/* Input Definisi Buah */}
        <div style={{ marginBottom: '20px' }}>
          <label
            htmlFor="fruitDefinition"
            style={{
              display: 'block',
              marginBottom: '8px',
              fontSize: '16px',
              fontWeight: 'bold'
            }}
          >
            Definisi/Deskripsi:
          </label>
          <textarea
            id="fruitDefinition"
            name="fruitDefinition"
            value={input.fruitDefinition}
            onChange={handleInputChange}
            placeholder="Masukkan definisi atau deskripsi buah..."
            rows="5"
            style={{
              width: '100%',
              padding: '10px',
              fontSize: '16px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              boxSizing: 'border-box',
              resize: 'vertical'
            }}
          />
        </div>

        {/* Tombol Aksi */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            type="submit"
            style={{
              flex: 1,
              padding: '12px',
              fontSize: '16px',
              backgroundColor: '#4CAF50',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontWeight: 'bold'
            }}
          >
            Update Data
          </button>
          
          <button
            type="button"
            onClick={handleCancel}
            style={{
              flex: 1,
              padding: '12px',
              fontSize: '16px',
              backgroundColor: '#757575',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontWeight: 'bold'
            }}
          >
            Batal
          </button>
        </div>
      </form>
    </div>
  );
}

export default UpdateWrite;
