package my.edu.tarc.fyp.shareapp.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetails
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemUiState

@Composable
fun EditUserProfileScreen(
    userData: FirebaseUser?,
    onSaveClick: (name:String, photoUrl: String) -> Unit,
    onCancelClick: ()  -> Unit
) {

    var displayName by rememberSaveable { mutableStateOf(userData?.displayName?: "") }
    var photoUrl by rememberSaveable { mutableStateOf(userData?.photoUrl?.toString()?: "") }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            photoUrl = uri.toString()
        }
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = if(photoUrl != "") photoUrl else R.drawable.baseline_person_24,
            contentDescription = "photoUrl",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        )

        Spacer(modifier = Modifier.padding(2.dp))

        Text(
            text = "*Click photo to change profile picture",
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text(text = "Display Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                modifier = Modifier
                    .weight(1f),

                onClick = {
                    onSaveClick(displayName,photoUrl)
                }
            ) {
                Text(text = "Save")
            }
            Button(
                shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Cancel")
            }
        }

    }

}

