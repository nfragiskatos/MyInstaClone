package com.example.myinstaclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myinstaclone.presentation.IgViewModel
import com.example.myinstaclone.presentation.NotificationMessage
import com.example.myinstaclone.presentation.ScreenDestinations
import com.example.myinstaclone.presentation.signupscreen.SignupScreen
import com.example.myinstaclone.ui.theme.MyInstaCloneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyInstaCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

@Composable
fun InstagramApp() {
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()
    NotificationMessage(vm = vm)
    NavHost(navController = navController, startDestination = ScreenDestinations.Signup.route) {
        composable(ScreenDestinations.Signup.route) {
            SignupScreen(navController = navController, vm = vm)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyInstaCloneTheme {
        InstagramApp()
    }
}