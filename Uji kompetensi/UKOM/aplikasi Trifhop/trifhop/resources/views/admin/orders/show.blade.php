@extends('layouts.admin')

@section('title', 'Order Detail - #' . $order->invoice_code)

@section('content')
<div class="flex flex-wrap justify-between items-center gap-4 mb-8">
    <div class="flex items-center gap-3">
        <a href="{{ route('admin.orders.index') }}" class="p-2 rounded-lg bg-gray-800 border border-border-dark text-slate-400 hover:text-white hover:bg-gray-700 transition-colors">
            <span class="material-symbols-outlined text-xl leading-none">arrow_back</span>
        </a>
        <div>
            <h1 class="text-white text-2xl font-extrabold tracking-tight">Order Detail</h1>
            <p class="text-slate-400 text-sm">Invoice <span class="text-primary font-bold">#{{ $order->invoice_code }}</span></p>
        </div>
    </div>

    {{-- Update Status Form --}}
    @if(!in_array($order->status, ['finished', 'cancelled']))
    <form action="{{ route('admin.orders.update-status', $order) }}" method="POST" class="flex items-center gap-3">
        @csrf
        <select name="status" class="rounded-lg bg-gray-800 border border-border-dark text-slate-300 text-sm px-3 py-2 focus:outline-none focus:border-primary transition-colors">
            <option value="pending"   {{ $order->status === 'pending'   ? 'selected' : '' }}>Pending</option>
            <option value="packed"    {{ $order->status === 'packed'    ? 'selected' : '' }}>Packed</option>
            <option value="sent"      {{ $order->status === 'sent'      ? 'selected' : '' }}>Sent</option>
            <option value="finished"  {{ $order->status === 'finished'  ? 'selected' : '' }}>Finished</option>
        </select>
        <button type="submit" class="flex items-center gap-2 rounded-lg h-9 px-4 bg-primary text-white text-sm font-semibold hover:bg-blue-600 transition-colors shadow-lg shadow-primary/20">
            <span class="material-symbols-outlined text-base leading-none">sync</span>
            Update Status
        </button>
    </form>
    @endif
</div>

@if(session('success'))
<div class="mb-6 px-4 py-3 rounded-lg bg-emerald-950/40 border border-emerald-500/30 text-emerald-400 text-sm flex items-center gap-2">
    <span class="material-symbols-outlined text-base">check_circle</span>
    {{ session('success') }}
</div>
@endif

<div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

    {{-- Left Column: Order Items --}}
    <div class="lg:col-span-2 flex flex-col gap-6">

        {{-- Order Items --}}
        <div class="bg-surface-dark rounded-xl border border-slate-800 overflow-hidden">
            <div class="px-6 py-4 border-b border-border-dark flex items-center gap-3">
                <span class="material-symbols-outlined text-primary">shopping_bag</span>
                <h2 class="text-white font-bold text-base">Order Items</h2>
                <span class="ml-auto text-xs text-slate-500 bg-gray-800 border border-border-dark px-2 py-0.5 rounded-full">{{ $order->details->count() }} item(s)</span>
            </div>
            <div class="divide-y divide-border-dark/50">
                @forelse($order->details as $detail)
                <div class="px-6 py-4 flex items-center gap-4">
                    {{-- Product Image --}}
                    <div class="size-16 rounded-lg bg-gray-800 border border-border-dark overflow-hidden flex-shrink-0">
                        @if($detail->product && $detail->product->image)
                            <img src="{{ asset('storage/' . $detail->product->image) }}" alt="{{ $detail->product->name }}" class="w-full h-full object-cover">
                        @else
                            <div class="w-full h-full flex items-center justify-center text-slate-600">
                                <span class="material-symbols-outlined text-2xl">checkroom</span>
                            </div>
                        @endif
                    </div>
                    {{-- Product Info --}}
                    <div class="flex-1 min-w-0">
                        <p class="text-slate-100 font-semibold text-sm truncate">{{ $detail->product->name ?? 'Product Deleted' }}</p>
                        <p class="text-slate-500 text-xs mt-0.5">Qty: {{ $detail->quantity }}</p>
                        <p class="text-slate-400 text-xs font-mono mt-0.5">Rp {{ number_format($detail->price_at_purchase, 0, ',', '.') }} / item</p>
                    </div>
                    {{-- Subtotal --}}
                    <div class="text-right flex-shrink-0">
                        <p class="text-white font-bold font-mono">Rp {{ number_format($detail->subtotal, 0, ',', '.') }}</p>
                    </div>
                </div>
                @empty
                <div class="px-6 py-10 text-center text-slate-500">
                    <span class="material-symbols-outlined text-4xl mb-2 block">remove_shopping_cart</span>
                    <p>No items found</p>
                </div>
                @endforelse
            </div>
            {{-- Total --}}
            <div class="px-6 py-4 bg-gray-900/60 border-t border-border-dark flex justify-between items-center">
                <span class="text-slate-400 font-medium text-sm">Total Order</span>
                <span class="text-white font-extrabold font-mono text-lg">Rp {{ number_format($order->total_price, 0, ',', '.') }}</span>
            </div>
        </div>

        {{-- Order Timeline --}}
        <div class="bg-surface-dark rounded-xl border border-slate-800 overflow-hidden">
            <div class="px-6 py-4 border-b border-border-dark flex items-center gap-3">
                <span class="material-symbols-outlined text-primary">timeline</span>
                <h2 class="text-white font-bold text-base">Order Timeline</h2>
            </div>
            <div class="px-6 py-5 flex flex-col gap-4">
                @php
                    $steps = [
                        ['key' => 'ordered', 'label' => 'Order Placed',  'icon' => 'receipt', 'time' => $order->ordered_at ?? $order->created_at],
                        ['key' => 'packed',  'label' => 'Packed',        'icon' => 'inventory_2', 'time' => $order->packed_at],
                        ['key' => 'sent',    'label' => 'Shipped',       'icon' => 'local_shipping', 'time' => $order->sent_at],
                        ['key' => 'finished','label' => 'Delivered',     'icon' => 'verified', 'time' => $order->finished_at],
                    ];
                    $statusOrder = ['pending' => 0, 'packed' => 1, 'sent' => 2, 'finished' => 3];
                    $currentStep = $statusOrder[$order->status] ?? 0;
                @endphp
                @foreach($steps as $i => $step)
                <div class="flex items-start gap-4">
                    <div class="flex flex-col items-center">
                        <div class="size-9 rounded-full flex items-center justify-center border-2 {{ $i <= $currentStep ? 'bg-primary/20 border-primary text-primary' : 'bg-gray-800 border-border-dark text-slate-600' }} transition-colors">
                            <span class="material-symbols-outlined text-base leading-none">{{ $step['icon'] }}</span>
                        </div>
                        @if(!$loop->last)
                            <div class="w-0.5 h-6 mt-1 {{ $i < $currentStep ? 'bg-primary/40' : 'bg-border-dark' }}"></div>
                        @endif
                    </div>
                    <div class="pb-1 pt-1">
                        <p class="text-sm font-semibold {{ $i <= $currentStep ? 'text-slate-100' : 'text-slate-600' }}">{{ $step['label'] }}</p>
                        <p class="text-xs text-slate-500 mt-0.5">
                            {{ $step['time'] ? \Carbon\Carbon::parse($step['time'])->format('d M Y, H:i') : '-' }}
                        </p>
                    </div>
                </div>
                @endforeach
            </div>
        </div>

    </div>

    {{-- Right Column: Customer & Order Info --}}
    <div class="flex flex-col gap-6">

        {{-- Status Badge --}}
        <div class="bg-surface-dark rounded-xl border border-slate-800 px-6 py-5 flex items-center gap-4">
            <div class="flex-1">
                <p class="text-xs text-slate-500 uppercase font-bold tracking-wider mb-2">Current Status</p>
                @if($order->status === 'finished')
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-emerald-950/40 text-emerald-400 border border-emerald-500/30">
                        <span class="size-2 rounded-full bg-emerald-400"></span> Completed
                    </span>
                @elseif($order->status === 'pending')
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-amber-950/40 text-amber-400 border border-amber-500/30">
                        <span class="size-2 rounded-full bg-amber-400 animate-pulse"></span> Pending
                    </span>
                @elseif($order->status === 'packed')
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-blue-950/40 text-blue-400 border border-blue-500/30">
                        <span class="size-2 rounded-full bg-blue-400"></span> Packed
                    </span>
                @elseif($order->status === 'sent')
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-sky-950/40 text-sky-400 border border-sky-500/30">
                        <span class="size-2 rounded-full bg-sky-400"></span> Shipped
                    </span>
                @elseif($order->status === 'cancelled')
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-red-950/40 text-red-400 border border-red-500/30">
                        <span class="size-2 rounded-full bg-red-400"></span> Cancelled
                    </span>
                @else
                    <span class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-bold bg-slate-800 text-slate-300 border border-border-dark">
                        {{ ucfirst($order->status) }}
                    </span>
                @endif
            </div>
            <span class="material-symbols-outlined text-4xl text-slate-700">receipt_long</span>
        </div>

        {{-- Customer Info --}}
        <div class="bg-surface-dark rounded-xl border border-slate-800 overflow-hidden">
            <div class="px-6 py-4 border-b border-border-dark flex items-center gap-3">
                <span class="material-symbols-outlined text-primary">person</span>
                <h2 class="text-white font-bold text-base">Customer</h2>
            </div>
            <div class="px-6 py-5 flex flex-col gap-3">
                <div class="flex items-center gap-3">
                    <div class="size-10 rounded-full bg-primary/20 text-primary flex items-center justify-center text-sm font-bold border border-primary/30">
                        {{ strtoupper(substr($order->customer_name ?? $order->user->name ?? 'U', 0, 2)) }}
                    </div>
                    <div>
                        <p class="text-slate-100 font-semibold text-sm">{{ $order->customer_name ?? $order->user->name ?? 'Unknown' }}</p>
                        <p class="text-slate-500 text-xs">{{ $order->user->email ?? '-' }}</p>
                    </div>
                </div>
                @if($order->customer_phone)
                <div class="flex items-center gap-2 text-sm text-slate-400">
                    <span class="material-symbols-outlined text-base text-slate-600">phone</span>
                    {{ $order->customer_phone }}
                </div>
                @endif
                @if($order->shipping_address)
                <div class="flex items-start gap-2 text-sm text-slate-400">
                    <span class="material-symbols-outlined text-base text-slate-600 mt-0.5">location_on</span>
                    <span>{{ $order->shipping_address }}</span>
                </div>
                @endif
            </div>
        </div>

        {{-- Order Info --}}
        <div class="bg-surface-dark rounded-xl border border-slate-800 overflow-hidden">
            <div class="px-6 py-4 border-b border-border-dark flex items-center gap-3">
                <span class="material-symbols-outlined text-primary">info</span>
                <h2 class="text-white font-bold text-base">Order Info</h2>
            </div>
            <dl class="divide-y divide-border-dark/50">
                <div class="px-6 py-3 flex justify-between gap-4">
                    <dt class="text-xs text-slate-500 uppercase tracking-wide font-semibold">Invoice</dt>
                    <dd class="text-primary font-bold text-sm">#{{ $order->invoice_code }}</dd>
                </div>
                <div class="px-6 py-3 flex justify-between gap-4">
                    <dt class="text-xs text-slate-500 uppercase tracking-wide font-semibold">Date</dt>
                    <dd class="text-slate-300 text-sm">{{ \Carbon\Carbon::parse($order->ordered_at ?? $order->created_at)->format('d M Y') }}</dd>
                </div>
                <div class="px-6 py-3 flex justify-between gap-4">
                    <dt class="text-xs text-slate-500 uppercase tracking-wide font-semibold">Items</dt>
                    <dd class="text-slate-300 text-sm">{{ $order->details->count() }}</dd>
                </div>
                <div class="px-6 py-3 flex justify-between gap-4">
                    <dt class="text-xs text-slate-500 uppercase tracking-wide font-semibold">Total</dt>
                    <dd class="text-white font-bold font-mono text-sm">Rp {{ number_format($order->total_price, 0, ',', '.') }}</dd>
                </div>
            </dl>
        </div>

    </div>
</div>
@endsection
