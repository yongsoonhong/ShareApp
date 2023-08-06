package my.edu.tarc.fyp.shareapp.presentation.manage

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import my.edu.tarc.fyp.shareapp.domain.ManageItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class ManageItemViewModel @Inject constructor(
): ViewModel() {


    var manageItemUiState by mutableStateOf(ManageItemUiState())
        private set

    private val imageRef = Firebase.storage.reference.child("images")
    private val db = Firebase.firestore

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()





    fun updateUiState(manageItemDetails: ManageItemDetails) {
        manageItemUiState =
            ManageItemUiState(itemDetails = manageItemDetails, isEntryValid = validateInput(manageItemDetails))
    }

    private fun validateInput(uiState: ManageItemDetails = manageItemUiState.itemDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && description.isNotBlank() && expiryDate.isNotBlank()
        }
    }
    private val _manageItem = MutableLiveData<ManageItem?>()
    val manageItem: LiveData<ManageItem?> get() = _manageItem

    fun getItemById(manageItemId: String) {
        viewModelScope.launch {
            db.collection("manageitems")
                .whereEqualTo("manageItemId", manageItemId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val manageItem = document.toObject(ManageItem::class.java)
                        _manageItem.value = manageItem
                    } else {
                        _manageItem.value = null
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    _manageItem.value = null
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    fun updateManageItemDetails(manageItemIdToUpdate: String){
        if (validateInput()) {
            val imageUri = manageItemUiState.itemDetails.imageUri
            if (imageUri != null) {
                viewModelScope.launch {
                    val filename = UUID.randomUUID().toString()
                    uploadImagesToStorage(filename)
                    // Save the item details along with the image path
                    val manageItem = manageItemUiState.itemDetails.toManageItem()

                    val manageItemRef = db.collection("manageitems").document(manageItemIdToUpdate)
                    manageItem.manageItemId = manageItemIdToUpdate
                    manageItem.imageUrl = imageUri.toString()

                    manageItemRef
                        .update(manageItem.asMap())
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully updated !"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                }
            }

        }
    }




    private suspend fun uploadImagesToStorage(filename: String){

        val imageUri = manageItemUiState.itemDetails.imageUri
        println(filename)

        try {
            imageUri?.let {
                imageRef.child(filename).putFile(it).await()
                manageItemUiState.itemDetails.imageUrl = "https://firebasestorage.googleapis.com/v0/b/share-app-87bba.appspot.com/o/images%2F$filename?alt=media"
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){

            }
        }

    }

    fun saveManageItem() {
        if (validateInput()) {
            val imageUri = manageItemUiState.itemDetails.imageUri
            if (imageUri != null) {
                viewModelScope.launch {
                    val filename = UUID.randomUUID().toString()
                    uploadImagesToStorage(filename)
                    // Save the item details along with the image path
                    val manageItem = manageItemUiState.itemDetails.toManageItem()

                    val newManageItemRef = db.collection("manageitems").document()
                    manageItem.manageItemId = newManageItemRef.id

                    newManageItemRef
                        .set(manageItem.asMap())
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                }
            }

        }

    }

    fun deleteItemById(manageItemId: String){
        db.collection("manageitems").document(manageItemId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    val manageItemPagingFlow = Pager(
        config = PagingConfig(pageSize = 6, enablePlaceholders = false),
        pagingSourceFactory = { ManageItemPagingSource() }
    ).flow.cachedIn(viewModelScope)



}


data class ManageItemUiState(
    val itemDetails: ManageItemDetails = ManageItemDetails(),
    val isEntryValid: Boolean = false,
)

data class ManageItemDetails(
    val title: String = "",
    val description: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String? = null,
    var expiryDate: String = ""

)

fun ManageItemDetails.toManageItem(): ManageItem {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    return ManageItem(
        manageItemId = "0",
        title = title,
        description = description,
        dateAdded = currentDate,
        userId = Firebase.auth.currentUser?.uid,
        imageUrl = imageUrl,
        expiryDate = expiryDate
    )
}


fun ManageItem.asMap(): Map<String, Any?> = ManageItem::class.memberProperties.associate { it.name to it.get(this)}
