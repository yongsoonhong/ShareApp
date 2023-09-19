package my.edu.tarc.fyp.shareapp.presentation.profile

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import my.edu.tarc.fyp.shareapp.domain.Channel
import my.edu.tarc.fyp.shareapp.domain.Message
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
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

    private suspend fun uploadImagesToStorage(filename: String, photoUrl: String){

        val imageRef = Firebase.storage.reference.child("images")

        val photoUri = Uri.parse(photoUrl)

        try {
            photoUri?.let {
                imageRef.child(filename).putFile(it).await()
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){

            }
        }

    }

    fun updateUserProfile(name: String, photoUrl: String){

        val imgFileName = UUID.randomUUID().toString()


        viewModelScope.launch{
            uploadImagesToStorage(imgFileName, photoUrl)
        }

        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/share-app-87bba.appspot.com/o/images%2F${imgFileName}?alt=media")
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }

        db.collection("users")
            .document(user.uid)
            .update("displayName", name, "photoUrl", "https://firebasestorage.googleapis.com/v0/b/share-app-87bba.appspot.com/o/images%2F${imgFileName}?alt=media")
            .addOnFailureListener{
                Log.d("Update Profile", it.toString())
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

                    if (!(currentMap.containsValue(fetchedSharedItem) && currentMap.containsKey(request))) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedSharedItem // Add the new key-value pair
                        _currentItemRequestsToUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e.let {
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

                    if (!(currentMap.containsValue(fetchedSharedItem) && currentMap.containsKey(request))) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedSharedItem // Add the new key-value pair
                        _currentItemRequestsFromUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e.let {
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

                    if (!currentMap.containsKey(request)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedUser // Add the new key-value pair
                        _currentRequestsToYouUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e.let {
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

                    if (!currentMap.containsKey(request)) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[request] = fetchedUser // Add the new key-value pair
                        _currentRequestsFromYouUser.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e.let {
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

    fun clearCurrentRequestFromYou() {
        _currentRequestsFromUser.value = listOf()
    }

    fun clearCurrentRequestToYou() {
        _currentRequestsToUser.value = listOf()
    }

    //Accept or Reject the request from other
    @RequiresApi(Build.VERSION_CODES.O)
    fun acceptRequest(request: Request){

        val userRequestIdFrom = "${request.fromUid}request"
        val userRequestIdTo = "${request.toUid}request"

        db.collection("requests")
            .document(userRequestIdFrom)
            .collection("requestfrom")
            .document(request.requestId)
            .update("status","Accepted")
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        db.collection("requests")
            .document(userRequestIdTo)
            .collection("requestto")
            .document(request.requestId)
            .delete()

        db.collection("reports").document("totaldonation")
            .update("no", FieldValue.increment(1))

        val currentDate = LocalDate.now()

        val donationMonthId = "donation${currentDate.format(DateTimeFormatter.ofPattern("yyyyMM"))}"

        db.collection("reports").document(donationMonthId)
            .set(mapOf("no" to FieldValue.increment(1)), SetOptions.merge())

        val newMsgTo = Message(
            body = "You have accepted a request from this user",
            from = "0",
            messageId = UUID.randomUUID().toString(),
            sentAt = Timestamp.now()
        )

        // Send to user1 -> user2 channel
        sendMessageToChannel(newMsgTo, request.toUid, request.fromUid, newMsgTo.messageId)

        // Send to user2 -> user1 channel (with 'from' set to '1')
        val reverseMessage = newMsgTo.copy(from = "1", body = "I have accepted a request from you. Please check on request list")
        sendMessageToChannel(reverseMessage, request.fromUid,  request.toUid, newMsgTo.messageId)
    }

    fun rejectRequest(request: Request){

        val userRequestIdFrom = "${request.fromUid}request"
        val userRequestIdTo = "${request.toUid}request"

        db.collection("requests")
            .document(userRequestIdFrom)
            .collection("requestfrom")
            .document(request.requestId)
            .update("status","Rejected")
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        db.collection("requests")
            .document(userRequestIdTo)
            .collection("requestto")
            .document(request.requestId)
            .delete()

        val newMsgTo = Message(
            body = "You have rejected a request from this user",
            from = "0",
            messageId = UUID.randomUUID().toString(),
            sentAt = Timestamp.now()
        )

        // Send to user1 -> user2 channel
        sendMessageToChannel(newMsgTo, request.toUid, request.fromUid, newMsgTo.messageId)

        // Send to user2 -> user1 channel (with 'from' set to '1')
        val reverseMessage = newMsgTo.copy(from = "1", body = "I have rejected a request from you. Please check on request list")
        sendMessageToChannel(reverseMessage, request.fromUid,  request.toUid, newMsgTo.messageId)
    }


    fun deleteRequest(request: Request){

        val userRequestIdFrom = "${request.fromUid}request"
        val userRequestIdTo = "${request.toUid}request"

        db.collection("requests")
            .document(userRequestIdFrom)
            .collection("requestfrom")
            .document(request.requestId)
            .delete()

    }

    private fun sendMessageToChannel(message: Message, userId1: String, userId2: String, messageId: String) {
        val channelId = getCombinedId(userId1, userId2)

        val channel = Channel(
            channelId = channelId,
            user1 = userId1,
            user2 = userId2,
        )

        db.collection("channels").document(channelId)
            .set(channel)
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("channels").document(channelId)
            .collection("messages")
            .document(messageId) // Use the same messageId as document ID
            .set(message)
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getCombinedId(userId1: String, userId2: String): String {
        // Implement how you generate the channelId based on userId1 and userId2
        return "$userId1-$userId2" // simple concatenation for illustration
    }
}