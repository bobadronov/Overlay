package org.bigblackowl.overlay.util

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


fun selectFiles(): List<String> {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "Select Images"
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = true // Дозволяємо вибір кількох файлів
        fileFilter = FileNameExtensionFilter("Image Files", "jpg", "png", "bmp", "jpeg")

    }

    return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFiles.map { it.absolutePath } // Отримуємо список файлів
    } else {
        emptyList() // Якщо вибір скасовано, повертаємо порожній список
    }
}