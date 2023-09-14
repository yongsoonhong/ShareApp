package my.edu.tarc.fyp.shareapp.domain

import com.google.firebase.Timestamp

data class Request(
    val requestId:String,
    val sharedItemId: String,
    val fromUid: String,
    val toUid: String,
    val timeRequest: Timestamp,
    val status: String
){
    constructor() : this("","", "", "",  Timestamp.now(), "")
}
