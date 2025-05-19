package com.example.diplomproject
import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun BreathingSquareScreen(
    onMenuClick: () -> Unit,
    navController: NavHostController,
    context: Context
) {
    var isRunning by remember { mutableStateOf(false) }
    var currentCycle by remember { mutableStateOf(0) }
    var totalCycles by remember { mutableStateOf(1) }
    var phaseIndex by remember { mutableStateOf(0) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (totalCycles > 0) currentCycle / totalCycles.toFloat() else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    val audioPlayer = remember { AudioPlayer(context, R.raw.breathing_music) }
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.pause()
            audioPlayer.reset()
        }
    }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            audioPlayer.start()
            while (currentCycle < totalCycles) {
                repeat(4) { phase ->
                    phaseIndex = phase
                    delay(4000L)
                }
                currentCycle++
            }
            isRunning = false
            currentCycle = 0
            phaseIndex = 0
            audioPlayer.pause()
            audioPlayer.reset()
        } else {
            audioPlayer.pause()
            audioPlayer.reset()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Назад",
                modifier = Modifier.size(24.dp)
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Квадратное дыхание",
                    fontSize = 28.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Практика квадратного дыхания помогает снять стресс и успокоиться.",
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                BreathingSquare(
                    phaseIndex = phaseIndex,
                    isRunning = isRunning,
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = Color(0xFFed9a66),
                    trackColor = Color(0xFFf6ddd0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$currentCycle / $totalCycles",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = totalCycles.toFloat(),
                    onValueChange = {
                        if (!isRunning) totalCycles = it.roundToInt()
                    },
                    valueRange = 1f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFed9a66),
                        inactiveTickColor = Color(0xFF4E756E),
                        activeTrackColor = Color(0xFFed9a66),
                        inactiveTrackColor = Color(0xFFf6ddd0),
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                IconButton(
                    onClick = {
                        isRunning = !isRunning
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    val iconRes = if (isRunning) R.drawable.ic_pause else R.drawable.ic_play
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isRunning) "Пауза" else "Начать",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun BreathingSquare(
    phaseIndex: Int,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val sideDurationMs = 4000L
    val progress = remember(phaseIndex) {
        Animatable(0f)
    }
    val phaseNames = listOf("Вдох", "Задержка", "Выдох", "Задержка")

    LaunchedEffect(phaseIndex, isRunning) {
        if (isRunning) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = sideDurationMs.toInt(), easing = LinearEasing)
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val baseStrokeWidth = 8.dp.toPx()
            val highlightStrokeWidth = 14.dp.toPx()
            val squareSize = size.minDimension
            val sideLength = squareSize - baseStrokeWidth

            val pathPoints = listOf(
                Offset(baseStrokeWidth / 2, baseStrokeWidth / 2),
                Offset(baseStrokeWidth / 2 + sideLength, baseStrokeWidth / 2),
                Offset(baseStrokeWidth / 2 + sideLength, baseStrokeWidth / 2 + sideLength),
                Offset(baseStrokeWidth / 2, baseStrokeWidth / 2 + sideLength)
            )

            // Рисуем базовый квадрат
            for (i in 0..3) {
                val start = pathPoints[i]
                val end = pathPoints[(i + 1) % 4]
                drawLine(
                    color = Color.Gray,
                    start = start,
                    end = end,
                    strokeWidth = baseStrokeWidth,
                    cap = StrokeCap.Round
                )
            }

            if (isRunning) {
                val currentStart = pathPoints[phaseIndex]
                val currentEnd = pathPoints[(phaseIndex + 1) % 4]

                val animatedEnd = Offset(
                    lerp(currentStart.x, currentEnd.x, progress.value),
                    lerp(currentStart.y, currentEnd.y, progress.value)
                )

                val gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFed9a66), Color(0xFF4E756E)),
                    start = currentStart,
                    end = animatedEnd
                )

                drawLine(
                    brush = gradient,
                    start = currentStart,
                    end = animatedEnd,
                    strokeWidth = highlightStrokeWidth * 2f,
                    cap = StrokeCap.Round,
                    alpha = 0.15f
                )

                drawLine(
                    brush = gradient,
                    start = currentStart,
                    end = animatedEnd,
                    strokeWidth = highlightStrokeWidth,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = Color(0xFFed9a66),
                    radius = highlightStrokeWidth / 2,
                    center = animatedEnd,
                    alpha = 0.9f
                )
                drawCircle(
                    color = Color(0xFFed9a66),
                    radius = highlightStrokeWidth,
                    center = animatedEnd,
                    alpha = 0.3f
                )
            }
        }

        Text(
            text = if (isRunning) phaseNames[phaseIndex] else "Старт",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}