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
fun AppNavigation(navController: NavHostController) {
    // single shared VM instance
    val txnVm: TransactionViewModel = viewModel()

    NavHost(
        navController   = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess         = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick          = { navController.navigate("signup") },
                onForgotPasswordClick  = { navController.navigate("forgotpassword") }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpComplete = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("forgotpassword") {
            ForgotPasswordScreen(
                onSubmit       = { /* TODO: handle reset */ },
                onReturnClick  = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToForm     = { navController.navigate("form") },
                onNavigateToSpending = { navController.navigate("spending") },
                viewModel            = txnVm
            )
        }

        composable("form") {
            FormScreen(
                viewModel    = txnVm,
                onReturnHome = { navController.popBackStack() }
            )
        }

        composable("spending") {
            SpendingScreen(
                viewModel = txnVm,
                onNavigateBack   = { navController.popBackStack() },
                onAddTransaction = { navController.navigate("form") },
                onViewChart      = { navController.navigate("chart") }
            )
        }
        composable("chart") {
            // pass the snapshot list
            val txns = txnVm.transactions.collectAsState().value
            ChartScreen(
                transactions    = txns,
                onNavigateBack  = { navController.popBackStack() }
            )
        }
    }
}



