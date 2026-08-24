package com.glazer.compliment.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.glazer.compliment.ui.rating.RateAppDialog
import com.glazer.compliment.ui.theme.MyAppTheme

@Preview
@Composable
fun RatingPreview() {
    MyAppTheme(isDarkTheme = false) {
        RateAppDialog(
            onRateClicked = {},
            onDismiss = {}
        )
    }
}