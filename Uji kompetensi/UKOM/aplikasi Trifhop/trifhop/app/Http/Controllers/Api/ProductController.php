<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Product;
use Illuminate\Http\Request;

class ProductController extends Controller
{
    /**
     * Get all products with filters
     */
    public function index(Request $request)
    {
        try {
            $query = Product::with('category');

            // Filter by category
            if ($request->has('category_id')) {
                $query->where('category_id', $request->category_id);
            }

            // Filter by status (default: available only)
            $status = $request->get('status', 'available');
            if ($status) {
                $query->where('status', $status);
            }

            // Search by name
            if ($request->has('search')) {
                $query->where('name', 'like', '%' . $request->search . '%');
            }

            // Filter by condition
            if ($request->has('condition')) {
                $query->where('condition', $request->condition);
            }

            // Filter by size
            if ($request->has('size')) {
                $query->where('size', $request->size);
            }

            // Price range filter
            if ($request->has('min_price')) {
                $query->where('price', '>=', $request->min_price);
            }
            if ($request->has('max_price')) {
                $query->where('price', '<=', $request->max_price);
            }

            // Sort
            $sortBy = $request->get('sort_by', 'created_at');
            $sortOrder = $request->get('sort_order', 'desc');
            $query->orderBy($sortBy, $sortOrder);

            // Get all products (untuk mobile app, tidak pakai pagination)
            $products = $query->get();

            return response()->json([
                'success' => true,
                'message' => 'Products retrieved successfully',
                'data' => $products,
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch products',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Get single product detail
     */
    public function show($id)
    {
        try {
            $product = Product::with('category')->find($id);

            if (!$product) {
                return response()->json([
                    'success' => false,
                    'message' => 'Product not found',
                ], 404);
            }

            return response()->json([
                'success' => true,
                'message' => 'Product retrieved successfully',
                'data' => $product,
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch product',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Get products by category slug
     */
    public function byCategory($categorySlug)
    {
        try {
            $products = Product::with('category')
                ->whereHas('category', function ($query) use ($categorySlug) {
                    $query->where('slug', $categorySlug);
                })
                ->where('status', 'available')
                ->latest()
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Products retrieved successfully',
                'data' => $products,
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch products',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
