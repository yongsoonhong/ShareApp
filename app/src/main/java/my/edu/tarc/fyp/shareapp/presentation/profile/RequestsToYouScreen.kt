package my.edu.tarc.fyp.shareapp.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData

@Composable
fun RequestsToYouScreen(
    requests: List<Request>,
    itemRequestToYou: Map<Request, SharedItem>,
    userRequested: Map<Request, UserData>
) {
    LazyColumn{
        items(requests){ request ->
            RequestToYouItemScreen(request = request, itemRequestToYou = itemRequestToYou[request] , userRequested = userRequested[request])
        }
    }
}

@Composable
fun RequestToYouItemScreen(
    request: Request,
    itemRequestToYou: SharedItem?,
    userRequested: UserData?
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = userRequested?.photoUrl ?: R.drawable.baseline_person_24,
                    contentDescription = "User Profile Pic",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(50.dp)
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Column {
                    Text(
                        text = userRequested?.displayName ?: "User",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "requested this item:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                AsyncImage(
                    model = itemRequestToYou?.imageUrl ?: R.drawable.baseline_person_24,
                    contentDescription = "User Profile Pic",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .width(50.dp)
                        .height(50.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))

                Column {
                    Text(
                        text = itemRequestToYou?.title?:"Shared Item",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Date requested: ${request.timeRequest}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                    modifier = Modifier
                        .weight(1f),
                    onClick = { }
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Accept")
                }
                Button(
                    shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                    modifier = Modifier
                        .weight(1f),
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Decline")
                }
            }


        }




    }
}