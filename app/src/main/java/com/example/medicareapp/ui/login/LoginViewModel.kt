package com.example.medicareapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    fun login(
        username: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        authRepository.login(
            username = username,
            password = password
        ) { successo, errore ->

            onResult(successo, errore)
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun utenteLoggato(): String? {
        return authRepository.utenteCorrente()
    }
}