package my.edu.tarc.fyp.shareapp.presentation.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.domain.SharedItem


@Composable
fun RestaurantItemDetailsScreen(
    restaurant: Restaurant,
){
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())

    ) {


        restaurant.imageUrl?.let {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = "Item image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Gray),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth().weight(3f),
                ) {
                    Text(
                        text = "${restaurant.restaurantName} is giving away!",
                        style = MaterialTheme.typography.bodyLarge

                    )

                    Text(
                        text = "Address: ${restaurant.address}",
                        style = MaterialTheme.typography.bodyLarge

                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Text(
                            text = "Pick up at: ${restaurant.pickUpStartTime} to ${restaurant.pickUpEndTime}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

            }

        }



        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column() {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = restaurant.description ?: "No description",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
    }
}
