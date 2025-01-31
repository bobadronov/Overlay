package org.bigblackowl.overlay.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick.invoke() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xff2b2a2a), // Фіолетовий колір кнопки
            contentColor = Color.White, // Білий колір тексту
            disabledContainerColor = Color.Gray, // Сірий колір для неактивної кнопки
            disabledContentColor = Color.LightGray // Світло-сірий текст у неактивній кнопці
        ),
        shape = RoundedCornerShape(12.dp), // Закруглені кути
    ) {
        Text(text, color = Color.White, modifier=Modifier.padding(end = 5.dp))
        icon()
    }
}