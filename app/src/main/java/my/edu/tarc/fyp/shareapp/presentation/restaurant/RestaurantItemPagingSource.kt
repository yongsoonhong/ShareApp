package my.edu.tarc.fyp.shareapp.presentation.restaurant

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.Restaurant
import my.edu.tarc.fyp.shareapp.domain.SharedItem

class RestaurantItemPagingSource : PagingSource<QuerySnapshot, Restaurant>() {
    private val db = Firebase.firestore



    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Restaurant> {



        return try {

            val currentPage = params.key ?: db.collection("restaurants")
                .orderBy("restaurantName")
                .limit(params.loadSize.toLong())
                .get()
                .await()


            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]


            val nextPage = db.collection("restaurant")
                .orderBy("restaurantName")
                .startAfter(lastDocumentSnapshot)
                .limit(params.loadSize.toLong())
                .get()
                .await()



            val data = try {
                currentPage.toObjects(Restaurant::class.java)
            } catch (e: FirebaseFirestoreException) {
                Log.e("Firestore", "Error converting document to Restaurant", e)
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

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Restaurant>): QuerySnapshot? {
        return null
    }
}
