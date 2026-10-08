package com.example.medicareapp.ui.paziente.visite

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.DisponibilitaRepository
import com.example.medicareapp.data.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VisitePazienteViewModel : ViewModel() {

    private val visitaRepository = VisitaRepository()
    private val disponibilitaRepository = DisponibilitaRepository()
    private val authRepository = AuthRepository()


    // -------------------------------------------------
    // VISITE
    // -------------------------------------------------

    private val _visite =
        MutableStateFlow<List<Visita>>(emptyList())

    val visite: StateFlow<List<Visita>> =
        _visite.asStateFlow()


    // -------------------------------------------------
    // CARICAMENTO
    // -------------------------------------------------

    private val _caricamento =
        MutableStateFlow(true)

    val caricamento: StateFlow<Boolean> =
        _caricamento.asStateFlow()


    // -------------------------------------------------
    // MESSAGGIO
    // -------------------------------------------------

    private val _messaggio =
        MutableStateFlow<String?>(null)

    val messaggio: StateFlow<String?> =
        _messaggio.asStateFlow()


    init {
        caricaVisite()
    }


    // -------------------------------------------------
    // CARICA VISITE
    // -------------------------------------------------

    fun caricaVisite() {

        val idPaziente =
            authRepository.utenteCorrente()

        if (idPaziente == null) {

            _caricamento.value = false

            _messaggio.value =
                "Utente non autenticato"

            return
        }

        _caricamento.value = true

        visitaRepository.getVisitePaziente(
            idPaziente = idPaziente
        ) { listaVisite ->

            _visite.value =
                listaVisite.sortedWith(
                    compareBy(
                        { it.data },
                        { it.ora }
                    )
                )

            _caricamento.value = false
        }
    }


    // -------------------------------------------------
    // CANCELLA VISITA
    // -------------------------------------------------

    fun cancellaVisita(
        visita: Visita
    ) {

        if (visita.stato != "prenotato") {

            _messaggio.value =
                "Questa visita non può essere cancellata"

            return
        }

        visitaRepository.cancellaVisita(
            idVisita = visita.idVisita
        ) { successo, errore ->

            if (!successo) {

                _messaggio.value =
                    errore
                        ?: "Errore durante la cancellazione"

                return@cancellaVisita
            }

            // -----------------------------------------
            // RIPRISTINIAMO LO SLOT
            // -----------------------------------------

            disponibilitaRepository.ripristinaDisponibilita(
                idDottore = visita.idDottore,
                data = visita.data,
                ora = visita.ora
            ) { disponibilitaRipristinata, erroreDisponibilita ->

                // Aggiorniamo comunque la visita localmente
                _visite.value =
                    _visite.value.map {

                        if (it.idVisita == visita.idVisita) {

                            it.copy(
                                stato = "cancellato"
                            )

                        } else {
                            it
                        }
                    }

                if (!disponibilitaRipristinata) {

                    _messaggio.value =
                        "Visita cancellata, ma impossibile ripristinare l'orario"

                    return@ripristinaDisponibilita
                }

                _messaggio.value =
                    "Visita cancellata con successo"
            }
        }
    }

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}