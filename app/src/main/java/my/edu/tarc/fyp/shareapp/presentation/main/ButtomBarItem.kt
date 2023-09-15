package my.edu.tarc.fyp.shareapp.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import my.edu.tarc.fyp.shareapp.R

sealed class BottomBarItem(
    val route: String,
    val title: String,
    val icon: Int
){
    object HomeScreen : BottomBarItem(
        route = "nearby_list",
        title = "Home",
        icon = R.drawable.baseline_home_24
    )
    object RestaurantScreen : BottomBarItem(
        route = "restaurant_list",
        title = "Restaurant",
        icon = R.drawable.baseline_shopping_bag_24
    )

    object FoodManageScreen : BottomBarItem(
        route = "manage_list",
        title = "Manage",
        icon = R.drawable.baseline_view_list_24
    )
    object MessageScreen : BottomBarItem(
        route = "message_list",
        title = "Message",
        icon = R.drawable.baseline_message_24
    )
    object UserScreen : BottomBarItem(
        route = "profile_home",
        title = "Profile",
        icon = R.drawable.baseline_person_24
    )

}
