package my.edu.tarc.fyp.shareapp.presentation.auth

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)