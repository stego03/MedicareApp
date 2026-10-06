package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Utente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registraUtente(
        username: String,
        password: String,
        codiceFiscale: String,
        nome: String,
        cognome: String,
        dataNascita: String,
        email: String,
        telefono: String,
        ruolo: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        val emailTecnica = "$username@medicare.local"

        auth.createUserWithEmailAndPassword(emailTecnica, password)
            .addOnSuccessListener { risultato ->

                val uid = risultato.user?.uid

                if (uid == null) {
                    onResult(false, "Errore: UID non trovato")
                    return@addOnSuccessListener
                }

                val utente = Utente(
                    uid = uid,
                    username = username,
                    codiceFiscale = codiceFiscale,
                    nome = nome,
                    cognome = cognome,
                    dataNascita = dataNascita,
                    email = email,
                    telefono = telefono,
                    ruolo = ruolo
                )

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
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    fun login(
        username: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        val emailTecnica = "$username@medicare.local"

        auth.signInWithEmailAndPassword(emailTecnica, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun utenteCorrente(): String? {
        return auth.currentUser?.uid
    }
}