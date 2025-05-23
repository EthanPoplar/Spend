// app/src/main/kotlin/com/example/spend/screens/AppNavigation.kt
package com.example.spend.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.spend.viewmodel.TransactionViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    onGoogleSignIn: () -> Unit,         // ← your “go home” lambda
    onNavigateToForm: () -> Unit,
    onNavigateToSpending: () -> Unit
) {
    // single shared VM instance
    val txnVm: TransactionViewModel = viewModel()
    val goHome: () -> Unit = {
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }

    NavHost(
        navController    = navController,
        startDestination = "login"
    ) {
        // LOGIN ROUTE
        composable("login") {
            LoginScreen(
                onLoginSuccess        = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick         = {
                    navController.navigate("signup")
                },
                onForgotPasswordClick = {
                    navController.navigate("forgotpassword")
                },
                onGoogleSignIn        = onGoogleSignIn  // ← call your go-home here
            )
        }

        // SIGN UP ROUTE
        composable("signup") {
            SignUpScreen(
                onSignUpComplete = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // FORGOT PASSWORD ROUTE
        composable("forgotpassword") {
            ForgotPasswordScreen(
                onSubmit       = { /* TODO: handle real reset */ },
                onReturnClick  = { navController.popBackStack() }
            )
        }

        // MAIN / HOME ROUTE
        composable("home") {
            HomeScreen(
                viewModel            = txnVm,
                onNavigateToForm     = onNavigateToForm,
                onNavigateToSpending = onNavigateToSpending
            )
        }

        // MANUAL ENTRY FORM ROUTE
        composable("form") {
            FormScreen(
                viewModel    = txnVm,
                onReturnHome = { navController.popBackStack() }
            )
        }

        // SPENDING OVERVIEW ROUTE
        composable("spending") {
            SpendingScreen(
                viewModel          = txnVm,
                onNavigateBack     = { navController.popBackStack() },
                onAddTransaction   = { navController.navigate("form") },
                onViewChart        = { navController.navigate("chart") }
            )
        }

        // CHART ROUTE
        composable("chart") {
            val txns = txnVm.transactions.collectAsState().value
            ChartScreen(
                transactions   = txns,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}







