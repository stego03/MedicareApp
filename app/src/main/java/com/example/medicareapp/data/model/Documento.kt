package com.example.medicareapp.data.model

data class Documento(
    val idDocumento: String = "",
    val idPaziente: String = "",
    val idDottore: String = "",
    val nome: String = "",
    val tipo: String = "",
    val data: String = "",
    val descrizione: String = "",
    val urlFile: String = "",
    val contenuto: String = ""
)