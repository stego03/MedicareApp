
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Visita

interface VisitaDataSource {

    fun prenotaVisita(
        visita: Visita,
        onResult: (Boolean, String?) -> Unit
    )

    fun getVisitePaziente(
        idPaziente: String,
        onResult: (List<Visita>) -> Unit
    )

    fun getVisiteDottore(
        idDottore: String,
        onResult: (List<Visita>) -> Unit
    )

    fun verificaAltreVisitePrenotate(
        visita: Visita,
        onResult: (Boolean, String?) -> Unit
    )

    fun cancellaVisita(
        idVisita: String,
        onResult: (Boolean, String?) -> Unit
    )

    fun segnaVisitaEffettuata(
        idVisita: String,
        onResult: (Boolean, String?) -> Unit
    )
}
