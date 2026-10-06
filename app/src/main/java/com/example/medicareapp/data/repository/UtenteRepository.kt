package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Utente
import com.google.firebase.firestore.FirebaseFirestore

class UtenteRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getUtente(
        uid: String,
        onResult: (Utente?) -> Unit
    ) {
        db.collection("utenti")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val utente = document.toObject(Utente::class.java)
                onResult(utente)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun aggiornaUtente(
        uid: String,
        utente: Utente,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("utenti")
            .document(uid)
            .set(utente)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}