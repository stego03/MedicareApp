
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Indisponibilita

interface IndisponibilitaDataSource {

    suspend fun getIndisponibilitaDottore(
        idDottore: String
    ): List<Indisponibilita>

    suspend fun getIndisponibilitaGiorno(
        idDottore: String,
        data: String
    ): List<Indisponibilita>

    suspend fun aggiungiIndisponibilitaOraria(
        idDottore: String,
        data: String,
        ora: String
    ): Boolean

    suspend fun rimuoviIndisponibilitaOraria(
        idDottore: String,
        data: String,
        ora: String
    ): Boolean

    suspend fun aggiungiIndisponibilitaGiornata(
        idDottore: String,
        data: String
    ): Boolean

    suspend fun rimuoviIndisponibilitaGiornata(
        idDottore: String,
        data: String
    ): Boolean
}
