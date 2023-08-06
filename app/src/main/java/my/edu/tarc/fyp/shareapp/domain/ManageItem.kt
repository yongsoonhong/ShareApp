package my.edu.tarc.fyp.shareapp.domain

data class ManageItem(
    var manageItemId: String? = null,
    val title: String = "",
    val description: String = "",
    val dateAdded: String? = null,
    val userId: String? = null,
    var imageUrl: String? = null,
    val expiryDate: String = ""
) {
    constructor() : this(null, "", "", null, null, null, "")
}
