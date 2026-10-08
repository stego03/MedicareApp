package com.example.medicareapp.ui.dottore.indisponibilita
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.repository.IndisponibilitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndisponibilitaViewModel : ViewModel() {

    private val repository = IndisponibilitaRepository()

    private val _indisponibilita = MutableStateFlow<List<Indisponibilita>>(emptyList())
    val indisponibilita: StateFlow<List<Indisponibilita>> =
        _indisponibilita.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errore = MutableStateFlow<String?>(null)
    val errore: StateFlow<String?> =
        _errore.asStateFlow()

    /**
     * Carica tutte le indisponibilità del dottore.
     */
    fun caricaIndisponibilita(idDottore: String) {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                _indisponibilita.value =
                    repository.getIndisponibilitaDottore(
                        idDottore
                    )

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il caricamento"

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Carica le indisponibilità di un determinato giorno.
     */
    fun caricaIndisponibilitaGiorno(
        idDottore: String,
        data: String
    ) {

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

    /**
     * Blocca un singolo orario.
     */
    fun bloccaOrario(
        idDottore: String,
        data: String,
        ora: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                val successo =
                    repository.aggiungiIndisponibilitaOraria(
                        idDottore = idDottore,
                        data = data,
                        ora = ora
                    )

                if (!successo) {

                    _errore.value =
                        "Impossibile bloccare l'orario"

                } else {

                    caricaIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )
                }

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il salvataggio"

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Rende nuovamente disponibile un singolo orario.
     */
    fun sbloccaOrario(
        idDottore: String,
        data: String,
        ora: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                val successo =
                    repository.rimuoviIndisponibilitaOraria(
                        idDottore = idDottore,
                        data = data,
                        ora = ora
                    )

                if (!successo) {

                    _errore.value =
                        "Impossibile rendere disponibile l'orario"

                } else {

                    caricaIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )
                }

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il salvataggio"

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Blocca l'intera giornata.
     */
    fun bloccaGiornata(
        idDottore: String,
        data: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                val successo =
                    repository.aggiungiIndisponibilitaGiornata(
                        idDottore = idDottore,
                        data = data
                    )

                if (!successo) {

                    _errore.value =
                        "Impossibile bloccare la giornata"

                } else {

                    caricaIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )
                }

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il salvataggio"

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Rende nuovamente disponibile l'intera giornata.
     */
    fun sbloccaGiornata(
        idDottore: String,
        data: String
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errore.value = null

            try {

                val successo =
                    repository.rimuoviIndisponibilitaGiornata(
                        idDottore = idDottore,
                        data = data
                    )

                if (!successo) {

                    _errore.value =
                        "Impossibile rendere disponibile la giornata"

                } else {

                    caricaIndisponibilitaGiorno(
                        idDottore = idDottore,
                        data = data
                    )
                }

            } catch (e: Exception) {

                _errore.value =
                    e.message ?: "Errore durante il salvataggio"

            } finally {

                _isLoading.value = false
            }
        }
    }

    /**
     * Cancella l'errore corrente.
     */
    fun clearErrore() {
        _errore.value = null
    }
}