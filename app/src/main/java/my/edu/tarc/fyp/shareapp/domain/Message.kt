package my.edu.tarc.fyp.shareapp.domain

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.util.Date


data class Message(
    val messageId: String,
    val from: String,
    val body: String,
    val sentAt: Timestamp
) {
    constructor() : this("","", "",  Timestamp.now())
}