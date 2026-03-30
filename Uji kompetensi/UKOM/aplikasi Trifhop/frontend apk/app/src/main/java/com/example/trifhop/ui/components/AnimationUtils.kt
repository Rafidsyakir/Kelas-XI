package com.example.trifhop.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.delay

/**
 * Navigation Animations - Premium Feel
 */
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(300))

fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(300))

fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(300))

fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(300))

fun fadeInTransition() = fadeIn(
    animationSpec = tween(400, easing = LinearEasing)
)

fun fadeOutTransition() = fadeOut(
    animationSpec = tween(400, easing = LinearEasing)
)

/**
 * Scale animation on press - Premium button feel
 */
fun Modifier.pressAnimation() = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_animation"
    )
    
    this
        .scale(scale)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            isPressed = true
        }
}

/**
 * Bounce animation for cards
 */
fun Modifier.bounceClick(onClick: () -> Unit) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounce_click"
    )
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
            onClick()
        }
    }
    
    this
        .scale(scale)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            isPressed = true
        }
}

/**
 * Shimmer animation for loading states
 */
@Composable
fun rememberShimmerAnimation(): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return translateAnim
}

/**
 * Fade in animation for list items
 */
fun Modifier.animateListItem(index: Int) = composed {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = LinearEasing),
        label = "list_item_fade"
    )
    
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "list_item_slide"
    )
    
    this
        .graphicsLayer {
            this.alpha = alpha
            this.translationY = translationY
        }
}

/**
 * Pulse animation for notifications/badges
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return scale
}
