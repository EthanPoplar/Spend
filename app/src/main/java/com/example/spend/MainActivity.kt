package com.example.spend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.spend.screens.AppNavigation
import com.example.spend.ui.theme.MyAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // build your GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MyAppTheme {
                val navController = rememberNavController()
                val goHome: () -> Unit = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                // 1️⃣ Create a composable-aware launcher
                val signInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
                    try {
                        // 2️⃣ Try to get a signed-in account from the result intent
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        task.getResult(ApiException::class.java)  // throws if sign-in failed

                        // 3️⃣ On success, navigate to home and clear the backstack
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        // sign-in failed or was cancelled
                        Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
                // 4️⃣ Pass that launcher into your AppNavigation
                AppNavigation(
                    navController      = navController,
                    onGoogleSignIn     = {
                        // launch the Google sign-in UI
                        val intent: Intent = googleSignInClient.signInIntent
                        signInLauncher.launch(intent)
                    },
                    onNavigateToForm     = { navController.navigate("form") },
                    onNavigateToSpending = { navController.navigate("spending") }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // you can put a dummy NavController here if you like
        }
    }
}


