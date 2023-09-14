package my.edu.tarc.fyp.shareapp.presentation.sharedItem

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
import my.edu.tarc.fyp.shareapp.domain.SharedItem
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharedItemScreen(
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
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Text(text = "Add")
                },
                onClick =  onAddClick,
                modifier = Modifier.padding(bottom = 50.dp),
            )
        }
    ) {
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
                        items(sharedItems) { sharedItem ->
                            if(sharedItem != null && sharedItem.userId == Firebase.auth.currentUser?.uid) {
                                SharedItemItem(
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