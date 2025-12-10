// Import library Firebase
import firebase from 'firebase/app';
import 'firebase/database';

// Konfigurasi Firebase menggunakan environment variables
const firebaseConfig = {
  apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
  authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
  storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.REACT_APP_FIREBASE_APP_ID,
  databaseURL: process.env.REACT_APP_FIREBASE_DATABASE_URL
};

// Inisialisasi Firebase
const app = firebase.initializeApp(firebaseConfig);

// Mendapatkan referensi ke Firebase Realtime Database
const database = firebase.database();

// Ekspor database dan app untuk digunakan di komponen lain
export { database, app };
export default app;
