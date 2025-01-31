package org.bigblackowl.overlay.tray

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {

        // Малюємо три точки (як кнопки вікна)
        val buttonRadius = 10f
        drawCircle(
            color = Color.Red,
            radius = buttonRadius,
            center = center
        )
    }
}
