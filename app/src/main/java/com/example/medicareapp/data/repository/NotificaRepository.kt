
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Notifica
import com.google.firebase.firestore.FirebaseFirestore

class NotificaRepository {

    private val db = FirebaseFirestore.getInstance()

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

    fun inviaNotifica(
        idPazienti: List<String>,
        titolo: String,
        messaggio: String,
        data: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (idPazienti.isEmpty()) {
            onResult(false, "Non ci sono pazienti destinatari.")
            return
        }

        val batch = db.batch()
        val collezione = db.collection("notifiche")

        idPazienti.distinct().forEach { idPaziente ->
            val riferimento = collezione.document()

            val notifica = Notifica(
                idNotifica = riferimento.id,
                idUtente = idPaziente,
                titolo = titolo,
                messaggio = messaggio,
                data = data,
                tipo = "dottore"
            )

            batch.set(riferimento, notifica)
        }

        batch.commit()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(
                    false,
                    errore.message ?: "Errore durante l'invio."
                )
            }
    }
}
