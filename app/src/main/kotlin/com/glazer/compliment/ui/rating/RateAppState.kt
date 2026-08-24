package com.glazer.compliment.ui.rating

data class RateAppState(
    val shownComplimentsCount: Int = 0,
    val wasAlreadyShown: Boolean = false
) {
    val shouldShow: Boolean get() = shownComplimentsCount > 100 && !wasAlreadyShown
}