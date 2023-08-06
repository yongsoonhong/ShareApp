package my.edu.tarc.fyp.shareapp.presentation.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
): ViewModel() {

    private val user = Firebase.auth.currentUser

    fun getCurrentUser(): FirebaseUser{
        return user!!
    }



    fun signOut(){
        try {
            Firebase.auth.signOut()
        }catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

}