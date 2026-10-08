package com.example.medicareapp.data.repository

import com.example.medicareapp.data.model.Indisponibilita
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class IndisponibilitaRepository {

    private val db = FirebaseFirestore.getInstance()

    private val collection =
        db.collection("indisponibilita")

    /**
     * Restituisce tutte le indisponibilità di un determinato dottore.
     */
    suspend fun getIndisponibilitaDottore(
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
    suspend fun getIndisponibilitaGiorno(
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
     */
    suspend fun aggiungiIndisponibilitaOraria(
        idDottore: String,
        data: String,
        ora: String
    ): Boolean {

        return try {

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
     */
    suspend fun rimuoviIndisponibilitaOraria(
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
     */
    suspend fun aggiungiIndisponibilitaGiornata(
        idDottore: String,
        data: String
    ): Boolean {

        return try {

            // Prima eliminiamo eventuali indisponibilità
            // parziali già presenti per quella giornata.
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
     */
    suspend fun rimuoviIndisponibilitaGiornata(
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