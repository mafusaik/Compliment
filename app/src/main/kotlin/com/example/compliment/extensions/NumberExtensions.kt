package com.example.compliment.extensions

fun Int.floorMod(other: Int): Int = ((this % other) + other) % other