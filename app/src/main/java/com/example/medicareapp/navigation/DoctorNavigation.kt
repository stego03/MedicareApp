package com.example.medicareapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.ui.dottore.home.HomeDottoreScreen
import com.example.medicareapp.ui.dottore.indisponibilita.IndisponibilitaScreen
import com.example.medicareapp.ui.dottore.notifiche.NotificheDottoreScreen
import com.example.medicareapp.ui.dottore.pazienti.PazientiDottoreScreen
import com.example.medicareapp.ui.dottore.pazienti.PazientiDottoreViewModel
import com.example.medicareapp.ui.dottore.visite.VisiteDottoreScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DoctorNavigation(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                // HOME
                NavigationBarItem(
                    selected = currentRoute == Routes.HOME_DOTTORE,
                    onClick = {
                        navController.navigate(Routes.HOME_DOTTORE) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME_DOTTORE) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )

                // VISITE
                NavigationBarItem(
                    selected = currentRoute == Routes.VISITE_DOTTORE,
                    onClick = {
                        navController.navigate(Routes.VISITE_DOTTORE) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME_DOTTORE) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = { Text("📅") },
                    label = { Text("Visite") }
                )

                // DISPONIBILITÀ
                NavigationBarItem(
                    selected = currentRoute == Routes.DISPONIBILITA_DOTTORE,
                    onClick = {
                        navController.navigate(Routes.DISPONIBILITA_DOTTORE) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME_DOTTORE) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = { Text("🕐") },
                    label = { Text("Disponibilità") }
                )

                // PAZIENTI
                NavigationBarItem(
                    selected = currentRoute == Routes.PAZIENTI_DOTTORE,
                    onClick = {
                        navController.navigate(Routes.PAZIENTI_DOTTORE) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME_DOTTORE) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = { Text("👥") },
                    label = { Text("Pazienti") }
                )

                // NOTIFICHE
                NavigationBarItem(
                    selected = currentRoute == Routes.NOTIFICHE_DOTTORE,
                    onClick = {
                        navController.navigate(Routes.NOTIFICHE_DOTTORE) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME_DOTTORE) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    icon = { Text("🔔") },
                    label = { Text("Notifiche") }
                )

                // LOGOUT
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onLogout()
                    },
                    icon = { Text("🚪") },
                    label = { Text("Esci") }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME_DOTTORE,
            modifier = Modifier.padding(innerPadding)
        ) {

            // HOME
            composable(Routes.HOME_DOTTORE) {
                HomeDottoreScreen()
            }

            // VISITE
            composable(Routes.VISITE_DOTTORE) {
                VisiteDottoreScreen()
            }

            // DISPONIBILITÀ
            composable(Routes.DISPONIBILITA_DOTTORE) {
                IndisponibilitaScreen()
            }

            // PAZIENTI
            composable(Routes.PAZIENTI_DOTTORE) {
                val pazientiViewModel: PazientiDottoreViewModel = viewModel()

                PazientiDottoreScreen(
                    viewModel = pazientiViewModel
                )
            }

            // NOTIFICHE
            composable(Routes.NOTIFICHE_DOTTORE) {
                NotificheDottoreScreen()
            }
        }
    }
}