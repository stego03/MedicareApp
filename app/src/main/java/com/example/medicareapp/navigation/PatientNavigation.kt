
package com.example.medicareapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.ui.paziente.areaPersonale.AreaPersonaleScreen
import com.example.medicareapp.ui.paziente.home.HomePazienteScreen
import com.example.medicareapp.ui.paziente.home.HomePazienteViewModel
import com.example.medicareapp.ui.paziente.visite.VisitePazienteScreen
import com.example.medicareapp.ui.paziente.documenti.DocumentoPazienteScreen
import com.example.medicareapp.ui.paziente.notifiche.NotifichePazienteScreen

@Composable
fun PatientNavigation(
    onLogout: () -> Unit
) {

    // =================================================
    // NAV CONTROLLER DEL PAZIENTE
    // =================================================

    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route


    // =================================================
    // SCAFFOLD
    // =================================================

    Scaffold(

        bottomBar = {

            NavigationBar {

                // -----------------------------------------
                // HOME
                // -----------------------------------------

                NavigationBarItem(

                    selected =
                        currentRoute == Routes.HOME_PAZIENTE,

                    onClick = {

                        navController.navigate(
                            Routes.HOME_PAZIENTE
                        ) {

                            popUpTo(
                                Routes.HOME_PAZIENTE
                            ) {
                                inclusive = false
                            }

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Text("🏠")
                    },

                    label = {
                        Text("Home")
                    }
                )


                // -----------------------------------------
                // VISITE
                // -----------------------------------------

                NavigationBarItem(

                    selected =
                        currentRoute == Routes.VISITE_PAZIENTE,

                    onClick = {

                        navController.navigate(
                            Routes.VISITE_PAZIENTE
                        ) {

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Text("📅")
                    },

                    label = {
                        Text("Visite")
                    }
                )


                // -----------------------------------------
                // DOCUMENTI
                // -----------------------------------------

                NavigationBarItem(

                    selected =
                        currentRoute ==
                                Routes.DOCUMENTI_PAZIENTE,

                    onClick = {

                        navController.navigate(
                            Routes.DOCUMENTI_PAZIENTE
                        ) {

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Text("📄")
                    },

                    label = {
                        Text("Documenti")
                    }
                )


                // -----------------------------------------
                // NOTIFICHE
                // -----------------------------------------

                NavigationBarItem(

                    selected =
                        currentRoute ==
                                Routes.NOTIFICHE_PAZIENTE,

                    onClick = {

                        navController.navigate(
                            Routes.NOTIFICHE_PAZIENTE
                        ) {

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Text("🔔")
                    },

                    label = {
                        Text("Notifiche")
                    }
                )


                // -----------------------------------------
                // AREA PERSONALE
                // -----------------------------------------

                NavigationBarItem(

                    selected =
                        currentRoute ==
                                Routes.AREA_PERSONALE_PAZIENTE,

                    onClick = {

                        navController.navigate(
                            Routes.AREA_PERSONALE_PAZIENTE
                        ) {

                            launchSingleTop = true
                        }
                    },

                    icon = {
                        Text("👤")
                    },

                    label = {
                        Text("Area personale")
                    }
                )
            }
        }

    ) { innerPadding ->


        // =================================================
        // NAV HOST PAZIENTE
        // =================================================

        NavHost(

            navController = navController,

            startDestination =
                Routes.HOME_PAZIENTE,

            modifier =
                Modifier.padding(innerPadding)
        ) {


            // ---------------------------------------------
            // HOME PAZIENTE
            // ---------------------------------------------

            composable(
                route = Routes.HOME_PAZIENTE
            ) {

                HomePazienteScreen(
                    viewModel =
                        viewModel<HomePazienteViewModel>()
                )
            }


            // ---------------------------------------------
            // VISITE PAZIENTE
            // ---------------------------------------------

            composable(
                route = Routes.VISITE_PAZIENTE
            ) {

                VisitePazienteScreen()
            }


            // ---------------------------------------------
            // DOCUMENTI PAZIENTE
            // ---------------------------------------------

            composable(
                route = Routes.DOCUMENTI_PAZIENTE
            ) {
                DocumentoPazienteScreen()
            }


            // ---------------------------------------------
            // AREA PERSONALE
            // ---------------------------------------------

            composable(
                route = Routes.AREA_PERSONALE_PAZIENTE
            ) {

                AreaPersonaleScreen(
                    onLogout = onLogout
                )
            }


            // ---------------------------------------------
            // NOTIFICHE PAZIENTE
            // ---------------------------------------------

            composable(
                route = Routes.NOTIFICHE_PAZIENTE
            ) {

                NotifichePazienteScreen()
            }
        }
    }
}

