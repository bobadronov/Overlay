package org.bigblackowl.overlay.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.bigblackowl.overlay.util.selectFiles
import org.bigblackowl.overlay.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.awt.datatransfer.DataFlavor
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ContentTab(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    showError: (String) -> Unit
) {
    val state: LazyListState = rememberLazyListState()
    val images by settingsViewModel.selectedImages.collectAsState()

    var isDragging by remember { mutableStateOf(false) }

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                isDragging = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDragging = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val droppedText = event.awtTransferable.let {
                    if (it.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        val files = it.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                        files.map { file -> file.absolutePath }
                    } else emptyList()
                }

                if (droppedText.isNotEmpty()) {
                    if (isValidFormat(droppedText)) {
                        settingsViewModel.updateUrls(images + droppedText)
                    } else showError("INVALID URL FORMAT")
                }
                return true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = if (isDragging) 2.dp else 1.dp,
                    color = if (isDragging) Color.Green else Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { true },
                    target = dragAndDropTarget
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isDragging) {
                Text("Введіть URL або виберіть файл", color = Color.White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var newUrl by remember { mutableStateOf(TextFieldValue("")) }

                    TextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newUrl.text.isNotBlank()) {
                                    if (isValidUrl(newUrl.text)) {
                                        settingsViewModel.addUrl(newUrl.text)
                                        newUrl =
                                            TextFieldValue("") // Очистка після успішного введення
                                    } else {
                                        showError("INVALID URL FORMAT")
                                        newUrl = newUrl.copy(
                                            selection = TextRange(
                                                0,
                                                newUrl.text.length
                                            )
                                        ) // Виділення всього тексту
                                    }
                                } else {
                                    showError("INVALID URL FORMAT")
                                    newUrl = newUrl.copy(
                                        selection = TextRange(
                                            0,
                                            newUrl.text.length
                                        )
                                    ) // Виділення всього тексту
                                }
                            }
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = darkTextFieldColors()
                    )

                    DefaultButton(
                        text = "Додати",
                        onClick = {
                            if (newUrl.text.isNotBlank()) {
                                if (isValidUrl(newUrl.text)) {
                                    settingsViewModel.addUrl(newUrl.text)
                                    newUrl = TextFieldValue("") // Очистка після успішного введення
                                } else {
                                    showError("INVALID URL FORMAT")
                                    newUrl = newUrl.copy(
                                        selection = TextRange(
                                            0,
                                            newUrl.text.length
                                        )
                                    ) // Виділення всього тексту
                                }
                            } else {
                                showError("INVALID URL FORMAT")
                                newUrl = newUrl.copy(
                                    selection = TextRange(
                                        0,
                                        newUrl.text.length
                                    )
                                ) // Виділення всього тексту
                            }
                        }
                    )
                }
                Text("або", color = Color.White)
                DefaultButton(
                    text = "Обрати локальні файли",
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    onClick = {
                        settingsViewModel.onShowSettingsClicked(value = false)
                        val selectedFiles = selectFiles()
                        if (selectedFiles.isNotEmpty()) {
                            settingsViewModel.updateUrls(images + selectedFiles)
                        }
                        settingsViewModel.onShowSettingsClicked(value = true)
                    },
                )

                Text("або перетягніть файли сюди", color = Color.White)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = .5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Text("Перетягніть файли сюди", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize()
        ) {
            items(images.size) { index ->
                LazyListItem(imageUrl = images[index], item = {
                    DefaultButton(
                        onClick = { settingsViewModel.removeUrl(images[index]) },
                        text = "Видалити",
                        icon = {
                            Icon(Icons.Outlined.Delete, contentDescription = null)
                        })
                })
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

@Composable
fun darkTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        textColor = Color.White,
        disabledTextColor = Color.Gray,
        backgroundColor = Color(0xFF2C2C2C), // Темний фон
        cursorColor = Color(0xFFBB86FC), // Фіолетовий курсор
        errorCursorColor = Color.Red,
        focusedIndicatorColor = Color(0xFFBB86FC), // Фіолетова лінія при фокусі
        unfocusedIndicatorColor = Color.Gray, // Сіра лінія без фокусу
        disabledIndicatorColor = Color.DarkGray,
        errorIndicatorColor = Color.Red,
        leadingIconColor = Color.White.copy(alpha = 0.7f),
        disabledLeadingIconColor = Color.Gray,
        errorLeadingIconColor = Color.Red,
        trailingIconColor = Color.White.copy(alpha = 0.7f),
        disabledTrailingIconColor = Color.Gray,
        errorTrailingIconColor = Color.Red,
        focusedLabelColor = Color(0xFFBB86FC), // Фіолетова мітка при фокусі
        unfocusedLabelColor = Color.LightGray,
        disabledLabelColor = Color.Gray,
        errorLabelColor = Color.Red,
        placeholderColor = Color.Gray.copy(alpha = 0.6f),
        disabledPlaceholderColor = Color.Gray.copy(alpha = 0.4f)
    )
}

private fun isValidFormat(urls: List<String>): Boolean {
    val validExtensions = listOf(".jpg", ".jpeg", ".png", ".bmp", ".webp")
    return (urls.all { url ->
        validExtensions.any {
            url.lowercase().endsWith(it)
        }
    }) && urls.isNotEmpty()
}

private fun isValidUrl(url: String): Boolean {
    return (url.startsWith("http://") || url.startsWith("https://")) && url.length >= 5
}
