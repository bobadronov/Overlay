package org.bigblackowl.overlay.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.launch
import org.bigblackowl.overlay.data.Settings
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import overlay.composeapp.generated.resources.Res
import overlay.composeapp.generated.resources.app_icon

@Composable
fun SettingsDialog(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onVisibilityChange: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val visible by settingsViewModel.showSettings.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(visible) {
        onVisibilityChange(!visible)
    }

    DialogWindow(
        onCloseRequest = {
            settingsViewModel.onShowSettingsClicked(value = false)
        },
        state = rememberDialogState(size = DpSize(width = 800.dp, height = 800.dp)),
        visible = visible,
        title = Settings.APP_NAME,
        icon = painterResource(Res.drawable.app_icon),
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { SnackbarHost(it) },
            backgroundColor = Color.DarkGray
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    backgroundColor = Color.Gray,
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Контент") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Налаштування") }
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                when (selectedTab) {
                    0 -> ContentTab(settingsViewModel, showError = { message ->
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(message)
                        }
                    })

                    1 -> SettingsTab(
                        settingsViewModel
                    )
                }
            }
        }
    }
}

