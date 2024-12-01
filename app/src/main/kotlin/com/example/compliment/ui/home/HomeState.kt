package com.example.compliment.ui.home

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