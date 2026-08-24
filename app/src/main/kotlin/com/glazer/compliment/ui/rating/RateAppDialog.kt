package com.glazer.compliment.ui.rating

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun RateAppDialog(
    onRateClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        RateAppCard(
            onRateClicked = onRateClicked,
            onDismiss = onDismiss
        )
    }
}