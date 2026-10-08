
package com.example.medicareapp.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.medicareapp.data.model.Documento
import com.google.firebase.firestore.FirebaseFirestore

class DocumentoRepository {

    private val db =
        FirebaseFirestore.getInstance()

    companion object {
        private const val MAX_FILE_SIZE = 800 * 1024
    }

    // =================================================
    // RECUPERA DOCUMENTI DEL PAZIENTE
    // =================================================

    fun getDocumentiPaziente(
        idPaziente: String,
        onResult: (List<Documento>) -> Unit
    ) {

        db.collection("documenti")
            .whereEqualTo(
                "idPaziente",
                idPaziente
            )
            .get()
            .addOnSuccessListener { risultato ->

                val documenti =
                    risultato.documents.mapNotNull {
                        it.toObject(
                            Documento::class.java
                        )
                    }

                onResult(documenti)
            }
            .addOnFailureListener {

                onResult(emptyList())
            }
    }


    // =================================================
    // CARICA DOCUMENTO IN FIRESTORE
    // =================================================

    fun caricaDocumento(
        context: Context,
        idPaziente: String,
        uriFile: Uri,
        nomeFile: String,
        tipo: String,
        data: String,
        descrizione: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        try {

            // -----------------------------------------
            // LEGGE IL FILE
            // -----------------------------------------

            val bytes =
                context.contentResolver
                    .openInputStream(uriFile)
                    ?.use {
                        it.readBytes()
                    }

            if (bytes == null) {

                onResult(
                    false,
                    "Impossibile leggere il file."
                )

                return
            }


            // -----------------------------------------
            // CONTROLLO DIMENSIONE
            // -----------------------------------------

            if (bytes.size > MAX_FILE_SIZE) {

                onResult(
                    false,
                    "Il file è troppo grande. " +
                            "La dimensione massima è 800 KB."
                )

                return
            }


            // -----------------------------------------
            // CONVERSIONE BASE64
            // -----------------------------------------

            val contenuto =
                Base64.encodeToString(
                    bytes,
                    Base64.NO_WRAP
                )


            // -----------------------------------------
            // CREA DOCUMENTO
            // -----------------------------------------

            val riferimento =
                db.collection("documenti")
                    .document()

            val documento =
                Documento(

                    idDocumento =
                        riferimento.id,

                    idPaziente =
                        idPaziente,

                    idDottore =
                        "",

                    nome =
                        nomeFile,

                    tipo =
                        tipo,

                    data =
                        data,

                    descrizione =
                        descrizione,

                    urlFile =
                        "",

                    contenuto =
                        contenuto
                )


            // -----------------------------------------
            // SALVA FIRESTORE
            // -----------------------------------------

            riferimento
                .set(documento)
                .addOnSuccessListener {

                    onResult(
                        true,
                        null
                    )
                }
                .addOnFailureListener { errore ->

                    onResult(
                        false,
                        errore.message
                    )
                }

        } catch (errore: Exception) {

            onResult(
                false,
                errore.message
                    ?: "Errore durante la lettura del file."
            )
        }
    }


    // =================================================
    // ELIMINA DOCUMENTO
    // =================================================

    fun eliminaDocumento(
        idDocumento: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        db.collection("documenti")
            .document(idDocumento)
            .delete()
            .addOnSuccessListener {

                onResult(
                    true,
                    null
                )
            }
            .addOnFailureListener { errore ->

                onResult(
                    false,
                    errore.message
                )
            }
    }
}

