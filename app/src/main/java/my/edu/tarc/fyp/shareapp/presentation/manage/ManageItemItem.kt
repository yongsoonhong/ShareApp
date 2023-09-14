package my.edu.tarc.fyp.shareapp.presentation.manage

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.ManageItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun ManageItemItem(
    manageItem: ManageItem,
    onItemClick: (ManageItem) -> Unit
) {

    fun isExpired(expiryDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val expiryLocalDate = LocalDate.parse(expiryDate, formatter)

        // Comparing the expiry date to the current date
        return expiryLocalDate.isBefore(LocalDate.now())
    }

    Card(
        modifier = Modifier
            .height(130.dp)
            .clickable {
                onItemClick(manageItem)
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
        ){
            AsyncImage(
                model = manageItem.imageUrl,
                contentDescription = manageItem.title,
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp),
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
                    text = manageItem.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = manageItem.description,
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    if (isExpired(manageItem.expiryDate)){
                        Text(
                            text = "Item Expired",
                            style = MaterialTheme.typography.caption,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Expiry Date: ${manageItem.expiryDate}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
