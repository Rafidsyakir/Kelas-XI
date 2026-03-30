<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class CategoryController extends Controller
{
    public function index()
    {
        $categories = Category::withCount('products')->get();

        return view('admin.categories.index', compact('categories'));
    }

    public function create()
    {
        return view('admin.categories.create');
    }

    public function store(Request $request)
    {
        \Log::info('📝 ADMIN CATEGORY CREATE REQUEST', [
            'user_id' => $request->user()?->id,
            'form_data' => $request->all(),
        ]);

        try {
            $validated = $request->validate([
                'name' => 'required|string|max:255|unique:categories',
                'description' => 'nullable|string',
                'icon_url' => 'nullable|url',
            ]);

            $validated['slug'] = Str::slug($validated['name']);

            $category = Category::create($validated);
            \Log::info('✅ Category created successfully', ['category_id' => $category->id, 'name' => $category->name]);

            return redirect()->route('admin.categories.index')
                ->with('success', "Category '{$category->name}' created successfully!");
        } catch (\Illuminate\Validation\ValidationException $e) {
            \Log::warning('⚠️  Validation error in category creation', ['errors' => $e->errors()]);
            return redirect()->back()
                ->withErrors($e->errors())
                ->withInput();
        } catch (\Exception $e) {
            \Log::error('❌ Error creating category', ['error' => $e->getMessage()]);
            return redirect()->back()
                ->with('error', 'Failed to create category: ' . $e->getMessage())
                ->withInput();
        }
    }

    public function edit($id)
    {
        $category = Category::withCount('products')->findOrFail($id);
        return view('admin.categories.edit', compact('category'));
    }

    public function update(Request $request, $id)
    {
        $category = Category::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255|unique:categories,name,' . $id,
            'icon_url' => 'nullable|url',
        ]);

        $validated['slug'] = Str::slug($validated['name']);

        $category->update($validated);

        return redirect()->route('admin.categories.index')
            ->with('success', 'Category updated successfully');
    }

    public function destroy($id)
    {
        $category = Category::findOrFail($id);
        
        if ($category->products()->count() > 0) {
            return redirect()->route('admin.categories.index')
                ->with('error', 'Cannot delete category with products');
        }

        $category->delete();

        return redirect()->route('admin.categories.index')
            ->with('success', 'Category deleted successfully');
    }
}
