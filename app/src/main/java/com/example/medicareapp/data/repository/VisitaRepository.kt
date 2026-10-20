package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Visita
import com.google.firebase.firestore.FirebaseFirestore

class VisitaRepository : VisitaDataSource {

    private val db = FirebaseFirestore.getInstance()

    // Crea una nuova visita
    override fun prenotaVisita(
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

    // Recupera le visite di un paziente
    override fun getVisitePaziente(
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

    // Recupera le visite di un dottore
    override fun getVisiteDottore(
        idDottore: String,
        onResult: (List<Visita>) -> Unit
    ) {
        db.collection("visite")
            .whereEqualTo("idDottore", idDottore)
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

    // Controlla se esiste un'altra visita prenotata
    // nello stesso giorno e alla stessa ora.
    override fun verificaAltreVisitePrenotate(
        visita: Visita,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("visite")
            .whereEqualTo("idDottore", visita.idDottore)
            .get()
            .addOnSuccessListener { risultato ->

                val esisteAltraVisita = risultato.documents
                    .mapNotNull { document ->
                        document.toObject(Visita::class.java)
                    }
                    .any { altraVisita ->
                        altraVisita.idVisita != visita.idVisita &&
                                altraVisita.data == visita.data &&
                                altraVisita.ora == visita.ora &&
                                altraVisita.stato.equals(
                                    "prenotato",
                                    ignoreCase = true
                                )
                    }

                onResult(esisteAltraVisita, null)
            }
            .addOnFailureListener { errore ->
                onResult(
                    false,
                    errore.message
                        ?: "Impossibile verificare le altre visite"
                )
            }
    }

    // Elimina definitivamente una visita da Firestore.
    override fun cancellaVisita(
        idVisita: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("visite")
            .document(idVisita)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    // Segna una visita come effettuata su Firestore.
    // Funzione utilizzata per cambiare stato visita
    // da prenotato ad effettuato.
    override fun segnaVisitaEffettuata(
        idVisita: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("visite")
            .document(idVisita)
            .update("stato", "effettuato")
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}