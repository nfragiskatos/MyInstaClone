package com.example.myinstaclone.presentation

sealed class ScreenDestinations(val route: String) {
    object Signup: ScreenDestinations("signup")
}
