package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Disponibilita
import com.google.firebase.firestore.FirebaseFirestore

class DisponibilitaRepository {

    private val db = FirebaseFirestore.getInstance()

    // Recupera tutti gli slot disponibili di un dottore
    fun getDisponibilitaDottore(
        idDottore: String,
        onResult: (List<Disponibilita>) -> Unit
    ) {
        db.collection("disponibilita")
            .whereEqualTo("idDottore", idDottore)
            .whereEqualTo("stato", true)
            .get()
            .addOnSuccessListener { risultato ->

                val disponibilita = risultato.documents.mapNotNull {
                    it.toObject(Disponibilita::class.java)
                }

                onResult(disponibilita)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Recupera gli slot disponibili per una determinata data
    fun getDisponibilitaData(
        idDottore: String,
        data: String,
        onResult: (List<Disponibilita>) -> Unit
    ) {
        db.collection("disponibilita")
            .whereEqualTo("idDottore", idDottore)
            .whereEqualTo("data", data)
            .whereEqualTo("stato", true)
            .get()
            .addOnSuccessListener { risultato ->

                val disponibilita = risultato.documents.mapNotNull {
                    it.toObject(Disponibilita::class.java)
                }

                onResult(disponibilita)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Crea un nuovo slot disponibile
    fun creaDisponibilita(
        disponibilita: Disponibilita,
        onResult: (Boolean, String?) -> Unit
    ) {
        val documento = db.collection("disponibilita").document()

        val nuovaDisponibilita = disponibilita.copy(
            idDisponibilita = documento.id
        )

        documento
            .set(nuovaDisponibilita)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    // Rende uno slot non disponibile
    fun rendiNonDisponibile(
        idDisponibilita: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("disponibilita")
            .document(idDisponibilita)
            .update("stato", false)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}