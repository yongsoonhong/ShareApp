package my.edu.tarc.fyp.shareapp.presentation.restaurant

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
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class RestaurantItemViewModel @Inject constructor(
): ViewModel() {


    var RestaurantItemUiState by mutableStateOf(RestaurantItemUiState())
        private set

    private val db = Firebase.firestore

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var currentUserLocation: LatLng = LatLng(0.0, 0.0)  // Default value, should be updated later

    fun updateCurrentUserLocation(latLng: LatLng) {
        currentUserLocation = latLng
    }


    fun addRestaurantApplication( uri: Uri, name: String, address: String, start: String, end: String, desc: String, lat: Double, lng: Double){

        val id = UUID.randomUUID().toString()
        val filename = UUID.randomUUID().toString()


        uploadImagesToStorage(filename, uri)

        val restaurant = Restaurant(
            restaurantId = id,
            restaurantName= name,
            pickUpStartTime = start,
            pickUpEndTime = end,
            address= address,
            description = desc,
            noLike = 0,
            noView = 0,
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/share-app-87bba.appspot.com/o/images%2F$filename?alt=media",
            longitude = lat,
            latitude = lng
        )


        db.collection("restaurantapplications").document(id)
            .set(restaurant)

        db.collection("restaurantapplications").document(id)
            .update("status","Pending")

    }
    private  fun uploadImagesToStorage(filename: String, imageUri: Uri){

        val imageRef = Firebase.storage.reference.child("images")
        imageRef.child(filename).putFile(imageUri)



    }

    private val _restaurant = MutableLiveData<Restaurant?>()
    val restaurant: LiveData<Restaurant?> get() = _restaurant

    fun getItemById(restaurantId: String) {
        viewModelScope.launch {
            db.collection("restaurants")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.documents[0]
                        val restaurant = document.toObject(Restaurant::class.java)
                        _restaurant.value = restaurant
                    } else {
                        _restaurant.value = null
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    _restaurant.value = null
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }


    val restaurantPagingFlow = Pager(
        config = PagingConfig(pageSize = 6, enablePlaceholders = false),
        pagingSourceFactory = { RestaurantItemPagingSource() }
    ).flow.cachedIn(viewModelScope)



}


data class RestaurantItemUiState(
    val itemDetails: RestaurantItemDetails = RestaurantItemDetails(),
    val isEntryValid: Boolean = false,
)

data class RestaurantItemDetails(
    val name: String = "",
    val description: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String? = null,
)

fun RestaurantItemDetails.toRestaurant(): Restaurant {
    return Restaurant(
        restaurantId = "0",
        restaurantName = name,
        description = description,
        pickUpStartTime = "",
        pickUpEndTime = "",
        noLike = 0,
        noView = 0,
        address = "",
        imageUrl = imageUrl
    )
}


fun Restaurant.asMap(): Map<String, Any?> = Restaurant::class.memberProperties.associate { it.name to it.get(this)}
