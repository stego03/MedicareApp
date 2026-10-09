package com.example.medicareapp.ui.dottore.pazienti

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Documento
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.repository.DocumentoRepository
import com.example.medicareapp.data.repository.UtenteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PazientiDottoreViewModel : ViewModel() {

    private val utenteRepository = UtenteRepository()
    private val documentoRepository = DocumentoRepository()

    private val _pazienti = MutableStateFlow<List<Utente>>(emptyList())
    val pazienti: StateFlow<List<Utente>> = _pazienti.asStateFlow()

    private val _documenti = MutableStateFlow<List<Documento>>(emptyList())
    val documenti: StateFlow<List<Documento>> = _documenti.asStateFlow()

    private val _pazienteSelezionato = MutableStateFlow<Utente?>(null)
    val pazienteSelezionato: StateFlow<Utente?> =
        _pazienteSelezionato.asStateFlow()

    private val _caricamento = MutableStateFlow(false)
    val caricamento: StateFlow<Boolean> = _caricamento.asStateFlow()

    private val _messaggio = MutableStateFlow<String?>(null)
    val messaggio: StateFlow<String?> = _messaggio.asStateFlow()

    init {
        caricaPazienti()
    }

    fun caricaPazienti() {
        _caricamento.value = true
        _messaggio.value = null

        utenteRepository.getPazienti { lista ->
            _pazienti.value = lista.sortedWith(
                compareBy(
                    { it.cognome.lowercase() },
                    { it.nome.lowercase() }
                )
            )
            _caricamento.value = false

            if (lista.isEmpty()) {
                _messaggio.value = "Nessun paziente trovato."
            }
        }
    }

    fun selezionaPaziente(paziente: Utente) {
        if (paziente.uid.isBlank()) {
            _messaggio.value = "Identificativo del paziente non valido."
            return
        }

        _pazienteSelezionato.value = paziente
        _documenti.value = emptyList()
        _caricamento.value = true
        _messaggio.value = null

        documentoRepository.getDocumentiPaziente(paziente.uid) { lista ->
            _documenti.value = lista.sortedByDescending { it.data }
            _caricamento.value = false

            if (lista.isEmpty()) {
                _messaggio.value =
                    "Questo paziente non ha documenti caricati."
            }
        }
    }

    fun tornaAllaLista() {
        _pazienteSelezionato.value = null
        _documenti.value = emptyList()
        _messaggio.value = null
    }

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}