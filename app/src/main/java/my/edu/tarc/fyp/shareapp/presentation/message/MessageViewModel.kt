package my.edu.tarc.fyp.shareapp.presentation.message

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import my.edu.tarc.fyp.shareapp.domain.Channel
import my.edu.tarc.fyp.shareapp.domain.Message
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import my.edu.tarc.fyp.shareapp.domain.UserData
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
): ViewModel(){

    val db = Firebase.firestore

    private val _channels = MutableStateFlow<List<Channel>>(listOf())
    val channels: StateFlow<List<Channel>> get() = _channels

    private val _currentMessages = MutableStateFlow<List<Message>>(listOf())
    val currentMessages: StateFlow<List<Message>> get() = _currentMessages

    private val _usersData = mutableStateOf<Map<String, UserData>>(mapOf())
    val usersData: MutableState<Map<String, UserData>> get() = _usersData

    private val _currentSharedItem = MutableStateFlow<List<SharedItem>>(listOf())
    val currentSharedItem: StateFlow<List<SharedItem>> get() = _currentSharedItem

    private val _currentRequestsToUser = MutableStateFlow<List<Request>>(listOf())
    val currentRequestsToUser: StateFlow<List<Request>> get() = _currentRequestsToUser
    private val _currentRequestsFromUser = MutableStateFlow<List<Request>>(listOf())
    val currentRequestsFromUser: StateFlow<List<Request>> get() = _currentRequestsFromUser

    private val _currentItemRequestsToUser = MutableStateFlow<Map<Request,SharedItem>>(mapOf())
    val currentItemRequestsToUser: StateFlow<Map<Request,SharedItem>> get() = _currentItemRequestsToUser
    private val _currentItemRequestsFromUser = MutableStateFlow<Map<Request,SharedItem>>(mapOf())
    val currentItemRequestsFromUser: StateFlow<Map<Request,SharedItem>> get() = _currentItemRequestsFromUser

    init {
        getChannels()
    }


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

    //Accept or Reject the request from other
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



    private fun storeRequestToFirestore(request: Request, requestId: String, requestItemId: String, fromOrTo:String) {
        db.collection("requests").document(requestId)
            .collection(fromOrTo)
            .document(requestItemId)
            .set(request)
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getCurrentRequestToUser(uid: String) {
        val requestId = "${Firebase.auth.currentUser!!.uid}request"


        db.collection("requests")
            .document(requestId)
            .collection("requestto")
            .whereEqualTo("fromUid",uid)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedRequests = value?.documents?.mapNotNull {
                    it.toObject(Request::class.java)
                }
                if (fetchedRequests != null) {
                    _currentRequestsToUser.value = fetchedRequests

                    fetchedRequests.forEach{ request ->
                        getCurrentRequestToUserSharedItem(request)
                    }
                }
            }
    }
    fun getCurrentRequestFromUser(uid: String) {
        val requestId = "${Firebase.auth.currentUser!!.uid}request"

        db.collection("requests")
            .document(requestId)
            .collection("requestfrom")
            .whereEqualTo("toUid",uid)
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
                        getCurrentRequestFromUserSharedItem(request)
                    }
                }
            }
    }

    fun getCurrentRequestToUserSharedItem(request: Request) {
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

    fun getCurrentRequestFromUserSharedItem(request: Request) {
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




    fun clearCurrentItemRequestsToUser() {
        _currentItemRequestsToUser.value = mapOf()
    }

    fun clearCurrentItemRequestsFromUser() {
        _currentItemRequestsFromUser.value = mapOf()
    }


    fun getCurrentSharedItem(uid: String) {
        db.collection("shareditems")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedItems = value?.documents?.mapNotNull {
                    it.toObject(SharedItem::class.java)
                }
                if (fetchedItems != null) {
                    _currentSharedItem.value = fetchedItems
                }
            }
    }

    fun fetchUserData(uid: String) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(UserData::class.java)
                    if (user != null) {
                        val updatedUserData = _usersData.value.toMutableMap()
                        updatedUserData[uid] = user
                        _usersData.value = updatedUserData
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    fun getChannels() {
        db.collection("channels")
            .whereEqualTo("user1", Firebase.auth.currentUser?.uid)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedChannels = value?.documents?.mapNotNull {
                    it.toObject(Channel::class.java)
                }
                if (fetchedChannels != null) {
                    _channels.value = fetchedChannels

                    fetchedChannels.forEach { channel ->
                        if (!_usersData.value.containsKey(channel.user2)) { // Only fetch if not already fetched
                            fetchUserData(channel.user2)
                        }
                    }
                }
            }
    }

    fun fetchMessagesForChannel(channelId: String) {
        db.collection("channels").document(channelId)
            .collection("messages")
            .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING )
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val fetchedMessages = value?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                }
                if (fetchedMessages != null) {
                    _currentMessages.value = fetchedMessages
                }
            }
    }

    fun send(messageBody: String, currentUserId: String, otherUserId: String) {
        viewModelScope.launch {
            val messageId = UUID.randomUUID().toString() // generate a unique ID
            val timestamp = Timestamp.now() // current timestamp

            val message = Message(
                body = messageBody,
                from = "0", // because it's from the current user
                messageId = messageId,
                sentAt = timestamp
            )

            // Send to user1 -> user2 channel
            sendMessageToChannel(message, currentUserId, otherUserId, messageId)

            // Send to user2 -> user1 channel (with 'from' set to '1')
            val reverseMessage = message.copy(from = "1")
            sendMessageToChannel(reverseMessage, otherUserId, currentUserId, messageId)
        }

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


    fun createChannelWithUID(otherUserId: String) {
        val currentUserId = Firebase.auth.currentUser?.uid

        // Check if UID exists in your user database
        db.collection("users").document(otherUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // User exists, now let's check if a channel already exists between them
                    val channelId = getCombinedId(currentUserId!!, otherUserId)
                    db.collection("channels").document(channelId)
                        .get()
                        .addOnSuccessListener { channelDocument ->
                            if (!channelDocument.exists()) {
                                // No channel exists, let's create one
                                val newChannel = Channel(
                                    channelId = channelId,
                                    user1 = currentUserId,
                                    user2 = otherUserId,
                                )
                                db.collection("channels").document(channelId)
                                    .set(newChannel)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Channel created successfully.")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error creating channel", e)
                                    }
                            } else {
                                Log.d(TAG, "Channel already exists between users.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error checking channel existence", e)
                        }
                } else {
                    Log.d(TAG, "User with UID: $otherUserId does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching user with UID: $otherUserId", e)
            }
    }

    fun getChannelIdWithUserId(otherUserId: String): String? {
        val channelId = getCombinedId(Firebase.auth.currentUser?.uid ?: "", otherUserId)
        db.collection("channels").document(channelId)
            .get()
            .addOnSuccessListener { channelDocument ->
                if (!channelDocument.exists()) {
                    // No channel exists, let's create one
                    val newChannel = Channel(
                        channelId = channelId,
                        user1 = Firebase.auth.currentUser?.uid ?: "",
                        user2 = otherUserId,
                    )
                    db.collection("channels").document(channelId)
                        .set(newChannel)
                        .addOnSuccessListener {
                            Log.d(TAG, "Channel created successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error creating channel", e)
                        }
                } else {
                    Log.d(TAG, "Channel already exists between users.")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking channel existence", e)
            }

        return channelId

    }




}