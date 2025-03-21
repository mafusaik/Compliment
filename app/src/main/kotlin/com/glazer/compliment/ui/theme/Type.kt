package com.glazer.compliment.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.glazer.compliment.R

val customFontFamily = FontFamily(Font(R.font.lato_regular))
val customFontFamilyCursive = FontFamily(Font(R.font.caveat_regular))
private val defaultTypography = Typography()

val customTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = customFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = customFontFamily),
    displaySmall =defaultTypography.displaySmall.copy(fontFamily = customFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = customFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = customFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = customFontFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = customFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = customFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = customFontFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = customFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = customFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = customFontFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = customFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = customFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = customFontFamily)
)