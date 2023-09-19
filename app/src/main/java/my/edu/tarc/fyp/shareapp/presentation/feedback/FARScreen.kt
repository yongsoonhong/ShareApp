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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.domain.Review
import my.edu.tarc.fyp.shareapp.domain.UserData
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun FARScreen(
    user: UserData,
    isReviewWritten: Boolean,
    star: Int,
    body: String,
    reviews: Map<Review,UserData>,
    aveStar: Double,
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
                        text = String.format("%.1f", aveStar),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 0.dp)
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
                if (reviews.toList().isEmpty()){
                    item {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "No Review from others")
                    }
                } else if(reviews.toList()[0].second.uid == Firebase.auth.uid && reviews.toList().size == 1){
                    item {
                        Text(text = "No Review from others", modifier = Modifier.padding(10.dp))
                    }
                } else{
                    items(reviews.toList()){ review ->
                        if (review.second.uid != Firebase.auth.currentUser!!.uid){
                            FeedbackItem(review = review.first,user = review.second)
                        }
                    }
                }

            }
        }



    }
}


@Composable
fun FeedbackItem(review: Review, user: UserData){
    fun yearsBetweenDates(d1: Date, d2: Date): Long {
        val calendar = Calendar.getInstance()

        calendar.time = d1
        val year1 = calendar.get(Calendar.YEAR)

        calendar.time = d2
        val year2 = calendar.get(Calendar.YEAR)

        return (year1 - year2).toLong()
    }

    fun monthsBetweenDates(d1: Date, d2: Date): Long {
        val calendar = Calendar.getInstance()

        calendar.time = d1
        val year1 = calendar.get(Calendar.YEAR)
        val month1 = calendar.get(Calendar.MONTH)

        calendar.time = d2
        val year2 = calendar.get(Calendar.YEAR)
        val month2 = calendar.get(Calendar.MONTH)

        return (((year1 * 12) + month1) - ((year2 * 12) + month2)).toLong()
    }

    fun timestampToTimeAgo(timestamp: Timestamp): String {
        val timestampDate = timestamp.toDate()
        val currentDate = Date()

        val differenceInMillis = currentDate.time - timestampDate.time

        // Get days, months, and years difference
        val daysDifference = TimeUnit.MILLISECONDS.toDays(differenceInMillis)
        val yearsDifference = yearsBetweenDates(currentDate, timestampDate)
        val monthsDifference = monthsBetweenDates(currentDate, timestampDate)

        return when {
            yearsDifference > 0 -> when (yearsDifference) {
                1L -> "1 year ago"
                else -> "$yearsDifference years ago"
            }
            monthsDifference > 0 -> when (monthsDifference) {
                1L -> "1 month ago"
                else -> "$monthsDifference months ago"
            }
            daysDifference == 0L -> "Today"
            daysDifference == 1L -> "1 day ago"
            else -> "$daysDifference days ago"
        }
    }




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
                        text = timestampToTimeAgo(review.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = review.body?:"",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }


    Spacer(modifier = Modifier.padding(8.dp))
}
