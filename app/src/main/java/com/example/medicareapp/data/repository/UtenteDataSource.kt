
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Utente

interface UtenteDataSource {

    fun getDottori(
        onResult: (List<Utente>) -> Unit
    )

    fun getUtente(
        uid: String,
        onResult: (Utente?) -> Unit
    )
}
