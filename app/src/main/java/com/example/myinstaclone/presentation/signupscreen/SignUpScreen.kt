package com.example.myinstaclone.presentation.signupscreen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myinstaclone.presentation.IgViewModel

@Composable
fun SignupScreen(
    navController: NavController,
    vm: IgViewModel
){
    Text(text = "Sign Up Screen")
}