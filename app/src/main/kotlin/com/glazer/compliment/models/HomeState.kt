package com.glazer.compliment.models

import androidx.compose.runtime.Immutable

@Immutable
data class HomeState(
    val isTextVisible: Boolean,
    val compliment: String,
    val showRatingDialog: Boolean = false,
) {

    companion object {

        val Initial = HomeState(
            isTextVisible = true,
            compliment = "",
            showRatingDialog = false
        )
    }
}