package my.edu.tarc.fyp.shareapp.domain

data class UserData(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null
){
    constructor() : this("", "", null, null)
}
