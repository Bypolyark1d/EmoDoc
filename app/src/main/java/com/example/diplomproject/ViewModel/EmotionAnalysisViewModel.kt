    package com.example.diplomproject.ViewModel

    import android.app.Application
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.diplomproject.TextPreProccesor.TextClassifier
    import com.example.diplomproject.TextPreProccesor.cleanText
    import com.example.diplomproject.TextPreProccesor.convertTextToFloatArray
    import com.example.diplomproject.TextPreProccesor.loadStopWords
    import com.example.diplomproject.TextPreProccesor.loadWordIndexFromAssets
    import com.example.diplomproject.data.EmotionEntry
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    class EmotionAnalysisViewModel(application: Application) : AndroidViewModel(application) {

        private val context = application.applicationContext
        private val classifier = TextClassifier(context.assets)
        private var wordIndex: Map<String, Int>? = null

        private val _emotionResult = MutableStateFlow<EmotionEntry?>(null)
        val emotionResult: StateFlow<EmotionEntry?> = _emotionResult

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error

        init {
            viewModelScope.launch {
                wordIndex = loadWordIndexFromAssets(context, "word_index.json")
                if (wordIndex == null) {
                    _error.value = "Не удалось загрузить словарь индексов"
                }
            }
        }

        fun analyzeText(originalText: String, userId: String) {
            viewModelScope.launch {
                try {
                    val stopWords = loadStopWords(context)
                    val cleaned = cleanText(originalText, stopWords)
                    val input = convertTextToFloatArray(cleaned, wordIndex ?: return@launch)

                    val result = classifier.classify(input)
                    val emotionIndex = result.indices.maxByOrNull { result[it] } ?: -1
                    val entry = EmotionEntry(
                        userId = userId,
                        text = originalText,
                        emotion = emotionIndex,
                        timestamp = System.currentTimeMillis()
                    )

                    _emotionResult.value = entry

                } catch (e: Exception) {
                    e.printStackTrace()
                    _error.value = "Ошибка при анализе текста: ${e.message}"
                }
            }
        }
    }