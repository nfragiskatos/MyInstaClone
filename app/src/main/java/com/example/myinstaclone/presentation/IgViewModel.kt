package com.example.myinstaclone.presentation

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myinstaclone.data.Event
import com.example.myinstaclone.data.remote.dto.PostDto
import com.example.myinstaclone.data.remote.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

const val USERS = "users"
const val POSTS = "posts"

@HiltViewModel
class IgViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {


    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserDto?>(null)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    val refreshPostsProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostDto>>(listOf())

    val searchedPosts = mutableStateOf<List<PostDto>>(listOf())
    val searchedPostsProgress = mutableStateOf(false)

    val postsFeed = mutableStateOf<List<PostDto>>(listOf())
    val postsFeedProgress = mutableStateOf(false)

    init {
//        auth.signOut()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun onSignup(username: String, email: String, pass: String) {
        if (username.isBlank() || email.isBlank() || pass.isBlank()) {
            handleException(message = "Please fill in all fields")
            return
        }
        inProgress.value = true

        db.collection(USERS).whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(message = "Username already exists")
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                createOrUpdateProfile(username = username)
                            } else {
                                handleException(task.exception, "Signup failed")
                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener { }
    }

    fun onLogin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            handleException(message = "Please fill in all fields")
            return
        }
        inProgress.value = true

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        getUserData(uid)
                    }
                } else {
                    handleException(task.exception, "Login failed")
                    inProgress.value = false
                }
            }
            .addOnFailureListener { e ->
                handleException(e, "Login failed")
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val userDto = UserDto(
            userId = uid,
            name = name ?: userData.value?.name,
            username = username ?: userData.value?.username,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData.value?.following
        )

        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS).document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        doc.reference.update(userDto.toMap())
                            .addOnSuccessListener {
                                userData.value = userDto
                                inProgress.value = false
                            }
                            .addOnFailureListener { e ->
                                handleException(e, "Cannot update user")
                                inProgress.value = false
                            }
                    } else {
                        db.collection(USERS).document(uid).set(userDto)
                        getUserData(uid)
                        inProgress.value = false
                    }
                }
                .addOnFailureListener { e ->
                    handleException(e, "Cannot create user")
                    inProgress.value = false
                }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USERS).document(uid).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject<UserDto>()
                userData.value = user
                inProgress.value = false
                refreshPosts()
                getPersonalizedFeed()
            }
            .addOnFailureListener { e ->
                handleException(e, "Cannot retrieve user data")
                inProgress.value = false
            }
    }

    fun handleException(exception: Exception? = null, message: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val formattedMessage = if (message.isBlank()) errorMessage else "$message: $errorMessage"
        popupNotification.value = Event(formattedMessage)
    }

    fun updateProfileData(name: String, username: String, bio: String) {
        createOrUpdateProfile(name, username, bio)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }
            .addOnFailureListener { e ->
                handleException(e)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
            updatePostUserImageData(it.toString())
        }
    }

    private fun updatePostUserImageData(imageUrl: String) {
        val currentUid = auth.currentUser?.uid
        db.collection(POSTS).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener {
                val posts = mutableStateOf<List<PostDto>>(arrayListOf())
                convertPosts(it, posts)
                val refs = arrayListOf<DocumentReference>()

                for (post in posts.value) {
                    post.postId?.let { id ->
                        refs.add(db.collection(POSTS).document(id))
                    }
                }

                if (refs.isNotEmpty()) {
                    db.runBatch { batch ->
                        for (ref in refs) {
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }
                        .addOnSuccessListener {
                            refreshPosts()
                        }
                }
            }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged Out")
        searchedPosts.value = listOf()
        postsFeed.value = listOf()
    }

    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImage(uri) {
            onCreatePost(it, description, onPostSuccess)
        }
    }

    private fun onCreatePost(imageUri: Uri, description: String, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentUserImage = userData.value?.imageUrl

        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()

            // Real-world application of course should do something more sophisticated
            // example list of words to exclude
            val fillerWords = listOf("the", "be", "to", "is", "of", "and", "or", "in", "it")
            // get distinct words that aren't filler and aren't special characters
            val searchTerms = description
                .split(" ", ".", ",", "?", "!", "#")
                .map { it.lowercase() }
                .filter { it.isNotEmpty() and !fillerWords.contains(it) }

            val post = PostDto(
                postId = postUuid,
                userId = currentUid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = imageUri.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf(),
                searchTerms = searchTerms
            )

            db.collection(POSTS).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = Event("Post successfully created")
                    inProgress.value = false
                    refreshPosts()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { e ->
                    handleException(e, "Unable to create post")
                    inProgress.value = false
                }


        } else {
            handleException(message = "Error: username unavailable. Unable to create post")
            onLogout()
            inProgress.value = false
        }
    }

    private fun refreshPosts() {
        val currentUid = auth.currentUser?.uid

        if (currentUid != null) {
            refreshPostsProgress.value = true
            db.collection(POSTS).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                }
                .addOnFailureListener { e ->
                    handleException(
                        exception = e,
                        message = "Unable to retrieve posts"
                    )
                    refreshPostsProgress.value = false
                }
        } else {
            handleException(message = "Error: username unavailable. Unable to refresh posts")
            onLogout()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostDto>>) {
        val newPosts = mutableListOf<PostDto>()
        documents.forEach { doc ->
            val post = doc.toObject<PostDto>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending { it.time }
        outState.value = sortedPosts
    }

    fun searchPosts(searchTerm: String) {
        if (searchTerm.isNotBlank()) {
            searchedPostsProgress.value = true
            db.collection(POSTS)
                .whereArrayContains("searchTerms", searchTerm.trim().lowercase()).get()
                .addOnSuccessListener {
                    convertPosts(it, searchedPosts)
                    searchedPostsProgress.value = false
                }
                .addOnFailureListener { e ->
                    handleException(e, "Cannot search posts")
                    searchedPostsProgress.value = false
                }
        }
    }

    fun onFollowClick(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            userData.value?.following?.let {
                following.addAll(it)
            }

            if (following.contains(userId)) {
                following.remove(userId)
            } else {
                following.add(userId)
            }

            db.collection(USERS).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                }
        }
    }

    private fun getPersonalizedFeed() {
        val following = userData.value?.following
        if (!following.isNullOrEmpty()) {
            postsFeedProgress.value = true
            db.collection(POSTS).whereIn("userId", following).get()
                .addOnSuccessListener {
                    convertPosts(documents = it, outState = postsFeed)
                    if (postsFeed.value.isEmpty()) {
                        getGeneralFeed()
                    } else {
                        postsFeedProgress.value = false
                    }
                }
                .addOnFailureListener { e ->
                    handleException(e, "Cannot get personalized feed")
                    postsFeedProgress.value = false

                }
        } else {
            getGeneralFeed()
        }
    }

    private fun getGeneralFeed() {
        postsFeedProgress.value = true
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 60 * 60 * 1000 // 1 day in millis
        db.collection(POSTS).whereGreaterThan("time", currentTime - difference).get()
            .addOnSuccessListener {
                convertPosts(documents = it, outState = postsFeed)
                postsFeedProgress.value = false
            }
            .addOnFailureListener { e ->
                handleException(e, "Cannot get feed")
                postsFeedProgress.value = false
            }
    }
}