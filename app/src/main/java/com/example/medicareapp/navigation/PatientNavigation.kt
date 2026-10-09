package com.example.medicareapp.navigation

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.ui.paziente.areaPersonale.AreaPersonaleScreen
import com.example.medicareapp.ui.paziente.documenti.DocumentoPazienteScreen
import com.example.medicareapp.ui.paziente.home.HomePazienteScreen
import com.example.medicareapp.ui.paziente.home.HomePazienteViewModel
import com.example.medicareapp.ui.paziente.notifiche.NotifichePazienteScreen
import com.example.medicareapp.ui.paziente.visite.VisitePazienteScreen
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
//Import per icone BottomBar della home paziente
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person

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
    // La logica di navigazione rimane invariata.
    // Modifichiamo solamente l'aspetto della barra.
    // =================================================

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {

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
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Visite"
                        )
                    },
                    label = {
                        Text(
                            text = "Visite",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Documenti"
                        )
                    },
                    label = {
                        Text(
                            text = "Documenti",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifiche"
                        )
                    },
                    label = {
                        Text(
                            text = "Notifiche",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
                )

                // -----------------------------------------
                // PROFILO
                // Il route rimane AREA_PERSONALE_PAZIENTE.
                // Cambia solamente il testo visualizzato.
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
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profilo"
                        )
                    },
                    label = {
                        Text(
                            text = "Profilo",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
                )

                // -----------------------------------------
                // LOGOUT
                // La funzione di logout rimane esattamente
                // quella già utilizzata dal progetto.
                // -----------------------------------------
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onLogout()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Esci"
                        )
                    },
                    label = {
                        Text(
                            text = "Esci",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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

// =========================================================
// COLORI DELLA BOTTOM NAVIGATION
// Manteniamo la palette MediCare e rimuoviamo il colore
// viola predefinito che stonava con il resto dell'app.
// =========================================================

@Composable
private fun coloriNavigationItem() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MediCareBlue,
        selectedTextColor = MediCareBlue,
        indicatorColor = MediCareBlueLight,
        unselectedIconColor =
            MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor =
            MaterialTheme.colorScheme.onSurfaceVariant
    )