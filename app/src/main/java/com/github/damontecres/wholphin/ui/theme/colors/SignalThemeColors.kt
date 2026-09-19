package com.github.damontecres.wholphin.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme
import com.github.damontecres.wholphin.ui.theme.ThemeColors

val SignalThemeColors =
    object : ThemeColors {
        val primaryLight = Color(0xFF8A5100)
        val onPrimaryLight = Color(0xFFFFFFFF)
        val primaryContainerLight = Color(0xFFFFB865)
        val onPrimaryContainerLight = Color(0xFF5C3400)
        val secondaryLight = Color(0xFF735A2B)
        val onSecondaryLight = Color(0xFFFFFFFF)
        val secondaryContainerLight = Color(0xFFFFE0A6)
        val onSecondaryContainerLight = Color(0xFF574118)
        val tertiaryLight = Color(0xFF2F6B2A)
        val onTertiaryLight = Color(0xFFFFFFFF)
        val tertiaryContainerLight = Color(0xFFB3F0A8)
        val onTertiaryContainerLight = Color(0xFF1B5117)
        val errorLight = Color(0xFFBA1A1A)
        val onErrorLight = Color(0xFFFFFFFF)
        val errorContainerLight = Color(0xFFFFDAD6)
        val onErrorContainerLight = Color(0xFF93000A)
        val backgroundLight = Color(0xFFFFF8F3)
        val onBackgroundLight = Color(0xFF211A13)
        val surfaceLight = Color(0xFFFFF8F3)
        val onSurfaceLight = Color(0xFF211A13)
        val surfaceVariantLight = Color(0xFFF1E0CC)
        val onSurfaceVariantLight = Color(0xFF504536)
        val outlineLight = Color(0xFF827564)
        val outlineVariantLight = Color(0xFFD4C4B1)
        val scrimLight = Color(0xFF000000)
        val inverseSurfaceLight = Color(0xFF372F27)
        val inverseOnSurfaceLight = Color(0xFFFCEEE2)
        val inversePrimaryLight = Color(0xFFFFA94D)

        // Dark scheme: black neutrals, orange for attention, green for state.
        val amber = Color(0xFFFF9F2E)
        val amberDeep = Color(0xFFB35C00)
        val amberLight = Color(0xFFFFD977)
        val statusGreen = Color(0xFF52B54B)
        val chipGrey = Color(0xFF3E3E3E)

        val primaryDark = amber
        val onPrimaryDark = Color(0xFF3A1F00)
        val primaryContainerDark = amberDeep
        val onPrimaryContainerDark = amberLight
        val secondaryDark = amberLight
        val onSecondaryDark = Color(0xFF3A2A00)
        val secondaryContainerDark = Color(0xFF4A3617)
        val onSecondaryContainerDark = Color(0xFFFFE9B8)
        val tertiaryDark = statusGreen
        val onTertiaryDark = Color(0xFF04300A)
        val tertiaryContainerDark = Color(0xFF2A5C26)
        val onTertiaryContainerDark = Color(0xFFC6F0BF)
        val errorDark = Color(0xFFFFB4AB)
        val onErrorDark = Color(0xFF690005)
        val errorContainerDark = Color(0xFF93000A)
        val onErrorContainerDark = Color(0xFFFFDAD6)
        val backgroundDark = Color(0xFF000000)
        val onBackgroundDark = Color(0xFFECECEC)
        val surfaceDark = Color(0xFF000000)
        val onSurfaceDark = Color(0xFFECECEC)
        val surfaceVariantDark = chipGrey
        val onSurfaceVariantDark = Color(0xFFC7C7C7)
        val outlineDark = Color(0xFF8A8A8A)
        val outlineVariantDark = Color(0xFF4A4A4A)
        val scrimDark = Color(0xFF000000)
        val inverseSurfaceDark = Color(0xFFECECEC)
        val inverseOnSurfaceDark = Color(0xFF000000)
        val inversePrimaryDark = amberDeep
        val surfaceTintDark = Color(0xFF9A9A9A)
        val surfaceContainerLowestDark = Color(0xFF000000)
        val surfaceContainerLowDark = Color(0xFF0A0A0A)
        val surfaceContainerDark = Color(0xFF121212)
        val surfaceContainerHighDark = Color(0xFF1A1A1A)
        val surfaceContainerHighestDark = Color(0xFF242424)
        val surfaceDimDark = Color(0xFF000000)
        val surfaceBrightDark = Color(0xFF2A2A2A)

        override val lightSchemeMaterial: ColorScheme =
            androidx.compose.material3.lightColorScheme(
                primary = primaryLight,
                onPrimary = onPrimaryLight,
                primaryContainer = primaryContainerLight,
                onPrimaryContainer = onPrimaryContainerLight,
                secondary = secondaryLight,
                onSecondary = onSecondaryLight,
                secondaryContainer = secondaryContainerLight,
                onSecondaryContainer = onSecondaryContainerLight,
                tertiary = tertiaryLight,
                onTertiary = onTertiaryLight,
                tertiaryContainer = tertiaryContainerLight,
                onTertiaryContainer = onTertiaryContainerLight,
                error = errorLight,
                onError = onErrorLight,
                errorContainer = errorContainerLight,
                onErrorContainer = onErrorContainerLight,
                background = backgroundLight,
                onBackground = onBackgroundLight,
                surface = surfaceLight,
                onSurface = onSurfaceLight,
                surfaceVariant = surfaceVariantLight,
                onSurfaceVariant = onSurfaceVariantLight,
                outline = outlineLight,
                outlineVariant = outlineVariantLight,
                scrim = scrimLight,
                inverseSurface = inverseSurfaceLight,
                inverseOnSurface = inverseOnSurfaceLight,
                inversePrimary = inversePrimaryLight,
            )

        override val darkSchemeMaterial =
            androidx.compose.material3.darkColorScheme(
                primary = primaryDark,
                onPrimary = onPrimaryDark,
                primaryContainer = primaryContainerDark,
                onPrimaryContainer = onPrimaryContainerDark,
                secondary = secondaryDark,
                onSecondary = onSecondaryDark,
                secondaryContainer = secondaryContainerDark,
                onSecondaryContainer = onSecondaryContainerDark,
                tertiary = tertiaryDark,
                onTertiary = onTertiaryDark,
                tertiaryContainer = tertiaryContainerDark,
                onTertiaryContainer = onTertiaryContainerDark,
                error = errorDark,
                onError = onErrorDark,
                errorContainer = errorContainerDark,
                onErrorContainer = onErrorContainerDark,
                background = backgroundDark,
                onBackground = onBackgroundDark,
                surface = surfaceDark,
                onSurface = onSurfaceDark,
                surfaceVariant = surfaceVariantDark,
                onSurfaceVariant = onSurfaceVariantDark,
                outline = outlineDark,
                outlineVariant = outlineVariantDark,
                scrim = scrimDark,
                inverseSurface = inverseSurfaceDark,
                inverseOnSurface = inverseOnSurfaceDark,
                inversePrimary = inversePrimaryDark,
                surfaceTint = surfaceTintDark,
                surfaceDim = surfaceDimDark,
                surfaceBright = surfaceBrightDark,
                surfaceContainerLowest = surfaceContainerLowestDark,
                surfaceContainerLow = surfaceContainerLowDark,
                surfaceContainer = surfaceContainerDark,
                surfaceContainerHigh = surfaceContainerHighDark,
                surfaceContainerHighest = surfaceContainerHighestDark,
            )

        override val lightScheme =
            lightColorScheme(
                primary = primaryLight,
                onPrimary = onPrimaryLight,
                primaryContainer = primaryContainerLight,
                onPrimaryContainer = onPrimaryContainerLight,
                secondary = secondaryLight,
                onSecondary = onSecondaryLight,
                secondaryContainer = secondaryContainerLight,
                onSecondaryContainer = onSecondaryContainerLight,
                tertiary = tertiaryLight,
                onTertiary = onTertiaryLight,
                tertiaryContainer = tertiaryContainerLight,
                onTertiaryContainer = onTertiaryContainerLight,
                error = errorLight,
                onError = onErrorLight,
                errorContainer = errorContainerLight,
                onErrorContainer = onErrorContainerLight,
                background = backgroundLight,
                onBackground = onBackgroundLight,
                surface = surfaceLight,
                onSurface = onSurfaceLight,
                surfaceVariant = surfaceVariantLight,
                onSurfaceVariant = onSurfaceVariantLight,
                scrim = scrimLight,
                inverseSurface = inverseSurfaceLight,
                inverseOnSurface = inverseOnSurfaceLight,
                inversePrimary = inversePrimaryLight,
                border = primaryLight,
            )

        override val darkScheme =
            darkColorScheme(
                primary = primaryDark,
                onPrimary = onPrimaryDark,
                primaryContainer = primaryContainerDark,
                onPrimaryContainer = onPrimaryContainerDark,
                secondary = secondaryDark,
                onSecondary = onSecondaryDark,
                secondaryContainer = secondaryContainerDark,
                onSecondaryContainer = onSecondaryContainerDark,
                tertiary = tertiaryDark,
                onTertiary = onTertiaryDark,
                tertiaryContainer = tertiaryContainerDark,
                onTertiaryContainer = onTertiaryContainerDark,
                error = errorDark,
                onError = onErrorDark,
                errorContainer = errorContainerDark,
                onErrorContainer = onErrorContainerDark,
                background = backgroundDark,
                onBackground = onBackgroundDark,
                surface = surfaceDark,
                onSurface = onSurfaceDark,
                surfaceVariant = surfaceVariantDark,
                onSurfaceVariant = onSurfaceVariantDark,
                scrim = scrimDark,
                inverseSurface = inverseSurfaceDark,
                inverseOnSurface = inverseOnSurfaceDark,
                inversePrimary = inversePrimaryDark,
                // surfaceTint defaults to primary, which quietly mixes the accent into
                // every raised panel via surfaceColorAtElevation. Keep it neutral.
                surfaceTint = surfaceTintDark,
                // Full-strength accent rather than a faded inversePrimary: in this app
                // the border role is what actually paints focus.
                border = amber,
            )
    }
