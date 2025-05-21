package com.example.spend.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") },
                onForgotPasswordClick = { navController.navigate("forgotpassword") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpComplete = { navController.navigate("home") }
            )
        }
        composable("forgotpassword") {
            ForgotPasswordScreen(
                // You can choose to navigate somewhere on submit or simply leave it as a placeholder.
                onSubmit = { /* handle submission or navigate to another screen if needed */ },
                onReturnClick = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToForm = { navController.navigate("form") },
                onNavigateToSpending = { navController.navigate("spending") }
            )
        }
        composable("form") {
            FormScreen(
                onReturnHome = {
                    navController.popBackStack()
                }
            )
        }
        composable("spending") {
            SpendingScreen()
        }
    }
}



