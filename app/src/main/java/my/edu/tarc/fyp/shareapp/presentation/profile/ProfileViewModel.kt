package my.edu.tarc.fyp.shareapp.presentation.profile

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
): ViewModel() {

    private val user = Firebase.auth.currentUser

    val db = Firebase.firestore


    private val _currentRequestsToUser = MutableStateFlow<List<Request>>(listOf())
    val currentRequestsToUser: StateFlow<List<Request>> get() = _currentRequestsToUser
    private val _currentRequestsFromUser = MutableStateFlow<List<Request>>(listOf())
    val currentRequestsFromUser: StateFlow<List<Request>> get() = _currentRequestsFromUser

    private val _currentItemRequestsToUser = MutableStateFlow<Map<Request, SharedItem>>(mapOf())
    val currentItemRequestsToUser: StateFlow<Map<Request, SharedItem>> get() = _currentItemRequestsToUser
    private val _currentItemRequestsFromUser = MutableStateFlow<Map<Request, SharedItem>>(mapOf())
    val currentItemRequestsFromUser: StateFlow<Map<Request, SharedItem>> get() = _currentItemRequestsFromUser

    private val _currentRequestsToYouUser = MutableStateFlow<Map<Request, UserData>>(mapOf())
    val currentRequestsToYouUser: StateFlow<Map<Request, UserData>> get() = _currentRequestsToYouUser
    private val _currentRequestsFromYouUser = MutableStateFlow<Map<Request, UserData>>(mapOf())
    val currentRequestsFromYouUser: StateFlow<Map<Request, UserData>> get() = _currentRequestsFromYouUser


    init {
        getCurrentRequestFromYou()
        getCurrentRequestToYou()
    }

    fun getCurrentUser(): FirebaseUser{
        return user!!
    }

    fun signOut(){
        try {
            Firebase.auth.signOut()
        }catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun updateUserProfile(name: String, photoUrl: String){

        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(photoUrl)
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    fun updatePassword(password: String){

        user!!.updatePassword(password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }
            }
    }


    fun getCurrentRequestToYou() {
        val requestId = "${user!!.uid}request"

        db.collection("requests")
            .document(requestId)
            .collection("requestto")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedRequests = value?.documents?.mapNotNull {
                    it.toObject(Request::class.java)
                }
                Log.d("1","1")

                if (fetchedRequests != null) {
                    Log.d("2",fetchedRequests.toString())
                    _currentRequestsToUser.value = fetchedRequests

                    fetchedRequests.forEach{ request ->
                        Log.d("3",request.toString())
                        getCurrentRequestToYouSharedItem(request)
                        getCurrentRequestToYouUser(request)
                    }
                }
            }
    }
    fun getCurrentRequestFromYou() {
        val requestId = "${user!!.uid}request"

        db.collection("requests")
            .document(requestId)
            .collection("requestfrom")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedRequests = value?.documents?.mapNotNull {
                    it.toObject(Request::class.java)
                }
                if (fetchedRequests != null) {
                    _currentRequestsFromUser.value = fetchedRequests

                    fetchedRequests.forEach{ request ->
                        getCurrentRequestFromYouSharedItem(request)
                        getCurrentRequestFromYouUser(request)

                    }
                }
            }
    }

    fun getCurrentRequestToYouSharedItem(request: Request) {
        val id = request.sharedItemId

        db.collection("shareditems")
            .document(id)
            .get()
            .addOnSuccessListener { value ->

                val fetchedSharedItem = value?.toObject(SharedItem::class.java)

                if (fetchedSharedItem != null) {
                    val currentMap = _currentItemRequestsToUser.value // Fetch the current map value

                    if (!currentMap.containsValue(fetchedSharedItem)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedSharedItem // Add the new key-value pair
                        _currentItemRequestsToUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e?.let {
                    Log.w(TAG, "Listen failed.", e)
                }
            }
    }

    fun getCurrentRequestFromYouSharedItem(request: Request) {
        val id = request.sharedItemId

        db.collection("shareditems")
            .document(id)
            .get()
            .addOnSuccessListener { value ->

                val fetchedSharedItem = value?.toObject(SharedItem::class.java)

                if (fetchedSharedItem != null) {
                    val currentMap = _currentItemRequestsFromUser.value // Fetch the current map value

                    if (!currentMap.containsValue(fetchedSharedItem)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedSharedItem // Add the new key-value pair
                        _currentItemRequestsFromUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e?.let {
                    Log.w(TAG, "Listen failed.", e)
                }
            }
    }

    fun getCurrentRequestToYouUser(request: Request) {
        val id = request.fromUid

        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { value ->

                val fetchedUser = value?.toObject(UserData::class.java)

                if (fetchedUser != null) {
                    val currentMap = _currentRequestsToYouUser.value // Fetch the current map value

                    if (!currentMap.containsValue(fetchedUser)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedUser // Add the new key-value pair
                        _currentRequestsToYouUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e?.let {
                    Log.w(TAG, "Listen failed.", e)
                }
            }
    }

    fun getCurrentRequestFromYouUser(request: Request) {
        val id = request.toUid

        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { value ->

                val fetchedUser = value?.toObject(UserData::class.java)

                if (fetchedUser != null) {
                    val currentMap = _currentRequestsFromYouUser.value // Fetch the current map value

                    if (!currentMap.containsValue(fetchedUser)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedUser // Add the new key-value pair
                        _currentRequestsFromYouUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e?.let {
                    Log.w(TAG, "Listen failed.", e)
                }
            }
    }

    fun clearCurrentItemRequestsToUser() {
        _currentItemRequestsToUser.value = mapOf()
    }

    fun clearCurrentItemRequestsFromUser() {
        _currentItemRequestsFromUser.value = mapOf()
    }

    fun clearCurrentRequestFromYouUser() {
        _currentRequestsFromYouUser.value = mapOf()
    }

    fun clearCurrentRequestToYouUser() {
        _currentRequestsToYouUser.value = mapOf()
    }
}