package com.example.medicareapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.ui.login.LoginScreen
import com.example.medicareapp.ui.registrazione.RegistrazioneScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // -----------------------------------------
        // LOGIN
        // -----------------------------------------

        composable(
            Routes.LOGIN
        ) {

            LoginScreen(

                onLoginSuccess = {

                    navController.navigate(
                        Routes.MAIN_PAZIENTE
                    ) {

                        popUpTo(
                            Routes.LOGIN
                        ) {
                            inclusive = true
                        }
                    }
                },

                onRegistrazioneClick = {

                    navController.navigate(
                        Routes.REGISTRAZIONE
                    )
                }
            )
        }

        // -----------------------------------------
        // REGISTRAZIONE
        // -----------------------------------------

        composable(
            Routes.REGISTRAZIONE
        ) {

            RegistrazioneScreen(

                onRegistrazioneSuccess = {

                    AuthRepository().logout()

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(
                            Routes.REGISTRAZIONE
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // AREA PAZIENTE
        // -----------------------------------------

        composable(
            Routes.MAIN_PAZIENTE
        ) {

            PatientNavigation()
        }
    }
}