package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Best Ofar Brand Palette (Vibrant Editorial Magazine)
val BrandMagenta = Color(0xFFE00070)
val BrandMagentaLight = Color(0xFFFF4D9E)
val BrandMagentaContainer = Color(0xFFFFD9E6)
val OnBrandMagentaContainer = Color(0xFF3E001D)

val BrandAmber = Color(0xFFFF8C00)
val BrandAmberLight = Color(0xFFFFB74D)
val BrandAmberContainer = Color(0xFFFFE6CC)
val OnBrandAmberContainer = Color(0xFF331900)

val BrandCyan = Color(0xFF00A8E8)
val BrandCyanContainer = Color(0xFFD2F3FF)
val OnBrandCyanContainer = Color(0xFF002738)

val EmeraldSuccess = Color(0xFF00B248)
val EmeraldSuccessContainer = Color(0xFFD3F8DF)
val OnEmeraldSuccess = Color(0xFF002910)

// Neutral Editorial Surfaces (Light)
val LightBackground = Color(0xFFFAF7FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF2ECF2)
val LightOnSurface = Color(0xFF1A161D)
val LightOnSurfaceVariant = Color(0xFF605766)
val LightOutline = Color(0xFFE2DAE5)

// Neutral Editorial Surfaces (Dark)
val DarkBackground = Color(0xFF121016)
val DarkSurface = Color(0xFF1B1821)
val DarkSurfaceVariant = Color(0xFF282330)
val DarkOnSurface = Color(0xFFF5EFF7)
val DarkOnSurfaceVariant = Color(0xFFBEB5C4)
val DarkOutline = Color(0xFF3D3647)

// Payment method brand accents
val BkashPink = Color(0xFFE2136E)
val NagadOrange = Color(0xFFF76B1C)
val RocketPurple = Color(0xFF8C3494)
val CardBlue = Color(0xFF1976D2)

object BestOfarGradients {
    val HeaderGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF0080),
            Color(0xFFFF8C00),
            Color(0xFF00C6FF)
        )
    )

    val PremiumButtonGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF0080),
            Color(0xFFFF8C00)
        )
    )

    val UnlockedGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00B248),
            Color(0xFF00A8E8)
        )
    )
}
