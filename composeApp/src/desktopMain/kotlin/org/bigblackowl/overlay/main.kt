package org.bigblackowl.overlay

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.bigblackowl.overlay.data.Settings
import org.bigblackowl.overlay.di.initKoin
import org.bigblackowl.overlay.dialog.SettingsDialog
import org.bigblackowl.overlay.tray.AppTray
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext
import overlay.composeapp.generated.resources.Res
import overlay.composeapp.generated.resources.app_icon

fun main() = application(exitProcessOnExit = false) {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
    var isDialogVisible by remember { mutableStateOf(true) }
    var isWindowVisible by remember { mutableStateOf(true) }
    window.minimumSize = Dimension(350, 600)
    val state = rememberWindowState(
        size = DpSize(400.dp, 250.dp)
    )

    LaunchedEffect(state) {
        println("${state.position}")
        println("${state.position.isSpecified}")
        println("${state.isMinimized}")
        println("${state.placement}")
    }

    key(state) {
        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = Settings.APP_NAME,
            icon = painterResource(Res.drawable.app_icon),
            transparent = true,
            undecorated = true,
            alwaysOnTop = isDialogVisible,
            visible = isWindowVisible
        ) {
            WindowDraggableArea {
                App(
                    onChangeWindowPosition = { newPosition ->
                        println("onChangeWindowPosition: $newPosition")
                        state.position = newPosition
                        println("state: ${state.position}, ${state.position == newPosition}")
                    },
                    state = state,
                )
            }
            AppTray(
                onChangeVisibility = {
                    isWindowVisible = !isWindowVisible
                }
            )
            SettingsDialog(onVisibilityChange = {
//                isDialogVisible = it
            })
        }
    }
}
