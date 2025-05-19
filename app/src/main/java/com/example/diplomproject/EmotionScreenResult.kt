package com.example.diplomproject
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.diplomproject.TextPreProccesor.getEmotionImageAndColor
import com.example.diplomproject.TextPreProccesor.getEmotionIndex
import com.example.diplomproject.TextPreProccesor.getEmotionName
import com.example.diplomproject.ViewModel.EmotionResultViewModel
import com.example.diplomproject.ViewModel.EntryViewModel
import com.example.diplomproject.ViewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionResultScreen(navController: NavHostController,
                        viewModel: EmotionResultViewModel = viewModel(), profileViewModel: ProfileViewModel = viewModel()) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )
    val scope = rememberCoroutineScope()
    val entryViewModel: EntryViewModel = viewModel()
    val showDialog = remember { mutableStateOf(false) }
    val emotionEntryState = viewModel.emotionEntry.collectAsState()
    val emotionEntry = emotionEntryState.value
    val (emotionImage, backgroundColor) = getEmotionImageAndColor(emotionEntry?.emotion ?: -1)
    val emotionName = getEmotionName(emotionEntry?.emotion ?: -1)
    val emotionEmoji = getEmotionEmoji(emotionName)
    val emotionDescription = getEmotionDescription(emotionName)

    if (emotionEntry == null) {
        Log.d("EmotionResultScreen", "EmotionEntry is null")
    } else {
        Log.d("EmotionResultScreen", "emotionEntry: $emotionEntry")
    }

    var isUIVisible by remember { mutableStateOf(false) }

    LaunchedEffect(emotionEntry) {
        isUIVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = isUIVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 1000)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 1000)
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 16.dp)
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.card_profile),
                        contentDescription = "Эмоция",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                    ) {
                        Text(
                            text = "Твоя эмоция",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A3439),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Image(
                            painter = emotionImage,
                            contentDescription = emotionName,
                            colorFilter = ColorFilter.tint(backgroundColor),
                            modifier = Modifier.size(100.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = emotionDescription,
                            fontSize = 16.sp,
                            color = Color(0xFF2A3439),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        Log.d("EmotionResultScreen", "Кнопка 'Далее' нажата")
                        scope.launch {
                            bottomSheetState.show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFffece0)),
                    modifier = Modifier.padding(16.dp)
                        .width(150.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color(0xFF2A3439))
                ) {
                    Text(text = "Далее", color = Color(0xFF2A3439))
                }
            }
            if (bottomSheetState.isVisible) {
                ModalBottomSheet(
                    containerColor = Color(0xFF4E756E),
                    sheetState = bottomSheetState,
                    onDismissRequest = {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        Text(
                            text = "Согласны ли вы с анализом?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    emotionEntry?.let {
                                        profileViewModel.incrementEntryCount()
                                        profileViewModel.updateCurrentEmotion(it.emotion)
                                        entryViewModel.saveEmotionEntry(it.text, it.emotion)
                                    }
                                    navController.navigate("analize")
                                },
                                modifier = Modifier.padding(end = 50.dp)
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFed9a66
                                    )
                                )
                            ) {
                                Text(text = "Да", color = Color.White)
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        bottomSheetState.hide()
                                        showDialog.value = true
                                    }
                                },
                                modifier = Modifier.width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFed9a66
                                    )
                                )

                            ) {
                                Text(text = "Нет", color = Color.White)
                            }
                        }
                    }

                }
            }
            if (showDialog.value) {
                val emotions = listOf("Грусть", "Радость", "Любовь", "Злость", "Страх", "Удивление")
                EmotionSelectionDialog(
                    emotions = emotions,
                    onEmotionSelected = { selectedEmotion ->
                        val index = emotions.indexOf(selectedEmotion)
                        viewModel.updateEmotion(index)
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun EmotionSelectionDialog(
    emotions: List<String>,
    onEmotionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState { emotions.size }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Выберите эмоцию",
                color = Color(0xFF2A3439),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp)
                ) { page ->
                    val emotion = emotions[page]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEmotionSelected(emotion)
                                onDismiss()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val (emotionPainter, emotionColor) = getEmotionImageAndColor(getEmotionIndex(emotion))
                        Image(
                            painter = emotionPainter,
                            contentDescription = "Эмоция",
                            colorFilter = ColorFilter.tint(emotionColor),  // <-- Вот тут окрашивание!
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emotion,
                            fontSize = 22.sp,
                            color = Color(0xFF2A3439),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(emotions.size) { index ->
                        val color = if (pagerState.currentPage == index) Color.DarkGray else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFffece0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(2.dp, Color(0xFF2A3439))
                ) {
                    Text(
                        text = "Назад",
                        color = Color(0xFF2A3439),
                        fontSize = 22.sp
                    )
                }
            }
        },
        containerColor = Color(0xFFffece0),
        shape = RoundedCornerShape(16.dp),
        titleContentColor = Color(0xFF2A3439),
        textContentColor = Color(0xFF2A3439)
    )
}

fun getEmotionEmoji(emotion: String): String {
    return when {
        "Грусть" in emotion -> "😢"
        "Радость" in emotion -> "😊"
        "Любовь" in emotion -> "😍"
        "Злость" in emotion -> "😡"
        "Страх" in emotion -> "😨"
        "Удивление" in emotion -> "😲"
        else -> "🤔"
    }
}

@Preview(showBackground = true)
@Composable
fun EmotionResultScreenPreview() {
    EmotionResultScreen(
        navController = rememberNavController(),
        viewModel = EmotionResultViewModel(),
        profileViewModel = ProfileViewModel()
    )
}
fun getEmotionDescription(emotion: String): String {
    return when {
        "Грусть" in emotion -> "Ты чувствуешь грусть. Попробуй отдохнуть и позаботься о себе"
        "Радость" in emotion -> "У тебя радостное настроение! Отличный момент, чтобы поделиться им с кем-то"
        "Любовь" in emotion -> "Ты испытываешь любовь — пусть это чувство придаёт сил"
        "Злость" in emotion -> "Похоже, ты злишься. Сделай паузу, попробуй подышать глубже"
        "Страх" in emotion -> "Ты испугался или обеспокоен. Вспомни, что тебе помогает чувствовать себя в безопасности"
        "Удивление" in emotion -> "Что-то удивило тебя! Иногда перемены — это хорошо"
        else -> "Эмоция не определена. Попробуй снова"
    }
}