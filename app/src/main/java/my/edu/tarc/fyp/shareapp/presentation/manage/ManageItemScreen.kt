package my.edu.tarc.fyp.shareapp.presentation.manage

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import my.edu.tarc.fyp.shareapp.domain.ManageItem


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ManageItemScreen(
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (ManageItem) -> Unit,
    onAddClick: () -> Unit,
    manageItems: LazyPagingItems<ManageItem>
) {

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val context = LocalContext.current
    LaunchedEffect(key1 = manageItems.loadState) {
        if(manageItems.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: " + (manageItems.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold (
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Text(text = "Add")
                },
                onClick =  onAddClick
            )
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            if(manageItems.loadState.refresh is LoadState.Loading) {
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
                        items(manageItems) { manageItem ->
                            if(manageItem != null && manageItem.userId == Firebase.auth.currentUser?.uid) {
                                ManageItemItem(
                                    manageItem = manageItem,
                                    onItemClick = onItemClick
                                )
                            }
                        }
                        item {
                            if(manageItems.loadState.append is LoadState.Loading) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

            }
        }
    }




}