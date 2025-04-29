package com.example.diplomproject.TextPreProccesor
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.diplomproject.R
import opennlp.tools.stemmer.PorterStemmer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

fun cleanText(text: String, stopWords: Set<String>): String {
    val stemmer = PorterStemmer()
    var cleanedText = text.lowercase(Locale.getDefault())

    Log.d("TextClassifier", "Original text: $text")

    cleanedText = cleanedText.replace(Regex("http\\S+|www\\.\\S+"), "")
    cleanedText = cleanedText.replace(Regex("[^\\w\\s]"), "")
    cleanedText = cleanedText.replace(Regex("\\w*\\d\\w*"), "")

    Log.d("TextClassifier", "Text after cleaning: $cleanedText")

    val tokens = cleanedText.split("\\s+".toRegex())
    val cleanedTokens = tokens
        .filter { it.isNotBlank() && it !in stopWords }
        .map { stemmer.stem(it) }

    Log.d("TextClassifier", "Tokens after stemming: $cleanedTokens")

    return cleanedTokens.joinToString(" ")
}

fun convertTextToFloatArray(text: String, wordIndex: Map<String, Int>, maxLen: Int = 500): FloatArray {
    val tokens = text.split(" ")
    val sequence = tokens.map { word -> wordIndex[word] ?: 0 }

    Log.d("TextClassifier", "Token sequence: $sequence")

    val padded = IntArray(maxLen) { 0 }
    for (i in sequence.indices) {
        if (i < maxLen) padded[i] = sequence[i]
    }

    Log.d("TextClassifier", "Padded sequence: ${padded.joinToString(", ")}")

    return padded.map { it.toFloat() }.toFloatArray()
}

fun loadStopWords(context: Context): Set<String> {
    val stopWords: MutableSet<String> = mutableSetOf()
    try {
        val inputStream = context.assets.open("stopwords.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.forEachLine { stopWords.add(it.trim()) }
        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    Log.d("TextClassifier", "Stop words loaded: ${stopWords.size} stop words")
    return stopWords
}
fun getEmotionName(emotionIndex: Int): String {
    return when (emotionIndex) {
        0 -> "Грусть"
        1 -> "Радость"
        2 -> "Любовь"
        3 -> "Злость"
        4 -> "Страх"
        5 -> "Удивление"
        else -> "Неизвестно"
    }
}
fun getEmotionIndex(name: String): Int {
    return when (name) {
        "Грусть" -> 0
        "Радость" -> 1
        "Любовь" -> 2
        "Злость" -> 3
        "Страх" -> 4
        "Удивление" -> 5
        else -> -1
    }
}
@Composable
fun getEmotionImageAndColor(emotion: Int): Pair<Painter, Color> {
    return when (emotion) {
        0 -> painterResource(id = R.drawable.sad) to Color(0xFF3498DB) // Грусть
        1 -> painterResource(id = R.drawable.joy) to Color(0xFFF1C40F) // Радость
        2 -> painterResource(id = R.drawable.love) to Color(0xFFE74C3C) // Любовь
        3 -> painterResource(id = R.drawable.angry) to Color(0xFFC0392B) // Злость
        4 -> painterResource(id = R.drawable.fear) to Color(0xFF7F8C8D) // Страх
        5 -> painterResource(id = R.drawable.surprise) to Color(0xFF9B59B6) // Удивление
        else -> painterResource(id = R.drawable.neutral) to Color(0xFF95A5A6) // Нейтральный
    }
}