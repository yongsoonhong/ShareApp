package my.edu.tarc.fyp.shareapp.presentation.nearby

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
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class NearbyItemViewModel @Inject constructor(
): ViewModel() {


    var nearbyItemUiState by mutableStateOf(NearbyItemUiState())
        private set

    private val db = Firebase.firestore

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var currentUserLocation: LatLng = LatLng(0.0, 0.0)  // Default value, should be updated later

    fun updateCurrentUserLocation(latLng: LatLng) {
        currentUserLocation = latLng
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


    val sharedItemPagingFlow = Pager(
        config = PagingConfig(pageSize = 6, enablePlaceholders = false),
        pagingSourceFactory = { NearbyItemPagingSource(this) }
    ).flow.cachedIn(viewModelScope)



}


data class NearbyItemUiState(
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
