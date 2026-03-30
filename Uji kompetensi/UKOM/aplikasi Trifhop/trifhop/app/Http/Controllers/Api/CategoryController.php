<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Category;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    /**
     * Get all categories with product count
     */
    public function index()
    {
        try {
            $categories = Category::withCount(['products as available_products_count' => function ($query) {
                $query->where('status', 'available');
            }])->get();

            return response()->json([
                'success' => true,
                'message' => 'Categories retrieved successfully',
                'data' => $categories,
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch categories',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Get single category with its products
     */
    public function show($id)
    {
        try {
            $category = Category::with(['products' => function ($query) {
                $query->where('status', 'available')->latest();
            }])->find($id);

            if (!$category) {
                return response()->json([
                    'success' => false,
                    'message' => 'Category not found',
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Category retrieved successfully',
                'data' => $category,
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch category',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
