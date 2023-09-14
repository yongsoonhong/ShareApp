package my.edu.tarc.fyp.shareapp.presentation.nearby

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
import androidx.compose.material3.ButtonDefaults
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
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData


@Composable
fun NearbyItemDetailsScreen(
    sharedItem: SharedItem,
    onItemRequestClick: () -> Unit,
    userData: UserData?
){

    Spacer(modifier = Modifier.padding(16.dp))

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())

    ) {


        sharedItem.imageUrl?.let {
            AsyncImage(
                model = sharedItem.imageUrl,
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
            if (userData != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    userData.photoUrl?.let {
                        AsyncImage(
                            model = userData.photoUrl,
                            contentDescription = "user's profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(CircleShape).weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(3f),
                    ) {
                        userData.displayName?.let {
                            Text(
                                text = "$it is giving away!",
                                style = MaterialTheme.typography.bodyLarge

                            )
                        }
                        Text(
                            text = sharedItem.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ){

                            Text(
                                text = "Added in: ${sharedItem.dateAdded!!}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
        ){
            Column() {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sharedItem.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }

        Button(
            onClick = { onItemRequestClick() },
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Request for this item")
        }
    }
}