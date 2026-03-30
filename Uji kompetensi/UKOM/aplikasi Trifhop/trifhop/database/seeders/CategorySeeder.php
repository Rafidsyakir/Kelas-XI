<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Category;
use Illuminate\Support\Str;

class CategorySeeder extends Seeder
{
    public function run(): void
    {
        $categories = [
            ['name' => 'Hoodie', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=hoodie'],
            ['name' => 'Knitwear', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=knitwear'],
            ['name' => 'Jacket', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=jacket'],
            ['name' => 'T-Shirt', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=tshirt'],
            ['name' => 'Jeans', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=jeans'],
            ['name' => 'Dress', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=dress'],
            ['name' => 'Skirt', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=skirt'],
            ['name' => 'Sweater', 'icon_url' => 'https://api.dicebear.com/7.x/shapes/svg?seed=sweater'],
        ];

        foreach ($categories as $category) {
            Category::create([
                'name' => $category['name'],
                'slug' => Str::slug($category['name']),
                'icon_url' => $category['icon_url'],
            ]);
        }
    }
}
