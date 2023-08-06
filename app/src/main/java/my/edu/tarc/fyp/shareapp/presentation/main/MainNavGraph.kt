package my.edu.tarc.fyp.shareapp.presentation.main

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import my.edu.tarc.fyp.shareapp.ScreenContent
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemAddScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemDetails
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemEditScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemScreen
import my.edu.tarc.fyp.shareapp.presentation.manage.ManageItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemScreen
import my.edu.tarc.fyp.shareapp.presentation.nearby.NearbyItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.profile.ProfileScreen
import my.edu.tarc.fyp.shareapp.presentation.profile.ProfileViewModel
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemScreen
import my.edu.tarc.fyp.shareapp.presentation.restaurant.RestaurantItemViewModel
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemAddScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetails
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetailsScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemEditScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemScreen
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemViewModel
import my.edu.tarc.fyp.shareapp.sharedViewModel

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
                        viewModel.currentUserLocation = latlng
                    }
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
                sharedItem?.let {
                    NearbyItemDetailsScreen(
                        sharedItem = it,
                        onItemRequestClick = {

                        }
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
                    }
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
                        restaurant = it,
                        onItemRequestClick = {

                        }
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
                ScreenContent(
                    name = "message_list",
                    onClick = { }
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
                    }
                )
            }
        }
    }

}
