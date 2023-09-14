package my.edu.tarc.fyp.shareapp.domain

data class Channel (
    val channelId: String,
    val user1: String,
    val user2: String,
){
    constructor() : this("","","")
}