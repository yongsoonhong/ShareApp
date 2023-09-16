package my.edu.tarc.fyp.shareapp.presentation.message

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.domain.Channel
import my.edu.tarc.fyp.shareapp.domain.Message
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    onItemClick: (Channel) -> Unit,
    channels: List<Channel>,
    usersData: Map<String, UserData>,
    onAddChannelClick: () -> Unit
){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)){

        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "Channels",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )},
                actions = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Channel",
                        modifier = Modifier.clickable(onClick = onAddChannelClick)
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(channels) { channel ->
                    ChannelItem(
                        channel = channel,
                        userData = usersData[channel.user2], // Pass the user data for the other user in the channel
                        onItemClick = onItemClick
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                }

            }
        }
    }
}

@Composable
fun ChannelItem(
    channel: Channel,
    userData: UserData?,
    onItemClick: (Channel) -> Unit
){

    Card(
        modifier = Modifier
            .height(130.dp)
            .clickable {
                onItemClick(channel)
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            if (userData?.photoUrl != null && userData?.photoUrl != "" ){
                AsyncImage(
                    model = userData.photoUrl,
                    contentDescription = "user profile pic",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "Default User",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }


            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = userData?.displayName ?: "Unknown", // Use the user's display name
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}



@Composable
fun ChatRoomScreen(
    channel: Channel,
    userData: UserData?,
    messages: List<Message>,
    sharedItems: List<SharedItem>,
    onSendMessage: (String) -> Unit,
    defaultText: String? = null,
    onSendRequest: (SharedItem) -> Unit,
    itemRequestFrom: Map<Request,SharedItem>,
    itemRequestTo: Map<Request,SharedItem>,
    onItemAcceptClick: (Request) -> Unit,
    onItemDeclineClick: (Request) -> Unit,
    onItemDeleteClick: (Request) -> Unit,
    ) {

    var showSharedItem by remember { mutableStateOf(false) }
    var showSharedItemDetails by remember { mutableStateOf<SharedItem?>(null) }

    var showRequest by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header for the chat room
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = userData?.displayName ?: "Unknown")
                IconButton(onClick = {
                    showRequest = showRequest.not()
                }) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Requests")
                }
            }
        }

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages) { message ->
                MessageItem(message = message)
            }
        }
        
        if (showSharedItem){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .height(150.dp)
            ){
                LazyRow(){
                   items(sharedItems){ item ->
                        UserSharedItemItem(
                            sharedItem = item,
                            onItemClick = {
                                showSharedItemDetails = it
                            }
                        )
                   }
                }
            }
        }
        

        // Input field and Send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var messageText by remember { mutableStateOf(TextFieldValue(defaultText ?: "")) }

            IconButton(
                onClick = {
                    showSharedItemDetails = null
                    showSharedItem = showSharedItem.not()
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Check User's sharedItem",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                modifier = Modifier.clickable {
                    onSendMessage(messageText.text)
                    messageText = TextFieldValue("")
                }
            )
        }
    }

    if (showSharedItemDetails  != null){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(400.dp)
                    .width(250.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = showSharedItemDetails!!.imageUrl,
                        contentDescription = "shared item image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(250.dp)
                            .height(250.dp)
                    )
                    Text(
                        text = showSharedItemDetails!!.title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp)
                    ){
                        Button(
                            shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                            onClick = {
                                onSendRequest(showSharedItemDetails!!)
                                showSharedItemDetails = null
                        },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Send", fontSize = 10.sp)
                        }
                        Button(
                            shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                            onClick = { showSharedItemDetails = null },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(text = "Cancel", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    if (showRequest){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(450.dp)
                    .width(300.dp)
                    .background(Color.LightGray)
            ) {
                LazyColumn(){
                    item {
                        Text(
                            text = "Item requested from you to this user",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        )
                    }
                    if (itemRequestFrom.toList().isEmpty()){
                        item {
                            Text(
                                text = "No item requested",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            )
                        }
                    }else{
                        items(itemRequestFrom.toList()){ item->
                            RequestItemFrom(
                                sharedItem = item,
                                onItemDeleteClick = {onItemDeleteClick(it)}
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Item requested from this user to you",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        )
                    }

                    if (itemRequestTo.toList().isEmpty()) {
                        item {
                            Text(
                                text = "No item requested",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            )
                        }
                    }else {
                        items(itemRequestTo.toList()){ item->
                            RequestItemTo(
                                sharedItem = item,
                                onItemAcceptClick = {
                                    onItemAcceptClick(it)
                                    showRequest = false
                                },
                                onItemDeclineClick = {
                                    onItemDeclineClick(it)
                                    showRequest = false
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun RequestItemFrom(
    sharedItem: Pair<Request, SharedItem>,
    onItemDeleteClick: (Request) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween ,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = sharedItem.second.imageUrl,
                    contentDescription = "shared item image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = sharedItem.second.title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = sharedItem.first.status,
                            modifier = Modifier
                                .weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(8.dp))

                        Button(
                            shape = CircleShape,
                            enabled = sharedItem.first.status != "Pending",
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                onItemDeleteClick(sharedItem.first)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sharedItem.first.status == "Rejected") Color.Red else Color.Green,
                                disabledContainerColor = Color.LightGray
                            )
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Item Received")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun RequestItemTo(
    sharedItem: Pair<Request, SharedItem>,
    onItemAcceptClick: (Request) -> Unit,
    onItemDeclineClick: (Request) -> Unit
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween ,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = sharedItem.second.imageUrl,
                    contentDescription = "shared item image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
                Column {
                    Text(
                        text = sharedItem.second.title,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 15.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Button(
                            shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                            modifier = Modifier
                                .weight(1f),
                            onClick = { onItemAcceptClick(sharedItem.first) }
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = "Accept")
                        }
                        Button(
                            shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                            modifier = Modifier
                                .weight(1f),
                            onClick = { onItemDeclineClick(sharedItem.first) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Decline")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MessageItem(message: Message) {
    val alignment = if (message.from == "0") Alignment.BottomEnd else Alignment.BottomStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = if (message.from == "0") Color.Gray else Color.Blue,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.body,
                modifier = Modifier.padding(8.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun UserSharedItemItem(
    sharedItem: SharedItem,
    onItemClick: (SharedItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
                .width(100.dp)
                .clickable {
                    onItemClick(sharedItem)
                }
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = sharedItem.imageUrl,
                    contentDescription = "shared item image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
                Text(
                    text = sharedItem.title,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}


@Composable
fun AddChannelScreen(
    onNavigateBack: () -> Unit,
    onCreateChannel: (uid: String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var searchedUserData by remember { mutableStateOf<UserData?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Enter UID") }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                    onCreateChannel(searchText)
            }) {
                Text("Search")
            }

            Button(onClick = onNavigateBack) {
                Text("Cancel")
            }
        }

        if (searchedUserData != null) {
            // Display searched user data
            Text(text = searchedUserData!!.displayName.toString())
            Button(onClick = {
                onCreateChannel(searchText)
            }) {
                Text("Create Channel")
            }
        }

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }
    }
}
