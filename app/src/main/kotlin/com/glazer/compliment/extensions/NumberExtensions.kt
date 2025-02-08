package com.glazer.compliment.extensions

fun Int.floorMod(other: Int): Int = ((this % other) + other) % other