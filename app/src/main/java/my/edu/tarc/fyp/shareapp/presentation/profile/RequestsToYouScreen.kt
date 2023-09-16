package my.edu.tarc.fyp.shareapp.presentation.profile

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowForward
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
    userRequested: Map<Request, UserData>,
    onAcceptClick: (Request) -> Unit,
    onRejectClick: (Request) -> Unit
) {
    Column {
        androidx.compose.material.Text(
            text = "Request To You",
            style = androidx.compose.material.MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        if(requests.isEmpty()){
            androidx.compose.material.Text(
                text = "There's no request from others",
                style = androidx.compose.material.MaterialTheme.typography.body2,
                modifier = Modifier.padding(10.dp)
            )
        } else{
            LazyColumn {
                items(requests) { request ->
                    RequestToYouItemScreen(
                        request = request,
                        itemRequestToYou = itemRequestToYou[request],
                        userRequested = userRequested[request],
                        onAcceptClick = {
                            onAcceptClick(it)
                        },
                        onRejectClick = {
                            onRejectClick(it)
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun RequestToYouItemScreen(
    request: Request,
    itemRequestToYou: SharedItem?,
    userRequested: UserData?,
    onAcceptClick: (Request) -> Unit,
    onRejectClick: (Request) -> Unit
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
                            text = "Date requested: ${request.timeRequest.toDate()}",
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
                    onClick = {
                        onAcceptClick(request)
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Accept")
                }
                Button(
                    shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                    modifier = Modifier
                        .weight(1f),
                    onClick = {
                              onRejectClick(request)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Decline")
                }
            }


        }




    }
}


@Composable
fun RequestsFromYouScreen(
    requests: List<Request>,
    itemRequestToYou: Map<Request, SharedItem>,
    userRequested: Map<Request, UserData>,
    onProceedClick: (Request, UserData?) -> Unit
) {

    Column() {
        androidx.compose.material.Text(
            text = "Request To You",
            style = androidx.compose.material.MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        if(requests.isEmpty()){
            androidx.compose.material.Text(
                text = "You haven't requested any request yet.",
                style = androidx.compose.material.MaterialTheme.typography.body2,
                modifier = Modifier.padding(10.dp)
            )
        } else{
            LazyColumn{
                items(requests){ request ->
                    RequestFromYouItemScreen(
                        request = request,
                        itemRequestToYou = itemRequestToYou[request] ,
                        userRequested = userRequested[request],
                        onProceedClick = { requestGet, userData ->
                            onProceedClick(requestGet, userData)
                        }
                    )
                }
            }
        }

    }

}
@Composable
fun RequestFromYouItemScreen(
    request: Request,
    itemRequestToYou: SharedItem?,
    userRequested: UserData?,
    onProceedClick: (Request, UserData?) -> Unit,
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
                        text = "You requested to this user",
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
                            text = "Date requested: ${request.timeRequest.toDate()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Status: ${request.status}",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    shape = CircleShape,
                    enabled = request.status != "Pending",
                    onClick = {
                        onProceedClick(request, userRequested)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (request.status == "Rejected") Color.Red else Color.Green,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Item Received")
                }
            }


        }




    }
}