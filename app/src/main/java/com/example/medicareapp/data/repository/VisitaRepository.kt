package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Visita
import com.google.firebase.firestore.FirebaseFirestore

class VisitaRepository {

    private val db = FirebaseFirestore.getInstance()

    // Crea una nuova visita
    fun prenotaVisita(
        visita: Visita,
        onResult: (Boolean, String?) -> Unit
    ) {
        val documento = db.collection("visite").document()

        val nuovaVisita = visita.copy(
            idVisita = documento.id
        )

        documento
            .set(nuovaVisita)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    // Recupera tutte le visite di un paziente
    fun getVisitePaziente(
        idPaziente: String,
        onResult: (List<Visita>) -> Unit
    ) {
        db.collection("visite")
            .whereEqualTo("idPaziente", idPaziente)
            .get()
            .addOnSuccessListener { risultato ->

                val visite = risultato.documents.mapNotNull {
                    it.toObject(Visita::class.java)
                }

                onResult(visite)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Cancella una visita
    fun cancellaVisita(
        idVisita: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("visite")
            .document(idVisita)
            .update("stato", "cancellato")
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}