package my.edu.tarc.fyp.shareapp.presentation.nearby

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.SharedItem


@Composable
fun NearbyItemDetailsScreen(
    sharedItem: SharedItem,
    onItemRequestClick: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        sharedItem.imageUrl?.let {
            AsyncImage(model = sharedItem.imageUrl, contentDescription = "Item image")
        }
        Text(text = sharedItem.title)
        Text(text = sharedItem.description)
        Text(text = sharedItem.dateAdded!!)
        Text(text = sharedItem.noView.toString())
        Text(text = sharedItem.noLike.toString())
        Text(text = sharedItem.userId!!)
        Button(onClick = { onItemRequestClick() }) {
            Text(text = "Request for this item")
        }
    }
}