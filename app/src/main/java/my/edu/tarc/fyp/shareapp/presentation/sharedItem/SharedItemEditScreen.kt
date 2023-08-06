package my.edu.tarc.fyp.shareapp.presentation.sharedItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.SharedItem


@Composable
fun SharedItemEditScreen(
    sharedItem: SharedItem,
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails)->Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        ImagePicker(sharedItemUiState = sharedItemUiState, onItemValueChange = onItemValueChange)
        ItemInputForm(sharedItemUiState = sharedItemUiState, onItemValueChange = onItemValueChange, enabled = true)
        Button(onClick = {onSaveClick()}) {
            Text(text = "Save")
        }
        Button(onClick = { onCancelClick() }) {
            Text(text = "Cancel")
        }
    }
}