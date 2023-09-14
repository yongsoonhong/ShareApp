package my.edu.tarc.fyp.shareapp.presentation.sharedItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
        ,
    ) {
        ImagePicker(sharedItemUiState = sharedItemUiState, onItemValueChange = onItemValueChange)
        ItemInputForm(sharedItemUiState = sharedItemUiState, onItemValueChange = onItemValueChange, enabled = true)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                modifier = Modifier
                    .weight(1f),

                onClick = onSaveClick
            ) {
                androidx.compose.material.Text(text = "Save")
            }
            Button(
                shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                androidx.compose.material.Text(text = "Cancel")
            }
        }
    }
}