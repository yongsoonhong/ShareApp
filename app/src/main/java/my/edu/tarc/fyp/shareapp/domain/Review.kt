package my.edu.tarc.fyp.shareapp.domain

import com.google.firebase.Timestamp


data class Review(
    val fromUid: String,
    val body: String? = null,
    val star: Int,
    val timestamp: com.google.firebase.Timestamp
){
    constructor() : this("", null, 0, Timestamp.now())
}
