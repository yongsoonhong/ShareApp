package my.edu.tarc.fyp.shareapp.presentation.sharedItem

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
fun FullMapScreen(
    usePreciseLocation: Boolean,
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit,
    onConfirm: () -> Unit
) {
    val itemDetails = sharedItemUiState.itemDetails
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()
    var currentLocation by remember { mutableStateOf(LatLng(0.0,0.0)) }
    val mapProperties = remember { MapProperties(maxZoomPreference = 20f, minZoomPreference = 1f) }
    val mapUiSettings = remember { MapUiSettings(mapToolbarEnabled = false, myLocationButtonEnabled = true, scrollGesturesEnabled = true, zoomControlsEnabled = true) }
    val cameraPositionState = remember { mutableStateOf(CameraPositionState(CameraPosition.fromLatLngZoom(currentLocation, 15f))) }

    fun setCameraPosition() {
        cameraPositionState.value.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    suspend fun setCurrentLocation() {
        val priority = if (usePreciseLocation) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        val result = locationClient.getCurrentLocation(priority, CancellationTokenSource().token).await()
        result?.let { fetchedLocation ->
            currentLocation = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
            onItemValueChange(itemDetails.copy(latLng = currentLocation))
            scope.launch {
                setCameraPosition()
            }
        }
    }

//    LaunchedEffect(Unit) {
//        setCurrentLocation()
//    }


    LaunchedEffect(cameraPositionState.value.position.target) {
        if (cameraPositionState.value.isMoving){
            currentLocation = cameraPositionState.value.position.target
            Log.d("CurrentLocation", currentLocation.toString())
            onItemValueChange(itemDetails.copy(latLng = currentLocation))
        }
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Cyan,
                contentColor= Color.Black,
                disabledContainerColor= Color.LightGray,
                disabledContentColor= Color.Black,
            ),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    setCurrentLocation()
                }
            }) {
            Text(text = "Use Current Location")
        }


        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            GoogleMap(properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState.value,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Selected Location"
                )
            }
            Button(
                modifier = Modifier.padding(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan,
                    contentColor= Color.Black,
                    disabledContainerColor= Color.LightGray,
                    disabledContentColor= Color.Black,
                ),
                onClick = {
                    onConfirm()
                }) {
                Text(text = "Confirm")
            }
        }
    }
}
