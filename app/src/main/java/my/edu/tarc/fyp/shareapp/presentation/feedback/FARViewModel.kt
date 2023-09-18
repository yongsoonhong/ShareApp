package my.edu.tarc.fyp.shareapp.presentation.feedback

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import my.edu.tarc.fyp.shareapp.domain.Request
import my.edu.tarc.fyp.shareapp.domain.Review
import my.edu.tarc.fyp.shareapp.domain.UserData
import javax.inject.Inject

@HiltViewModel
class FARViewModel @Inject constructor(
): ViewModel(){
    val db = Firebase.firestore

    private val _userToDisplay = MutableStateFlow<UserData>(UserData())
    val userToDisplay: StateFlow<UserData> get() = _userToDisplay

    private val _star = MutableStateFlow<Int>(0)
    val star: StateFlow<Int> get() = _star
    private val _reviewBody = MutableStateFlow<String>("")
    val reviewBody: StateFlow<String> get() = _reviewBody
    private val _isReviewWritten = MutableStateFlow<Boolean>(false)
    val isReviewWritten: StateFlow<Boolean> get() = _isReviewWritten

    fun getUserToDisplay(uid: String){
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { value ->
                val fetchedUser = value?.toObject(UserData::class.java)
                if (fetchedUser != null) {
                    _userToDisplay.value = fetchedUser
                    isReviewWritten(fetchedUser.uid)
                }
            }
            .addOnFailureListener { e ->
                e.let {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                }
            }
    }

    fun isReviewWritten(uid:String){
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
}