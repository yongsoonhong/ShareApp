package my.edu.tarc.fyp.shareapp.presentation.restaurant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.domain.SharedItem


@Composable
fun RestaurantItemDetailsScreen(
    restaurant: Restaurant,
    onItemRequestClick: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        restaurant.imageUrl?.let {
            AsyncImage(model = restaurant.imageUrl, contentDescription = "Item image")
        }
        Text(text = restaurant.restaurantName)
        restaurant.description?.let { Text(text = it) }
        Text(text = "Pick Up Time: ${restaurant.pickUpStartTime!!} - ${restaurant.pickUpEndTime!!}")
        Text(text = restaurant.noView.toString())
        Text(text = restaurant.noLike.toString())
        restaurant.address?.let { Text(text = it) }

        Button(onClick = { onItemRequestClick() }) {
            Text(text = "Request for this item")
        }
    }
}