package com.example.medicareapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.ui.home.PatientHomeScreen
import com.example.medicareapp.ui.login.LoginScreen
import com.example.medicareapp.ui.registrazione.RegistrazioneScreen
import com.example.medicareapp.ui.theme.MedicareAppTheme

class MainActivity : ComponentActivity() {

    private var schermata by mutableStateOf("login")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MedicareAppTheme {

                when (schermata) {

                    "login" -> {
                        LoginScreen(
                            onLoginSuccess = {
                                schermata = "home"
                            },
                            onRegistrazioneClick = {
                                schermata = "registrazione"
                            }
                        )
                    }

                    "registrazione" -> {
                        RegistrazioneScreen(
                            onRegistrazioneSuccess = {
                                AuthRepository().logout()
                                schermata = "login"
                            }
                        )
                    }

                    "home" -> {
                        PatientHomeScreen(
                            onPrenotaVisitaClick = {
                                Log.d("MediCare", "Prenota visita")
                            },
                            onVisiteClick = {
                                Log.d("MediCare", "Le mie visite")
                            },
                            onProfiloClick = {
                                Log.d("MediCare", "Area personale")
                            },
                            onDocumentiClick = {
                                Log.d("MediCare", "Documenti")
                            }
                        )
                    }
                }
            }
        }
    }
}