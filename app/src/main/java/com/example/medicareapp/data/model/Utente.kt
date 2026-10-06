package com.example.medicareapp.data.model

data class Utente(
    val uid: String = "",
    val username: String = "",
    val codiceFiscale: String = "",
    val nome: String = "",
    val cognome: String = "",
    val dataNascita: String = "",
    val email: String = "",
    val telefono: String = "",
    val ruolo: String = ""
)