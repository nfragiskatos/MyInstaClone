package com.example.myinstaclone.presentation.bottomnavigationmenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myinstaclone.R
import com.example.myinstaclone.presentation.ScreenDestination
import com.example.myinstaclone.presentation.navigateTo

enum class BottomNavigationItem(val icon: Int, val navDestination: ScreenDestination) {
    FEED(R.drawable.ic_home, ScreenDestination.Feed),
    SEARCH(R.drawable.ic_search, ScreenDestination.Search),
    POSTS(R.drawable.ic_posts, ScreenDestination.MyPosts)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {
        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "Feed",
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController, item.navDestination)
                    },
                colorFilter = ColorFilter.tint(
                    if (item == selectedItem) Color.Black else Color.Gray
                )
            )
        }
    }
}