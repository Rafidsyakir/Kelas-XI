# 🛍️ TRIFHOP - E-Commerce Thrift Shop System

Sistem e-commerce lengkap untuk toko thrift dengan Web Admin Panel dan Aplikasi Mobile Android.

## 📋 DESKRIPSI SISTEM

Trifhop adalah platform jual beli pakaian thrift yang terdiri dari:

1. **Backend Laravel** - API & Web Admin Panel
2. **Frontend Android** - Aplikasi Mobile untuk customer
3. **Database MySQL** - Penyimpanan data

## ✨ FITUR UTAMA

### 🌐 Web Admin Panel

#### 1. Dashboard
- Total income dari penjualan
- Jumlah pakaian terjual
- Stok pakaian available
- Pending orders
- Sales analytics chart (30 hari)
- Recent transactions

#### 2. Inventory
- Daftar produk yang dijual
- Tambah produk baru
- Edit & hapus produk
- Upload gambar produk
- Filter by category & status

#### 3. Categories
- Daftar kategori pakaian
- Jumlah produk per kategori
- CRUD categories

#### 4. Customers
- Daftar customer (pengguna aplikasi)
- Total orders per customer
- Total pembelian
- Detail customer & order history

#### 5. Orders
- Daftar semua pesanan
- Invoice, customer, shipping address
- Update status order:
  - **Pending** - Pesanan baru
  - **Packed** - Sedang dikemas
  - **Sent** - Sudah dikirim
  - **Finished** - Selesai
- View detail pesanan

#### 6. Reports
- Total revenue
- Total orders
- Products sold
- Average order value
- Monthly sales trend
- Top selling products
- Recent transactions

### 📱 Aplikasi Android

#### 1. Authentication
- Login
- Register
- Logout

#### 2. Home
- Produk by category
- Scroll horizontal per category
- Lihat detail produk

#### 3. Explore
- Search produk by name
- Filter by category (Hoodie, Knitwear, T-Shirt, dll)
- Grid layout

#### 4. Cart
- List produk di cart
- Total price
- Remove item
- Checkout dengan shipping address

#### 5. Profile
- User information
- Current orders (pending, packed, sent)
- Past orders (finished)
- Track order by invoice
- Edit profile
- Change password

## 🔗 SINKRONISASI DATA

| Aplikasi Mobile | Web Admin Panel |
|-----------------|-----------------|
| Register user → | Customers |
| Browse products ← | Inventory |
| Checkout order → | Orders |
| Track order ← | Update status |
| Completed order → | Dashboard & Reports |

## 🚀 QUICK START

### 1. Setup Backend

```bash
cd trifhop
composer install
npm install
cp .env.example .env
# Edit .env (database config)
php artisan key:generate
php artisan migrate

# Buat admin user
php artisan tinker
# Lihat QUICK_START.md untuk detail
```

### 2. Jalankan Backend

```bash
# Terminal 1
php artisan serve

# Terminal 2
npm run dev
```

### 3. Login Web Admin

```
URL: http://localhost:8000
Email: admin@trifhop.com
Password: admin123
```

### 4. Setup Android

1. Buka "frontend apk" di Android Studio
2. Update BASE_URL di RetrofitClient.kt
3. Sync Gradle
4. Run aplikasi

Lihat **[QUICK_START.md](QUICK_START.md)** untuk panduan lengkap!

## 📚 DOKUMENTASI

- **[QUICK_START.md](QUICK_START.md)** - Panduan cepat memulai
- **[PANDUAN_LENGKAP.md](PANDUAN_LENGKAP.md)** - Tutorial lengkap setup & testing
- **[RINGKASAN_SISTEM.md](RINGKASAN_SISTEM.md)** - Overview sistem
- **[ANDROID_DOCUMENTATION.md](frontend%20apk/ANDROID_DOCUMENTATION.md)** - Dokumentasi Android
- **[STRUKTUR_FILE.md](STRUKTUR_FILE.md)** - Struktur file lengkap

## 🛠️ TEKNOLOGI

### Backend
- **Laravel 11** - PHP Framework
- **MySQL** - Database
- **Sanctum** - API Authentication
- **Tailwind CSS** - Styling

### Frontend
- **Kotlin** - Programming Language
- **Jetpack Compose** - UI Framework
- **Retrofit** - HTTP Client
- **MVVM** - Architecture Pattern
- **Coroutines** - Asynchronous Programming

## 📊 DATABASE SCHEMA

```
users
├── id
├── name
├── email
├── password
└── role (admin/customer)

categories
├── id
└── name

products
├── id
├── category_id (FK → categories)
├── name
├── price
├── description
├── condition
├── size
├── image_url
└── status (available/sold)

transactions
├── id
├── user_id (FK → users)
├── invoice_code
├── total_price
├── status (pending/packed/sent/finished)
└── shipping_address

transaction_details
├── id
├── transaction_id (FK → transactions)
├── product_id (FK → products)
└── price_at_purchase
```

## 🎯 USE CASE

### Scenario 1: Customer Membeli Produk

1. Customer register di aplikasi
2. Browse produk di Home
3. Search produk di Explore
4. Add to cart
5. Checkout dengan shipping address
6. Admin lihat pesanan di Orders (status: Pending)
7. Admin update status: Packed → Sent → Finished
8. Customer track order di aplikasi
9. Dashboard & Reports terupdate

### Scenario 2: Admin Mengelola Produk

1. Admin login ke web panel
2. Tambah produk baru di Inventory
3. Upload gambar & set price
4. Produk muncul di aplikasi
5. Customer beli produk
6. Status produk berubah: available → sold
7. Analytics di Dashboard update

## 📝 API ENDPOINTS

### Public
- `POST /api/register` - Register user
- `POST /api/login` - Login user
- `GET /api/products` - List products
- `GET /api/categories` - List categories

### Private (Auth Required)
- `POST /api/logout` - Logout
- `GET /api/user` - Get profile
- `POST /api/orders` - Checkout
- `GET /api/orders/current` - Current orders
- `GET /api/orders/past` - Past orders
- `GET /api/orders/track/{invoice}` - Track order

Lihat **[ANDROID_DOCUMENTATION.md](frontend%20apk/ANDROID_DOCUMENTATION.md)** untuk detail lengkap!

## 🧪 TESTING

### Manual Testing Checklist

Backend:
- [ ] Login web admin
- [ ] Tambah produk
- [ ] Tambah kategori
- [ ] Lihat customers
- [ ] Lihat orders
- [ ] Update status order
- [ ] Lihat dashboard
- [ ] Lihat reports

Frontend:
- [ ] Register account
- [ ] Login
- [ ] Browse products
- [ ] Search & filter
- [ ] Add to cart
- [ ] Checkout
- [ ] View current orders
- [ ] Track order
- [ ] View profile

Sinkronisasi:
- [ ] User register → muncul di Customers
- [ ] Checkout → muncul di Orders
- [ ] Update status di admin → update di app
- [ ] Order finished → analytics update

## 🐛 TROUBLESHOOTING

### Backend tidak jalan
```bash
php artisan cache:clear
php artisan config:clear
php artisan serve
```

### Aplikasi tidak connect
- Cek BASE_URL di RetrofitClient.kt
- Pastikan Laravel server running
- Test: `curl http://localhost:8000/api/products`

### Login gagal
```bash
php artisan tinker
# Reset password admin
```

Lihat **[PANDUAN_LENGKAP.md](PANDUAN_LENGKAP.md)** untuk troubleshooting lengkap!

## 👨‍💻 PENGEMBANGAN

### Struktur Project

```
trifhop/                    # Backend Laravel
├── app/
│   ├── Http/Controllers/
│   │   ├── Api/           # API Controllers
│   │   └── Admin/         # Web Admin Controllers
│   └── Models/            # Models
├── database/migrations/   # Database schema
├── resources/views/       # Web admin views
└── routes/
    ├── api.php           # API routes
    └── web.php           # Web routes

frontend apk/              # Aplikasi Android
└── app/src/main/java/com/example/trifhop/
    ├── data/             # Data layer (API, Models, Repository)
    ├── ui/               # UI layer (Screens, ViewModel)
    └── utils/            # Utilities
```

## 📄 LICENSE

Project ini dibuat untuk keperluan ujian kompetensi.

## 🤝 KONTRIBUTOR

- Backend Developer: System
- Frontend Developer: System
- Database Design: System
- Documentation: System

---

## 📞 SUPPORT

Jika ada pertanyaan atau masalah, lihat dokumentasi:
- [QUICK_START.md](QUICK_START.md)
- [PANDUAN_LENGKAP.md](PANDUAN_LENGKAP.md)
- [TROUBLESHOOTING section in PANDUAN_LENGKAP.md](PANDUAN_LENGKAP.md#troubleshooting)

---

**Dibuat dengan ❤️ untuk Ujian Kompetensi UKOM**

Sistem siap digunakan! 🚀
