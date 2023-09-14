package my.edu.tarc.fyp.shareapp.presentation.sharedItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.fyp.shareapp.domain.SharedItem

@Composable
fun SharedItemDetailsScreen(
    sharedItem: SharedItem,
    onItemEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Display image
        sharedItem.imageUrl?.let {
            val painter = rememberAsyncImagePainter(it)
            Image(
                painter = painter,
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

        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = sharedItem.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Date added
                Text(text = "Added on: ${sharedItem.dateAdded}",
                    style = MaterialTheme.typography.labelMedium
                )
            }



        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(16.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                modifier = Modifier
                    .weight(1f),

                onClick = onItemEditClick
            ) {
                androidx.compose.material.Text(text = "Edit")
            }
            Button(
                shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                androidx.compose.material.Text(text = "Delete")
            }
        }


    }

}
