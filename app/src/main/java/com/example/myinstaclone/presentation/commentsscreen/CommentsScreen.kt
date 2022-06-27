package com.example.myinstaclone.presentation.commentsscreen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myinstaclone.presentation.IgViewModel

@Composable
fun CommentsScreen(
    navController: NavController,
    viewModel: IgViewModel,
    postId: String
) {
    Text(text = "Comments Screeen")
}