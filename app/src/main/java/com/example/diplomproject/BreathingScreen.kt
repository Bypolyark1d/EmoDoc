package com.example.diplomproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BreathingScreen(
    onMenuClick: () -> Unit,
    onPracticeClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
    ) {
        TopBar(title = "Дыхательные практики", onMenuClick = onMenuClick)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Зачем нужны дыхательные практики?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A3439)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Дыхательные техники помогают снять стресс, улучшить концентрацию и бороться с тревожностью.",
                    fontSize = 16.sp,
                    color = Color(0xFF2A3439)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PracticeCard(
            title = "Расслабляющий ритм",
            description = "Простая и эффективная методика для быстрого расслабления, разработанная доктором Вейлом. Помогает успокоиться и подготовиться ко сну за несколько циклов.",
            imageRes = R.drawable.card_profile,
            backgroundColor = Color(0xFFffece0),
            onClick = { onPracticeClick(Screens.Breathing478.route) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PracticeCard(
            title = "Квадратное дыхание",
            description = "Простая техника для моментального успокоения и восстановления концентрации. Особенно эффективна при стрессе и панических состояниях.",
            imageRes = R.drawable.card_profile,
            backgroundColor = Color(0xFFffece0),
            onClick = { onPracticeClick(Screens.BreathingSquare.route) }
        )
    }
}

@Composable
fun PracticeCard(
    title: String,
    description: String,
    imageRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}
