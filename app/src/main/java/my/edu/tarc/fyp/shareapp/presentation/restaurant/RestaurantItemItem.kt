package my.edu.tarc.fyp.shareapp.presentation.restaurant

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
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.domain.SharedItem

@Composable
fun RestaurantItemItem(
    restaurant: Restaurant,
    onItemClick: (Restaurant) -> Unit
) {
    Card(
        modifier = Modifier
            .height(130.dp)
            .clickable {
                onItemClick(restaurant)
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
        ){
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.restaurantName,
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight(),
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
                    text = restaurant.restaurantName,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = restaurant.description!!,
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Pick Up Time: ${restaurant.pickUpStartTime!!} - ${restaurant.pickUpEndTime!!}",
                        style = MaterialTheme.typography.caption,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No of View: ${restaurant.noView}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
