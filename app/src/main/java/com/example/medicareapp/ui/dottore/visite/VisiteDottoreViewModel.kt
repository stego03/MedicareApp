package com.example.medicareapp.ui.dottore.visite

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.NotificaRepository
import com.example.medicareapp.data.repository.UtenteRepository
import com.example.medicareapp.data.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisiteDottoreViewModel : ViewModel() {

    private val visitaRepository = VisitaRepository()
    private val notificaRepository = NotificaRepository()
    private val utenteRepository = UtenteRepository()
    private val authRepository = AuthRepository()

    private val _visite = MutableStateFlow<List<Visita>>(emptyList())
    val visite: StateFlow<List<Visita>> = _visite.asStateFlow()

    private val _pazienti = MutableStateFlow<Map<String, Utente>>(emptyMap())
    val pazienti: StateFlow<Map<String, Utente>> = _pazienti.asStateFlow()

    private val _caricamento = MutableStateFlow(false)
    val caricamento: StateFlow<Boolean> = _caricamento.asStateFlow()

    private val _messaggio = MutableStateFlow<String?>(null)
    val messaggio: StateFlow<String?> = _messaggio.asStateFlow()

    init {
        caricaVisite()
    }

    fun caricaVisite() {
        val idDottore = authRepository.utenteCorrente()

        if (idDottore == null) {
            _caricamento.value = false
            _messaggio.value = "Utente non autenticato"
            return
        }

        _caricamento.value = true
        _messaggio.value = null

        visitaRepository.getVisiteDottore(idDottore) { listaVisite ->
            _visite.value = listaVisite.sortedWith(
                compareBy({ it.data }, { it.ora })
            )

            caricaPazienti(listaVisite)
        }
    }

    private fun caricaPazienti(visite: List<Visita>) {
        val idPazienti = visite
            .map { it.idPaziente }
            .filter { it.isNotBlank() }
            .toSet()

        if (idPazienti.isEmpty()) {
            _pazienti.value = emptyMap()
            _caricamento.value = false
            return
        }

        val pazientiCaricati = mutableMapOf<String, Utente>()
        var richiesteCompletate = 0

        idPazienti.forEach { idPaziente ->
            utenteRepository.getUtente(idPaziente) { paziente ->
                if (paziente != null) {
                    pazientiCaricati[idPaziente] = paziente
                }

                richiesteCompletate++

                if (richiesteCompletate == idPazienti.size) {
                    _pazienti.value = pazientiCaricati.toMap()
                    _caricamento.value = false
                }
            }
        }
    }

    fun cancellaVisita(visita: Visita) {
        if (visita.stato.trim().lowercase() != "prenotato") {
            _messaggio.value =
                "È possibile cancellare solo le visite prenotate."
            return
        }

        val idDottore = authRepository.utenteCorrente()

        if (idDottore == null || idDottore != visita.idDottore) {
            _messaggio.value =
                "Non sei autorizzato a cancellare questa visita."
            return
        }

        _caricamento.value = true
        _messaggio.value = null

        visitaRepository.verificaAltreVisitePrenotate(visita) {
                esisteAltraVisita, errore ->

            if (errore != null) {
                _caricamento.value = false
                _messaggio.value =
                    "Impossibile verificare le altre visite: $errore"
            } else {
                eseguiCancellazione(
                    visita = visita,
                    esisteAltraVisita = esisteAltraVisita
                )
            }
        }
    }

    private fun eseguiCancellazione(
        visita: Visita,
        esisteAltraVisita: Boolean
    ) {
        visitaRepository.cancellaVisita(visita.idVisita) { successo, errore ->

            if (!successo) {
                _caricamento.value = false
                _messaggio.value =
                    errore ?: "Errore durante la cancellazione."
                return@cancellaVisita
            }

            completaCancellazione(
                visita = visita,
                esisteAltraVisita = esisteAltraVisita
            )
        }
    }

    private fun completaCancellazione(
        visita: Visita,
        esisteAltraVisita: Boolean
    ) {
        val dataNotifica = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val notifica = Notifica(
            idUtente = visita.idPaziente,
            titolo = "Visita cancellata",
            messaggio = "Il dottore ha cancellato la visita " +
                    "del ${visita.data} alle ${visita.ora}. " +
                    "Contatta lo studio per maggiori informazioni.",
            data = dataNotifica,
            tipo = "cancellazione_visita"
        )

        notificaRepository.creaNotifica(notifica) {
                notificaCreata, erroreNotifica ->

            _visite.value = _visite.value
                .map { elemento ->
                    if (elemento.idVisita == visita.idVisita) {
                        elemento.copy(stato = "cancellato")
                    } else {
                        elemento
                    }
                }
                .sortedWith(compareBy({ it.data }, { it.ora }))

            _caricamento.value = false

            val messaggioBase = if (esisteAltraVisita) {
                "Visita cancellata. L'orario resta occupato " +
                        "da un'altra visita prenotata."
            } else {
                "Visita cancellata. L'orario non è più occupato " +
                        "da questa prenotazione."
            }

            _messaggio.value = when {
                !notificaCreata ->
                    "$messaggioBase Attenzione: notifica non inviata. " +
                            (erroreNotifica ?: "")

                else ->
                    "$messaggioBase Notifica registrata per il paziente."
            }
        }
    }

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}