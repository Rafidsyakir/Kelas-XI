package com.example.trifhop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trifhop.ui.theme.*

/**
 * ════════════════════════════════════════════════════════════════════════════
 *  EMPTY STATE COMPONENTS - Premium UX Design
 * ════════════════════════════════════════════════════════════════════════════
 * 
 * Award-winning empty states for various scenarios:
 * - Empty product list
 * - Empty cart
 * - Empty wishlist
 * - Empty search results
 * - No orders
 * - Network error
 * 
 * Features:
 * - Consistent layout (icon + title + description + action button)
 * - Subtle animations (fade in, bounce)
 * - Helpful action buttons
 * - Perfect color harmony
 * - Clear visual hierarchy
 */

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  EMPTY PRODUCTS STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun EmptyProductsState(
    title: String = "No Products Found",
    description: String = "Try adjusting your filters or check back later for new items",
    actionLabel: String? = "Browse All Products",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = title,
        description = description,
        actionLabel = actionLabel,
        onActionClick = onActionClick,
        modifier = modifier
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  EMPTY CART STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun EmptyCartState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = "Your Cart is Empty",
        description = "Add some amazing thrift items to your cart and get ready to checkout!",
        actionLabel = "Start Shopping",
        onActionClick = onStartShopping,
        modifier = modifier
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  EMPTY WISHLIST STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun EmptyWishlistState(
    onExploreProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = "No Favorites Yet",
        description = "Save your favorite items here and never lose track of what you love",
        actionLabel = "Explore Products",
        onActionClick = onExploreProducts,
        modifier = modifier
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  EMPTY SEARCH RESULTS STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun EmptySearchResultsState(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = "No Results Found",
        description = "We couldn't find any products matching \"$searchQuery\". Try different keywords.",
        actionLabel = "Clear Search",
        onActionClick = onClearSearch,
        modifier = modifier
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  EMPTY ORDERS STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun EmptyOrdersState(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = "No Orders Yet",
        description = "Your order history will appear here once you make your first purchase",
        actionLabel = "Start Shopping",
        onActionClick = onStartShopping,
        modifier = modifier
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  NETWORK ERROR STATE
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun NetworkErrorState(
    onRetry: () -> Unit,
    errorMessage: String = "Unable to connect to the server",
    modifier: Modifier = Modifier
) {
    EmptyStateTemplate(
        title = "Connection Problem",
        description = errorMessage,
        actionLabel = "Try Again",
        onActionClick = onRetry,
        modifier = modifier,
        isError = true
    )
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  LOADING STATE WITH SHIMMER
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Composable
fun LoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = TrifhopBlue,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun rememberFadeInAnimation(durationMillis: Int = 500): Float {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMillis))
    }
    return alpha.value
}

@Composable
private fun rememberSlideUpAnimation(durationMillis: Int = 500): Float {
    val offset = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        offset.animateTo(0f, animationSpec = tween(durationMillis))
    }
    return offset.value
}

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  BASE TEMPLATE FOR ALL EMPTY STATES
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * Consistent layout with fade-in animations
 */
@Composable
private fun EmptyStateTemplate(
    title: String,
    description: String,
    actionLabel: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    // Fade in animation
    val alpha = rememberFadeInAnimation(durationMillis = 600)
    val offsetY = rememberSlideUpAnimation(durationMillis = 600)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .alpha(alpha)
            .offset(y = offsetY.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon/Illustration placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (isError) ErrorRed.copy(alpha = 0.1f) else TrifhopBlue.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isError) "⚠️" else "📦",
                fontSize = 48.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        // Action Button
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrifhopBlue,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 200.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

