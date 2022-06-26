package com.example.myinstaclone.data.remote.dto

data class CommentDto(
    val commentId: String? = null,
    val postId: String? = null,
    val username: String? = null,
    val text: String? = null,
    val timestamp: Long? = null
)
