// app/src/main/kotlin/com/example/spend/screens/LoginScreen.kt

package com.example.spend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Regular login button, still works as before
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))
        // Google-sign-in button now just calls the same callback
        Button(onClick = onGoogleSignIn, modifier = Modifier.fillMaxWidth()) {
            Text("Sign in with Google")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot password?")
        }
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign up")
        }
    }
}


