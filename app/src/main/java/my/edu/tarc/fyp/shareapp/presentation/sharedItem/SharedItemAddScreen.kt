package my.edu.tarc.fyp.shareapp.presentation.sharedItem

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
import my.edu.tarc.fyp.shareapp.PermissionBox
import my.edu.tarc.fyp.shareapp.R

@Composable
fun SharedItemAddScreen(
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ImagePicker(sharedItemUiState, onItemValueChange)
        ItemInputForm(sharedItemUiState, onItemValueChange, enabled = true)
        LocationPicker(sharedItemUiState, onItemValueChange)
        SaveButton(onSaveClick, sharedItemUiState.isEntryValid)
    }
}

@Composable
fun ImagePicker(
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit
) {
    val itemDetails = sharedItemUiState.itemDetails

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            onItemValueChange(itemDetails.copy(imageUri = uri))
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        Button(
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text(text = "pick photo")
        }

        AsyncImage(
            model = selectedImageUri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ItemInputForm(
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit,
    enabled: Boolean
) {

    val itemDetails = sharedItemUiState.itemDetails

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        OutlinedTextField(
            value = itemDetails.title,
            onValueChange = { onItemValueChange(itemDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.item_title_req)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = itemDetails.description,
            onValueChange = { onItemValueChange(itemDetails.copy(description = it)) },
            label = { Text(stringResource(R.string.description_req)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }


    }
}

@Composable
fun LocationPicker(
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit,
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
        MapsScreen2(
            usePreciseLocation = usePreciseLocation,
            sharedItemUiState = sharedItemUiState,
            onItemValueChange = onItemValueChange
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapsScreen2(
    usePreciseLocation: Boolean,
    sharedItemUiState: SharedItemUiState,
    onItemValueChange: (SharedItemDetails) -> Unit
) {
    val itemDetails = sharedItemUiState.itemDetails
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()
    var currentLocation by remember { mutableStateOf(LatLng(0.0,0.0)) }
    val mapProperties = remember { MapProperties(maxZoomPreference = 20f, minZoomPreference = 1f) }
    val mapUiSettings = remember { MapUiSettings(mapToolbarEnabled = false, myLocationButtonEnabled = true, scrollGesturesEnabled = true, zoomControlsEnabled = false) }
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

    LaunchedEffect(Unit) {
        setCurrentLocation()
    }


    LaunchedEffect(cameraPositionState.value.position.target) {
        if (cameraPositionState.value.isMoving){
            currentLocation = cameraPositionState.value.position.target
            Log.d("CurrentLocation", currentLocation.toString())
            onItemValueChange(itemDetails.copy(latLng = currentLocation))
        }
    }



    Column {
        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                setCurrentLocation()
            }
        }) {
            Text(text = "Use Current Location")
        }


        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
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
        }
    }
}


@Composable
fun SaveButton(onSaveClick: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onSaveClick,
        enabled = isEnabled,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.save_action))
    }
}