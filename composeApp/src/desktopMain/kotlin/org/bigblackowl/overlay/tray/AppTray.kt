package org.bigblackowl.overlay.tray

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import overlay.composeapp.generated.resources.Res
import overlay.composeapp.generated.resources.app_icon

@Composable
fun ApplicationScope.AppTray(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onChangeVisibility: () -> Unit
) {

    Tray(
        icon = painterResource(Res.drawable.app_icon),
        menu = {
            Item("Налаштування", onClick = {
                settingsViewModel.onShowSettingsClicked(value = true)
            })
            Separator()
            Item("Exit", onClick = ::exitApplication)
        },
        onAction = { onChangeVisibility.invoke() }
    )
}