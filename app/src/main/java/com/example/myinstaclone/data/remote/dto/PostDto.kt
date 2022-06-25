package com.example.myinstaclone.data.remote.dto

data class PostDto(
    val postId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val userImage: String? = null,

    // kludgy way of not having to do a join on collections with Firestore. Store the user's profile
    // pic, directly in the post, and when they change their profile pic, just update all their
    // posts. Not an efficient solution.
    val postImage: String? = null,
    val postDescription: String? = null,
    val time: Long? = null
)
