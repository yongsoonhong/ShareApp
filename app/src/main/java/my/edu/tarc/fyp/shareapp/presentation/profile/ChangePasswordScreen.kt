package my.edu.tarc.fyp.shareapp.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ChangePasswordScreen(
    onSaveClick: (password: String) -> Unit,
    onCancelClick: () -> Unit
){

    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    fun validatePassword(password: String): Boolean{
        return password.length >= 8
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        Text(
            text = "Change Password",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.padding(10.dp))


        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text(text = "Password")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(2.dp))

        Text(
            text = "Password must be more than or equals to 8 characters",
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = {
                Text(text = "Confirm Password")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(10.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                shape = RoundedCornerShape(10.dp,0.dp,0.dp,10.dp),
                modifier = Modifier
                    .weight(1f),
                enabled = (validatePassword(password) && password == confirmPassword),
                onClick = {
                    onSaveClick(password)
                }
            ) {
                Text(text = "Save")
            }
            Button(
                shape = RoundedCornerShape(0.dp,10.dp,10.dp,0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Cancel")
            }
        }

    }


}