package my.edu.tarc.fyp.shareapp.presentation.nearby

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.SharedItem

class NearbyItemPagingSource(private val viewModel: NearbyItemViewModel): PagingSource<QuerySnapshot, SharedItem>() {
    private val db = Firebase.firestore



    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, SharedItem> {



        return try {



            val currentPage = params.key ?: db.collection("shareditems")
                .whereNotEqualTo("userId",Firebase.auth.currentUser?.uid)
                .orderBy("userId")
                .limit(params.loadSize.toLong())
                .get()
                .await()


            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]


            val nextPage = db.collection("shareditems")
                .orderBy("userId")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()



            val data = try {
                currentPage.toObjects(SharedItem::class.java)
            } catch (e: FirebaseFirestoreException) {
                Log.e("Firestore", "Error converting document to SharedItem", e)
                throw e
            }catch(t: Throwable) {
                Log.e("Firestore", "Throwable caught", t)
                throw t
            }


            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = if (nextPage.isEmpty) null else nextPage
            )
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, SharedItem>): QuerySnapshot? {
        return null
    }
}
