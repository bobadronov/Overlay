package org.bigblackowl.overlay.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImageScreen(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val savedSpeed by settingsViewModel.animationSpeed.collectAsState()
    val isAnimationStatic by settingsViewModel.isAnimationStatic.collectAsState()
    val singleImage by settingsViewModel.singleImage.collectAsState()
    val images by settingsViewModel.selectedImages.collectAsState()

    var currentIndex by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf(images.getOrNull(0)) }
    var isVisible by remember { mutableStateOf(true) }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "Fade Animation"
    )

    // Запускаем цикл анимации только если isAnimationStatic == false
    LaunchedEffect(images, savedSpeed, isAnimationStatic) {
        if (!isAnimationStatic && images.size > 1 && savedSpeed > 0) {
            while (true) {
                isVisible = false  // Затемнение
                delay(1100)        // Ждем анимацию исчезновения
                currentIndex = (currentIndex + 1) % images.size
                currentUrl = images[currentIndex]
                isVisible = true   // Появление
                delay((savedSpeed - 1100).toLong())
//                println("LOG TIME: ${LocalTime.now()}, delay: ${(savedSpeed - 1100).toLong()}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .graphicsLayer { this.alpha = alpha },
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = isAnimationStatic) { staticMode ->
            if (staticMode) {
                val validSingleImage = singleImage?.takeIf { it in images.indices }?.let { images[it] }
                if (!validSingleImage.isNullOrEmpty()) {
                    ShowIcon(validSingleImage)
                } else {
                    FalseIcon()
                }
            } else {
                if (!currentUrl.isNullOrEmpty()) {
                    ShowIcon(currentUrl!!)
                } else {
                    FalseIcon()
                }
            }
        }
    }
}

@Composable
fun ShowIcon(url:String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun FalseIcon() {
    Icon(
        Icons.Default.Close,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(.7f),
        tint = Color.Red
    )
}
