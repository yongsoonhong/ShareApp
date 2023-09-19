package my.edu.tarc.fyp.shareapp.presentation.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.PermissionBox
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.MapsScreen2
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemDetails
import my.edu.tarc.fyp.shareapp.presentation.sharedItem.SharedItemUiState
import java.lang.Math.cos


@SuppressLint("MissingPermission")
@Composable
fun NearbyItemScreen(
    currentLocation: LatLng,
    onUserLocationChange:(LatLng) -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (SharedItem) -> Unit,
    onAddClick: () -> Unit,
    sharedItems: LazyPagingItems<SharedItem>
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

        LaunchedEffect(true) {
            setCurrentLocation()
        }



        NearbyItemScreenBody(
            currentLocation = currentLocation,
            onRefresh = onRefresh,
            isLoading = isLoading,
            onItemClick = onItemClick,
            onAddClick = onAddClick,
            sharedItems = sharedItems
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NearbyItemScreenBody(
    currentLocation: LatLng,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (SharedItem) -> Unit,
    onAddClick: () -> Unit,
    sharedItems: LazyPagingItems<SharedItem>
) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val context = LocalContext.current
    LaunchedEffect(key1 = sharedItems.loadState) {
        if(sharedItems.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (sharedItems.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold (
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Add")
                    Text(text = "Own Shared Item")
                },
                modifier = Modifier.padding(bottom = 50.dp),
                onClick =  onAddClick
            )
        }
    ) {
        Column {
            Text(
                text = "Nearby Items",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
            ) {
                if(sharedItems.loadState.refresh is LoadState.Loading) {
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
                            val longChange = 10 / (111.320 * cos(Math.toRadians(currentLocation.latitude)))

                            val minLat = currentLocation.latitude - latChange
                            val maxLat = currentLocation.latitude + latChange

                            val minLong = currentLocation.longitude - longChange
                            val maxLong = currentLocation.longitude + longChange



                            items(sharedItems) { sharedItem ->
                                if((sharedItem != null) &&
                                    sharedItem.noLike == 0 &&
                                    (sharedItem.userId != Firebase.auth.currentUser?.uid) &&
                                    (sharedItem.latitude!! >= minLat) &&
                                    (sharedItem.latitude <= maxLat) &&
                                    (sharedItem.longitude!! >= minLong) &&
                                    (sharedItem.longitude <= maxLong)) {
                                    NearbyItemItem(
                                        sharedItem = sharedItem,
                                        onItemClick = onItemClick
                                    )
                                }
                            }
                            item {
                                if(sharedItems.loadState.append is LoadState.Loading) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }

                }
            }
        }


    }


}



