package com.example.myinstaclone.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myinstaclone.data.Event
import com.example.myinstaclone.data.remote.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val USERS = "users"

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

    fun onSignup(username: String, email: String, pass: String) {
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

    }

    fun handleException(exception: Exception? = null, message: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val formattedMessage = if (message.isBlank()) errorMessage else "$message: $errorMessage"
        popupNotification.value = Event(formattedMessage)
    }
}