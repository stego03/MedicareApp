package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Utente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository : AuthDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    private fun traduciErrore(errore: Exception): String {
        val codice = (errore as? com.google.firebase.auth.FirebaseAuthException)
            ?.errorCode

        return when (codice) {
            "ERROR_INVALID_EMAIL" ->
                "L'indirizzo email non è valido."

            "ERROR_INVALID_CREDENTIAL",
            "ERROR_WRONG_PASSWORD",
            "ERROR_USER_NOT_FOUND" ->
                "Username o password non corretti."

            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "Questo username è già registrato."

            "ERROR_WEAK_PASSWORD" ->
                "La password è troppo debole. Scegline una più sicura."

            "ERROR_TOO_MANY_REQUESTS" ->
                "Troppi tentativi. Riprova più tardi."

            "ERROR_NETWORK_REQUEST_FAILED" ->
                "Connessione assente. Controlla la rete e riprova."

            else ->
                "Si è verificato un errore. Riprova più tardi."
        }
    }


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
                        onResult(false, traduciErrore(errore))
                    }
            }
            .addOnFailureListener { errore ->
                onResult(false, traduciErrore(errore))
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
                onResult(false, traduciErrore(errore))
            }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun utenteCorrente(): String? {
        return auth.currentUser?.uid
    }
}