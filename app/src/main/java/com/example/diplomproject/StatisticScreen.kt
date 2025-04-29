package com.example.diplomproject

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.diplomproject.ViewModel.StatisticViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diplomproject.data.StressSurveyResult
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatisticScreen(navController: NavController) {
    val viewModel: StatisticViewModel = viewModel()
    val stressSurveyResults by viewModel.stressSurveyResults.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.getStressSurveyResults(14)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = "Статистика",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            var isCardVisible by remember { mutableStateOf(false) }

            LaunchedEffect(stressSurveyResults) {
                isCardVisible = true
            }

            AnimatedVisibility(
                visible = isCardVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                ),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    StressLevelGraph(stressSurveyResults)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StressLevelGraph(stressSurveyResults: List<StressSurveyResult>) {
    var daysToShow by remember { mutableStateOf(7) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Уровень стресса",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2A3439)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "7/14 обследований",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A3439),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = daysToShow == 14,
                onCheckedChange = { isChecked ->
                    daysToShow = if (isChecked) 14 else 7
                    Log.d("StressGraph", "daysToShow = $daysToShow")
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2A3439),
                    uncheckedThumbColor = Color(0xFF2A3439),
                    checkedTrackColor = Color(0xFFed9a66),
                    uncheckedTrackColor = Color(0xFF4E756E),
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val sortedData = stressSurveyResults.sortedByDescending { it.timestamp }

        val dataToShow = if (daysToShow == 7) {
            sortedData.take(7).reversed()
        } else {
            sortedData.take(14).reversed()
        }
        Log.d("StressGraph", "Всего записей: ${stressSurveyResults.size}")
        Log.d("StressGraph", "Отображаем записей: ${dataToShow.size}")
        if (dataToShow.isEmpty()) {
            Text("Нет данных для отображения", color = Color.Gray)
        } else {
            LineChartView(dataToShow)
        }
        dataToShow.forEachIndexed { index, result ->
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(result.timestamp))
            Log.d("StressGraph", "[$index] Дата: $date, Стресс: ${result.stressLevel}")
        }
    }
}

@Composable
fun LineChartView(data: List<StressSurveyResult>) {
    val entries = data.mapIndexed { index, result ->
        Entry(index.toFloat(), result.stressLevel)
    }

    val dataSet = LineDataSet(entries, "Уровень стресса").apply {
        color = Color(0xFFed9a66).toArgb()
        setCircleColor(Color(0xFF4E756E).toArgb())
        lineWidth = 3f
        circleRadius = 5f
        setDrawCircleHole(false)
        valueTextColor = Color(0xFF2A3439).toArgb()
        valueTextSize = 0f
        setDrawValues(false)
        setDrawFilled(true)
        fillColor = Color(0xFFed9a66).toArgb()
        fillAlpha = 70
    }

    val lineData = LineData(dataSet)

    val xLabels = data.map { result ->
        val date = Date(result.timestamp)
        val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        dateFormat.format(date)
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = lineData
                setDrawGridBackground(false)
                setTouchEnabled(true)
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.isEnabled = true
                axisRight.isEnabled = false

                // Ось X
                xAxis.apply {
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    setGridLineWidth(0.5f)
                    setGridColor(Color(0xFF4E756E).copy(alpha = 0.3f).toArgb())
                    setLabelCount(data.size, true)
                    valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xLabels)
                    textSize = 16f
                    textColor = Color(0xFF4E756E).toArgb()
                    axisLineColor = Color(0xFF4E756E).toArgb()
                    axisLineWidth = 1f
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    setGridLineWidth(0.5f)
                    setGridColor(Color(0xFF4E756E).copy(alpha = 0.3f).toArgb())
                    textColor = Color(0xFF4E756E).toArgb()
                    textSize = 16f
                    axisLineColor = Color(0xFF4E756E).toArgb()
                    axisLineWidth = 1f
                }

                animateX(1000)
                animateY(1000)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        update = { chart ->
            val entries = data.mapIndexed { index, result ->
                Entry(index.toFloat(), result.stressLevel)
            }

            val dataSet = LineDataSet(entries, "Уровень стресса").apply {
                color = Color(0xFFed9a66).toArgb()
                setCircleColor(Color(0xFF4E756E).toArgb())
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircleHole(false)
                valueTextColor = Color(0xFF2A3439).toArgb()
                valueTextSize = 0f
                setDrawValues(false)
                setDrawFilled(true)
                fillColor = Color(0xFFed9a66).toArgb()
                fillAlpha = 70
            }

            val lineData = LineData(dataSet)

            chart.data = lineData
            chart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                data.map { result ->
                    val date = Date(result.timestamp)
                    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                    dateFormat.format(date)
                }
            )

            chart.notifyDataSetChanged()
            chart.invalidate()

            chart.animateX(1000)
            chart.animateY(1000)
        }
    )
}

