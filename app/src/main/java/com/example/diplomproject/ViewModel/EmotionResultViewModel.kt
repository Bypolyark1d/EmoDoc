package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diplomproject.data.EmotionEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmotionResultViewModel : ViewModel() {
    private val _emotionEntry = MutableStateFlow<EmotionEntry?>(null)
    val emotionEntry: StateFlow<EmotionEntry?> = _emotionEntry

    fun setEmotionEntry(entry: EmotionEntry) {
        Log.d("EmotionResultViewModel", "Setting emotionEntry: $entry")
        _emotionEntry.value = entry
    }
    fun updateEmotion(newEmotionIndex: Int) {
        _emotionEntry.value = _emotionEntry.value?.copy(emotion = newEmotionIndex)
    }
}