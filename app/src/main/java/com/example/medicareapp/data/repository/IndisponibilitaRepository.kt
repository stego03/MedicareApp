
package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.data.model.Visita
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IndisponibilitaRepository : IndisponibilitaDataSource  {

    private val db = FirebaseFirestore.getInstance()

    private val collection =
        db.collection("indisponibilita")

    /**
     * Restituisce tutte le indisponibilità di un determinato dottore.
     */
    override suspend fun getIndisponibilitaDottore(
        idDottore: String
    ): List<Indisponibilita> {

        return try {

            val snapshot = collection
                .whereEqualTo("idDottore", idDottore)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->

                document.toObject(Indisponibilita::class.java)
                    ?.copy(
                        id = document.id
                    )
            }

        } catch (e: Exception) {

            emptyList()
        }
    }

    /**
     * Restituisce le indisponibilità di un determinato giorno.
     */
    override suspend fun getIndisponibilitaGiorno(
        idDottore: String,
        data: String
    ): List<Indisponibilita> {

        return try {

            val snapshot = collection
                .whereEqualTo("idDottore", idDottore)
                .whereEqualTo("data", data)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->

                document.toObject(Indisponibilita::class.java)
                    ?.copy(
                        id = document.id
                    )
            }

        } catch (e: Exception) {

            emptyList()
        }
    }

    /**
     * Blocca un singolo orario.
     * Se esiste una visita prenotata, la cancella
     * e registra la notifica per il paziente.
     */
    override suspend fun aggiungiIndisponibilitaOraria(
        idDottore: String,
        data: String,
        ora: String
    ): Boolean {

        return try {

            cancellaVisitePrenotate(
                idDottore = idDottore,
                data = data,
                ora = ora
            )

            val indisponibilita = Indisponibilita(
                idDottore = idDottore,
                data = data,
                ora = ora,
                tuttoGiorno = false
            )

            collection
                .add(indisponibilita)
                .await()

            true

        } catch (e: Exception) {

            false
        }
    }

    /**
     * Rimuove l'indisponibilità di un singolo orario.
     * Le visite cancellate non vengono ripristinate.
     */
    override suspend fun rimuoviIndisponibilitaOraria(
        idDottore: String,
        data: String,
        ora: String
    ): Boolean {

        return try {

            val snapshot = collection
                .whereEqualTo("idDottore", idDottore)
                .whereEqualTo("data", data)
                .whereEqualTo("ora", ora)
                .whereEqualTo("tuttoGiorno", false)
                .get()
                .await()

            for (document in snapshot.documents) {
                document.reference.delete().await()
            }

            true

        } catch (e: Exception) {

            false
        }
    }

    /**
     * Rende indisponibile l'intera giornata.
     * Cancella le visite prenotate della giornata
     * e registra una notifica per ogni paziente interessato.
     */
    override suspend fun aggiungiIndisponibilitaGiornata(
        idDottore: String,
        data: String
    ): Boolean {

        return try {

            cancellaVisitePrenotate(
                idDottore = idDottore,
                data = data
            )

            eliminaIndisponibilitaGiorno(
                idDottore = idDottore,
                data = data
            )

            val indisponibilita = Indisponibilita(
                idDottore = idDottore,
                data = data,
                ora = "",
                tuttoGiorno = true
            )

            collection
                .add(indisponibilita)
                .await()

            true

        } catch (e: Exception) {

            false
        }
    }

    /**
     * Ripristina l'intera giornata.
     * Non ripristina le visite precedentemente cancellate.
     */
    override suspend fun rimuoviIndisponibilitaGiornata(
        idDottore: String,
        data: String
    ): Boolean {

        return try {

            eliminaIndisponibilitaGiorno(
                idDottore = idDottore,
                data = data
            )

            true

        } catch (e: Exception) {

            false
        }
    }

    /**
     * Cancella logicamente le visite prenotate per il dottore,
     * nella data indicata ed eventualmente all'ora specificata.
     *
     * Per ogni visita crea la stessa notifica della cancellazione
     * manuale effettuata dal dottore.
     */
    private suspend fun cancellaVisitePrenotate(
        idDottore: String,
        data: String,
        ora: String? = null
    ) {

        val snapshot = db.collection("visite")
            .whereEqualTo("idDottore", idDottore)
            .whereEqualTo("data", data)
            .get()
            .await()

        val visiteDaCancellare = snapshot.documents.mapNotNull { document ->

            val visita = document.toObject(Visita::class.java)

            if (
                visita != null &&
                visita.stato.equals("prenotato", ignoreCase = true) &&
                (ora == null || visita.ora == ora) &&
                visita.idPaziente.isNotBlank()
            ) {
                document to visita
            } else {
                null
            }
        }

        val dataNotifica = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        // Ogni visita richiede due operazioni:
        // aggiornamento dello stato e creazione della notifica.
        visiteDaCancellare.chunked(250).forEach { gruppo ->

            val batch = db.batch()
            val collezioneNotifiche = db.collection("notifiche")

            gruppo.forEach { (document, visita) ->

                // Aggiorna lo stato della visita.
                batch.update(
                    document.reference,
                    "stato",
                    "cancellato"
                )

                // Crea la notifica con gli stessi dati
                // utilizzati nella cancellazione manuale.
                val riferimentoNotifica =
                    collezioneNotifiche.document()

                val notifica = Notifica(
                    idNotifica = riferimentoNotifica.id,
                    idUtente = visita.idPaziente,
                    titolo = "Visita cancellata",
                    messaggio = "Il dottore ha cancellato la visita " +
                            "del ${visita.data} alle ${visita.ora}. " +
                            "Contatta lo studio per maggiori informazioni.",
                    data = dataNotifica,
                    tipo = "cancellazione_visita"
                )

                batch.set(
                    riferimentoNotifica,
                    notifica
                )
            }

            batch.commit().await()
        }
    }

    /**
     * Elimina tutte le indisponibilità presenti
     * in una determinata giornata.
     */
    private suspend fun eliminaIndisponibilitaGiorno(
        idDottore: String,
        data: String
    ) {

        val snapshot = collection
            .whereEqualTo("idDottore", idDottore)
            .whereEqualTo("data", data)
            .get()
            .await()

        for (document in snapshot.documents) {
            document.reference.delete().await()
        }
    }
}
