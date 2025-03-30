package com.example.diplomproject.ViewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isUserAuthenticated = MutableStateFlow<Boolean?>(null)
    val isUserAuthenticated: StateFlow<Boolean?> = _isUserAuthenticated

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkUser()
    }

    private fun checkUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    _isUserAuthenticated.value = document.exists()
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _isUserAuthenticated.value = false
                    _isLoading.value = false
                }
        } else {
            _isUserAuthenticated.value = false
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    val userRef = firestore.collection("users").document(user.uid)
                    userRef.get().addOnSuccessListener { document ->
                        if (!document.exists()) {
                            userRef.set(mapOf("email" to user.email))
                        }
                        _user.value = user
                        _isUserAuthenticated.value = true
                        _isLoading.value = false
                        onSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                onFailure(e)
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
        _isUserAuthenticated.value = false
        _isLoading.value = false
    }
}