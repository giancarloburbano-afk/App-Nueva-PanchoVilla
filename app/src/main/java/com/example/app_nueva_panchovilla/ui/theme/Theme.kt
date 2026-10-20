package com.example.app_nueva_panchovilla.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Cuero,
    onPrimary = Color.White,
    primaryContainer = CueroSuave,
    onPrimaryContainer = CueroOscuro,
    secondary = Mostaza,
    onSecondary = Color.White,
    secondaryContainer = MostazaSuave,
    onSecondaryContainer = MostazaOscuro,
    tertiary = Ladrillo,
    onTertiary = Color.White,
    tertiaryContainer = LadrilloSuave,
    onTertiaryContainer = LadrilloOscuro,
    background = Crema,
    onBackground = Tinta,
    surface = Crema,
    onSurface = Tinta,
    surfaceVariant = CremaVariante,
    onSurfaceVariant = TintaSuave,
    outline = Borde,
    inverseSurface = Tinta,
    inverseOnSurface = Crema,
    surfaceContainerLowest = SuperficieMasBaja,
    surfaceContainerLow = SuperficieBaja,
    surfaceContainer = SuperficieMedia,
    surfaceContainerHigh = SuperficieAlta,
    surfaceContainerHighest = SuperficieMasAlta
)

private val DarkColorScheme = darkColorScheme(
    primary = CueroNoche,
    onPrimary = SobreCueroNoche,
    primaryContainer = CueroNocheSuave,
    onPrimaryContainer = CueroSuave,
    secondary = MostazaNoche,
    onSecondary = SobreMostazaNoche,
    secondaryContainer = MostazaNocheSuave,
    onSecondaryContainer = MostazaSuave,
    tertiary = LadrilloNoche,
    onTertiary = SobreLadrilloNoche,
    tertiaryContainer = LadrilloNocheSuave,
    onTertiaryContainer = LadrilloSuave,
    background = FondoNoche,
    onBackground = TextoNoche,
    surface = FondoNoche,
    onSurface = TextoNoche,
    surfaceVariant = TintaSuave,
    onSurfaceVariant = TextoVarianteNoche,
    outline = BordeNoche,
    inverseSurface = TextoNoche,
    inverseOnSurface = FondoNoche,
    surfaceContainerLowest = SuperficieNocheMasBaja,
    surfaceContainerLow = SuperficieNocheBaja,
    surfaceContainer = SuperficieNocheMedia,
    surfaceContainerHigh = SuperficieNocheAlta,
    surfaceContainerHighest = SuperficieNocheMasAlta
)

@Composable
fun AppNuevaPanchoVillaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sin colores dinámicos, para que siempre se vea la identidad de Pancho Villa
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}