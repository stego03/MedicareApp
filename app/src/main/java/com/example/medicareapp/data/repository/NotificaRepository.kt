package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Notifica
import com.google.firebase.firestore.FirebaseFirestore

class NotificaRepository {

    private val db = FirebaseFirestore.getInstance()

    // Recupera le notifiche di un utente
    fun getNotificheUtente(
        idUtente: String,
        onResult: (List<Notifica>) -> Unit
    ) {
        db.collection("notifiche")
            .whereEqualTo("idUtente", idUtente)
            .get()
            .addOnSuccessListener { risultato ->

                val notifiche = risultato.documents.mapNotNull {
                    it.toObject(Notifica::class.java)
                }

                onResult(notifiche)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Crea una nuova notifica
    fun creaNotifica(
        notifica: Notifica,
        onResult: (Boolean, String?) -> Unit
    ) {
        val riferimento = db.collection("notifiche").document()

        val nuovaNotifica = notifica.copy(
            idNotifica = riferimento.id
        )

        riferimento
            .set(nuovaNotifica)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    // Segna una notifica come letta
    fun segnaComeLetta(
        idNotifica: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("notifiche")
            .document(idNotifica)
            .update("letta", true)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}