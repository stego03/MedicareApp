
package com.example.medicareapp.ui.dottore.indisponibilita

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.repository.IndisponibilitaDataSource
import com.example.medicareapp.data.repository.IndisponibilitaRepository
import com.example.medicareapp.data.validation.IndisponibilitaValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndisponibilitaViewModel(
    private val repository: IndisponibilitaDataSource =
        IndisponibilitaRepository()
) : ViewModel() {

    private val _indisponibilita =
        MutableStateFlow<List<Indisponibilita>>(emptyList())
    val indisponibilita: StateFlow<List<Indisponibilita>> =
        _indisponibilita.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> = _errore.asStateFlow()

    private fun dataPassata(data: String): Boolean {
        return !IndisponibilitaValidator.dataValida(data)
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
                    e.message ?: "Errore durante il caricamento delle indisponibilità"
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
                    repository.getIndisponibilitaGiorno(idDottore, data)
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il caricamento delle indisponibilità"
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

        if (!IndisponibilitaValidator.orarioValido(ora)) {
            _errore.value = "Orario non valido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo =
                    repository.aggiungiIndisponibilitaOraria(
                        idDottore,
                        data,
                        ora
                    )

                if (successo) {
                    caricaIndisponibilitaGiorno(idDottore, data)
                } else {
                    _errore.value = "Impossibile bloccare l'orario"
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il blocco dell'orario"
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

        if (!IndisponibilitaValidator.orarioValido(ora)) {
            _errore.value = "Orario non valido"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errore.value = null

            try {
                val successo =
                    repository.rimuoviIndisponibilitaOraria(
                        idDottore,
                        data,
                        ora
                    )

                if (successo) {
                    caricaIndisponibilitaGiorno(idDottore, data)
                } else {
                    _errore.value =
                        "Impossibile rendere disponibile l'orario"
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante lo sblocco dell'orario"
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
                val successo =
                    repository.aggiungiIndisponibilitaGiornata(
                        idDottore,
                        data
                    )

                if (successo) {
                    caricaIndisponibilitaGiorno(idDottore, data)
                } else {
                    _errore.value = "Impossibile bloccare la giornata"
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante il blocco della giornata"
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
                val successo =
                    repository.rimuoviIndisponibilitaGiornata(
                        idDottore,
                        data
                    )

                if (successo) {
                    caricaIndisponibilitaGiorno(idDottore, data)
                } else {
                    _errore.value =
                        "Impossibile rendere disponibile la giornata"
                }
            } catch (e: Exception) {
                _errore.value =
                    e.message ?: "Errore durante lo sblocco della giornata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        val ORARI_LAVORATIVI: List<String> =
            IndisponibilitaValidator.ORARI_LAVORATIVI
    }
}
