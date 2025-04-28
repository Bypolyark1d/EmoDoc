package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomproject.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun fetchUserProfile() {
        val firebaseUser = auth.currentUser ?: return
        val userId = firebaseUser.uid

        viewModelScope.launch {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userProfile = document.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            val updatedProfile = userProfile.copy(
                                userId = userProfile.userId.ifBlank { userId },
                                name = userProfile.name.ifBlank { firebaseUser.displayName ?: "" },
                                email = userProfile.email.ifBlank { firebaseUser.email ?: "" },
                                countEntry = userProfile.countEntry.takeIf { it >= 0 } ?: 0,
                                currentEmotion = userProfile.currentEmotion.takeIf { it >= 0 } ?: -1,
                                stressLevel = userProfile.stressLevel.takeIf { it >= 0.0f } ?: -1.0f
                            )
                            updateUserProfile(updatedProfile)
                        }
                    } else {
                        val newUserProfile = UserProfile(
                            userId = userId,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            countEntry = 0,
                            currentEmotion = -1,
                            stressLevel = -1.0f
                        )
                        updateUserProfile(newUserProfile)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileViewModel", "Ошибка при загрузке профиля: ${e.message}")
                }
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            firestore.collection("users").document(userId).set(profile)
                .addOnSuccessListener {
                    _userProfile.value = profile
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileViewModel", "Ошибка при обновлении профиля: ${e.message}")
                }
        }
    }

    fun incrementEntryCount() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val userDocRef = firestore.collection("users").document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val currentCount = snapshot.getLong("countEntry") ?: 0
                val newCount = currentCount + 1
                transaction.update(userDocRef, "countEntry", newCount)
            }.addOnSuccessListener {
                _userProfile.value = _userProfile.value?.copy(countEntry = (_userProfile.value?.countEntry ?: 0) + 1)
                Log.d("ProfileViewModel", "countEntry увеличен успешно")
            }.addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Ошибка при увеличении countEntry: ${e.message}")
            }
        }
    }

    fun updateCurrentEmotion(emotion: Int) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val userDocRef = firestore.collection("users").document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                transaction.update(userDocRef, "currentEmotion", emotion)
            }.addOnSuccessListener {
                _userProfile.value = _userProfile.value?.copy(currentEmotion = emotion)
                Log.d("ProfileViewModel", "currentEmotion обновлена успешно на $emotion")
            }.addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Ошибка при обновлении currentEmotion: ${e.message}")
            }
        }
    }
    fun updateStressLevel(level: Float) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val userDocRef = firestore.collection("users").document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                transaction.update(userDocRef, "stressLevel", level)
            }.addOnSuccessListener {
                _userProfile.value = _userProfile.value?.copy(stressLevel = level)
                Log.d("ProfileViewModel", "stressLevel обновлён успешно на $level")
            }.addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Ошибка при обновлении stressLevel: ${e.message}")
            }
        }
    }
}