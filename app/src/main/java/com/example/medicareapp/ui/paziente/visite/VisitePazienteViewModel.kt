
package com.example.medicareapp.ui.paziente.visite

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.UtenteRepository
import com.example.medicareapp.data.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.medicareapp.data.repository.VisitaDataSource
import com.example.medicareapp.data.repository.UtenteDataSource
import com.example.medicareapp.data.repository.AuthDataSource

class VisitePazienteViewModel(
    private val visitaRepository: VisitaDataSource =
        VisitaRepository(),
    private val utenteRepository: UtenteDataSource =
        UtenteRepository(),
    private val authRepository: AuthDataSource =
        AuthRepository()
) : ViewModel() {

    private val _visite = MutableStateFlow<List<Visita>>(emptyList())
    val visite: StateFlow<List<Visita>> = _visite.asStateFlow()

    private val _dottori = MutableStateFlow<Map<String, Utente>>(emptyMap())
    val dottori: StateFlow<Map<String, Utente>> = _dottori.asStateFlow()

    private val _caricamento = MutableStateFlow(true)
    val caricamento: StateFlow<Boolean> = _caricamento.asStateFlow()

    private val _messaggio = MutableStateFlow<String?>(null)
    val messaggio: StateFlow<String?> = _messaggio.asStateFlow()

    init {
        caricaVisite()
    }

    fun caricaVisite() {
        val idPaziente = authRepository.utenteCorrente()

        if (idPaziente == null) {
            _caricamento.value = false
            _messaggio.value = "Utente non autenticato"
            return
        }

        _caricamento.value = true
        _messaggio.value = null

        visitaRepository.getVisitePaziente(idPaziente) { listaVisite ->
            _visite.value = listaVisite
                .filter {
                    it.stato.trim().lowercase() == "prenotato"
                }
                .sortedWith(compareBy({it.data}, {it.ora}))

            caricaDottori(listaVisite)
            _caricamento.value = false
        }
    }

    private fun caricaDottori(visite: List<Visita>) {
        utenteRepository.getDottori { listaDottori ->
            val idsNecessari = visite.map { it.idDottore }.toSet()

            _dottori.value = listaDottori
                .filter { it.uid in idsNecessari }
                .associateBy { it.uid }
        }
    }

    fun cancellaVisita(visita: Visita) {
        if (visita.stato != "prenotato") {
            _messaggio.value = "Questa visita non può essere cancellata"
            return
        }

        visitaRepository.cancellaVisita(visita.idVisita) { successo, errore ->
            if (!successo) {
                _messaggio.value =
                    errore ?: "Errore durante la cancellazione"
                return@cancellaVisita
            }

            _visite.value = _visite.value.map {
                if (it.idVisita == visita.idVisita) {
                    it.copy(stato = "cancellato")
                } else {
                    it
                }
            }

            _messaggio.value = "Visita cancellata con successo"
        }
    }

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}
