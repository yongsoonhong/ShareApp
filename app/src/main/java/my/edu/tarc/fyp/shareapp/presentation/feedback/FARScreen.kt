package my.edu.tarc.fyp.shareapp.presentation.feedback

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.domain.UserData

@Composable
fun FARScreen(
    user: UserData,
    isReviewWritten: Boolean,
    star: Int,
    body: String,
    onSendReviewClick: (Int, String, String) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {


        val ownFAR = user.uid == Firebase.auth.currentUser!!.uid

        //get star and feedback from viewModel-FireStore
        var starGet by remember { mutableStateOf(0)}
        var reviewTF by remember { mutableStateOf( "") }
        var isEditing by remember { mutableStateOf( false) }


        starGet = star
        reviewTF = body
        isEditing = !isReviewWritten


        //User's photo and Name
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            AsyncImage(
                model = user.photoUrl ?: R.drawable.baseline_person_24,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .height(100.dp)
                    .width(100.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column {
                Text(
                    text = (user.displayName ?: user.uid),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "5.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }

        //show this if not ownFAR
        if (!ownFAR) {

            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Review this user",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(20.dp,0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star1",
                            tint = if (starGet >= 1) Color.Yellow else Color.Gray,
                            modifier = Modifier.clickable {
                                if (isEditing) starGet = 1
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star2",
                            tint = if (starGet >= 2) Color.Yellow else Color.Gray,
                            modifier = Modifier.clickable {
                                if (isEditing) starGet = 2
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star2",
                            tint = if (starGet >= 3) Color.Yellow else Color.Gray,
                            modifier = Modifier.clickable {
                                if (isEditing) starGet = 3
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star4",
                            tint = if (starGet >= 4) Color.Yellow else Color.Gray,
                            modifier = Modifier.clickable {
                                if (isEditing) starGet = 4
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star5",
                            tint = if (starGet >= 5) Color.Yellow else Color.Gray,
                            modifier = Modifier.clickable {
                                if (isEditing) starGet = 5
                            }
                        )
                    } //Stars

                    Spacer(modifier = Modifier.padding(5.dp))

                    androidx.compose.material.TextField(
                        value = reviewTF,
                        onValueChange = {
                            reviewTF = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.LightGray,
                            cursorColor = Color.Black,
                            disabledLabelColor = Color.LightGray,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = false,
                        placeholder = {
                            androidx.compose.material.Text(text = "Review")
                        },
                        minLines = 3,
                        maxLines = 6,
                        enabled = isEditing
                    )

                    Spacer(modifier = Modifier.padding(5.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if (isReviewWritten){
                            if (isEditing){
                                OutlinedButton(
                                    onClick = {
                                        isEditing = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = Color.Red
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        onSendReviewClick(starGet,reviewTF,user.uid)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = "Send",
                                        color = Color.Black
                                    )
                                }
                            } else{
                                OutlinedButton(
                                    onClick = {
                                        isEditing = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        text = "Edit",
                                        color = Color.Black
                                    )
                                }
                            }
                        } else{
                            OutlinedButton(
                                onClick = {
                                    onSendReviewClick(starGet,reviewTF,user.uid)
                                    isEditing  = false
                                },
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    text = "Send",
                                    color = Color.Black
                                )
                            }
                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding())

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "Review",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
            LazyColumn {
                //TODO get feedback and user...
                items(listOf(user, user, user, user)){
                    FeedbackItem(user = user)
                }
            }
        }



    }
}


@Composable
fun FeedbackItem(user: UserData){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            AsyncImage(
                model = user.photoUrl ?: R.drawable.baseline_person_24,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .height(50.dp)
                    .width(50.dp)
            )

            Spacer(modifier = Modifier.padding(3.dp))

            Column{
                Row {
                    Text(
                        text = (user.displayName ?: user.uid),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = "1 days ago",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "Feedback to this User display here",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }


    Spacer(modifier = Modifier.padding(8.dp))
}
