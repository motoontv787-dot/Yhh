package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBackgroundEnd
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import kotlin.random.Random

// Subtle particle definition for cyber background
private data class CyberParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun CyberMatrixBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 24
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_bg_anim")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_motion"
    )

    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_motion"
    )

    val particles = remember {
        val colors = listOf(
            CyberCyan.copy(alpha = 0.45f),
            CyberPurple.copy(alpha = 0.45f),
            Color(0xFF0077FE).copy(alpha = 0.35f),
            Color(0xFF00FF9D).copy(alpha = 0.4f)
        )
        List(particleCount) {
            CyberParticle(
                xRatio = Random.nextFloat(),
                yRatio = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                color = colors[it % colors.size]
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Cyber Dark Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CyberBackground,
                    CyberBackgroundEnd,
                    Color(0xFF05070E)
                )
            ),
            size = size
        )

        // 2. Subtle Cyber Grid lines
        val gridStep = 48.dp.toPx()
        val gridAlpha = 0.05f
        val gridColor = CyberCyan.copy(alpha = gridAlpha)

        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridStep
        }

        var y = (gridOffset % gridStep)
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridStep
        }

        // 3. Floating Digital Particles
        for (particle in particles) {
            val currentY = ((particle.yRatio + animProgress * particle.speed) % 1f) * height
            val currentX = particle.xRatio * width
            drawCircle(
                color = particle.color,
                radius = particle.radius.dp.toPx(),
                center = Offset(currentX, currentY)
            )
        }
    }
}

// Glassmorphism cyberpunk modifier
fun Modifier.cyberGlass(
    cornerRadius: Dp = 16.dp,
    borderColor: Color = CyberCyan.copy(alpha = 0.28f),
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = CyberCardBg
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(backgroundColor, RoundedCornerShape(cornerRadius))
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                borderColor,
                borderColor.copy(alpha = 0.08f),
                CyberPurple.copy(alpha = 0.25f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

// Glowing border pulse modifier for featured cards/buttons
fun Modifier.cyberGlowPulse(
    color: Color = CyberCyan,
    cornerRadius: Dp = 16.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    this.border(
        width = 1.5.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                CyberPurple.copy(alpha = alpha * 0.7f),
                color.copy(alpha = alpha * 0.4f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
}

// Subtle haptic vibration for futuristic feel
fun performCyberHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(20L)
        }
    } catch (_: Exception) {}
}
