
package com.example.medicareapp.ui.paziente.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.IndisponibilitaRepository
import com.example.medicareapp.data.repository.UtenteRepository
import com.example.medicareapp.data.repository.VisitaRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomePazienteViewModel : ViewModel() {

    private val utenteRepository = UtenteRepository()
    private val indisponibilitaRepository = IndisponibilitaRepository()
    private val visitaRepository = VisitaRepository()
    private val authRepository = AuthRepository()

    companion object {
        val ORARI_LAVORATIVI = listOf(
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "15:00",
            "16:00",
            "17:00"
        )
    }

    private val _dottori = MutableStateFlow<List<Utente>>(emptyList())
    val dottori: StateFlow<List<Utente>> = _dottori.asStateFlow()

    private val _dottoreSelezionato = MutableStateFlow<Utente?>(null)
    val dottoreSelezionato: StateFlow<Utente?> =
        _dottoreSelezionato.asStateFlow()

    private val _indisponibilita =
        MutableStateFlow<List<Indisponibilita>>(emptyList())
    val indisponibilita: StateFlow<List<Indisponibilita>> =
        _indisponibilita.asStateFlow()

    private val _visiteDottore = MutableStateFlow<List<Visita>>(emptyList())

    private val _giornoSelezionato = MutableStateFlow<String?>(null)
    val giornoSelezionato: StateFlow<String?> =
        _giornoSelezionato.asStateFlow()

    private val _orariDisponibili = MutableStateFlow<List<String>>(emptyList())
    val orariDisponibili: StateFlow<List<String>> =
        _orariDisponibili.asStateFlow()

    private val _messaggioPrenotazione = MutableStateFlow<String?>(null)
    val messaggioPrenotazione: StateFlow<String?> =
        _messaggioPrenotazione.asStateFlow()

    private val _prenotazioneInCorso = MutableStateFlow(false)
    val prenotazioneInCorso: StateFlow<Boolean> =
        _prenotazioneInCorso.asStateFlow()

    init {
        caricaDottori()
    }

    private fun caricaDottori() {
        utenteRepository.getDottori { listaDottori ->
            _dottori.value = listaDottori
        }
    }

    fun selezionaDottore(dottore: Utente) {
        _dottoreSelezionato.value = dottore
        _giornoSelezionato.value = null
        _orariDisponibili.value = emptyList()
        _indisponibilita.value = emptyList()
        _visiteDottore.value = emptyList()
        _messaggioPrenotazione.value = null

        viewModelScope.launch {
            _indisponibilita.value =
                indisponibilitaRepository.getIndisponibilitaDottore(
                    dottore.uid
                )
        }

        visitaRepository.getVisiteDottore(dottore.uid) { visite ->
            if (_dottoreSelezionato.value?.uid == dottore.uid) {
                _visiteDottore.value = visite
            }
        }
    }

    fun selezionaGiorno(data: String) {
        val dataSelezionata = try {
            LocalDate.parse(data)
        } catch (e: Exception) {
            _giornoSelezionato.value = null
            _orariDisponibili.value = emptyList()
            _messaggioPrenotazione.value = "Data non valida"
            return
        }

        if (dataSelezionata.isBefore(LocalDate.now())) {
            _giornoSelezionato.value = null
            _orariDisponibili.value = emptyList()
            _messaggioPrenotazione.value =
                "Non puoi selezionare un giorno passato"
            return
        }

        val dottore = _dottoreSelezionato.value ?: return

        _giornoSelezionato.value = data
        _orariDisponibili.value = emptyList()
        _messaggioPrenotazione.value = null

        caricaOrariDisponibili(
            idDottore = dottore.uid,
            data = data
        )
    }

    private fun caricaOrariDisponibili(
        idDottore: String,
        data: String
    ) {
        viewModelScope.launch {
            try {
                val blocchi = indisponibilitaRepository
                    .getIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )

                if (_giornoSelezionato.value != data ||
                    _dottoreSelezionato.value?.uid != idDottore
                ) {
                    return@launch
                }

                val giornoBloccato = blocchi.any { it.tuttoGiorno }

                if (giornoBloccato) {
                    _orariDisponibili.value = emptyList()
                    return@launch
                }

                val visitePrenotate = _visiteDottore.value.filter {
                    it.data == data &&
                            it.stato.equals("prenotato", ignoreCase = true)
                }

                _orariDisponibili.value = ORARI_LAVORATIVI.filter { ora ->
                    val orarioBloccato = blocchi.any {
                        !it.tuttoGiorno && it.ora == ora
                    }

                    val orarioPrenotato = visitePrenotate.any {
                        it.ora == ora
                    }

                    !orarioBloccato && !orarioPrenotato
                }
            } catch (e: Exception) {
                _orariDisponibili.value = emptyList()
                _messaggioPrenotazione.value =
                    "Errore durante il caricamento degli orari"
            }
        }
    }

    fun prenotaVisita(
        ora: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (_prenotazioneInCorso.value) return

        fun errorePrenotazione(messaggio: String) {
            _messaggioPrenotazione.value = messaggio
            _prenotazioneInCorso.value = false
            onResult(false, messaggio)
        }

        val idPaziente = authRepository.utenteCorrente()

        if (idPaziente == null) {
            errorePrenotazione("Utente non autenticato")
            return
        }

        val dottore = _dottoreSelezionato.value
        if (dottore == null) {
            errorePrenotazione("Seleziona un dottore")
            return
        }

        val data = _giornoSelezionato.value
        if (data == null) {
            errorePrenotazione("Seleziona una data")
            return
        }

        val dataSelezionata = try {
            LocalDate.parse(data)
        } catch (e: Exception) {
            errorePrenotazione("Data non valida")
            return
        }

        if (dataSelezionata.isBefore(LocalDate.now())) {
            errorePrenotazione("Non puoi prenotare una data passata")
            _giornoSelezionato.value = null
            _orariDisponibili.value = emptyList()
            return
        }

        if (ora !in ORARI_LAVORATIVI) {
            errorePrenotazione("Orario non valido")
            return
        }

        _prenotazioneInCorso.value = true
        _messaggioPrenotazione.value = null

        viewModelScope.launch {
            try {
                val blocchi = indisponibilitaRepository
                    .getIndisponibilitaGiorno(
                        idDottore = dottore.uid,
                        data = data
                    )

                val orarioBloccato = blocchi.any {
                    it.tuttoGiorno || it.ora == ora
                }

                if (orarioBloccato) {
                    errorePrenotazione(
                        "Questo orario non è disponibile. Scegline un altro."
                    )
                    caricaOrariDisponibili(dottore.uid, data)
                    return@launch
                }

                val visita = Visita(
                    idPaziente = idPaziente,
                    idDottore = dottore.uid,
                    data = data,
                    ora = ora,
                    stato = "prenotato"
                )

                visitaRepository.verificaAltreVisitePrenotate(visita) {
                        esisteAltraVisita, erroreVerifica ->

                    if (erroreVerifica != null) {
                        errorePrenotazione(
                            "Impossibile verificare l'orario: $erroreVerifica"
                        )
                        return@verificaAltreVisitePrenotate
                    }

                    if (esisteAltraVisita) {
                        errorePrenotazione(
                            "Questo orario è già stato prenotato. Scegline un altro."
                        )
                        visitaRepository.getVisiteDottore(dottore.uid) {
                            _visiteDottore.value = it
                            caricaOrariDisponibili(dottore.uid, data)
                        }
                        return@verificaAltreVisitePrenotate
                    }

                    visitaRepository.prenotaVisita(visita) { successo, errore ->
                        if (!successo) {
                            errorePrenotazione(
                                errore ?: "Errore durante la prenotazione"
                            )
                            return@prenotaVisita
                        }

                        _visiteDottore.value =
                            _visiteDottore.value + visita

                        _orariDisponibili.value =
                            _orariDisponibili.value.filter { it != ora }

                        _messaggioPrenotazione.value =
                            "Visita prenotata con successo"

                        _prenotazioneInCorso.value = false
                        onResult(true, null)
                    }
                }
            } catch (e: Exception) {
                errorePrenotazione(
                    e.message ?: "Errore durante la prenotazione"
                )
            }
        }
    }
}
