
package com.example.medicareapp.ui.paziente.areaPersonale

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.UtenteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AreaPersonaleViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val utenteRepository = UtenteRepository()

    private val _utente = MutableStateFlow<Utente?>(null)
    val utente: StateFlow<Utente?> = _utente.asStateFlow()

    private val _caricamento = MutableStateFlow(true)
    val caricamento: StateFlow<Boolean> = _caricamento.asStateFlow()

    private val _salvataggio = MutableStateFlow(false)
    val salvataggio: StateFlow<Boolean> = _salvataggio.asStateFlow()

    private val _messaggio = MutableStateFlow<String?>(null)
    val messaggio: StateFlow<String?> = _messaggio.asStateFlow()

    init {
        caricaUtente()
    }

    fun caricaUtente() {

        val uid = authRepository.utenteCorrente()

        if (uid == null) {
            _caricamento.value = false
            _messaggio.value = "Utente non autenticato."
            return
        }

        _caricamento.value = true
        _messaggio.value = null

        utenteRepository.getUtente(uid) { risultato ->

            _utente.value = risultato
            _caricamento.value = false

            if (risultato == null) {
                _messaggio.value =
                    "Impossibile caricare i dati personali."
            }
        }
    }

    fun salvaModifiche(
        nome: String,
        cognome: String,
        dataNascita: String,
        email: String,
        telefono: String
    ) {

        val utenteCorrente = _utente.value

        if (utenteCorrente == null) {
            _messaggio.value =
                "Dati utente non disponibili."
            return
        }

        val uid = authRepository.utenteCorrente()

        if (uid == null) {
            _messaggio.value =
                "Utente non autenticato."
            return
        }

        if (nome.isBlank()) {
            _messaggio.value =
                "Il nome è obbligatorio."
            return
        }

        if (cognome.isBlank()) {
            _messaggio.value =
                "Il cognome è obbligatorio."
            return
        }

        if (dataNascita.isBlank()) {
            _messaggio.value =
                "La data di nascita è obbligatoria."
            return
        }

        if (email.isBlank()) {
            _messaggio.value =
                "L'email è obbligatoria."
            return
        }

        val utenteAggiornato = utenteCorrente.copy(
            nome = nome.trim(),
            cognome = cognome.trim(),
            dataNascita = dataNascita.trim(),
            email = email.trim(),
            telefono = telefono.trim()
        )

        _salvataggio.value = true
        _messaggio.value = null

        utenteRepository.aggiornaUtente(
            uid = uid,
            utente = utenteAggiornato
        ) { successo, errore ->

            _salvataggio.value = false

            if (successo) {

                _utente.value = utenteAggiornato

                _messaggio.value =
                    "Dati aggiornati correttamente."

            } else {

                _messaggio.value =
                    errore
                        ?: "Errore durante il salvataggio."
            }
        }
    }

    fun pulisciMessaggio() {
        _messaggio.value = null
    }
}

