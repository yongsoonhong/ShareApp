package my.edu.tarc.fyp.shareapp.domain

data class Restaurant(
    var restaurantId: String? = null,
    val restaurantName: String = "",
    val pickUpStartTime: String? = null,
    val pickUpEndTime: String? = null,
    val address: String? = null,
    val description: String? = null,
    val noLike: Int? = null,
    val noView: Int? = null,
    var imageUrl: String? = null
) {
    constructor() : this(null, "", "", null, null, null, null, null, null)
}