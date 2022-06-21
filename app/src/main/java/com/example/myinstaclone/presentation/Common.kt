package com.example.myinstaclone.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationMessage(vm: IgViewModel) {
    val notificationState = vm.popupNotification.value
    val notificationMessage = notificationState?.getContentOrNull()
    if (notificationMessage != null) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_LONG).show()
    }
}