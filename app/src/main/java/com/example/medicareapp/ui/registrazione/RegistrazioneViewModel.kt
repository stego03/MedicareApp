package com.example.medicareapp.ui.registrazione

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.repository.AuthRepository

class RegistrazioneViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun registraPaziente(
        username: String,
        password: String,
        codiceFiscale: String,
        nome: String,
        cognome: String,
        dataNascita: String,
        email: String,
        telefono: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        authRepository.registraUtente(
            username = username,
            password = password,
            codiceFiscale = codiceFiscale,
            nome = nome,
            cognome = cognome,
            dataNascita = dataNascita,
            email = email,
            telefono = telefono,
            ruolo = "paziente"
        ) { successo, errore ->

            onResult(successo, errore)
        }
    }
}