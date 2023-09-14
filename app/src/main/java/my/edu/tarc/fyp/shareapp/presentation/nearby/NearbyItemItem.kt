package my.edu.tarc.fyp.shareapp.presentation.nearby

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.SharedItem

@Composable
fun NearbyItemItem(
    sharedItem: SharedItem,
    onItemClick: (SharedItem) -> Unit
) {
    Card(
        modifier = Modifier
            .height(130.dp)
            .clickable {
                onItemClick(sharedItem)
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
        ){
            AsyncImage(
                model = sharedItem.imageUrl,
                contentDescription = sharedItem.title,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(15.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sharedItem.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sharedItem.description,
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Added in ${sharedItem.dateAdded}",
                        style = MaterialTheme.typography.caption,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
