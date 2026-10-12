package com.example.medicareapp.ui.dottore.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.UtenteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

import com.example.medicareapp.data.repository.VisitaRepository
import java.time.LocalDate


class HomeDottoreViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val utenteRepository = UtenteRepository()

    private val visitaRepository = VisitaRepository()

    private val _utente = MutableStateFlow<Utente?>(null)
    val utente: StateFlow<Utente?> = _utente.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> = _errore.asStateFlow()

    private val _visiteOggi = MutableStateFlow(0)
    val visiteOggi: StateFlow<Int> = _visiteOggi.asStateFlow()

    private val _numeroPazienti = MutableStateFlow(0)
    val numeroPazienti: StateFlow<Int> = _numeroPazienti.asStateFlow()

    init {
        caricaDatiDottore()
    }

    fun caricaDatiDottore() {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                val uid = authRepository.utenteCorrente()

                if (uid == null) {

                    _errore.value =
                        "Nessun utente autenticato"

                    return@launch
                }

                val risultato =
                    suspendCancellableCoroutine<Utente?> { continuation ->

                        utenteRepository.getUtente(uid) { utente ->

                            if (continuation.isActive) {
                                continuation.resume(utente)
                            }
                        }
                    }

                if (risultato == null) {

                    _errore.value =
                        "Impossibile recuperare i dati del dottore"

                } else {

                    _utente.value = risultato
                    caricaStatistiche(risultato.uid)
                }

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il caricamento"

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun clearErrore() {
        _errore.value = null
    }

    private fun caricaStatistiche(idDottore: String) {

        visitaRepository.getVisiteDottore(idDottore) { visite ->

            val oggi = LocalDate.now().toString()

            //Si prendono visite non cancellate e del giorno stesso
            _visiteOggi.value = visite.count {
                it.data == oggi &&
                        !it.stato.equals("cancellato", ignoreCase = true)
            }

            //Si prendono pazienti associati alle visite del dottore (no duplicati -> toSet())
            _numeroPazienti.value = visite
                .filter {
                    !it.stato.equals("cancellato", ignoreCase = true)
                }
                .map { it.idPaziente }
                .filter { it.isNotBlank() }
                .toSet()
                .size
        }
    }
}