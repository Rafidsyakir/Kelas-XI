// ========================================
// E-Commerce Use Case - Order Service
// ========================================
// File: services/order.service.js
// 
// Skenario: 
// 1. Pembeli menyelesaikan pembayaran
// 2. Backend memproses payment
// 3. Seller menerima notifikasi FCM
// 4. Seller update status pesanan
// 5. Buyer menerima notifikasi update status

const fcmService = require('./fcm.service');

class OrderService {
  
  /**
   * STEP 1: Process payment dan kirim notifikasi ke seller
   * 
   * Flow:
   * 1. Update order status menjadi 'paid'
   * 2. Get seller FCM token from database
   * 3. Send notification ke seller
   * 4. Log notification history
   * 
   * @param {number} orderId - Order ID
   * @param {object} paymentData - Payment details
   * @returns {Promise<object>} Result dengan status notification
   */
  async processPayment(orderId, paymentData) {
    try {
      console.log(`\n📦 Processing payment for Order #${orderId}...`);
      
      // 1. Update order status menjadi 'paid'
      // In production: use actual database query
      await this.updateOrderStatus(orderId, 'paid');
      console.log('✅ Order status updated to: paid');
      
      // 2. Get order details dengan seller info dari database
      const order = await this.getOrderWithSellerInfo(orderId);
      
      if (!order) {
        throw new Error(`Order #${orderId} not found`);
      }
      
      console.log(`📋 Order Details:`);
      console.log(`   - Product: ${order.product_name}`);
      console.log(`   - Amount: Rp ${this.formatCurrency(order.amount)}`);
      console.log(`   - Buyer: ${order.buyer_name}`);
      console.log(`   - Seller: ${order.seller_name}`);
      
      // 3. Validasi seller memiliki FCM token
      if (!order.seller_fcm_token) {
        console.warn(`⚠️ Seller ${order.seller_name} doesn't have FCM token`);
        return {
          success: true,
          message: 'Payment processed but notification not sent (no FCM token)',
          order_id: orderId,
          seller_notified: false
        };
      }
      
      // 4. Kirim notifikasi ke seller
      const notificationTitle = '💰 Pembayaran Diterima!';
      const notificationBody = `${order.buyer_name} telah membayar pesanan "${order.product_name}" sebesar Rp ${this.formatCurrency(order.amount)}`;
      
      const notificationData = {
        type: 'payment_received',
        order_id: String(order.id),
        buyer_id: String(order.buyer_id),
        buyer_name: order.buyer_name,
        product_name: order.product_name,
        amount: String(order.amount),
        payment_method: paymentData.payment_method || 'unknown',
        action: 'open_order_details',
        timestamp: String(Date.now())
      };
      
      console.log(`\n📤 Sending notification to seller...`);
      
      await fcmService.sendTargetedNotification(
        order.seller_fcm_token,
        notificationTitle,
        notificationBody,
        notificationData
      );
      
      console.log(`✅ Payment notification sent to seller: ${order.seller_name}`);
      
      // 5. Optional: Log notification history untuk tracking
      await this.logNotification({
        user_id: order.seller_id,
        order_id: orderId,
        type: 'payment_received',
        title: notificationTitle,
        body: notificationBody,
        sent_at: new Date()
      });
      
      return {
        success: true,
        message: 'Payment processed and seller notified',
        order_id: orderId,
        seller_notified: true,
        seller_name: order.seller_name
      };
      
    } catch (error) {
      console.error('❌ Error processing payment:', error);
      throw error;
    }
  }
  
  /**
   * STEP 2: Seller update status pesanan, buyer menerima notifikasi
   * 
   * @param {number} orderId - Order ID
   * @param {string} newStatus - New order status (processing, shipped, completed)
   * @param {number} sellerId - Seller ID yang melakukan update
   * @returns {Promise<object>} Result
   */
  async updateOrderStatusAndNotifyBuyer(orderId, newStatus, sellerId) {
    try {
      console.log(`\n📦 Updating order status for Order #${orderId}...`);
      
      // 1. Validate seller authorization
      const order = await this.getOrderWithBuyerInfo(orderId);
      
      if (!order) {
        throw new Error(`Order #${orderId} not found`);
      }
      
      if (order.seller_id !== sellerId) {
        throw new Error('Unauthorized: Seller can only update their own orders');
      }
      
      // 2. Update order status
      await this.updateOrderStatus(orderId, newStatus);
      console.log(`✅ Order status updated to: ${newStatus}`);
      
      // 3. Validasi buyer memiliki FCM token
      if (!order.buyer_fcm_token) {
        console.warn(`⚠️ Buyer ${order.buyer_name} doesn't have FCM token`);
        return {
          success: true,
          message: 'Status updated but notification not sent',
          order_id: orderId,
          buyer_notified: false
        };
      }
      
      // 4. Prepare notification berdasarkan status
      const statusConfig = this.getStatusNotificationConfig(newStatus);
      
      const title = statusConfig.title;
      const body = `Pesanan "${order.product_name}" dari ${order.seller_name} - ${statusConfig.message}`;
      
      const data = {
        type: 'order_status_update',
        order_id: String(orderId),
        status: newStatus,
        product_name: order.product_name,
        seller_name: order.seller_name,
        action: 'open_order_tracking',
        timestamp: String(Date.now())
      };
      
      console.log(`\n📤 Sending status update notification to buyer...`);
      
      // 5. Kirim notifikasi ke buyer
      await fcmService.sendTargetedNotification(
        order.buyer_fcm_token,
        title,
        body,
        data
      );
      
      console.log(`✅ Status update notification sent to buyer: ${order.buyer_name}`);
      
      // 6. Log notification
      await this.logNotification({
        user_id: order.buyer_id,
        order_id: orderId,
        type: 'order_status_update',
        title: title,
        body: body,
        sent_at: new Date()
      });
      
      return {
        success: true,
        message: 'Status updated and buyer notified',
        order_id: orderId,
        new_status: newStatus,
        buyer_notified: true
      };
      
    } catch (error) {
      console.error('❌ Error updating order status:', error);
      throw error;
    }
  }
  
  /**
   * BONUS: Kirim notifikasi broadcast ke semua sellers
   * Use case: Flash sale, promo, announcement, dll
   * 
   * @param {string} title - Notification title
   * @param {string} body - Notification body
   * @param {object} data - Data payload
   * @returns {Promise<object>} Result dengan count
   */
  async notifyAllSellers(title, body, data = {}) {
    try {
      console.log(`\n📢 Broadcasting notification to all sellers...`);
      
      // 1. Get all active seller FCM tokens dari database
      const sellers = await this.getAllActiveSellers();
      
      if (!sellers || sellers.length === 0) {
        console.warn('⚠️ No active sellers with FCM tokens found');
        return { 
          success: false, 
          message: 'No sellers to notify',
          sellers_notified: 0
        };
      }
      
      console.log(`📋 Found ${sellers.length} sellers with FCM tokens`);
      
      // 2. Extract FCM tokens
      const fcmTokens = sellers.map(s => s.fcm_token).filter(t => t);
      
      if (fcmTokens.length === 0) {
        return {
          success: false,
          message: 'No valid FCM tokens',
          sellers_notified: 0
        };
      }
      
      // 3. Send multicast notification
      const result = await fcmService.sendMulticastNotification(
        fcmTokens,
        title,
        body,
        {
          type: 'seller_broadcast',
          broadcast_type: data.broadcast_type || 'general',
          ...data
        }
      );
      
      console.log(`✅ Broadcast notification sent to ${result.successCount} sellers`);
      
      if (result.failureCount > 0) {
        console.warn(`⚠️ Failed to send to ${result.failureCount} sellers`);
      }
      
      return {
        success: true,
        message: `Notification sent to ${result.successCount} sellers`,
        sellers_notified: result.successCount,
        failed: result.failureCount,
        total_sellers: sellers.length
      };
      
    } catch (error) {
      console.error('❌ Error broadcasting to sellers:', error);
      throw error;
    }
  }
  
  /**
   * BONUS: Send reminder notification untuk unpaid orders
   * Run this as cron job (e.g., every 1 hour)
   */
  async sendPaymentReminderForUnpaidOrders() {
    try {
      console.log(`\n⏰ Checking for unpaid orders...`);
      
      // Get orders yang belum dibayar > 1 jam
      const unpaidOrders = await this.getUnpaidOrders(60); // 60 minutes
      
      if (unpaidOrders.length === 0) {
        console.log('✅ No unpaid orders found');
        return { success: true, reminders_sent: 0 };
      }
      
      console.log(`📋 Found ${unpaidOrders.length} unpaid orders`);
      
      let remindersSent = 0;
      
      for (const order of unpaidOrders) {
        if (!order.buyer_fcm_token) continue;
        
        try {
          await fcmService.sendTargetedNotification(
            order.buyer_fcm_token,
            '⏰ Selesaikan Pembayaran',
            `Pesanan "${order.product_name}" menunggu pembayaran. Selesaikan sekarang!`,
            {
              type: 'payment_reminder',
              order_id: String(order.id),
              product_name: order.product_name,
              amount: String(order.amount),
              action: 'open_payment'
            }
          );
          
          remindersSent++;
          
        } catch (error) {
          console.error(`Failed to send reminder for order #${order.id}:`, error);
        }
      }
      
      console.log(`✅ Sent ${remindersSent} payment reminders`);
      
      return {
        success: true,
        reminders_sent: remindersSent,
        total_unpaid: unpaidOrders.length
      };
      
    } catch (error) {
      console.error('❌ Error sending payment reminders:', error);
      throw error;
    }
  }
  
  // ==========================================
  // Helper Methods (Database simulation)
  // ==========================================
  
  /**
   * Simulate database query - Get order with seller info
   */
  async getOrderWithSellerInfo(orderId) {
    // In production: Replace with actual database query
    // Example with MySQL:
    // const [rows] = await db.query(`
    //   SELECT 
    //     o.id, o.product_name, o.amount, o.status,
    //     b.id as buyer_id, b.name as buyer_name,
    //     s.id as seller_id, s.name as seller_name, s.fcm_token as seller_fcm_token
    //   FROM orders o
    //   JOIN users b ON o.buyer_id = b.id
    //   JOIN users s ON o.seller_id = s.id
    //   WHERE o.id = ?
    // `, [orderId]);
    // return rows[0];
    
    // Simulated data
    return {
      id: orderId,
      product_name: 'Smartphone Samsung Galaxy S23',
      amount: 12000000,
      status: 'paid',
      buyer_id: 101,
      buyer_name: 'John Doe',
      seller_id: 201,
      seller_name: 'Tech Store Indonesia',
      seller_fcm_token: 'dXY1Z2hpajklmnopqrst1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefgh'
    };
  }
  
  /**
   * Simulate database query - Get order with buyer info
   */
  async getOrderWithBuyerInfo(orderId) {
    // In production: Replace with actual database query
    return {
      id: orderId,
      product_name: 'Smartphone Samsung Galaxy S23',
      amount: 12000000,
      status: 'processing',
      buyer_id: 101,
      buyer_name: 'John Doe',
      buyer_fcm_token: 'abc123def456ghi789jklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz01',
      seller_id: 201,
      seller_name: 'Tech Store Indonesia'
    };
  }
  
  /**
   * Simulate database update
   */
  async updateOrderStatus(orderId, status) {
    // In production: Replace with actual database update
    // await db.query('UPDATE orders SET status = ? WHERE id = ?', [status, orderId]);
    console.log(`[DB] Updated order #${orderId} status to: ${status}`);
  }
  
  /**
   * Get all active sellers with FCM tokens
   */
  async getAllActiveSellers() {
    // In production:
    // const [sellers] = await db.query(`
    //   SELECT id, name, email, fcm_token 
    //   FROM users 
    //   WHERE role = 'seller' AND fcm_token IS NOT NULL
    // `);
    // return sellers;
    
    // Simulated data
    return [
      { id: 201, name: 'Tech Store Indonesia', fcm_token: 'token1abc...' },
      { id: 202, name: 'Fashion Outlet', fcm_token: 'token2def...' },
      { id: 203, name: 'Book Store', fcm_token: 'token3ghi...' }
    ];
  }
  
  /**
   * Get unpaid orders older than specified minutes
   */
  async getUnpaidOrders(olderThanMinutes) {
    // In production:
    // const [orders] = await db.query(`
    //   SELECT o.id, o.product_name, o.amount, b.fcm_token as buyer_fcm_token
    //   FROM orders o
    //   JOIN users b ON o.buyer_id = b.id
    //   WHERE o.status = 'pending'
    //   AND o.created_at < DATE_SUB(NOW(), INTERVAL ? MINUTE)
    //   AND b.fcm_token IS NOT NULL
    // `, [olderThanMinutes]);
    // return orders;
    
    return [];
  }
  
  /**
   * Log notification to database
   */
  async logNotification(logData) {
    // In production:
    // await db.query(`
    //   INSERT INTO notification_logs 
    //   (user_id, order_id, type, title, body, sent_at)
    //   VALUES (?, ?, ?, ?, ?, ?)
    // `, [
    //   logData.user_id,
    //   logData.order_id,
    //   logData.type,
    //   logData.title,
    //   logData.body,
    //   logData.sent_at
    // ]);
    
    console.log(`[DB] Notification logged: ${logData.type} for user #${logData.user_id}`);
  }
  
  /**
   * Get notification config based on order status
   */
  getStatusNotificationConfig(status) {
    const configs = {
      'processing': {
        title: '⚙️ Pesanan Sedang Diproses',
        message: 'Pesanan Anda sedang diproses oleh seller'
      },
      'shipped': {
        title: '🚚 Pesanan Telah Dikirim',
        message: 'Pesanan Anda sudah dikirim dan dalam perjalanan'
      },
      'completed': {
        title: '✅ Pesanan Selesai',
        message: 'Pesanan Anda telah selesai. Terima kasih!'
      },
      'cancelled': {
        title: '❌ Pesanan Dibatalkan',
        message: 'Pesanan Anda telah dibatalkan'
      }
    };
    
    return configs[status] || {
      title: '📦 Update Status Pesanan',
      message: `Status: ${status}`
    };
  }
  
  /**
   * Format currency to Indonesian Rupiah
   */
  formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID').format(amount);
  }
}

// Export singleton instance
module.exports = new OrderService();

// ==========================================
// EXAMPLE USAGE
// ==========================================

/*
// Example 1: Process payment (called from payment controller)
const orderService = require('./services/order.service');

async function handlePaymentCallback(orderId, paymentResult) {
  if (paymentResult.status === 'success') {
    const result = await orderService.processPayment(orderId, {
      payment_method: paymentResult.method,
      transaction_id: paymentResult.transaction_id
    });
    
    console.log(result);
    // {
    //   success: true,
    //   message: 'Payment processed and seller notified',
    //   order_id: 12345,
    //   seller_notified: true,
    //   seller_name: 'Tech Store Indonesia'
    // }
  }
}

// Example 2: Seller updates order status
async function sellerUpdateStatus(orderId, sellerId, newStatus) {
  const result = await orderService.updateOrderStatusAndNotifyBuyer(
    orderId,
    newStatus,
    sellerId
  );
  
  console.log(result);
  // {
  //   success: true,
  //   message: 'Status updated and buyer notified',
  //   order_id: 12345,
  //   new_status: 'shipped',
  //   buyer_notified: true
  // }
}

// Example 3: Broadcast to all sellers
async function sendPromoToAllSellers() {
  const result = await orderService.notifyAllSellers(
    '🎉 Flash Sale Alert!',
    'Naikkan penjualan dengan diskon 50% hari ini!',
    {
      broadcast_type: 'promo',
      promo_id: 'FLASH50',
      valid_until: '2024-12-31'
    }
  );
  
  console.log(result);
}
*/
