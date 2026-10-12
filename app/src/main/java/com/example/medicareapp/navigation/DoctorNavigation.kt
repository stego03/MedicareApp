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
//Import per icone BottomBar del flusso dottore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NoAdultContent
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
//Import
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.unit.dp
//Import palette (colori)
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp

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
            NavigationBar(
                //Colore bottomBar
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ){

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
                    //Icona homepage
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
                    //Icona visite
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
                    //Icona disponibilità
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Disponibilità"
                        )
                    },
                    label = {
                        Text(
                            text = "Disponibilità",
                            maxLines = 1,
                            softWrap = false,
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = (-0.05).sp
                            )
                        )
                    },
                    colors = coloriNavigationItem()
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
                    //Icona pazienti
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Pazienti"
                        )
                    },
                    label = {
                        Text(
                            text = "Pazienti",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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
                    //Icona notifiche
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

                // LOGOUT
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        onLogout()
                    },
                    //Icona di logout
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    },
                    label = {
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = coloriNavigationItem()
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


/*COLORI DELLA BOTTOM NAVIGATION
Stessa palette utilizzata nel flusso Paziente.
*/

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