package com.example.diplomproject.TextPreProccesor

import android.content.res.AssetManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TextClassifier(private val assetManager: AssetManager) {

    private var tflite: Interpreter? = null

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile("cnn_model_ru.tflite")
            tflite = Interpreter(modelBuffer)
            Log.d("TextClassifier", "Модель успешно загружена.")
        } catch (e: Exception) {
            Log.e("TextClassifier", "Ошибка загрузки модели: ${e.message}")
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    suspend fun classify(input: FloatArray): FloatArray = withContext(Dispatchers.IO) {
        if (tflite == null) {
            Log.e("TextClassifier", "Модель не инициализирована!")
            return@withContext FloatArray(0)
        }
        val inputTensor = arrayOf(input) // [1][500]
        val outputTensor = Array(1) { FloatArray(6) } // [1][6]

        Log.d("TextClassifier", "Классификация. Вход: ${input.take(10).joinToString()}...") // лог первые 10 значений

        tflite?.run(inputTensor, outputTensor)

        val result = outputTensor[0]
        Log.d("TextClassifier", "Результат: ${result.joinToString(", ")}")

        return@withContext result
    }

    fun close() {
        tflite?.close()
    }
}
