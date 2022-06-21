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
                                // create profile
                            } else {
                                handleException(task.exception, "Signup failed")
                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener { }
    }

    fun handleException(exception: Exception? = null, message: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val formattedMessage = if (message.isBlank()) errorMessage else "$message: $errorMessage"
        popupNotification.value = Event(formattedMessage)
    }
}