package com.example.myinstaclone.presentation.singlepostscreen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myinstaclone.data.remote.dto.PostDto
import com.example.myinstaclone.presentation.IgViewModel

@Composable
fun SinglePostScreen(
    navController: NavController,
    vm: IgViewModel,
    post: PostDto
) {
    Text(text = "Single post screen ${post.postDescription}")
}