package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BrandMagenta,
    onPrimary = Color.White,
    primaryContainer = BrandMagentaContainer,
    onPrimaryContainer = OnBrandMagentaContainer,
    secondary = BrandAmber,
    onSecondary = Color.White,
    secondaryContainer = BrandAmberContainer,
    onSecondaryContainer = OnBrandAmberContainer,
    tertiary = BrandCyan,
    onTertiary = Color.White,
    tertiaryContainer = BrandCyanContainer,
    onTertiaryContainer = OnBrandCyanContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandMagentaLight,
    onPrimary = Color(0xFF440020),
    primaryContainer = Color(0xFF6B0036),
    onPrimaryContainer = BrandMagentaContainer,
    secondary = BrandAmberLight,
    onSecondary = Color(0xFF3E2000),
    secondaryContainer = Color(0xFF5A3100),
    onSecondaryContainer = BrandAmberContainer,
    tertiary = BrandCyan,
    onTertiary = Color(0xFF002738),
    tertiaryContainer = Color(0xFF004766),
    onTertiaryContainer = BrandCyanContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline
)

val BestOfarShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BestOfarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BestOfarTypography,
        shapes = BestOfarShapes,
        content = content
    )
}
