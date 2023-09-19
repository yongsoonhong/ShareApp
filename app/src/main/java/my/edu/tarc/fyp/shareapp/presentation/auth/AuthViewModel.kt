package my.edu.tarc.fyp.shareapp.presentation.auth

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import my.edu.tarc.fyp.shareapp.data.AuthRepository
import my.edu.tarc.fyp.shareapp.domain.UserData
import my.edu.tarc.fyp.shareapp.util.Resource
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    val db = Firebase.firestore

    //SignInWithEmail
    private val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    //SignUpWithEmail
    private val _signUpState = Channel<SignUpState>()
    val signUpState = _signUpState.receiveAsFlow()

    //SignInWithGoogle
    private val _googleState = mutableStateOf(GoogleSignInState())
    val googleState: State<GoogleSignInState> = _googleState

    fun googleSignIn(credential: AuthCredential) = viewModelScope.launch {
        authRepository.googleSignIn(credential).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _googleState.value = GoogleSignInState(success = result.data)
                }
                is Resource.Loading -> {
                    _googleState.value = GoogleSignInState(loading = true)
                }
                is Resource.Error -> {
                    _googleState.value = GoogleSignInState(error = result.message!!)
                }
            }


        }
    }


    fun loginUser(email: String, password: String) = viewModelScope.launch {
        authRepository.loginUser(email, password).collect{ result ->
            when(result){
                is Resource.Success -> {

                    var userExist = false

                    db.collection("users")
                        .whereEqualTo("email",email)
                        .get()
                        .addOnSuccessListener { value ->
                            if (!value.isEmpty){
                                userExist = true

                            }
                        }
                        .addOnFailureListener { e ->
                            e.let {
                                Log.w(ContentValues.TAG, "Listen failed.", e)
                            }
                        }.await()

                    if (userExist){
                        _signInState.send(SignInState(isSuccess = "Sign In Success"))
                    }else{
                        _signInState.send(SignInState(isError = "Account have been removed"))
                    }

                }

                is Resource.Loading -> {
                    _signInState.send(SignInState(isLoading = true))
                }
                is Resource.Error ->{
                    _signInState.send(SignInState(isError = result.message))
                }
            }
        }
    }

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        authRepository.registerUser(email, password).collect{ result ->
            when(result){
                is Resource.Success -> {
                    db.collection("reports").document("totaluser")
                        .update("no",FieldValue.increment(1))
                    _signUpState.send(SignUpState(isSuccess = "Sign Up Success"))
                }

                is Resource.Loading -> {
                    _signUpState.send(SignUpState(isLoading = true))
                }
                is Resource.Error ->{
                    _signUpState.send(SignUpState(isError = result.message))
                }
            }
        }
    }



}