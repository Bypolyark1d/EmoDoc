package com.example.diplomproject.TextPreProccesor
import android.content.Context
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

fun loadWordIndexFromAssets(context: Context, fileName: String): Map<String, Int>? {
    try {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        Log.d("Assets", "File content loaded: $jsonString")

        val tempMap: Map<String, Any> = Gson().fromJson(jsonString, object : TypeToken<Map<String, Any>>() {}.type)

        val wordIndex = tempMap.mapNotNull {
            val value = it.value as? Double
            if (value != null) {
                it.key to value.toInt()
            } else {
                null
            }
        }.toMap()

        Log.d("Assets", "Parsed word index size: ${wordIndex.size}")
        return wordIndex
    } catch (e: Exception) {
        Log.e("Assets", "Error loading or parsing word index", e)
        return null
    }
}