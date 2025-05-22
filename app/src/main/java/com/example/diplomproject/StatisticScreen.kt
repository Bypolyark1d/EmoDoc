package com.example.diplomproject

import android.util.Log
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diplomproject.TextPreProccesor.getEmotionImageAndColor
import com.example.diplomproject.TextPreProccesor.getEmotionName
import com.example.diplomproject.data.EmotionEntry
import com.example.diplomproject.data.StressSurveyResult
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticScreen(navController: NavController) {
    val viewModel: StatisticViewModel = viewModel()
    val stressSurveyResults by viewModel.stressSurveyResults.observeAsState(emptyList())
    val emotionEntries by viewModel.emotionEntries.observeAsState(emptyList())
    val scope = rememberCoroutineScope()
    val tabs = listOf("Статистика", "История")
    val pagerState = rememberPagerState { tabs.size }

    val statisticsIcon = painterResource(id = R.drawable.ic_stat)
    val historyIcon = painterResource(id = R.drawable.ic_history)

    LaunchedEffect(Unit) {
        viewModel.getStressHistory()
        viewModel.getEmotionHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFFffece0),
            contentColor = Color(0xFF2A3439),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color(0xFFed9a66)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = Color(0xFFed9a66),
                    unselectedContentColor = Color(0xFF2A3439)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        val icon = if (index == 0) statisticsIcon else historyIcon
                        Image(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(if (pagerState.currentPage == index) Color(0xFFed9a66) else Color(0xFF2A3439))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pagerState.currentPage == index) Color(0xFFed9a66) else Color(0xFF2A3439)
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> StatisticTab(stressSurveyResults, emotionEntries)
                1 -> HistoryTab(stressSurveyResults, emotionEntries)
            }
        }
    }
}


@Composable
fun StatisticTab(
    stressSurveyResults: List<StressSurveyResult>,
    emotionEntries: List<EmotionEntry>
) {
    var isCardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(stressSurveyResults, emotionEntries) {
        isCardVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    StressLevelGraph(stressSurveyResults)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column {
                        Text(
                            text = "Общее распределение эмоций",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A3439),
                            modifier = Modifier.padding(16.dp)
                        )
                        EmotionDistributionChart(emotionEntries)
                    }
                }
            }
        }
    }
}
@Composable
fun EmotionDistributionChart(emotionEntries: List<EmotionEntry>) {
    val emotionCount = emotionEntries.groupingBy { it.emotion }.eachCount()
    val totalCount = emotionCount.values.sum().toFloat()
    val entries = emotionCount.map { (emotion, count) ->
        PieEntry(count.toFloat(), "")
    }

    val baseColors = listOf(
        Color(0xFF83B2D2),  
        Color(0xFFFFCE00),
        Color(0xFFFFB5B5),
        Color(0xFFFB5757),
        Color(0xFF007960),
        Color(0xFF51D8E7),
    )
    val emotionColors = emotionCount.keys.mapIndexed { index, emotion ->
        val base = baseColors[index % baseColors.size]
        val adjusted = base.copy(alpha = 0.85f)
        emotion to adjusted
    }.toMap()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFffece0), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        description.isEnabled = false
                        setUsePercentValues(true)
                        legend.isEnabled = false
                        setEntryLabelColor(android.graphics.Color.TRANSPARENT)
                        setHoleColor(android.graphics.Color.TRANSPARENT)
                        holeRadius = 60f
                        transparentCircleRadius = 65f
                        setCenterTextSize(18f)
                        setCenterTextColor(android.graphics.Color.DKGRAY)
                        setCenterText("Эмоции")
                        animateY(1200, Easing.EaseInOutQuad)
                    }
                },
                update = { chart ->
                    val dataSet = PieDataSet(entries, "")
                    dataSet.colors = emotionColors.values.map { it.toArgb() }
                    dataSet.sliceSpace = 4f
                    dataSet.valueTextColor = android.graphics.Color.TRANSPARENT
                    dataSet.valueTextSize = 0f
                    dataSet.setDrawValues(false)

                    val data = PieData(dataSet)
                    chart.data = data
                    chart.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emotionCount.forEach { (emotion, count) ->
                val percentage = (count.toFloat() / totalCount) * 100
                val color = emotionColors[emotion] ?: Color.Gray

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(color, RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = getEmotionName(emotion),
                            color = Color(0xFF2A3439),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${"%.1f".format(percentage)}%",
                            color = Color(0xFF707070),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun HistoryTab(
    stressSurveyResults: List<StressSurveyResult>,
    emotionEntries: List<EmotionEntry>
) {
    val combinedEntries = (stressSurveyResults.map { it as Any } + emotionEntries.map { it as Any })
        .sortedByDescending {
            when (it) {
                is StressSurveyResult -> it.timestamp
                is EmotionEntry -> it.timestamp
                else -> 0L
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (combinedEntries.isEmpty()) {
            Text(
                text = "Нет данных",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            combinedEntries.forEach { entry ->
                when (entry) {
                    is StressSurveyResult -> HistoryItemStress(entry)
                    is EmotionEntry -> HistoryItemEmotion(entry)
                }
            }
        }
    }
}

@Composable
fun HistoryItemEmotion(entry: EmotionEntry) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val date = dateFormat.format(Date(entry.timestamp))
    val (emotionIcon, emotionColor) = getEmotionImageAndColor(entry.emotion)
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(emotionColor.copy(alpha = 0.8f))
                    .padding(6.dp)
            ) {
                Text(
                    text = date,
                    color = Color(0xFFffece0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Image(
                        painter = emotionIcon,
                        contentDescription = null,
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = getEmotionName(entry.emotion),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A3439),
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(
                            id = if (isExpanded) R.drawable.arrow_up else R.drawable.arrow_down
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(0.6f),
                        colorFilter = ColorFilter.tint(Color(0xFF2A3439))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                AnimatedVisibility(visible = isExpanded) {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(Color(0xFFed9a66))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = entry.text,
                            color = Color(0xFF2A3439),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun HistoryItemStress(result: StressSurveyResult) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val date = dateFormat.format(Date(result.timestamp))

    val stressColor = when {
        result.stressLevel < 3 -> Color(0xFF76D7B2)
        result.stressLevel < 6 -> Color(0xFFDCD36A)
        else -> Color(0xFFE66761)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(stressColor.copy(alpha = 0.8f))
                    .padding(6.dp)
            ) {
                Text(
                    text = date,
                    color = Color(0xFFffece0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularStressIndicator(result.stressLevel)

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Уровень стресса:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A3439),
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
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
        if (dataToShow.isEmpty()) {
            Text("Нет данных для отображения", color = Color.Gray)
        } else {
            LineChartView(dataToShow)
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

