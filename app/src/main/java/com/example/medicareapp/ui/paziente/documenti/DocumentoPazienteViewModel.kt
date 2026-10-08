
package com.example.medicareapp.ui.paziente.documenti

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Documento
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.DocumentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DocumentoPazienteViewModel : ViewModel() {

    private val documentoRepository =
        DocumentoRepository()

    private val authRepository =
        AuthRepository()


    // =================================================
    // DOCUMENTI
    // =================================================

    private val _documenti =
        MutableStateFlow<List<Documento>>(emptyList())

    val documenti: StateFlow<List<Documento>> =
        _documenti.asStateFlow()


    // =================================================
    // CARICAMENTO LISTA
    // =================================================

    private val _caricamento =
        MutableStateFlow(true)

    val caricamento: StateFlow<Boolean> =
        _caricamento.asStateFlow()


    // =================================================
    // CARICAMENTO FILE
    // =================================================

    private val _caricamentoFile =
        MutableStateFlow(false)

    val caricamentoFile: StateFlow<Boolean> =
        _caricamentoFile.asStateFlow()


    // =================================================
    // MESSAGGIO
    // =================================================

    private val _messaggio =
        MutableStateFlow<String?>(null)

    val messaggio: StateFlow<String?> =
        _messaggio.asStateFlow()


    init {
        caricaDocumenti()
    }


    // =================================================
    // CARICA DOCUMENTI
    // =================================================

    fun caricaDocumenti() {

        val idPaziente =
            authRepository.utenteCorrente()

        if (idPaziente == null) {

            _caricamento.value = false

            _messaggio.value =
                "Utente non autenticato."

            return
        }

        _caricamento.value = true

        documentoRepository.getDocumentiPaziente(
            idPaziente = idPaziente
        ) { lista ->

            _documenti.value =
                lista.sortedByDescending {
                    it.data
                }

            _caricamento.value = false
        }
    }


    // =================================================
    // CARICA NUOVO DOCUMENTO
    // =================================================

    fun caricaNuovoDocumento(
        context: Context,
        uriFile: Uri,
        nomeFile: String,
        tipo: String,
        data: String,
        descrizione: String
    ) {

        val idPaziente =
            authRepository.utenteCorrente()

        if (idPaziente == null) {

            _messaggio.value =
                "Utente non autenticato."

            return
        }

        if (nomeFile.isBlank()) {

            _messaggio.value =
                "Nome file non valido."

            return
        }

        _caricamentoFile.value = true
        _messaggio.value = null

        documentoRepository.caricaDocumento(

            context = context,

            idPaziente = idPaziente,

            uriFile = uriFile,

            nomeFile = nomeFile,

            tipo = tipo,

            data = data,

            descrizione = descrizione

        ) { successo, errore ->

            _caricamentoFile.value = false

            if (successo) {

                _messaggio.value =
                    "Documento caricato correttamente."

                caricaDocumenti()

            } else {

                _messaggio.value =
                    errore
                        ?: "Errore durante il caricamento."
            }
        }
    }


    // =================================================
    // ELIMINA DOCUMENTO
    // =================================================

    fun eliminaDocumento(
        documento: Documento
    ) {

        documentoRepository.eliminaDocumento(
            idDocumento =
                documento.idDocumento
        ) { successo, errore ->

            if (successo) {

                _messaggio.value =
                    "Documento eliminato."

                caricaDocumenti()

            } else {

                _messaggio.value =
                    errore
                        ?: "Errore durante l'eliminazione."
            }
        }
    }


    // =================================================
    // PULISCI MESSAGGIO
    // =================================================

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}
