package my.edu.tarc.fyp.shareapp.presentation.restaurant

import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.R

@Composable
fun RestaurantApplicationFormScreen(
    onSaveClick:(Uri, String, String, String, String, String, Double, Double) -> Unit,
    onCancelClick: () -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }
    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    val timeOptions = List(48) { i ->
        String.format("%02d:%02d", i / 2, (i % 2) * 30)
    }


    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pickUpStartTime by remember { mutableStateOf("00:00") }
    var pickUpEndTime by remember { mutableStateOf("00:00") }
    var description by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf(0.00) }
    var latitude by remember { mutableStateOf(0.00) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AsyncImage(
                model = selectedImageUri ?: R.drawable.add_a_photo,
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .padding(10.dp)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    .border(BorderStroke(2.dp, Color.LightGray)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(text = "Click to Add or Change Image")
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Restaurant Name") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = "Address") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )


            Row {
                Text(text = "Choose Pick Up Start Time:")

                Spacer(modifier = Modifier.padding(8.dp))

                Box {
                    TextButton(onClick = { expanded1 = true }) {
                        Text(pickUpStartTime)
                    }
                    DropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                        timeOptions.forEach { time ->
                            DropdownMenuItem(onClick = {
                                pickUpStartTime = time
                                expanded1 = false
                            }) {
                                Text(time)
                            }
                        }
                    }
                }
            }

            Row {
                Text(text = "Choose Pick Up End Time")

                Box {
                    TextButton(onClick = { expanded2 = true }) {
                        Text(pickUpEndTime)
                    }
                    DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                        timeOptions.forEach { time ->
                            DropdownMenuItem(onClick = {
                                pickUpEndTime = time
                                expanded2 = false
                            }) {
                                Text(time)
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Description") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = { latitude = it.toDouble() },
                label = { Text(text = "Latitude") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )
            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = { longitude = it.toDouble() },
                label = { Text(text = "Longitude") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )

        }



        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp, 0.dp, 0.dp, 10.dp),
                modifier = Modifier
                    .weight(1f),

                onClick = {
                    if (selectedImageUri != null && name != "" && address != "" && pickUpStartTime != "" && pickUpEndTime != "" && description != "") {
                        onSaveClick(
                            selectedImageUri!!,
                            name,
                            address,
                            pickUpStartTime,
                            pickUpEndTime,
                            description,
                            latitude,
                            longitude
                        )
                    } else {
                        showDialog = true
                    }
                }
            ) {
                Text(text = "Apply")
            }
            Button(
                shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Cancel")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    title = {
                        Text(text = "Alert")
                    },
                    text = {
                        Text("Please fill all fields and select an image before saving.")
                    },
                    buttons = {
                        Button(onClick = {
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}


