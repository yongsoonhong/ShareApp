package my.edu.tarc.fyp.shareapp.presentation.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import my.edu.tarc.fyp.shareapp.domain.ManageItem


@Composable
fun ManageItemEditScreen(
    manageItem: ManageItem,
    manageItemUiState: ManageItemUiState,
    onItemValueChange: (ManageItemDetails)->Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        ImagePicker(manageItemUiState = manageItemUiState, onItemValueChange = onItemValueChange)
        ItemInputForm(manageItemUiState = manageItemUiState, onItemValueChange = onItemValueChange, enabled = true)
        Button(onClick = {onSaveClick()}) {
            Text(text = "Save")
        }
        Button(onClick = { onCancelClick() }) {
            Text(text = "Cancel")
        }
    }
}