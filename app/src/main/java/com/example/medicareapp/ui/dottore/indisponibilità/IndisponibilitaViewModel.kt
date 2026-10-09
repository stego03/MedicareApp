
package com.example.medicareapp.ui.dottore.indisponibilita

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.repository.IndisponibilitaRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndisponibilitaViewModel : ViewModel() {

    private val repository = IndisponibilitaRepository()

    private val _indisponibilita =
        MutableStateFlow<List<Indisponibilita>>(emptyList())
    val indisponibilita: StateFlow<List<Indisponibilita>> =
        _indisponibilita.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> = _errore.asStateFlow()

    private fun dataPassata(data: String): Boolean {
        return try {
            LocalDate.parse(data).isBefore(LocalDate.now())
        } catch (e: Exception) {
            true
        }
    }

    fun caricaIndisponibilita(idDottore: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                _indisponibilita.value =
                    repository.getIndisponibilitaDottore(idDottore)
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il caricamento"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun caricaIndisponibilitaGiorno(
        idDottore: String,
        data: String
    ) {
        if (dataPassata(data)) {
            _errore.value = "Non puoi modificare un giorno passato"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                _indisponibilita.value =
                    repository.getIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il caricamento"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bloccaOrario(
        idDottore: String,
        data: String,
        ora: String
    ) {
        if (dataPassata(data)) {
            _errore.value = "Non puoi modificare un giorno passato"
            return
        }

        if (ora !in ORARI_LAVORATIVI) {
            _errore.value = "Orario non valido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo = repository.aggiungiIndisponibilitaOraria(
                    idDottore = idDottore,
                    data = data,
                    ora = ora
                )

                if (!successo) {
                    _errore.value = "Impossibile bloccare l'orario"
                } else {
                    caricaIndisponibilitaGiorno(idDottore, data)
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il salvataggio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sbloccaOrario(
        idDottore: String,
        data: String,
        ora: String
    ) {
        if (dataPassata(data)) {
            _errore.value = "Non puoi modificare un giorno passato"
            return
        }

        if (ora !in ORARI_LAVORATIVI) {
            _errore.value = "Orario non valido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo = repository.rimuoviIndisponibilitaOraria(
                    idDottore = idDottore,
                    data = data,
                    ora = ora
                )

                if (!successo) {
                    _errore.value =
                        "Impossibile rendere disponibile l'orario"
                } else {
                    caricaIndisponibilitaGiorno(idDottore, data)
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il salvataggio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bloccaGiornata(
        idDottore: String,
        data: String
    ) {
        if (dataPassata(data)) {
            _errore.value = "Non puoi modificare un giorno passato"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo = repository.aggiungiIndisponibilitaGiornata(
                    idDottore = idDottore,
                    data = data
                )

                if (!successo) {
                    _errore.value = "Impossibile bloccare la giornata"
                } else {
                    caricaIndisponibilitaGiorno(idDottore, data)
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il salvataggio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sbloccaGiornata(
        idDottore: String,
        data: String
    ) {
        if (dataPassata(data)) {
            _errore.value = "Non puoi modificare un giorno passato"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo = repository.rimuoviIndisponibilitaGiornata(
                    idDottore = idDottore,
                    data = data
                )

                if (!successo) {
                    _errore.value =
                        "Impossibile rendere disponibile la giornata"
                } else {
                    caricaIndisponibilitaGiorno(idDottore, data)
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il salvataggio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrore() {
        _errore.value = null
    }

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
}
