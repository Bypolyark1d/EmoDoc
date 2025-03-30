package com.example.diplomproject

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun EmotionAnalysisScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<String?>(null) }
    val maxChars = 1000
    val charCount = text.length

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад",
                        tint = Color(0xFF2A3439)
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Анализ настроения",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A3439),
                    modifier = Modifier.weight(1f)
                )
            }

            val isFocused = remember { mutableStateOf(false) }
            val borderColor by animateColorAsState(
                if (isFocused.value) Color(0xFFed9a66) else Color.Gray
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(Color(0xFFffece0), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.length <= maxChars) text = newText
                    },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF2A3439)),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .onFocusChanged { isFocused.value = it.isFocused }
                )
            }

            Text(
                text = "$charCount / $maxChars символов",
                fontSize = 14.sp,
                color = if (charCount > maxChars) Color.Red else Color(0xFFed9a66),
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { analysisResult = analyzeEmotion(text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFF2A3439), RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFffece0),
                    contentColor = Color(0xFF2A3439)
                )
            ) {
                Text(text = "Анализировать", fontSize = 18.sp)
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Вывод результата анализа
            analysisResult?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Результат анализа:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}




//
fun analyzeEmotion(text: String): String {
    return when {
        text.contains("счастлив", true) -> "😊 Радость"
        text.contains("грусть", true) -> "😢 Грусть"
        text.contains("злой", true) -> "😡 Злость"
        else -> "🤔 Не удалось определить эмоцию"
    }
}