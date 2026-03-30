# PowerShell test script untuk order creation flow

$BASE_URL = "http://127.0.0.1:8000/api"
$TEST_EMAIL = "test$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
$TEST_PASSWORD = "password123"

Write-Host "=== Testing Complete Order & Checkout Flow ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Register user
Write-Host "1. Registering test user..." -ForegroundColor Yellow
$registerBody = @{
    name = "Test User"
    email = $TEST_EMAIL
    password = $TEST_PASSWORD
    password_confirmation = $TEST_PASSWORD
} | ConvertTo-Json

try {
    $registerRes = Invoke-WebRequest -Uri "$BASE_URL/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody `
        -UseBasicParsing
    
    $registerData = $registerRes.Content | ConvertFrom-Json
    $TOKEN = $registerData.data.token
    
    if ($TOKEN) {
        Write-Host "✓ User registered & token received" -ForegroundColor Green
        Write-Host "  Email: $TEST_EMAIL" -ForegroundColor Gray
        Write-Host "  Token: $($TOKEN.Substring(0, 30))..." -ForegroundColor Gray
    } else {
        Write-Host "✗ Failed to get token" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Step 2: Get products
Write-Host "2. Fetching available products..." -ForegroundColor Yellow
try {
    $productsRes = Invoke-WebRequest -Uri "$BASE_URL/products" `
        -Method Get `
        -UseBasicParsing
    
    $productsData = $productsRes.Content | ConvertFrom-Json
    $productId = $productsData.data[0].id
    $productName = $productsData.data[0].name
    
    Write-Host "✓ Got $($productsData.data.Count) products" -ForegroundColor Green
    Write-Host "  Using product: #$productId - $productName" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to fetch products: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Step 3: Create order
Write-Host "3. Creating order..." -ForegroundColor Yellow
$orderBody = @{
    products = @(
        @{
            id = $productId
            quantity = 1
        }
    )
    customer_name = "John Doe"
    customer_phone = "081234567890"
    shipping_address = "Jl. Test Street No. 123, Jakarta"
    shipping_fee = 10000
} | ConvertTo-Json

try {
    $orderRes = Invoke-WebRequest -Uri "$BASE_URL/orders" `
        -Method Post `
        -ContentType "application/json" `
        -Body $orderBody `
        -Headers @{ "Authorization" = "Bearer $TOKEN" } `
        -UseBasicParsing
    
    $orderData = $orderRes.Content | ConvertFrom-Json
    $orderId = $orderData.data.id
    $totalPrice = $orderData.data.total_price
    
    if ($orderId) {
        Write-Host "✓ Order created successfully" -ForegroundColor Green
        Write-Host "  Order ID: $orderId" -ForegroundColor Gray
        Write-Host "  Total Price: Rp $('{0:N0}' -f $totalPrice)" -ForegroundColor Gray
        Write-Host "  Status: $($orderData.data.status)" -ForegroundColor Gray
    } else {
        Write-Host "✗ Order ID not found in response" -ForegroundColor Red
        Write-Host "Response: $($orderRes.Content)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Order creation failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $streamReader.ReadToEnd()
        Write-Host "Response: $errorBody" -ForegroundColor Red
    }
    exit 1
}

Write-Host ""

# Step 4: Create checkout/invoice
Write-Host "4. Creating payment invoice..." -ForegroundColor Yellow
$checkoutBody = @{
    order_id = $orderId
} | ConvertTo-Json

try {
    $checkoutRes = Invoke-WebRequest -Uri "$BASE_URL/checkout" `
        -Method Post `
        -ContentType "application/json" `
        -Body $checkoutBody `
        -Headers @{ "Authorization" = "Bearer $TOKEN" } `
        -UseBasicParsing
    
    $checkoutData = $checkoutRes.Content | ConvertFrom-Json
    $invoiceUrl = $checkoutData.data.invoice_url
    $invoiceStatus = $checkoutData.data.invoice_status
    
    if ($invoiceUrl) {
        Write-Host "✓ Invoice created successfully" -ForegroundColor Green
        Write-Host "  Invoice URL: $invoiceUrl" -ForegroundColor Gray
        Write-Host "  Invoice Status: $invoiceStatus" -ForegroundColor Gray
        Write-Host "  Invoice ID: $($checkoutData.data.invoice_id)" -ForegroundColor Gray
    } else {
        Write-Host "✗ Invoice URL not found" -ForegroundColor Red
        Write-Host "Response: $($checkoutRes.Content)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Checkout failed" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $streamReader.ReadToEnd()
        Write-Host "Response: $errorBody" -ForegroundColor Red
    }
    exit 1
}

Write-Host ""
Write-Host "=== ✓ SUCCESS ===" -ForegroundColor Green
Write-Host "Complete flow tested successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  1. User registered: $TEST_EMAIL" -ForegroundColor Gray
Write-Host "  2. Order #$orderId created" -ForegroundColor Gray
Write-Host "  3. Payment ready at:" -ForegroundColor Gray
Write-Host "     $invoiceUrl" -ForegroundColor Cyan
