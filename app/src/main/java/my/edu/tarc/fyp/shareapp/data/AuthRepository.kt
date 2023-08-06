package my.edu.tarc.fyp.shareapp.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import my.edu.tarc.fyp.shareapp.util.Resource

interface AuthRepository {

    fun loginUser(email:String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email:String, password: String): Flow<Resource<AuthResult>>

    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>


}