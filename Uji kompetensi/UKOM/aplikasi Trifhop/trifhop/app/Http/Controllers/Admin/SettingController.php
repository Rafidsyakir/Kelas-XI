<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Setting;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class SettingController extends Controller
{
    /**
     * Show settings page
     */
    public function index()
    {
        $appName = Setting::get('app_name', 'Trifhop');
        $appDescription = Setting::get('app_description', '');
        $logoPath = Setting::get('app_logo');
        
        return view('admin.settings.index', compact('appName', 'appDescription', 'logoPath'));
    }

    /**
     * Update setting
     */
    public function update(Request $request)
    {
        try {
            // Validate input
            $validated = $request->validate([
                'app_name' => 'required|string|max:255',
                'app_description' => 'nullable|string|max:1000',
                'app_logo' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:5120', // 5MB
            ]);

            // Handle logo upload
            if ($request->hasFile('app_logo')) {
                // Delete old logo if exists
                $oldLogo = Setting::get('app_logo');
                if ($oldLogo && Storage::disk('public')->exists($oldLogo)) {
                    Storage::disk('public')->delete($oldLogo);
                }

                // Store new logo
                $logoPath = $request->file('app_logo')->store('logos', 'public');
                Setting::set('app_logo', $logoPath, 'image', 'Application Logo');
            }

            // Update app name
            Setting::set('app_name', $validated['app_name'], 'text', 'Application Name');

            // Update app description
            Setting::set('app_description', $validated['app_description'] ?? '', 'text', 'Application Description');

            return redirect()->route('admin.settings.index')->with('success', 'Pengaturan berhasil diperbarui!');
        } catch (\Throwable $e) {
            return redirect()->route('admin.settings.index')->with('error', 'Gagal memperbarui pengaturan: ' . $e->getMessage());
        }
    }

    /**
     * Delete logo
     */
    public function deleteLogo()
    {
        try {
            $logoPath = Setting::get('app_logo');
            if ($logoPath && Storage::disk('public')->exists($logoPath)) {
                Storage::disk('public')->delete($logoPath);
            }
            Setting::set('app_logo', null, 'image', 'Application Logo');

            return redirect()->route('admin.settings.index')->with('success', 'Logo berhasil dihapus!');
        } catch (\Throwable $e) {
            return redirect()->route('admin.settings.index')->with('error', 'Gagal menghapus logo: ' . $e->getMessage());
        }
    }
}
