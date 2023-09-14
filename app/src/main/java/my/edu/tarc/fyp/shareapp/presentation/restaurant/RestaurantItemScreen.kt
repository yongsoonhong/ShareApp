package my.edu.tarc.fyp.shareapp.presentation.restaurant

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.PermissionBox
import my.edu.tarc.fyp.shareapp.domain.Restaurant


@SuppressLint("MissingPermission")
@Composable
fun RestaurantItemScreen(
    currentLocation: LatLng,
    onUserLocationChange:(LatLng) -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (Restaurant) -> Unit,
    onAddClick: () -> Unit,
    restaurants: LazyPagingItems<Restaurant>
) {
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    var permissionGranted by remember { mutableStateOf(false) }
    var usePreciseLocation by remember { mutableStateOf(false) }
    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
        onGranted = { grantedPermissions ->
            permissionGranted = true
            usePreciseLocation = grantedPermissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)
        },
    )
    if (permissionGranted) {
        val context = LocalContext.current
        val locationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }


        suspend fun setCurrentLocation() {
            val result = locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).await()
            result?.let { fetchedLocation ->
                onUserLocationChange(LatLng(fetchedLocation.latitude,fetchedLocation.longitude))
            }
        }

        LaunchedEffect(Unit) {
            setCurrentLocation()
        }


        RestaurantItemScreenBody(
            currentLocation = currentLocation,
            onRefresh = onRefresh,
            isLoading = isLoading,
            onItemClick = onItemClick,
            onAddClick = onAddClick,
            restaurants = restaurants
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RestaurantItemScreenBody(
    currentLocation: LatLng,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (Restaurant) -> Unit,
    onAddClick: () -> Unit,
    restaurants: LazyPagingItems<Restaurant>
) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val context = LocalContext.current
    LaunchedEffect(key1 = restaurants.loadState) {
        if(restaurants.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (restaurants.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold (
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            if(restaurants.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                SwipeRefresh(state = swipeRefreshState, onRefresh = { onRefresh() }) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val latChange = 10 / 110.574
                        val longChange = 10 / (111.320 * Math.cos(Math.toRadians(currentLocation.latitude)))

                        val minLat = currentLocation.latitude - latChange
                        val maxLat = currentLocation.latitude + latChange

                        val minLong = currentLocation.longitude - longChange
                        val maxLong = currentLocation.longitude + longChange

                        items(restaurants) { restaurant ->
                            if(restaurant != null &&
                                (restaurant.latitude!! >= minLat) &&
                                (restaurant.latitude <= maxLat) &&
                                (restaurant.longitude!! >= minLong) &&
                                (restaurant.longitude <= maxLong)) {
                                RestaurantItemItem(
                                    restaurant = restaurant,
                                    onItemClick = onItemClick
                                )
                            }
                        }
                        item {
                            if(restaurants.loadState.append is LoadState.Loading) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

            }
        }
    }


}



