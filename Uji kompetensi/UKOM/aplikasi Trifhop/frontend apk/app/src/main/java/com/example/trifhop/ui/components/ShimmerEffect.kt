package com.example.trifhop.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer Effect for Loading States - Premium Feel
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color(0xFF2D3748).copy(alpha = 0.9f),
        Color(0xFF4A5568).copy(alpha = 0.5f),
        Color(0xFF2D3748).copy(alpha = 0.9f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

/**
 * Product Card Shimmer - Loading placeholder
 */
@Composable
fun ProductCardShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Image shimmer
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title shimmer
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category shimmer
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Price & condition row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .width(80.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                ShimmerEffect(
                    modifier = Modifier
                        .width(60.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

/**
 * Category Chip Shimmer
 */
@Composable
fun CategoryChipShimmer(
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier
            .width(100.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
    )
}

/**
 * Grid Shimmer - Multiple product cards
 */
@Composable
fun ProductGridShimmer(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    rows: Int = 3
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(columns) {
                    ProductCardShimmer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * List Item Shimmer - For order history, wishlist
 */
@Composable
fun ListItemShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Image shimmer
            ShimmerEffect(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content shimmers
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Profile Avatar Shimmer
 */
@Composable
fun AvatarShimmer(
    size: Int = 80,
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
    )
}
