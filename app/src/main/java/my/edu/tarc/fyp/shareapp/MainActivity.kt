package my.edu.tarc.fyp.shareapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import my.edu.tarc.fyp.shareapp.presentation.auth.SignInScreen
import my.edu.tarc.fyp.shareapp.presentation.auth.SignUpScreen
import my.edu.tarc.fyp.shareapp.presentation.main.BottomBar
import my.edu.tarc.fyp.shareapp.presentation.main.mainNavGraph
import my.edu.tarc.fyp.shareapp.ui.theme.ShareAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareAppTheme {
                // A surface container using the 'background' color from the theme
                androidx.compose.material.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material.MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination?.route

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    androidx.compose.material.Text(
                                        text = currentDestination ?: "Share",
                                    )
                                }
                            )
                        },
                        bottomBar = { BottomBar(navController = navController) }
                    ) {
                        NavHost(navController = navController, startDestination = "auth") {
                            navigation(
                                route = "auth",
                                startDestination = "auth_login"
                            ) {
                                composable("auth_login") {
                                    SignInScreen(
                                        signInSuccess = {
                                            navController.navigate("main") {
                                                popUpTo("auth") {
                                                    inclusive = true
                                                }
                                            }
                                        },
                                        onSignUpClick = {
                                            navController.navigate("auth_register")
                                        }
                                    )
                                }
                                composable("auth_register") {
                                    SignUpScreen(
                                        signInSuccess = {
                                            navController.navigate("main") {
                                                popUpTo("auth") {
                                                    inclusive = true
                                                }
                                            }
                                        },
                                        onSignInClick = {
                                            navController.navigate("auth_login")
                                        }
                                    )
                                }
                                composable("auth_password") {
                                    ScreenContent(
                                        name = "auth_password",
                                        onClick = {
                                            navController.navigate("main") {
                                                popUpTo("auth") {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            mainNavGraph(navController = navController)
                        }
                    }




                }
            }
        }
    }
}


@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T{
    val navGraphRoute = destination.parent?.route?: return hiltViewModel()
    val parentEntry = remember(this){
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}