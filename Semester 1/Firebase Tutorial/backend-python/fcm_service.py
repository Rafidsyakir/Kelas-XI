# ========================================
# FCM Service - Python Implementation
# ========================================
# File: fcm_service.py

from firebase_admin import messaging
from typing import Dict, List, Optional, Union
import time

class FCMService:
    """
    FCM Service untuk mengirim notifikasi menggunakan Firebase Admin SDK
    
    Features:
    - Send to single device
    - Send to multiple devices (multicast)
    - Send to topics
    - Error handling dengan retry logic
    - Token validation
    """
    
    def send_targeted_notification(
        self,
        fcm_token: str,
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> str:
        """
        Kirim notifikasi ke satu device berdasarkan FCM Token
        
        Args:
            fcm_token: FCM Token dari device target
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload untuk logika bisnis (opsional)
            
        Returns:
            str: Message ID jika berhasil
            
        Example:
            >>> fcm_service.send_targeted_notification(
            ...     'fcmToken123...',
            ...     'Pesanan Baru',
            ...     'Anda mendapat pesanan dari John Doe',
            ...     {'order_id': '12345', 'type': 'new_order'}
            ... )
            'projects/123/messages/456'
        """
        try:
            # Validasi input
            if not fcm_token or not title or not body:
                raise ValueError('fcm_token, title, and body are required')
            
            # Convert data values ke string (FCM requirement)
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            # Construct message payload
            message = messaging.Message(
                # Notification payload - untuk menampilkan notifikasi
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                # Data payload - untuk logika bisnis
                data=data_payload,
                # Android specific configuration
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound='default',
                        color='#FF6B6B',
                        channel_id='fcm_default_channel',
                        icon='ic_notification'
                    )
                ),
                # APNs (iOS) specific configuration
                apns=messaging.APNSConfig(
                    payload=messaging.APNSPayload(
                        aps=messaging.Aps(
                            sound='default',
                            badge=1
                        )
                    )
                ),
                # Target FCM Token
                token=fcm_token
            )
            
            # Send message ke FCM
            response = messaging.send(message)
            
            print(f'✅ Successfully sent notification: {response}')
            print(f'📱 Target token: {fcm_token}')
            print(f'📬 Title: {title}')
            print(f'📝 Body: {body}')
            print(f'📦 Data: {data}')
            
            return response  # Returns message ID
            
        except Exception as error:
            print(f'❌ Error sending notification: {error}')
            
            # Handle specific FCM errors
            error_str = str(error)
            if 'invalid-registration-token' in error_str:
                print('🗑️ Invalid FCM token format. Token should be removed.')
            elif 'registration-token-not-registered' in error_str:
                print('🗑️ FCM token not registered. User may have uninstalled app.')
            elif 'invalid-argument' in error_str:
                print('⚠️ Invalid message payload. Check data types and format.')
            
            raise error
    
    def send_multicast_notification(
        self,
        fcm_tokens: List[str],
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> Dict:
        """
        Kirim notifikasi ke multiple devices sekaligus (batch)
        Lebih efisien daripada mengirim satu per satu
        
        Args:
            fcm_tokens: List of FCM tokens (max 500 per call)
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload
            
        Returns:
            dict: Response dengan success dan failure counts
            
        Example:
            >>> tokens = ['token1', 'token2', 'token3']
            >>> result = fcm_service.send_multicast_notification(
            ...     tokens,
            ...     'Flash Sale!',
            ...     'Diskon 50% untuk semua produk',
            ...     {'type': 'promo', 'promo_id': '789'}
            ... )
            >>> print(result)
            {'success_count': 2, 'failure_count': 1, 'responses': [...]}
        """
        try:
            # Validasi input
            if not fcm_tokens or len(fcm_tokens) == 0:
                raise ValueError('fcm_tokens must be a non-empty list')
            
            # FCM limit: maksimal 500 tokens per batch
            if len(fcm_tokens) > 500:
                raise ValueError('Maximum 500 tokens per batch')
            
            # Prepare data payload
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            # Construct multicast message
            message = messaging.MulticastMessage(
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                data=data_payload,
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound='default',
                        color='#FF6B6B',
                        channel_id='fcm_default_channel'
                    )
                ),
                tokens=fcm_tokens
            )
            
            # Send multicast message
            response = messaging.send_multicast(message)
            
            print(f'✅ Successfully sent {response.success_count} notifications')
            print(f'❌ Failed to send {response.failure_count} notifications')
            
            # Log failed tokens untuk cleanup
            if response.failure_count > 0:
                print('\n📋 Failed tokens:')
                for idx, resp in enumerate(response.responses):
                    if not resp.success:
                        print(f'  - Token {idx}: {fcm_tokens[idx]}')
                        print(f'    Error: {resp.exception}')
            
            return {
                'success_count': response.success_count,
                'failure_count': response.failure_count,
                'responses': response.responses
            }
            
        except Exception as error:
            print(f'❌ Error sending multicast notification: {error}')
            raise error
    
    def send_to_topic(
        self,
        topic: str,
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None
    ) -> str:
        """
        Kirim notifikasi ke topic (subscribe-based messaging)
        
        Args:
            topic: Topic name (tanpa "/topics/" prefix)
            title: Judul notifikasi
            body: Isi notifikasi
            data: Data payload
            
        Returns:
            str: Message ID
            
        Example:
            >>> fcm_service.send_to_topic(
            ...     'all_sellers',
            ...     'Pengumuman Penting',
            ...     'Sistem maintenance pada 1 Jan 2025',
            ...     {'type': 'announcement', 'priority': 'high'}
            ... )
        """
        try:
            if not topic or not title or not body:
                raise ValueError('topic, title, and body are required')
            
            if data is None:
                data = {}
            
            data_payload = {
                **{k: str(v) for k, v in data.items()},
                'timestamp': str(int(time.time() * 1000))
            }
            
            message = messaging.Message(
                notification=messaging.Notification(
                    title=title,
                    body=body
                ),
                data=data_payload,
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound='default',
                        color='#FF6B6B'
                    )
                ),
                topic=topic
            )
            
            response = messaging.send(message)
            
            print(f'✅ Successfully sent notification to topic: {topic}')
            print(f'📬 Message ID: {response}')
            
            return response
            
        except Exception as error:
            print(f'❌ Error sending to topic: {error}')
            raise error
    
    def send_custom_notification(
        self,
        fcm_token: str,
        title: str,
        body: str,
        options: Optional[Dict] = None
    ) -> str:
        """
        Kirim notifikasi dengan kustomisasi penuh
        
        Args:
            fcm_token: FCM Token
            title: Title
            body: Body
            options: Additional options (image_url, sound, color, icon, etc.)
            
        Returns:
            str: Message ID
            
        Example:
            >>> fcm_service.send_custom_notification(
            ...     'fcmToken123',
            ...     'New Message',
            ...     'You have a new message',
            ...     {
            ...         'image_url': 'https://example.com/image.jpg',
            ...         'sound': 'message_tone.mp3',
            ...         'color': '#00FF00',
            ...         'data': {'chat_id': '789'}
            ...     }
            ... )
        """
        try:
            if options is None:
                options = {}
            
            message = messaging.Message(
                notification=messaging.Notification(
                    title=title,
                    body=body,
                    image=options.get('image_url')
                ),
                data=options.get('data', {}),
                android=messaging.AndroidConfig(
                    priority='high',
                    notification=messaging.AndroidNotification(
                        sound=options.get('sound', 'default'),
                        color=options.get('color', '#FF6B6B'),
                        icon=options.get('icon', 'ic_notification'),
                        channel_id=options.get('channel_id', 'fcm_default_channel'),
                        click_action=options.get('click_action'),
                        tag=options.get('tag')
                    )
                ),
                apns=messaging.APNSConfig(
                    payload=messaging.APNSPayload(
                        aps=messaging.Aps(
                            sound=options.get('sound', 'default'),
                            badge=options.get('badge', 1),
                            category=options.get('category')
                        )
                    )
                ),
                token=fcm_token
            )
            
            response = messaging.send(message)
            print(f'✅ Custom notification sent: {response}')
            
            return response
            
        except Exception as error:
            print(f'❌ Error sending custom notification: {error}')
            raise error
    
    def subscribe_to_topic(
        self,
        tokens: Union[str, List[str]],
        topic: str
    ) -> Dict:
        """
        Subscribe FCM token(s) ke topic
        
        Args:
            tokens: Single token or list of tokens
            topic: Topic name
            
        Returns:
            dict: Response dengan success/failure counts
        """
        try:
            token_list = [tokens] if isinstance(tokens, str) else tokens
            response = messaging.subscribe_to_topic(token_list, topic)
            
            print(f'✅ Successfully subscribed {response.success_count} tokens to topic: {topic}')
            if response.failure_count > 0:
                print(f'❌ Failed to subscribe {response.failure_count} tokens')
            
            return {
                'success_count': response.success_count,
                'failure_count': response.failure_count
            }
        except Exception as error:
            print(f'❌ Error subscribing to topic: {error}')
            raise error
    
    def unsubscribe_from_topic(
        self,
        tokens: Union[str, List[str]],
        topic: str
    ) -> Dict:
        """
        Unsubscribe FCM token(s) dari topic
        
        Args:
            tokens: Single token or list of tokens
            topic: Topic name
            
        Returns:
            dict: Response dengan success/failure counts
        """
        try:
            token_list = [tokens] if isinstance(tokens, str) else tokens
            response = messaging.unsubscribe_from_topic(token_list, topic)
            
            print(f'✅ Successfully unsubscribed {response.success_count} tokens from topic: {topic}')
            if response.failure_count > 0:
                print(f'❌ Failed to unsubscribe {response.failure_count} tokens')
            
            return {
                'success_count': response.success_count,
                'failure_count': response.failure_count
            }
        except Exception as error:
            print(f'❌ Error unsubscribing from topic: {error}')
            raise error
    
    def send_with_retry(
        self,
        fcm_token: str,
        title: str,
        body: str,
        data: Optional[Dict[str, str]] = None,
        max_retries: int = 3
    ) -> Dict:
        """
        Send notification dengan retry logic
        
        Args:
            fcm_token: FCM Token
            title: Title
            body: Body
            data: Data payload
            max_retries: Maximum retry attempts
            
        Returns:
            dict: Result dengan success status dan response
        """
        last_error = None
        
        for attempt in range(1, max_retries + 1):
            try:
                print(f'📤 Attempt {attempt} of {max_retries}')
                
                result = self.send_targeted_notification(fcm_token, title, body, data)
                
                return {'success': True, 'result': result}
                
            except Exception as error:
                last_error = error
                error_str = str(error)
                
                # Don't retry for permanent errors
                permanent_errors = [
                    'invalid-registration-token',
                    'registration-token-not-registered',
                    'invalid-argument'
                ]
                
                if any(err in error_str for err in permanent_errors):
                    print(f'❌ Permanent error, not retrying: {error}')
                    break
                
                # Exponential backoff
                if attempt < max_retries:
                    delay = (2 ** attempt) * 1.0  # 2s, 4s, 8s
                    print(f'⏳ Retry attempt {attempt} failed. Waiting {delay}s...')
                    time.sleep(delay)
        
        print('❌ All retry attempts failed')
        return {'success': False, 'error': str(last_error)}

# Create singleton instance
fcm_service = FCMService()
