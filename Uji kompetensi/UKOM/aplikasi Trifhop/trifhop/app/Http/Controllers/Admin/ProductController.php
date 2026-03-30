<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Product;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class ProductController extends Controller
{
    public function index()
    {
        $products = Product::with('category')->latest()->paginate(20);
        
        // Stats untuk dashboard products
        $totalProducts = Product::count();
        $inStock = Product::where('stock', '>', 0)->count();
        $lowStock = Product::where('stock', '<=', 5)->where('stock', '>', 0)->count();
        $outOfStock = Product::where('stock', 0)->count();
        
        return view('admin.products.index', compact('products', 'totalProducts', 'inStock', 'lowStock', 'outOfStock'));
    }

    public function create()
    {
        $categories = Category::all();
        return view('admin.products.create', compact('categories'));
    }

    public function store(Request $request)
    {
        \Log::info('📝 ADMIN PRODUCT CREATE REQUEST', [
            'user_id' => $request->user()?->id,
            'has_file' => $request->hasFile('image'),
            'form_data' => $request->except('image'),
        ]);

        try {
            $validated = $request->validate([
                'category_id' => 'required|exists:categories,id',
                'name' => 'required|string|max:255',
                'price' => 'required|numeric|min:0',
                'description' => 'required|string',
                'condition' => 'nullable|string',
                'size' => 'nullable|string',
                'image' => 'required|image|mimes:jpeg,png,jpg,webp|max:2048',
                'stock' => 'required|integer|min:0',
            ]);

            // Set default values for new required fields if not provided
            if (empty($validated['condition'])) {
                $validated['condition'] = 'new';
            }
            if (empty($validated['size'])) {
                $validated['size'] = 'One Size';
            }

            \Log::info('✅ Admin product validation passed', ['validated' => $validated]);

            // Handle image upload
            if ($request->hasFile('image')) {
                $image = $request->file('image');
                $imageName = time() . '_' . $image->getClientOriginalName();
                
                if (!is_dir(public_path('images/products'))) {
                    mkdir(public_path('images/products'), 0755, true);
                    \Log::info('📁 Created images/products directory');
                }
                
                $image->move(public_path('images/products'), $imageName);
                $validated['image_url'] = '/images/products/' . $imageName;
                \Log::info('✅ Image uploaded', ['image_name' => $imageName]);
            }

            $validated['status'] = $validated['stock'] > 0 ? 'available' : 'sold';

            $product = Product::create($validated);
            \Log::info('✅ Product created successfully', ['product_id' => $product->id, 'name' => $product->name]);

            return redirect()->route('admin.products.index')
                ->with('success', "Product '{$product->name}' created successfully!");
        } catch (\Illuminate\Validation\ValidationException $e) {
            \Log::warning('⚠️  Validation error in product creation', ['errors' => $e->errors()]);
            return redirect()->back()
                ->withErrors($e->errors())
                ->withInput();
        } catch (\Exception $e) {
            \Log::error('❌ Error creating product', ['error' => $e->getMessage(), 'file' => $e->getFile()]);
            return redirect()->back()
                ->with('error', 'Failed to create product: ' . $e->getMessage())
                ->withInput();
        }
    }

    public function edit($id)
    {
        $product = Product::findOrFail($id);
        $categories = Category::all();
        return view('admin.products.edit', compact('product', 'categories'));
    }

    public function update(Request $request, $id)
    {
        try {
            $product = Product::findOrFail($id);
            \Log::info('📝 ADMIN PRODUCT UPDATE REQUEST', ['product_id' => $id, 'user_id' => $request->user()?->id]);

            $validated = $request->validate([
                'category_id' => 'required|exists:categories,id',
                'name' => 'required|string|max:255',
                'price' => 'required|numeric|min:0',
                'description' => 'required|string',
                'condition' => 'nullable|string',
                'size' => 'nullable|string',
                'image' => 'nullable|image|mimes:jpeg,png,jpg,webp|max:2048',
                'stock' => 'required|integer|min:0',
            ]);

            // Set default values for new required fields if not provided
            if (empty($validated['condition'])) {
                $validated['condition'] = 'new';
            }
            if (empty($validated['size'])) {
                $validated['size'] = 'One Size';
            }

            // Handle image upload
            if ($request->hasFile('image')) {
                // Delete old image if exists
                if ($product->image_url && file_exists(public_path($product->image_url))) {
                    unlink(public_path($product->image_url));
                    \Log::info('🗑️  Delete old image', ['image_url' => $product->image_url]);
                }

                $image = $request->file('image');
                $imageName = time() . '_' . $image->getClientOriginalName();
                $image->move(public_path('images/products'), $imageName);
                $validated['image_url'] = '/images/products/' . $imageName;
                \Log::info('✅ New image uploaded', ['image_name' => $imageName]);
            }

            $validated['status'] = $validated['stock'] > 0 ? 'available' : 'sold';

            $product->update($validated);
            \Log::info('✅ Product updated successfully', ['product_id' => $product->id]);

            return redirect()->route('admin.products.index')
                ->with('success', "Product '{$product->name}' updated successfully!");
        } catch (\Illuminate\Validation\ValidationException $e) {
            \Log::warning('⚠️  Validation error in product update', ['errors' => $e->errors()]);
            return redirect()->back()
                ->withErrors($e->errors())
                ->withInput();
        } catch (\Exception $e) {
            \Log::error('❌ Error updating product', ['error' => $e->getMessage(), 'file' => $e->getFile()]);
            return redirect()->back()
                ->with('error', 'Failed to update product: ' . $e->getMessage())
                ->withInput();
        }
    }

    public function destroy($id)
    {
        $product = Product::findOrFail($id);

        // Delete image if exists
        if ($product->image_url && file_exists(public_path($product->image_url))) {
            unlink(public_path($product->image_url));
        }

        $product->delete();

        return redirect()->route('admin.products.index')
            ->with('success', 'Product deleted successfully');
    }
}
