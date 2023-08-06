package my.edu.tarc.fyp.shareapp.presentation.auth

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)