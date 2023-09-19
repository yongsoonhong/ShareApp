package my.edu.tarc.fyp.shareapp.domain

data class SharedItem(
    var sharedItemId: String? = null,
    val title: String = "",
    val description: String = "",
    val dateAdded: String? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val noLike: Int? = 0,
    val noView: Int? = null,
    val userId: String? = null,
    var imageUrl: String? = null
) {
    constructor() : this(null, "", "", null, null, null, 0, null, null, null)
}
