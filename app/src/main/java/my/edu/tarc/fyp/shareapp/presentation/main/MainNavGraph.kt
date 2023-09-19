package my.edu.tarc.fyp.shareapp.presentation.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import my.edu.tarc.fyp.shareapp.presentation.feedback.FARScreen
import my.edu.tarc.fyp.shareapp.presentation.feedback.FARViewModel
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemAddScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemDetails
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemEditScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.message.AddChannelScreen
import my.edu.tarc.fyp.shareapp.presentation.message.ChatRoomScreen
import my.edu.tarc.fyp.shareapp.presentation.message.MessageListScreen
import my.edu.tarc.fyp.shareapp.presentation.message.MessageViewModel
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemScreen
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.profile.ChangePasswordScreen
import my.edu.tarc.fyp.shareapp.presentation.profile.EditUserProfileScreen
import my.edu.tarc.fyp.shareapp.presentation.profile.ProfileScreen
import my.edu.tarc.fyp.shareapp.presentation.profile.ProfileViewModel
import my.edu.tarc.fyp.shareapp.presentation.profile.RequestsFromYouScreen
import my.edu.tarc.fyp.shareapp.presentation.profile.RequestsToYouScreen
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemScreen
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.FullMapScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemAddScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetails
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemEditScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemViewModel
import my.edu.tarc.fyp.shareapp.sharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainNavGraph(navController: NavHostController){
    navigation(
        route = "main",
        startDestination = "main_nearby"
    ){
        navigation(
            route = "main_nearby",
            startDestination = "nearby_list",
        ){
            composable("nearby_list"){
                val viewModel = it.sharedViewModel<NearbyItemViewModel>(navController = navController)
                val sharedItems = viewModel.sharedItemPagingFlow.collectAsLazyPagingItems()
                val isLoading by viewModel.isLoading.collectAsState()
                val coroutineScope = rememberCoroutineScope()

                val currentLocation by viewModel.currentUserLocation.collectAsState()



                NearbyItemScreen(
                    isLoading = isLoading,
                    sharedItems = sharedItems,
                    onItemClick = { sharedItem ->
                        val args = sharedItem.sharedItemId
                        navController.navigate(
                            "nearby_details/${args}"
                        )
                    },
                    onAddClick = {
                        navController.navigate("shared")
                    },
                    onRefresh = {
                        coroutineScope.launch {
                            sharedItems.refresh()
                        }
                    },
                    onUserLocationChange = { latlng ->
                        viewModel.updateCurrentUserLocation(latlng)
                    },
                    currentLocation = currentLocation
                )
            }
            composable(
                route = "nearby_details/{sharedItemId}",
                arguments = listOf(navArgument("sharedItemId"){
                    type = NavType.StringType
                })
            ){
                    navBackStackEntry ->
                val viewModel: NearbyItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                val sharedItemId = navBackStackEntry.arguments?.getString("sharedItemId")
                val coroutineScope = rememberCoroutineScope()

                sharedItemId?.let { id ->
                    viewModel.getItemById(id)
                }

                val sharedItem by viewModel.sharedItem.observeAsState()



                sharedItem?.let {item ->
                    viewModel.fetchUserData(item.userId!!)
                }

                val userData by viewModel.userData.observeAsState()

                userData?.let {data ->
                    NearbyItemDetailsScreen(
                        sharedItem = sharedItem!!,
                        onItemRequestClick = {
                            viewModel.addRequest(sharedItemId!!,userData!!.uid)
                            navController.navigate("message_room/${Firebase.auth.currentUser!!.uid}-${data.uid}")
                        },
                        onFARClick = {
                            navController.navigate("feedback_user/${it}")
                        },
                        userData = data
                    )
                }
            }
            navigation(
                route = "shared",
                startDestination = "shared_list"
            ){
                composable("shared_list"){
                    val viewModel = it.sharedViewModel<SharedItemViewModel>(navController = navController)
                    val sharedItems = viewModel.sharedItemPagingFlow.collectAsLazyPagingItems()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val coroutineScope = rememberCoroutineScope()

                    SharedItemScreen(
                        isLoading = isLoading,
                        sharedItems = sharedItems,
                        onItemClick = { sharedItem ->
                            val args = sharedItem.sharedItemId
                            navController.navigate(
                                "shared_details/${args}"
                            )
                        },
                        onAddClick = {
                            navController.navigate("shared_add")
                        },
                        onRefresh = {
                            coroutineScope.launch {
                                sharedItems.refresh()
                            }
                        }

                    )
                }
                composable(
                    route = "shared_details/{sharedItemId}",
                    arguments = listOf(navArgument("sharedItemId"){
                        type = NavType.StringType
                    })
                ){ navBackStackEntry ->
                    val viewModel: SharedItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                    val sharedItemId = navBackStackEntry.arguments?.getString("sharedItemId")
                    val coroutineScope = rememberCoroutineScope()


                    sharedItemId?.let { id ->
                        viewModel.getItemById(id)
                    }

                    val sharedItem by viewModel.sharedItem.observeAsState()
                    sharedItem?.let {
                        SharedItemDetailsScreen(
                            sharedItem = it,
                            onDeleteClick = {
                                coroutineScope.launch {
                                    navController.navigate("shared_list"){
                                        popUpTo("shared_list"){
                                            inclusive = true
                                        }
                                    }
                                    viewModel.deleteItemById(sharedItemId!!)
                                }
                            },
                            onItemEditClick = {
                                val sharedItemDetails = SharedItemDetails(
                                    title = sharedItem!!.title,
                                    description = sharedItem!!.description,
                                    imageUri = sharedItem!!.imageUrl?.toUri(),
                                    latLng = sharedItem!!.latitude?.let { it1 -> sharedItem!!.longitude?.let { it2 ->
                                        LatLng(it1,
                                            it2
                                        )
                                    } }
                                )

                                viewModel.updateUiState(sharedItemDetails)

                                navController.navigate("shared_edit/${sharedItemId}")
                            },
                            onStartSharingClick = { idGet ->
                                viewModel.startSharingItem(idGet)
                                navController.popBackStack()
                            },
                            onStopSharingClick = { idGet ->
                                viewModel.stopSharingItem(idGet)
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable(
                    route = "shared_edit/{sharedItemId}",
                    arguments = listOf(navArgument("sharedItemId"){
                        type = NavType.StringType
                    })
                ){ navBackStackEntry ->
                    val viewModel: SharedItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                    val sharedItemId = navBackStackEntry.arguments?.getString("sharedItemId")
                    val coroutineScope = rememberCoroutineScope()

                    sharedItemId?.let { id ->
                        viewModel.getItemById(id)
                    }

                    val sharedItem by viewModel.sharedItem.observeAsState()
                    sharedItem?.let {
                        val sharedItemUiState = viewModel.sharedItemUiState

                        SharedItemEditScreen(
                            sharedItem = it,
                            onCancelClick = {
                                navController.popBackStack()
                            },
                            onSaveClick = {
                                coroutineScope.launch {
                                    viewModel.updateSharedItemDetails(sharedItemId!!)
                                    navController.popBackStack()
                                }
                            },
                            sharedItemUiState = sharedItemUiState,
                            onItemValueChange = viewModel::updateUiState
                        )
                    }
                }
                composable("shared_add"){
                    val viewModel = it.sharedViewModel<SharedItemViewModel>(navController = navController)
                    val sharedItemUiState = viewModel.sharedItemUiState
                    val coroutineScope = rememberCoroutineScope()

                    SharedItemAddScreen(
                        sharedItemUiState = sharedItemUiState,
                        onItemValueChange = viewModel::updateUiState,
                        onSaveClick = {
                            coroutineScope.launch {
                                viewModel.saveSharedItem()
                                navController.popBackStack()
                            }
                        },
                        onMapClick = {
                            navController.navigate("full_google_map")
                        }
                    )
                }
                composable("full_google_map"){
                    val viewModel = it.sharedViewModel<SharedItemViewModel>(navController = navController)
                    val sharedItemUiState = viewModel.sharedItemUiState
                    FullMapScreen(
                        usePreciseLocation = true,
                        sharedItemUiState = sharedItemUiState,
                        onItemValueChange = viewModel::updateUiState,
                        onConfirm = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }

        navigation(
            route = "main_restaurant",
            startDestination = "restaurant_list",
        ){
            composable("restaurant_list"){
                val viewModel = it.sharedViewModel<RestaurantItemViewModel>(navController = navController)
                val restaurants = viewModel.restaurantPagingFlow.collectAsLazyPagingItems()
                val isLoading by viewModel.isLoading.collectAsState()
                val coroutineScope = rememberCoroutineScope()


                RestaurantItemScreen(
                    isLoading = isLoading,
                    restaurants = restaurants,
                    onItemClick = { restaurant ->
                        val args = restaurant.restaurantId
                        navController.navigate(
                            "restaurant_details/${args}"
                        )
                    },
                    onAddClick = {

                    },
                    onRefresh = {
                        coroutineScope.launch {
                            restaurants.refresh()
                        }
                    },
                    onUserLocationChange = { latlng ->
                        viewModel.currentUserLocation = latlng
                    },
                    currentLocation = viewModel.currentUserLocation
                )
            }
            composable(
                route = "restaurant_details/{restaurantId}",
                arguments = listOf(navArgument("restaurantId"){
                    type = NavType.StringType
                })
            ){
                    navBackStackEntry ->
                val viewModel: RestaurantItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                val restaurantId = navBackStackEntry.arguments?.getString("restaurantId")
                val coroutineScope = rememberCoroutineScope()

                if (restaurantId != null) {
                    Log.d("Restaurant",restaurantId)
                }
                restaurantId?.let { id ->
                    viewModel.getItemById(id)
                }



                val restaurant by viewModel.restaurant.observeAsState()
                restaurant?.let {
                    RestaurantItemDetailsScreen(
                        restaurant = it
                    )
                }
            }
        }

        navigation(
            route = "main_manage",
            startDestination = "manage_list",
        ){
            composable("manage_list"){
                val viewModel = it.sharedViewModel<ManageItemViewModel>(navController = navController)
                val manageItems = viewModel.manageItemPagingFlow.collectAsLazyPagingItems()
                val isLoading by viewModel.isLoading.collectAsState()
                val coroutineScope = rememberCoroutineScope()



                ManageItemScreen(
                    isLoading = isLoading,
                    manageItems = manageItems,
                    onItemClick = { manageItem ->
                        val args = manageItem.manageItemId
                        navController.navigate(
                            "manage_details/${args}"
                        )
                    },
                    onAddClick = {
                        navController.navigate("manage_add")
                    },
                    onRefresh = {
                        coroutineScope.launch {
                            manageItems.refresh()
                        }
                    }

                )
            }
            composable(
                route = "manage_details/{manageItemId}",
                arguments = listOf(navArgument("manageItemId"){
                    type = NavType.StringType
                })
            ){ navBackStackEntry ->
                val viewModel: ManageItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                val manageItemId = navBackStackEntry.arguments?.getString("manageItemId")
                val coroutineScope = rememberCoroutineScope()


                manageItemId?.let { id ->
                    viewModel.getItemById(id)
                }

                val manageItem by viewModel.manageItem.observeAsState()
                manageItem?.let {
                    ManageItemDetailsScreen(
                        manageItem = it,
                        onDeleteClick = {
                            coroutineScope.launch {
                                navController.navigate("manage_list"){
                                    popUpTo("manage_list"){
                                        inclusive = true
                                    }
                                }
                                viewModel.deleteItemById(manageItemId!!)
                            }
                        },
                        onItemEditClick = {
                            val manageItemDetails = ManageItemDetails(
                                title = manageItem!!.title,
                                description = manageItem!!.description,
                                imageUri = manageItem!!.imageUrl?.toUri(),
                                expiryDate = manageItem!!.expiryDate
                            )

                            viewModel.updateUiState(manageItemDetails)

                            navController.navigate("manage_edit/${manageItemId}")
                        }
                    )
                }
            }
            composable(
                route = "manage_edit/{manageItemId}",
                arguments = listOf(navArgument("manageItemId"){
                    type = NavType.StringType
                })
            ){ navBackStackEntry ->
                val viewModel: ManageItemViewModel = navBackStackEntry.sharedViewModel(navController = navController)
                val manageItemId = navBackStackEntry.arguments?.getString("manageItemId")
                val coroutineScope = rememberCoroutineScope()

                manageItemId?.let { id ->
                    viewModel.getItemById(id)
                }

                val manageItem by viewModel.manageItem.observeAsState()
                manageItem?.let {
                    val manageItemUiState = viewModel.manageItemUiState

                    ManageItemEditScreen(
                        manageItem = it,
                        onCancelClick = {
                            navController.popBackStack()
                        },
                        onSaveClick = {
                            coroutineScope.launch {
                                viewModel.updateManageItemDetails(manageItemId!!)
                                navController.popBackStack()
                            }
                        },
                        manageItemUiState = manageItemUiState,
                        onItemValueChange = viewModel::updateUiState
                    )
                }
            }
            composable("manage_add"){
                val viewModel = it.sharedViewModel<ManageItemViewModel>(navController = navController)
                val manageItemUiState = viewModel.manageItemUiState
                val coroutineScope = rememberCoroutineScope()

                ManageItemAddScreen(
                    manageItemUiState = manageItemUiState,
                    onItemValueChange = viewModel::updateUiState,
                    onSaveClick = {
                        coroutineScope.launch {
                            viewModel.saveManageItem()
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        navigation(
            route = "main_message",
            startDestination = "message_list",
        ){
            composable("message_list"){
                val viewModel = it.sharedViewModel<MessageViewModel>(navController = navController)

                // Collect the channels as state
                val channels by viewModel.channels.collectAsState(initial = emptyList())
                val usersData = viewModel.usersData.value


                Log.d("Channels", channels.toString())

                // Composable Integration
                MessageListScreen(
                    onItemClick = { channel ->
                        navController.navigate("message_room/${channel.channelId}")
                    },
                    channels = channels,
                    usersData = usersData,
                    onAddChannelClick = { navController.navigate("message_add") }

                )
            }

            composable(
                route = "message_room/{channelId}",
                arguments = listOf(navArgument("channelId") {
                    type = NavType.StringType
                })
            ) { navBackStackEntry ->

                val channelId = navBackStackEntry.arguments?.getString("channelId")
                val viewModel: MessageViewModel = navBackStackEntry.sharedViewModel(navController = navController)

                val channels by viewModel.channels.collectAsState()
                val currentMessages by viewModel.currentMessages.collectAsState()
                val usersData = viewModel.usersData.value
                val sharedItems by viewModel.currentSharedItem.collectAsState()
                val itemReqFrom by viewModel.currentItemRequestsFromUser.collectAsState()
                val itemReqTo by viewModel.currentItemRequestsToUser.collectAsState()

                // Fetch messages for the channel when we navigate to this composable
                viewModel.fetchMessagesForChannel(channelId ?: "")


                // Find the specific channel using channelId
                val channel = channels.find { it.channelId == channelId }


                val userData = channel?.let { usersData[it.user2] }
                LaunchedEffect(userData){
                    userData?.uid?.let {
                        viewModel.getCurrentSharedItem(it)
                        viewModel.clearCurrentItemRequestsToUser()
                        viewModel.clearCurrentItemRequestsFromUser()
                        viewModel.getCurrentRequestFromUser(it)
                        viewModel.getCurrentRequestToUser(it)
                    }
                }


                if (channel != null) {
                    ChatRoomScreen(
                        channel = channel,
                        messages = currentMessages,
                        sharedItems = sharedItems,
                        userData = userData,  // Passing the user data map to the ChatRoomScreen
                        onSendMessage = { messageText ->
                            viewModel.send(messageText, channel.user1, channel.user2)
                        },
                        onSendRequest = {
                            Log.d("SharedItemRequestInNav", it.toString())
                            viewModel.addRequest(it.sharedItemId!!,it.userId!!)
                        },
                        itemRequestFrom = itemReqFrom,
                        itemRequestTo = itemReqTo,
                        onItemAcceptClick = {
                            viewModel.acceptRequest(it)
                            viewModel.clearCurrentItemRequestsToUser()
                            viewModel.clearCurrentItemRequestsFromUser()
                            viewModel.getCurrentRequestFromUser(userData!!.uid)
                            viewModel.getCurrentRequestToUser(userData.uid)
                        },
                        onItemDeclineClick = {
                            viewModel.rejectRequest(it)
                            viewModel.clearCurrentItemRequestsToUser()
                            viewModel.clearCurrentItemRequestsFromUser()
                            viewModel.getCurrentRequestFromUser(userData!!.uid)
                            viewModel.getCurrentRequestToUser(userData.uid)
                        },
                        onItemDeleteClick = {
                            viewModel.deleteRequest(it)
                            viewModel.clearCurrentItemRequestsToUser()
                            viewModel.clearCurrentItemRequestsFromUser()
                            viewModel.getCurrentRequestFromUser(userData!!.uid)
                            viewModel.getCurrentRequestToUser(userData.uid)
                        },
                        onFARClick = {
                            navController.navigate("feedback_user/${it}")
                        }
                    )
                } else {
                    Log.e("Channel", "Error finding channel")
                }
            }

            composable("message_add") { navBackStackEntry ->

                val viewModel: MessageViewModel = navBackStackEntry.sharedViewModel(navController = navController)


                AddChannelScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCreateChannel = { uid ->
                        viewModel.createChannelWithUID(uid)
                        navController.popBackStack()
                    }
                )
            }


        }

        navigation(
            route = "main_profile",
            startDestination = "profile_home",
        ){

            composable("profile_home"){

                val viewModel = it.sharedViewModel<ProfileViewModel>(navController = navController)

                ProfileScreen(
                    onSignOutClick = {
                        viewModel.signOut()
                        navController.navigate("auth"){
                            popUpTo("main"){
                                inclusive = true
                            }
                        }
                    },
                    onEditUserProfileClick = {
                        navController.navigate("profile_editUserProfile")
                    },
                    onChangePasswordClick = {
                        navController.navigate("profile_changePassword")
                    },
                    onRequestFromYouClick = {
                        navController.navigate("profile_requestFromYou")

                    },
                    onRequestToYouClick = {
                        navController.navigate("profile_requestToYou")
                    },
                    onFARClick = {
                        navController.navigate("feedback_user/${Firebase.auth.currentUser!!.uid}")
                    }
                )
            }

            composable("profile_editUserProfile"){
                val viewModel = it.sharedViewModel<ProfileViewModel>(navController = navController)

                EditUserProfileScreen(
                    userData = Firebase.auth.currentUser,
                    onSaveClick = { name, photoUrl ->
                        viewModel.updateUserProfile(name, photoUrl)
                        navController.popBackStack()
                    },
                    onCancelClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile_changePassword"){
                val viewModel = it.sharedViewModel<ProfileViewModel>(navController = navController)

                ChangePasswordScreen(
                    onSaveClick = { pass ->
                        viewModel.updatePassword(pass)
                        navController.popBackStack()

                    },
                    onCancelClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile_requestFromYou"){
                val viewModel = it.sharedViewModel<ProfileViewModel>(navController = navController)

                val requests by viewModel.currentRequestsFromUser.collectAsState()
                val itemReqFrom by viewModel.currentItemRequestsFromUser.collectAsState()
                val userRequested by viewModel.currentRequestsFromYouUser.collectAsState()



                RequestsFromYouScreen(
                    requests = requests,
                    itemRequestToYou = itemReqFrom,
                    userRequested = userRequested,
                    onProceedClick = { requestGet, userData ->
                        viewModel.deleteRequest(requestGet)
                        viewModel.clearCurrentRequestFromYou()
                        viewModel.getCurrentRequestFromYou()
                        if (userData != null) {
                            navController.navigate("message_room/${Firebase.auth.currentUser!!.uid}-${userData.uid}")
                        }
                    },
                )

            }

            composable("profile_requestToYou"){
                val viewModel = it.sharedViewModel<ProfileViewModel>(navController = navController)

                val requests by viewModel.currentRequestsToUser.collectAsState()
                val itemReqTo by viewModel.currentItemRequestsToUser.collectAsState()
                val userRequested by viewModel.currentRequestsToYouUser.collectAsState()



                RequestsToYouScreen(
                    requests = requests,
                    itemRequestToYou = itemReqTo,
                    userRequested = userRequested,
                    onAcceptClick = { requestGet ->
                        viewModel.acceptRequest(requestGet)
                        viewModel.clearCurrentRequestToYou()
                        viewModel.getCurrentRequestToYou()
                    },
                    onRejectClick = { requestGet ->
                        viewModel.rejectRequest(requestGet)
                        viewModel.clearCurrentRequestToYou()
                        viewModel.getCurrentRequestToYou()
                    }
                )

            }
        }

        navigation(
            route = "feedback",
            startDestination = "feedback_user",
        ){
            composable(
                route = "feedback_user/{uid}",
                arguments = listOf(navArgument("uid") {
                    type = NavType.StringType
                })
            ){ navBackStackEntry ->

                val userToDisplayUid = navBackStackEntry.arguments?.getString("uid")

                val viewModel = navBackStackEntry.sharedViewModel<FARViewModel>(navController = navController)
                val userToDisplay by viewModel.userToDisplay.collectAsState()
                val star by viewModel.star.collectAsState()
                val reviewBody by viewModel.reviewBody.collectAsState()
                val isReviewWritten by viewModel.isReviewWritten.collectAsState()
                val reviews by viewModel.userReviewsToDisplay.collectAsState()
                val aveStar by viewModel.aveStar.collectAsState()

                LaunchedEffect(userToDisplayUid){
                    userToDisplayUid?.let {
                        viewModel.getUserToDisplay(it)
                    }
                }


                FARScreen(
                    user = userToDisplay,
                    onSendReviewClick = { starGet, body, uid ->
                        viewModel.sendReview(starGet, body, uid)
                        viewModel.getUserToDisplay(uid)
                    },
                    star = star,
                    body = reviewBody,
                    isReviewWritten = isReviewWritten,
                    reviews = reviews,
                    aveStar = aveStar
                )
            }
        }
    }
}
