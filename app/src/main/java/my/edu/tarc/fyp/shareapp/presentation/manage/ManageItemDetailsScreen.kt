package my.edu.tarc.fyp.shareapp.presentation.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import my.edu.tarc.fyp.shareapp.domain.ManageItem
import my.edu.tarc.fyp.shareapp.domain.SharedItem


@Composable
fun ManageItemDetailsScreen(
    manageItem: ManageItem,
    onItemEditClick: () -> Unit,
    onDeleteClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        manageItem.imageUrl?.let {
            AsyncImage(model = manageItem.imageUrl, contentDescription = "Item image")
        }
        Text(text = manageItem.title)
        Text(text = manageItem.description)
        Text(text = manageItem.dateAdded!!)
        Text(text = manageItem.expiryDate.toString())
        Text(text = manageItem.userId!!)
        Button(onClick = { onItemEditClick() }) {
            Text(text = "Edit item")
        }
        Button(onClick = { onDeleteClick() }) {
            Text(text = "Delete item")
        }
    }
}