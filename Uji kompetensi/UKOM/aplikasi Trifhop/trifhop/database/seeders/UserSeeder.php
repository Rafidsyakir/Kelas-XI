<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    public function run(): void
    {
        // Admin User
        User::create([
            'name' => 'Admin Trifhop',
            'email' => 'admin@trifhop.com',
            'password' => Hash::make('admin123'),
            'role' => 'admin',
            'phone' => '081234567890',
            'address' => 'Jl. Admin No. 1, Jakarta',
        ]);

        // Customer User untuk testing
        User::create([
            'name' => 'Customer Test',
            'email' => 'customer@test.com',
            'password' => Hash::make('customer123'),
            'role' => 'customer',
            'phone' => '081234567891',
            'address' => 'Jl. Customer No. 2, Bandung',
        ]);
    }
}
