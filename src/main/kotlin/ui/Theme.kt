package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = cyan700,
    primaryVariant = cyan500,
    secondary = deep_orange500,
    /* secondaryVariant = deep_orange500, */
    background = white,
    surface = white,
    error = red700,
    onPrimary = white,
    onSecondary = white,
    onBackground = grey900,
    onSurface = grey900,
    onError = white,
)

@Composable
fun AzulTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        shapes = CalculatorShapes,
        content = content,
    )
}