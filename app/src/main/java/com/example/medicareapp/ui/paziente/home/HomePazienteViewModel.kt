package com.example.medicareapp.ui.paziente.home

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Disponibilita
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.DisponibilitaRepository
import com.example.medicareapp.data.repository.UtenteRepository
import com.example.medicareapp.data.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomePazienteViewModel : ViewModel() {

    // -------------------------------------------------
    // REPOSITORY
    // -------------------------------------------------

    private val utenteRepository = UtenteRepository()
    private val disponibilitaRepository = DisponibilitaRepository()
    private val visitaRepository = VisitaRepository()
    private val authRepository = AuthRepository()


    // -------------------------------------------------
    // DOTTORE
    // -------------------------------------------------

    private val _dottori =
        MutableStateFlow<List<Utente>>(emptyList())

    val dottori: StateFlow<List<Utente>> =
        _dottori.asStateFlow()


    private val _dottoreSelezionato =
        MutableStateFlow<Utente?>(null)

    val dottoreSelezionato: StateFlow<Utente?> =
        _dottoreSelezionato.asStateFlow()


    // -------------------------------------------------
    // DISPONIBILITÀ
    // -------------------------------------------------

    private val _disponibilita =
        MutableStateFlow<List<Disponibilita>>(emptyList())

    val disponibilita: StateFlow<List<Disponibilita>> =
        _disponibilita.asStateFlow()


    // -------------------------------------------------
    // GIORNO SELEZIONATO
    // -------------------------------------------------

    private val _giornoSelezionato =
        MutableStateFlow<String?>(null)

    val giornoSelezionato: StateFlow<String?> =
        _giornoSelezionato.asStateFlow()


    // -------------------------------------------------
    // ORARI DISPONIBILI
    // -------------------------------------------------

    private val _orariDisponibili =
        MutableStateFlow<List<String>>(emptyList())

    val orariDisponibili: StateFlow<List<String>> =
        _orariDisponibili.asStateFlow()


    // -------------------------------------------------
    // MESSAGGIO PRENOTAZIONE
    // -------------------------------------------------

    private val _messaggioPrenotazione =
        MutableStateFlow<String?>(null)

    val messaggioPrenotazione: StateFlow<String?> =
        _messaggioPrenotazione.asStateFlow()


    // -------------------------------------------------
    // INIT
    // -------------------------------------------------

    init {
        caricaDottori()
    }


    // -------------------------------------------------
    // CARICA DOTTORI
    // -------------------------------------------------

    private fun caricaDottori() {

        utenteRepository.getDottori { listaDottori ->

            _dottori.value = listaDottori
        }
    }


    // -------------------------------------------------
    // SELEZIONA DOTTORE
    // -------------------------------------------------

    fun selezionaDottore(
        dottore: Utente
    ) {

        _dottoreSelezionato.value = dottore

        _giornoSelezionato.value = null

        _orariDisponibili.value = emptyList()

        _disponibilita.value = emptyList()

        _messaggioPrenotazione.value = null

        // IMPORTANTE:
        // idDottore = Firebase UID
        caricaDisponibilitaDottore(
            dottore.uid
        )
    }


    // -------------------------------------------------
    // CARICA DISPONIBILITÀ DEL DOTTORE
    // -------------------------------------------------

    private fun caricaDisponibilitaDottore(
        idDottore: String
    ) {

        disponibilitaRepository.getDisponibilitaDottore(
            idDottore = idDottore
        ) { listaDisponibilita ->

            _disponibilita.value = listaDisponibilita
        }
    }


    // -------------------------------------------------
    // SELEZIONA GIORNO
    // -------------------------------------------------

    fun selezionaGiorno(
        data: String
    ) {

        _giornoSelezionato.value = data

        _messaggioPrenotazione.value = null

        val dottore =
            _dottoreSelezionato.value
                ?: return

        // IMPORTANTE:
        // idDottore = Firebase UID
        caricaOrariDisponibili(
            idDottore = dottore.uid,
            data = data
        )
    }


    // -------------------------------------------------
    // CARICA ORARI DISPONIBILI
    // -------------------------------------------------

    private fun caricaOrariDisponibili(
        idDottore: String,
        data: String
    ) {

        disponibilitaRepository.getDisponibilitaData(
            idDottore = idDottore,
            data = data
        ) { listaDisponibilita ->

            _orariDisponibili.value =
                listaDisponibilita.map {
                    it.ora
                }
        }
    }


    // -------------------------------------------------
    // PRENOTA VISITA
    // -------------------------------------------------

    fun prenotaVisita(
        ora: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        // ---------------------------------------------
        // UID DEL PAZIENTE
        // ---------------------------------------------

        val idPaziente =
            authRepository.utenteCorrente()

        if (idPaziente == null) {

            onResult(
                false,
                "Utente non autenticato"
            )

            return
        }


        // ---------------------------------------------
        // DOTTORE SELEZIONATO
        // ---------------------------------------------

        val dottore =
            _dottoreSelezionato.value

        if (dottore == null) {

            onResult(
                false,
                "Seleziona un dottore"
            )

            return
        }


        // ---------------------------------------------
        // DATA SELEZIONATA
        // ---------------------------------------------

        val data =
            _giornoSelezionato.value

        if (data == null) {

            onResult(
                false,
                "Seleziona una data"
            )

            return
        }


        // ---------------------------------------------
        // CERCA DISPONIBILITÀ
        // ---------------------------------------------

        val disponibilitaSelezionata =
            _disponibilita.value.find {

                it.data == data &&
                        it.ora == ora
            }


        if (disponibilitaSelezionata == null) {

            onResult(
                false,
                "L'orario selezionato non è più disponibile"
            )

            return
        }


        // ---------------------------------------------
        // CREAZIONE VISITA
        // ---------------------------------------------

        val visita = Visita(

            // Firebase UID del paziente
            idPaziente = idPaziente,

            // Firebase UID del dottore
            idDottore = dottore.uid,

            data = data,

            ora = ora,

            stato = "prenotato"
        )


        // ---------------------------------------------
        // SALVATAGGIO FIRESTORE
        // ---------------------------------------------

        visitaRepository.prenotaVisita(
            visita = visita
        ) { successo, errore ->

            if (!successo) {

                onResult(
                    false,
                    errore
                        ?: "Errore durante la prenotazione"
                )

                return@prenotaVisita
            }


            // -----------------------------------------
            // SLOT NON PIÙ DISPONIBILE
            // -----------------------------------------

            disponibilitaRepository.rendiNonDisponibile(
                idDisponibilita =
                    disponibilitaSelezionata.idDisponibilita
            ) { disponibilitaAggiornata, erroreDisponibilita ->

                if (!disponibilitaAggiornata) {

                    onResult(
                        false,
                        erroreDisponibilita
                            ?: "Visita prenotata, ma impossibile aggiornare la disponibilità"
                    )

                    return@rendiNonDisponibile
                }


                // -------------------------------------
                // AGGIORNIAMO LA UI
                // -------------------------------------

                _orariDisponibili.value =
                    _orariDisponibili.value.filter {
                        it != ora
                    }

                _disponibilita.value =
                    _disponibilita.value.filter {
                        it.idDisponibilita !=
                                disponibilitaSelezionata.idDisponibilita
                    }

                _messaggioPrenotazione.value =
                    "Visita prenotata con successo"


                onResult(
                    true,
                    null
                )
            }
        }
    }
}