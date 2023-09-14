package my.edu.tarc.fyp.shareapp.presentation.nearby

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.Message
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData
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

    private val _currentUserLocation = MutableStateFlow<LatLng>(LatLng(0.0,0.0))
    val currentUserLocation: StateFlow<LatLng> get() = _currentUserLocation

    fun updateCurrentUserLocation(latLng: LatLng) {
        _currentUserLocation.value = latLng
    }

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    fun addRequest(sharedItemId: String, toUid: String) {
        viewModelScope.launch {

            val currentUserRequestId = "${Firebase.auth.currentUser!!.uid}request"
            val toUserRequestId = "${toUid}request"
            val timestamp = Timestamp.now() // current timestamp
            val randRequestItemId1 = "${sharedItemId}Item"
            val randRequestItemId2 = "${sharedItemId}Item"


            val currentUserRequest = Request(
                requestId=randRequestItemId1,
                sharedItemId=sharedItemId,
                fromUid=Firebase.auth.currentUser!!.uid,
                toUid=toUid,
                timeRequest=timestamp,
                status="Pending"
            )

            val toUserRequest = Request(
                requestId=randRequestItemId2,
                sharedItemId=sharedItemId,
                fromUid=Firebase.auth.currentUser!!.uid,
                toUid=toUid,
                timeRequest=timestamp,
                status="Pending"
            )

            storeRequestToFirestore(currentUserRequest,currentUserRequestId,randRequestItemId1,"requestfrom")
            storeRequestToFirestore(toUserRequest,toUserRequestId,randRequestItemId2,"requestto")


        }

    }
    private fun storeRequestToFirestore(request: Request, requestId: String, requestItemId: String, fromOrTo:String) {
        db.collection("requests").document(requestId)
            .collection(fromOrTo)
            .document(requestItemId)
            .set(request)
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    // Suspend function to fetch user data
    fun fetchUserData(uid: String) {
        viewModelScope.launch {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(UserData::class.java)
                        _userData.value = user  // Update the MutableStateFlow with the fetched data

                    } else {
                        _userData.value = null
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    _userData.value = null
                    Log.d(TAG, "get failed with ", exception)
                }
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
