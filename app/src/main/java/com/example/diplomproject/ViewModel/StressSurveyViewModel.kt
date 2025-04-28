package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diplomproject.data.StressSurveyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StressSurveyViewModel : ViewModel() {
    private val _stressResult = MutableStateFlow<StressSurveyResult?>(null)
    val stressResult: StateFlow<StressSurveyResult?> = _stressResult

    fun setStressResult(result: StressSurveyResult) {
        Log.d("StressSurveyViewModel", "Setting stressResult: $result")
        _stressResult.value = result
    }
    fun updateStressLevel(newLevel: Float) {
        _stressResult.value = _stressResult.value?.copy(stressLevel = newLevel)
    }
}