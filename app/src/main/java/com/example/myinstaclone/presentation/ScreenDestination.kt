package com.example.myinstaclone.presentation

sealed class ScreenDestination(val route: String) {
    object Signup : ScreenDestination("signup")
    object Login : ScreenDestination("login")
}
