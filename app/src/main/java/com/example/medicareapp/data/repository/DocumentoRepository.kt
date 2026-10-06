package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Documento
import com.google.firebase.firestore.FirebaseFirestore

class DocumentoRepository {

    private val db = FirebaseFirestore.getInstance()

    // Recupera tutti i documenti di un paziente
    fun getDocumentiPaziente(
        idPaziente: String,
        onResult: (List<Documento>) -> Unit
    ) {
        db.collection("documenti")
            .whereEqualTo("idPaziente", idPaziente)
            .get()
            .addOnSuccessListener { risultato ->

                val documenti = risultato.documents.mapNotNull {
                    it.toObject(Documento::class.java)
                }

                onResult(documenti)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Aggiunge un nuovo documento
    fun aggiungiDocumento(
        documento: Documento,
        onResult: (Boolean, String?) -> Unit
    ) {
        val riferimento = db.collection("documenti").document()

        val nuovoDocumento = documento.copy(
            idDocumento = riferimento.id
        )

        riferimento
            .set(nuovoDocumento)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }

    // Elimina un documento
    fun eliminaDocumento(
        idDocumento: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        db.collection("documenti")
            .document(idDocumento)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { errore ->
                onResult(false, errore.message)
            }
    }
}