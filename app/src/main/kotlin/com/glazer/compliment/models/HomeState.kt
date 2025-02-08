package com.glazer.compliment.models

import androidx.compose.runtime.Immutable

@Immutable
data class HomeState(
    val isTextVisible: Boolean,
    val compliment: String,
) {

    companion object {

        val Initial = HomeState(
            isTextVisible = true,
            compliment = ""
        )
    }
}