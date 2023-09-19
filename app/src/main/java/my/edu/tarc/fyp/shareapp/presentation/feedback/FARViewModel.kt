package my.edu.tarc.fyp.shareapp.presentation.feedback

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import my.edu.tarc.fyp.shareapp.domain.Review
import my.edu.tarc.fyp.shareapp.domain.UserData
import javax.inject.Inject

@HiltViewModel
class FARViewModel @Inject constructor(
): ViewModel(){
    val db = Firebase.firestore

    private val _userToDisplay = MutableStateFlow(UserData())
    val userToDisplay: StateFlow<UserData> get() = _userToDisplay

    private val _star = MutableStateFlow(0)
    val star: StateFlow<Int> get() = _star
    private val _reviewBody = MutableStateFlow("")
    val reviewBody: StateFlow<String> get() = _reviewBody
    private val _isReviewWritten = MutableStateFlow(false)
    val isReviewWritten: StateFlow<Boolean> get() = _isReviewWritten

    private val _userReviewsToDisplay = MutableStateFlow<Map<Review, UserData>>(mapOf())
    val userReviewsToDisplay: StateFlow<Map<Review, UserData>> get() = _userReviewsToDisplay

    private val _aveStar = MutableStateFlow(0.0)
    val aveStar: StateFlow<Double> get() = _aveStar

    private var calStar = 0.0
    private var noOfReview = 0


    fun getUserToDisplay(uid: String){
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { value ->
                val fetchedUser = value?.toObject(UserData::class.java)
                if (fetchedUser != null) {
                    _userToDisplay.value = fetchedUser
                    getAveStar(fetchedUser.uid)
                    isReviewWritten(fetchedUser.uid)
                    getUserReviewsToDisplay(fetchedUser.uid)

                }
            }
            .addOnFailureListener { e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
            }
    }

    private fun isReviewWritten(uid:String){
        val farId = "${uid}FAR"
        val reviewId = "${Firebase.auth.currentUser!!.uid}review"

        db.collection("FARs").document(farId).collection("review").document(reviewId)
            .get()
            .addOnSuccessListener { value ->

                val fetchedReview = value?.toObject(Review::class.java)

                if (fetchedReview != null){
                    _isReviewWritten.value = true
                    _star.value = fetchedReview.star
                    _reviewBody.value = fetchedReview.body?:""
                } else {
                    _isReviewWritten.value = false
                }
            }.addOnFailureListener{ e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
                _isReviewWritten.value = false
            }

    }

    fun sendReview(star: Int, body: String?, toUid: String){

        val farId = "${toUid}FAR"
        val reviewId = "${Firebase.auth.currentUser!!.uid}review"

        db.collection("FARs").document(farId).collection("review").document(reviewId)
            .set(
                Review(
                    fromUid = Firebase.auth.currentUser!!.uid,
                    body = body,
                    star = star,
                    timestamp = Timestamp.now()
                )
            ).addOnFailureListener { e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
            }
    }

    private fun getUserReviewsToDisplay(uid: String){
        val farId = "${uid}FAR"

        calStar = 0.0
        noOfReview = 0

        db.collection("FARs").document(farId).collection("review")
            .orderBy("timestamp")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                value?.documents?.mapNotNull {
                    it.toObject(Review::class.java)
                }?.forEach { review ->
                    calStar += review.star
                    getUserReviewToDisplay(review)
                }

                storeNewStar(uid)

            }
    }

    private fun storeNewStar(uid: String){
        val farId = "${uid}FAR"

        if (noOfReview == 0) {
            noOfReview = 1
        }

        db.collection("FARs").document(farId)
            .set(mapOf("aveStar" to calStar/noOfReview), SetOptions.merge())

    }

    private fun getAveStar(uid: String){

        val farId = "${uid}FAR"

        db.collection("FARs").document(farId)
            .get()
            .addOnSuccessListener { value ->

                _aveStar.value = value.get("aveStar") as Double

            }
            .addOnFailureListener { e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
            }
    }

    private fun getUserReviewToDisplay(review: Review){

        db.collection("users").document(review.fromUid)
            .get()
            .addOnSuccessListener { value ->

                val fetchedUser = value?.toObject(UserData::class.java)

                if (fetchedUser != null) {
                    val currentMap = _userReviewsToDisplay.value // Fetch the current map value

                    if (!(currentMap.containsValue(fetchedUser) && currentMap.containsKey(review))) {
                        val newMap = currentMap.toMutableMap() // Convert to a MutableMap to make changes
                        newMap[review] = fetchedUser // Add the new key-value pair
                        _userReviewsToDisplay.value = newMap // Set the new map as the new value of the MutableStateFlow

                    }
                }
            }
            .addOnFailureListener { e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
            }
    }
}