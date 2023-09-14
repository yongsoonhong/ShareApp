package my.edu.tarc.fyp.shareapp.presentation.manage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import my.edu.tarc.fyp.shareapp.domain.ManageItem
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun ManageItemDetailsScreen(
    manageItem: ManageItem,
    onItemEditClick: () -> Unit,
    onDeleteClick: () -> Unit
){

    fun isExpired(expiryDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val expiryLocalDate = LocalDate.parse(expiryDate, formatter)

        // Comparing the expiry date to the current date
        return expiryLocalDate.isBefore(LocalDate.now())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        manageItem.imageUrl?.let {
            AsyncImage(
                model = manageItem.imageUrl,
                contentDescription = "Item image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Gray),
            )
        }
        Text(
            text = manageItem.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )

        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = if (isExpired(manageItem.expiryDate)) "Expired" else " ",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = "Expiry Date: ${manageItem.expiryDate}",
                style = MaterialTheme.typography.labelMedium,
            )
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
                    text = manageItem.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
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