package my.edu.tarc.fyp.shareapp.presentation.sharedItem

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
import com.google.android.gms.maps.model.LatLng
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
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class SharedItemViewModel @Inject constructor(
): ViewModel() {


    var sharedItemUiState by mutableStateOf(SharedItemUiState())
        private set

    private val imageRef = Firebase.storage.reference.child("images")
    private val db = Firebase.firestore

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()





    fun updateUiState(sharedItemDetails: SharedItemDetails) {
        sharedItemUiState =
            SharedItemUiState(itemDetails = sharedItemDetails, isEntryValid = validateInput(sharedItemDetails))
    }

    private fun validateInput(uiState: SharedItemDetails = sharedItemUiState.itemDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && description.isNotBlank()
        }
    }
    private val _sharedItem = MutableLiveData<SharedItem?>()
    val sharedItem: LiveData<SharedItem?> get() = _sharedItem

    fun getItemById(sharedItemId: String) {
        viewModelScope.launch {
            db.collection("shareditems")
                .whereEqualTo("sharedItemId", sharedItemId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val sharedItem = document.toObject(SharedItem::class.java)
                        _sharedItem.value = sharedItem
                    } else {
                        _sharedItem.value = null
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    _sharedItem.value = null
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    fun updateSharedItemDetails(sharedItemIdToUpdate: String){
        if (validateInput()) {
            val imageUri = sharedItemUiState.itemDetails.imageUri
            if (imageUri != null) {
                viewModelScope.launch {
                    val filename = UUID.randomUUID().toString()
                    uploadImagesToStorage(filename)
                    // Save the item details along with the image path
                    val sharedItem = sharedItemUiState.itemDetails.toSharedItem()

                    val sharedItemRef = db.collection("shareditems").document(sharedItemIdToUpdate)
                    sharedItem.sharedItemId = sharedItemIdToUpdate
                    sharedItem.imageUrl = imageUri.toString()

                    sharedItemRef
                        .update(sharedItem.asMap())
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

        val imageUri = sharedItemUiState.itemDetails.imageUri
        println(filename)

        try {
            imageUri?.let {
                imageRef.child(filename).putFile(it).await()
                sharedItemUiState.itemDetails.imageUrl = "https://firebasestorage.googleapis.com/v0/b/share-app-87bba.appspot.com/o/images%2F$filename?alt=media"
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){

            }
        }

    }

    fun saveSharedItem() {
        if (validateInput()) {
            val imageUri = sharedItemUiState.itemDetails.imageUri
            if (imageUri != null) {
                viewModelScope.launch {
                    val filename = UUID.randomUUID().toString()
                    uploadImagesToStorage(filename)
                    // Save the item details along with the image path
                    val sharedItem = sharedItemUiState.itemDetails.toSharedItem()

                    val newSharedItemRef = db.collection("shareditems").document()
                    sharedItem.sharedItemId = newSharedItemRef.id

                    newSharedItemRef
                        .set(sharedItem.asMap())
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                            clearSharedItemDetails()
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                }
            }

        }

    }

    fun clearSharedItemDetails(){
        updateUiState(SharedItemDetails(
            title = "",
            description = "",
            imageUri = null,
            imageUrl= null,
            latLng = null
        ))
    }

    fun deleteItemById(sharedItemId: String){
        db.collection("shareditems").document(sharedItemId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    val sharedItemPagingFlow = Pager(
        config = PagingConfig(pageSize = 6, enablePlaceholders = false),
        pagingSourceFactory = { SharedItemPagingSource() }
    ).flow.cachedIn(viewModelScope)



}


data class SharedItemUiState(
    val itemDetails: SharedItemDetails = SharedItemDetails(),
    val isEntryValid: Boolean = false,
)

data class SharedItemDetails(
    val title: String = "",
    val description: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String? = null,
    var latLng: LatLng? = null

)

fun SharedItemDetails.toSharedItem(): SharedItem {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    return SharedItem(
        sharedItemId = "0",
        title = title,
        description = description,
        dateAdded = currentDate,
        longitude = latLng?.longitude,
        latitude = latLng?.latitude,
        noLike = 0,
        noView = 0,
        userId = Firebase.auth.currentUser?.uid,
        imageUrl = imageUrl
    )
}


fun SharedItem.asMap(): Map<String, Any?> = SharedItem::class.memberProperties.associate { it.name to it.get(this)}
