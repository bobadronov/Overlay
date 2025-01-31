package org.bigblackowl.overlay.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun LazyListItem(
    imageUrl: String,
    item: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.Gray
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.height(100.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Fit
            )

            Text(
                imageUrl,
                color = Color.White,
                modifier = Modifier.weight(1f).padding(horizontal = 5.dp),
                maxLines = 4
            )
            item()

        }
    }
}