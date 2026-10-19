
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Notifica

interface NotificaDataSource {

    fun getNotificheUtente(
        idUtente: String,
        onResult: (List<Notifica>) -> Unit
    )

    fun creaNotifica(
        notifica: Notifica,
        onResult: (Boolean, String?) -> Unit
    )

    fun inviaNotifica(
        idPazienti: List<String>,
        titolo: String,
        messaggio: String,
        data: String,
        onResult: (Boolean, String?) -> Unit
    )
}

