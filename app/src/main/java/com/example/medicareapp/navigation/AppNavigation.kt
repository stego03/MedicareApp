package com.example.medicareapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.ui.home.HomePazienteScreen
import com.example.medicareapp.ui.home.HomePazienteViewModel
import com.example.medicareapp.ui.login.LoginScreen
import com.example.medicareapp.ui.registrazione.RegistrazioneScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {

            LoginScreen(

                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },

                onRegistrazioneClick = {
                    navController.navigate("registrazione")
                }
            )
        }

        composable("registrazione") {

            RegistrazioneScreen(

                onRegistrazioneSuccess = {

                    AuthRepository().logout()

                    navController.navigate("login") {
                        popUpTo("registrazione") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("home") {

            HomePazienteScreen(
                viewModel = viewModel<HomePazienteViewModel>()
            )
        }
    }
}