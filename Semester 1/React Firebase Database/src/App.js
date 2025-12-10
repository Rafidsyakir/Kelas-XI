import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import DataList from './DataList';
import UpdateRead from './UpdateRead';
import UpdateWrite from './UpdateWrite';

function App() {
  return (
    <Router>
      <div className="App">
        {/* Navigation Menu */}
        <nav style={{
          backgroundColor: '#333',
          padding: '15px',
          marginBottom: '20px'
        }}>
          <ul style={{
            listStyle: 'none',
            display: 'flex',
            gap: '20px',
            margin: 0,
            padding: 0
          }}>
            <li>
              <Link
                to="/"
                style={{
                  color: 'white',
                  textDecoration: 'none',
                  fontSize: '16px',
                  fontWeight: 'bold'
                }}
              >
                Home (Data List)
              </Link>
            </li>
            <li>
              <Link
                to="/update-read"
                style={{
                  color: 'white',
                  textDecoration: 'none',
                  fontSize: '16px',
                  fontWeight: 'bold'
                }}
              >
                Manage Fruits (RUD)
              </Link>
            </li>
          </ul>
        </nav>

        {/* Routes Configuration */}
        <Routes>
          {/* Route untuk DataList (Home) */}
          <Route path="/" element={<DataList />} />
          
          {/* Route untuk UpdateRead (Daftar data dengan Update & Delete) */}
          <Route path="/update-read" element={<UpdateRead />} />
          
          {/* Route untuk UpdateWrite (Form update dengan parameter dinamis) */}
          <Route path="/update-write/:firebaseID" element={<UpdateWrite />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
