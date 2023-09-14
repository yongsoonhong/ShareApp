package my.edu.tarc.fyp.shareapp.presentation.auth


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import my.edu.tarc.fyp.shareapp.R
import my.edu.tarc.fyp.shareapp.data.Constant.ServerClient

@Composable
fun SignInScreen(
    signInSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val googleSignInState = viewModel.googleState.value


    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
                viewModel.googleSignIn(credentials)
            } catch (it: ApiException) {
                print(it)
            }
        }


    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signInState.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.applogo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .clip(shape = CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = "Login",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )
        Text(
            text = "Enter your credential's to login",
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Gray,
        )
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.LightGray,
                cursorColor = Color.Black,
                disabledLabelColor = Color.LightGray, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Email")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.LightGray,
                cursorColor = Color.Black,
                disabledLabelColor = Color.LightGray, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Password")
            }
        )

        Button(
            onClick = {
                scope.launch {
                    viewModel.loginUser(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 30.dp, end = 30.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black, contentColor = Color.White
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text = "Sign In", color = Color.White, modifier = Modifier.padding(7.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (state.value?.isLoading == true) {
                CircularProgressIndicator()
            }
        }
        Text(
            modifier = Modifier
                .padding(15.dp)
                .clickable {
                    onSignUpClick()
                },
            text = "New User? Sign Up",
            fontWeight = FontWeight.Bold, color = Color.Black
        )
        Text(
            modifier = Modifier
                .padding(
                    top = 40.dp,
                ),
            text = "Or connect with",
            fontWeight = FontWeight.Medium, color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(ServerClient)
                    .build()

                val googleSingInClient = GoogleSignIn.getClient(context, gso)

                launcher.launch(googleSingInClient.signInIntent)

            }) {
                Icon(
                    painter = painterResource(id =  R.drawable.ic_google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }
            LaunchedEffect(key1 = state.value?.isSuccess) {
                scope.launch {
                    if (state.value?.isSuccess?.isNotEmpty() == true) {
                        val success = state.value?.isSuccess
                        Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                        signInSuccess()
                    }
                }
            }

            LaunchedEffect(key1 = state.value?.isError) {
                scope.launch {
                    if (state.value?.isError?.isNotEmpty() == true) {
                        val error = state.value?.isError
                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                    }
                }
            }

            LaunchedEffect(key1 = googleSignInState.success) {
                scope.launch {
                    if (googleSignInState.success != null) {
                        Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show()
                        signInSuccess()
                    }
                }
            }

        }
        


    }
}