package com.example.myinstaclone.data.remote.dto

import android.os.Parcel
import android.os.Parcelable

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
    val time: Long? = null,
    val likes: List<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(userImage)
        parcel.writeString(postImage)
        parcel.writeString(postDescription)
        parcel.writeValue(time)
        parcel.writeStringList(likes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostDto> {
        override fun createFromParcel(parcel: Parcel): PostDto {
            return PostDto(parcel)
        }

        override fun newArray(size: Int): Array<PostDto?> {
            return arrayOfNulls(size)
        }
    }

}
