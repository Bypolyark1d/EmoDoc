package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diplomproject.data.EmotionEntry
import com.example.diplomproject.data.StressSurveyResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EntryViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun saveEmotionEntry(text: String, emotionIndex: Int) {
        val userId = auth.currentUser?.uid ?: return
        val entry = EmotionEntry(
            userId = userId,
            text = text,
            emotion = emotionIndex
        )
        db.collection("users")
            .document(userId)
            .collection("entries")
            .add(entry)
            .addOnSuccessListener { Log.d("Firestore", "Emotion entry saved") }
            .addOnFailureListener { Log.e("Firestore", "Error saving entry", it) }
    }

    fun saveStressSurveyResult(stressLevel: Float) {
        val userId = auth.currentUser?.uid ?: return
        val result = StressSurveyResult(
            userId = userId,
            stressLevel = stressLevel
        )
        db.collection("users")
            .document(userId)
            .collection("surveys")
            .add(result)
            .addOnSuccessListener { Log.d("Firestore", "Survey saved") }
            .addOnFailureListener { Log.e("Firestore", "Error saving survey", it) }
    }
}