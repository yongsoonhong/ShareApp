package my.edu.tarc.fyp.shareapp.presentation.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.fyp.shareapp.R

@Composable
fun ProfileScreen(
    onSignOutClick: () -> Unit,
    onEditUserProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onRequestToYouClick: () -> Unit,
    onRequestFromYouClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
){
    val user = viewModel.getCurrentUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        onEditUserProfileClick()
                    }
            ) {

                AsyncImage(
                    model = if (user.photoUrl != null) user.photoUrl else R.drawable.baseline_person_24,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .height(100.dp)
                        .width(100.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = (if (user.displayName != null) user.displayName else user.uid)!!,
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }

        Text(
            text = "* Click to edit",
            color = Color.LightGray,
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.padding(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
                    .height(200.dp)
                    .clickable {
                        onRequestFromYouClick()
                    },
                shape = RoundedCornerShape(8.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.requestfromyou),
                    contentDescription = "Request from you",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
                    .height(200.dp)
                    .clickable {
                        onRequestToYouClick()
                    },
                shape = RoundedCornerShape(8.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.requesttoyou),
                    contentDescription = "Request to you",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
            ) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.padding(10.dp))


                user.email?.let {
                    Text(
                        text = "Email: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.padding(5.dp))

                Text(
                    text = "User Uid : ${user.uid}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedButton(
                    onClick = onChangePasswordClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "Change password",
                        color = Color.Red
                    )
                }
            }
        }

        Box(modifier = Modifier.padding(15.dp)){
            OutlinedButton(
                onClick = {
                    onSignOutClick()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Sign Out",
                    color = Color.Black
                )
            }
        }


    }
}