package my.edu.tarc.fyp.shareapp.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.domain.UserData
import my.edu.tarc.fyp.shareapp.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow{
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow{
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            val user = result.user

            if (user != null) {
                // Create user data object
                val userData = UserData(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "",
                    photoUrl = null  // This can be null for email/password sign up
                )

                // Store new user data in Firestore
                val userRef = firestore.collection("users").document(user.uid)
                userRef.set(userData).await()
            }

            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()


            val user = result.user

            if (user != null) {
                // Create user data object
                val userData = UserData(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString()
                )

                // Check if user profile already exists in Firestore
                val userRef = firestore.collection("users").document(user.uid)
                val document = userRef.get().await()

                if (!document.exists()) {
                    // Store new user data in Firestore
                    userRef.set(userData).await()
                    firestore.collection("reports").document("totaluser")
                        .update("no", FieldValue.increment(1))
                }
            }
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}