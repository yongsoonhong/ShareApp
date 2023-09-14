package my.edu.tarc.fyp.shareapp.presentation.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onItemRequestClick: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        restaurant.imageUrl?.let {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = "Item image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Gray), // Placeholder color
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text = restaurant.restaurantName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(text = "Pick Up Time: ${restaurant.pickUpStartTime!!} - ${restaurant.pickUpEndTime!!}")
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = restaurant.address!!,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.padding(10.dp))

            Box(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
            ){
                Column() {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = restaurant.description!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }
            Spacer(modifier = Modifier.padding(10.dp))


        }


    }
}