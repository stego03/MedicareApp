
package com.example.medicareapp.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.UtenteRepository
import com.example.medicareapp.ui.login.LoginScreen
import com.example.medicareapp.ui.registrazione.RegistrazioneScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val authRepository = remember { AuthRepository() }
    val utenteRepository = remember { UtenteRepository() }

    // Destinazione iniziale: viene determinata dalla sessione Firebase.
    var destinazioneIniziale by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        val uid = authRepository.utenteCorrente()

        if (uid == null) {
            // Nessun utente autenticato: mostriamo il login.
            destinazioneIniziale = Routes.LOGIN
        } else {
            // Esiste una sessione: recuperiamo il ruolo da Firestore.
            utenteRepository.getUtente(uid = uid) { utente ->

                Log.d(
                    "APP_NAVIGATION",
                    "Utente recuperato all'avvio: $utente"
                )

                destinazioneIniziale =
                    destinazionePerRuolo(utente?.ruolo)
                        ?: run {
                            Log.e(
                                "APP_NAVIGATION",
                                "Utente non trovato o ruolo non riconosciuto"
                            )
                            Routes.LOGIN
                        }
            }
        }
    }

    // Attendiamo il controllo della sessione prima di mostrare la navigazione.
    if (destinazioneIniziale == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        NavHost(
            navController = navController,
            startDestination = destinazioneIniziale!!
        ) {

            // =========================================
            // LOGIN
            // =========================================

            composable(Routes.LOGIN) {
                LoginScreen(

                    onLoginSuccess = {
                        Log.d(
                            "APP_NAVIGATION",
                            "Login effettuato con successo"
                        )

                        val uid = authRepository.utenteCorrente()

                        Log.d(
                            "APP_NAVIGATION",
                            "UID utente corrente: $uid"
                        )

                        if (uid == null) {
                            Log.e(
                                "APP_NAVIGATION",
                                "ERRORE: UID nullo dopo il login"
                            )
                            return@LoginScreen
                        }

                        // Recuperiamo l'utente da Firestore.
                        utenteRepository.getUtente(uid = uid) { utente ->

                            Log.d(
                                "APP_NAVIGATION",
                                "Risultato Firestore: $utente"
                            )

                            if (utente == null) {
                                Log.e(
                                    "APP_NAVIGATION",
                                    "ERRORE: nessun documento trovato in utenti/$uid"
                                )
                                return@getUtente
                            }

                            Log.d(
                                "APP_NAVIGATION",
                                "Utente trovato: ${utente.nome} ${utente.cognome}"
                            )

                            Log.d(
                                "APP_NAVIGATION",
                                "Ruolo ricevuto da Firestore: '${utente.ruolo}'"
                            )

                            // Controlliamo il ruolo usando la funzione testata.
                            when (destinazionePerRuolo(utente.ruolo)) {

                                // PAZIENTE
                                Routes.MAIN_PAZIENTE -> {
                                    Log.d(
                                        "APP_NAVIGATION",
                                        "Navigazione verso MAIN_PAZIENTE"
                                    )

                                    navController.navigate(
                                        Routes.MAIN_PAZIENTE
                                    ) {
                                        popUpTo(Routes.LOGIN) {
                                            inclusive = true
                                        }
                                    }
                                }

                                // DOTTORE
                                Routes.MAIN_DOTTORE -> {
                                    Log.d(
                                        "APP_NAVIGATION",
                                        "Navigazione verso MAIN_DOTTORE"
                                    )

                                    navController.navigate(
                                        Routes.MAIN_DOTTORE
                                    ) {
                                        popUpTo(Routes.LOGIN) {
                                            inclusive = true
                                        }
                                    }
                                }

                                // RUOLO NON RICONOSCIUTO
                                else -> {
                                    Log.e(
                                        "APP_NAVIGATION",
                                        "ERRORE: ruolo non riconosciuto: '${utente.ruolo}'"
                                    )
                                }
                            }
                        }
                    },

                    onRegistrazioneClick = {
                        navController.navigate(Routes.REGISTRAZIONE)
                    }
                )
            }

            // =========================================
            // REGISTRAZIONE
            // =========================================

            composable(Routes.REGISTRAZIONE) {
                RegistrazioneScreen(

                    onRegistrazioneSuccess = {
                        authRepository.logout()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTRAZIONE) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // =========================================
            // MAIN PAZIENTE
            // =========================================

            composable(Routes.MAIN_PAZIENTE) {
                PatientNavigation(

                    onLogout = {
                        authRepository.logout()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN_PAZIENTE) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // =========================================
            // MAIN DOTTORE
            // =========================================

            composable(Routes.MAIN_DOTTORE) {
                DoctorNavigation(

                    onLogout = {
                        authRepository.logout()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN_DOTTORE) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
