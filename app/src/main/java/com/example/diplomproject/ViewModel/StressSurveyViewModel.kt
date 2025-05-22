package com.example.diplomproject.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diplomproject.data.StressSurveyResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class StressSurveyViewModel : ViewModel() {
    private val _stressResult = MutableStateFlow<StressSurveyResult?>(null)
    open val stressResult: StateFlow<StressSurveyResult?> = _stressResult

    fun setStressResult(result: StressSurveyResult) {
        Log.d("StressSurveyViewModel", "Setting stressResult: $result")
        _stressResult.value = result
    }
}