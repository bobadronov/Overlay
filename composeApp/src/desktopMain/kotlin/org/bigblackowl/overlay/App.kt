package org.bigblackowl.overlay

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.delay
import org.bigblackowl.overlay.screen.ImageScreen
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    settingsViewModel: SettingsViewModel = koinViewModel(), // ✅ создаём здесь,
    onChangeWindowPosition: (
        WindowPosition.Absolute
    ) -> Unit,
    state: WindowState,
) {
    val windowPosition by settingsViewModel.windowPosition.collectAsState()
    var showBorder by remember { mutableStateOf(false) }

    LaunchedEffect(windowPosition) {
        println("App windowPosition: $windowPosition")
        onChangeWindowPosition(windowPosition)
    }

    LaunchedEffect(state.position) {
        showBorder = true
        if (state.position.isSpecified) {
            delay(5000)
            settingsViewModel.savePosition(
                WindowPosition.Absolute(state.position.x, state.position.y)
            )
            showBorder = false
        }
    }
        Surface(
            modifier = Modifier.fillMaxSize()
                .border(
                    1.dp,
                    if (showBorder) Color.Red else Color.Transparent,
                    RoundedCornerShape(10.dp)
                )
                .padding(5.dp),
            color = Color.Transparent
        ) {
            ImageScreen(
                settingsViewModel = settingsViewModel,
            )
        }
}
