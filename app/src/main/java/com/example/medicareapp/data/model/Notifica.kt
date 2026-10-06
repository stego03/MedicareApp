package com.example.medicareapp.data.model

data class Notifica(
    val idNotifica: String = "",
    val idUtente: String = "",
    val titolo: String = "",
    val messaggio: String = "",
    val data: String = "",
    val tipo: String = "",
    val letta: Boolean = false
)