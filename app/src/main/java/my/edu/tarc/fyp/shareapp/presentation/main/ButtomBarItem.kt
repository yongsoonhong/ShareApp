package my.edu.tarc.fyp.shareapp.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarItem(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object HomeScreen : BottomBarItem(
        route = "nearby_list",
        title = "Home",
        icon = Icons.Default.Home
    )
    object RestaurantScreen : BottomBarItem(
        route = "restaurant_list",
        title = "Restaurant",
        icon = Icons.Default.ArrowBack
    )

    object FoodManageScreen : BottomBarItem(
        route = "manage_list",
        title = "Manage",
        icon = Icons.Default.Add
    )
    object MessageScreen : BottomBarItem(
        route = "message_list",
        title = "Message",
        icon = Icons.Default.Send
    )
    object UserScreen : BottomBarItem(
        route = "profile_home",
        title = "Profile",
        icon = Icons.Default.AccountCircle
    )

}
