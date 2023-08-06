package my.edu.tarc.fyp.shareapp.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    onSignOutClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
){
    val user = viewModel.getCurrentUser()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user.photoUrl?.let {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .height(200.dp)
                    .width(200.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
        }
        user.displayName?.let {
            Text(text = it)
        }
        user.email?.let {
            Text(text = it)
        }
        Button(onClick = {
            onSignOutClick()
        }) {
            Text(text = "Sign Out")
        }

    }
}