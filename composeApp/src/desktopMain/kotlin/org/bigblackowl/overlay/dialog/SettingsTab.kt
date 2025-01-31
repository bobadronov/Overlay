package org.bigblackowl.overlay.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsTab(
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
    val isAnimationStatic by settingsViewModel.isAnimationStatic.collectAsState()
    val selectedImageIndex by settingsViewModel.singleImage.collectAsState()
    val images by settingsViewModel.selectedImages.collectAsState()
    val savedSpeed by settingsViewModel.animationSpeed.collectAsState()
    val savedIsMinutes by settingsViewModel.savedIsMinutes.collectAsState()
    var speed by remember { mutableStateOf(savedSpeed) }
    var isMinutes by remember { mutableStateOf(false) } // Переключатель между секундами и минутами
    val state: LazyListState = rememberLazyListState()
    LaunchedEffect(savedSpeed, isMinutes) {
        // Устанавливаем начальное значение в секундах или минутах
        isMinutes = savedIsMinutes
        speed = savedSpeed
    }

    // Вычисляем текущее значение в секундах или минутах
    val displayTime = if (isMinutes) {
        (speed / 60000).toInt() // Конвертируем миллисекунды в минуты
    } else {
        (speed / 1000).toInt() // Конвертируем миллисекунды в секунды
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Static Image Toggle
        Row(
            modifier = Modifier.fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Статична картинка", color = Color.White)
            Switch(
                checked = isAnimationStatic,
                onCheckedChange = { settingsViewModel.toggleAnimationStatic() }
            )
        }
        AnimatedVisibility(!isAnimationStatic) {
            // Ползунок и переключатель для изменения скорости
            Column(
                modifier = Modifier.fillMaxWidth().border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                ) .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Змінити швидкість", color = Color.White)

                // Ползунок
                Slider(
                    value = speed,
                    onValueChange = { newSpeed ->
                        speed = newSpeed
                        settingsViewModel.setSpeed(newSpeed.roundToInt().toFloat(), isMinutes)
                    },
                    valueRange = if (isMinutes) 60000f..1200000f else 2000f..60000f, // 1-20 хвилин для минут и 2-60 секунд для секунд
                    steps = if (isMinutes) 19 else 58, // Шаги для секунд (2-60) или минут (1-20)
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                // Переключатель между секундами и минутами
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isMinutes) "В хвилинах -> " else "В секундах -> ",
                        color = Color.White
                    )
                    Switch(
                        checked = isMinutes,
                        onCheckedChange = { isChecked ->
                            isMinutes = isChecked
                            // Пересчитываем скорость при переключении
                            val convertedSpeed = if (isMinutes) {
                                (displayTime * 60000).toFloat() // Переводим из минут в миллисекунды
                            } else {
                                (displayTime * 1000).toFloat()// Переводим из секунд в миллисекунды
                            }
                            speed = convertedSpeed
                            settingsViewModel.setSpeed(speed, isMinutes)
                        }
                    )
                }

                // Показать, что выбрано
                Text(
                    "Зміна контенту кожні: $displayTime ${if (isMinutes) "хв" else "с"}",
                    color = Color.White
                )
            }
        }
        AnimatedVisibility(isAnimationStatic) {
            LazyColumn(
                state = state,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text("Виберіть картинку", color = Color.White)
                }
                items(images.size) { index ->
                    LazyListItem(imageUrl = images[index], item = {
                        RadioButton(
                            selected = selectedImageIndex == index,
                            onClick = { settingsViewModel.selectSingleImage(index) }
                        )
                    })
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}