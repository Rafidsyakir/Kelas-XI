<?php

return [
    /*
    |--------------------------------------------------------------------------
    | Midtrans Configuration
    |--------------------------------------------------------------------------
    |
    | Configuration for Midtrans payment gateway integration
    | Supports both sandbox and production environments
    |
    */

    // Merchant ID
    'merchant_id' => env('MIDTRANS_MERCHANT_ID', 'M028507205'),

    // Server Key (used for server-to-server communication)
    'server_key' => env('MIDTRANS_SERVER_KEY'),

    // Client Key (used for frontend/JavaScript payment)
    'client_key' => env('MIDTRANS_CLIENT_KEY'),

    // Environment: 'sandbox' or 'production'
    'mode' => env('MIDTRANS_MODE', 'sandbox'),

    // Enable 3D Secure for additional fraud protection
    'enable_3d_secure' => env('MIDTRANS_ENABLE_3D_SECURE', true),

    // Redirect URLs after payment
    'success_redirect_url' => env('MIDTRANS_SUCCESS_REDIRECT_URL', 'https://checkout.trifhop/success'),
    'failure_redirect_url' => env('MIDTRANS_FAILURE_REDIRECT_URL', 'https://checkout.trifhop/failed'),

    // API endpoints
    'base_url_sandbox' => 'https://app.sandbox.midtrans.com',
    'base_url_production' => 'https://app.midtrans.com',

    // Snap API endpoint
    'snap_api_sandbox' => 'https://app.sandbox.midtrans.com/snap/v1',
    'snap_api_production' => 'https://app.midtrans.com/snap/v1',

    // Core API endpoint
    'core_api_sandbox' => 'https://api.sandbox.midtrans.com/v2',
    'core_api_production' => 'https://api.midtrans.com/v2',
];
