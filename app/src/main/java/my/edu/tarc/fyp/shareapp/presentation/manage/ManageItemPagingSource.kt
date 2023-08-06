package my.edu.tarc.fyp.shareapp.presentation.manage

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.ManageItem
import my.edu.tarc.fyp.shareapp.domain.SharedItem

class ManageItemPagingSource: PagingSource<QuerySnapshot, ManageItem>() {
    private val db = Firebase.firestore

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ManageItem> {
        return try {
            val currentPage = params.key ?: db.collection("manageitems")
                .whereEqualTo("userId",Firebase.auth.currentUser?.uid)
                .orderBy("expiryDate")
                .limit(params.loadSize.toLong())
                .get()
                .await()


            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]


            val nextPage = db.collection("manageitems")
                .orderBy("expiryDate")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()



            val data = try {
                currentPage.toObjects(ManageItem::class.java)
            } catch (e: FirebaseFirestoreException) {
                Log.e("Firestore", "Error converting document to ManageItem", e)
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

    override fun getRefreshKey(state: PagingState<QuerySnapshot, ManageItem>): QuerySnapshot? {
        return null
    }
}
