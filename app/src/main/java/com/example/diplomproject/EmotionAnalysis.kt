package com.example.diplomproject

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diplomproject.ViewModel.EmotionAnalysisViewModel
import com.example.diplomproject.ViewModel.EmotionResultViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun EmotionAnalysisScreen(navController: NavHostController,resultViewModel: EmotionResultViewModel = viewModel(), viewModel: EmotionAnalysisViewModel = viewModel()) {
    var text by remember { mutableStateOf("") }
    val maxChars = 1000
    val isLoading = remember { mutableStateOf(false) }
    val emotionResult by viewModel.emotionResult.collectAsState()
    val error by viewModel.error.collectAsState()

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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A3439),
                    modifier = Modifier.weight(1f)
                )
            }

            // Карточка с описанием анализа
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 16.dp)
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E)),
                shape = RectangleShape
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.card_profile),
                        contentDescription = "Анализ настроения",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Column {
                            Text(
                                text = "Описание анализа",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Введите ваш текст, и наш алгоритм проанализирует ваше эмоциональное состояние.",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            val isFocused = remember { mutableStateOf(false) }
            val borderColor by animateColorAsState(
                if (isFocused.value) Color(0xFFed9a66) else Color.Gray
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
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
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
                    isLoading.value = true
                    viewModel.analyzeText(text,userId)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFed9a66),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Анализировать", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            error?.let {
                Text(text = it, color = Color.Red)
            }
            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            LaunchedEffect(emotionResult) {
                emotionResult?.let {
                    Log.d("EmotionAnalysisScreen", "Analysis result received: $it")
                    isLoading.value = false
                    resultViewModel.setEmotionEntry(it)
                    navController.navigate("emotion_result")
                }
            }
        }
    }
}