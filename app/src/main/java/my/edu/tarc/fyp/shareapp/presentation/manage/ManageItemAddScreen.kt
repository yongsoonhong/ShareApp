package my.edu.tarc.fyp.shareapp.presentation.manage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.R

@Composable
fun ManageItemAddScreen(
    manageItemUiState: ManageItemUiState,
    onItemValueChange: (ManageItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ImagePicker(manageItemUiState, onItemValueChange)
        ItemInputForm(manageItemUiState, onItemValueChange, enabled = true)
        SaveButton(onSaveClick, manageItemUiState.isEntryValid)
    }
}

@Composable
fun ImagePicker(
    manageItemUiState: ManageItemUiState,
    onItemValueChange: (ManageItemDetails) -> Unit
) {
    val itemDetails = manageItemUiState.itemDetails

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            onItemValueChange(itemDetails.copy(imageUri = uri))
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        Button(
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text(text = "pick photo")
        }

        AsyncImage(
            model = selectedImageUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ItemInputForm(
    manageItemUiState: ManageItemUiState,
    onItemValueChange: (ManageItemDetails) -> Unit,
    enabled: Boolean
) {

    val itemDetails = manageItemUiState.itemDetails

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        OutlinedTextField(
            value = itemDetails.title,
            onValueChange = { onItemValueChange(itemDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.item_title_req)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.description,
            onValueChange = { onItemValueChange(itemDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.description_req)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
        )
        OutlinedTextField(
            value = itemDetails.expiryDate,
            onValueChange = { onItemValueChange(itemDetails.copy(expiryDate = it)) },
            label = { Text(stringResource(R.string.expiry_req)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }


    }
}

@Composable
fun SaveButton(onSaveClick: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onSaveClick,
        enabled = isEnabled,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.save_action))
    }
}