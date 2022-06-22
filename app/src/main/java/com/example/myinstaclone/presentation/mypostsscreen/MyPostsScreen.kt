package com.example.myinstaclone.presentation.mypostsscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myinstaclone.presentation.IgViewModel
import com.example.myinstaclone.presentation.bottomnavigationmenu.BottomNavigationItem
import com.example.myinstaclone.presentation.bottomnavigationmenu.BottomNavigationMenu

@Composable
fun MyPostsScreen(navController: NavController, vm: IgViewModel) {

    val userData = vm.userData.value
    val isLoading = vm.inProgress.value


    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(imageUrl = userData?.imageUrl) {

                }
                Text(
                    text = "15\nposts", modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "54\nfollowers", modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "93\nfollowing", modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                val usernameDisplay =
                    if (userData?.username == null) "" else "@${userData.username}"
                Text(text = userData?.name ?: "", fontWeight = FontWeight.Bold)
                Text(text = usernameDisplay)
                Text(text = userData?.bio ?: "")
            }
            OutlinedButton(
                onClick = { }, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                shape = RoundedCornerShape(10)
            ) {
                Text(text = "Edit Profile", color = Color.Black)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Posts List")
            }

        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.POSTS,
            navController = navController
        )
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {

}