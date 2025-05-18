package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.diplomproject.data.EmotionEntry
import com.example.diplomproject.data.StressSurveyResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class StatisticViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _emotionEntries = MutableLiveData<List<EmotionEntry>>()
    val emotionEntries: LiveData<List<EmotionEntry>> = _emotionEntries
    private val _stressSurveyResults = MutableLiveData<List<StressSurveyResult>>()
    val stressSurveyResults: LiveData<List<StressSurveyResult>> = _stressSurveyResults

    private val _emotionDistribution = MutableLiveData<Map<Int, Int>>()
    val emotionDistribution: LiveData<Map<Int, Int>> = _emotionDistribution

    private val _lastSurveyPerDay = MutableLiveData<Map<String, StressSurveyResult>>()
    val lastSurveyPerDay: LiveData<Map<String, StressSurveyResult>> = _lastSurveyPerDay

    fun getStressSurveyResults(lastN: Int) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("surveys")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(lastN.toLong())
            .get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.mapNotNull { it.toObject(StressSurveyResult::class.java) }
                _stressSurveyResults.value = results
            }
            .addOnFailureListener { Log.e("Firestore", "Error fetching surveys", it) }
    }
    fun getEmotionDistribution() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("entries")
            .get()
            .addOnSuccessListener { snapshot ->
                val emotionCounts = mutableMapOf<Int, Int>()
                snapshot.documents.forEach { doc ->
                    val emotion = doc.toObject(EmotionEntry::class.java)?.emotion
                    emotion?.let { emotionCounts[it] = emotionCounts.getOrDefault(it, 0) + 1 }
                }
                _emotionDistribution.value = emotionCounts
            }
            .addOnFailureListener { Log.e("Firestore", "Error fetching emotions", it) }
    }
    fun getLastSurveyPerDay() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("surveys")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val lastSurveyPerDay = mutableMapOf<String, StressSurveyResult>()
                snapshot.documents.forEach { doc ->
                    val survey = doc.toObject(StressSurveyResult::class.java)
                    survey?.let {
                        val date = SimpleDateFormat("yyyy-MM-dd").format(Date(it.timestamp))
                        if (!lastSurveyPerDay.containsKey(date)) {
                            lastSurveyPerDay[date] = it
                        }
                    }
                }
                _lastSurveyPerDay.value = lastSurveyPerDay
            }
            .addOnFailureListener { Log.e("Firestore", "Error fetching surveys", it) }
    }
    fun getStressHistory() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("surveys")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.mapNotNull { it.toObject(StressSurveyResult::class.java) }
                _stressSurveyResults.value = results
            }
            .addOnFailureListener { Log.e("Firestore", "Error fetching stress history", it) }
    }

    fun getEmotionHistory() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("entries")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val emotionResults = snapshot.documents.mapNotNull { it.toObject(EmotionEntry::class.java) }
                _emotionEntries.value = emotionResults
            }
            .addOnFailureListener { Log.e("Firestore", "Error fetching emotion history", it) }
    }
}