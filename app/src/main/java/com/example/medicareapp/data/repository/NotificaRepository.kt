package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Notifica
import com.google.firebase.firestore.FirebaseFirestore

class NotificaRepository {

    private val db = FirebaseFirestore.getInstance()

    // Recupera esclusivamente le notifiche destinate al paziente.
    // Il paziente ha solo permessi di lettura nella UI.
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

                onResult(
                    notifiche.sortedByDescending { it.data }
                )
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Metodo predisposto per il futuro lato Dottore.
    // Non viene chiamato dalla parte Paziente.
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
}
