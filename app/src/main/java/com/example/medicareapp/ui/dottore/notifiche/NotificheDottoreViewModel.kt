
package com.example.medicareapp.ui.dottore.notifiche

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.repository.NotificaRepository
import com.example.medicareapp.data.repository.UtenteRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificheDottoreViewModel : ViewModel() {

    private val utenteRepository = UtenteRepository()
    private val notificaRepository = NotificaRepository()

    private val _pazienti = MutableStateFlow<List<Utente>>(emptyList())
    val pazienti = _pazienti.asStateFlow()

    private val _idPazienteSelezionato =
        MutableStateFlow("TUTTI")
    val idPazienteSelezionato =
        _idPazienteSelezionato.asStateFlow()

    private val _titolo = MutableStateFlow("")
    val titolo = _titolo.asStateFlow()

    private val _messaggio = MutableStateFlow("")
    val messaggio = _messaggio.asStateFlow()

    private val _caricamento = MutableStateFlow(false)
    val caricamento = _caricamento.asStateFlow()

    private val _esito = MutableStateFlow<String?>(null)
    val esito = _esito.asStateFlow()

    init {
        caricaPazienti()
    }

    fun caricaPazienti() {
        utenteRepository.getPazienti { lista ->
            _pazienti.value = lista
        }
    }

    fun aggiornaTitolo(valore: String) {
        _titolo.value = valore
        _esito.value = null
    }

    fun aggiornaMessaggio(valore: String) {
        _messaggio.value = valore
        _esito.value = null
    }

    fun selezionaPaziente(id: String) {
        _idPazienteSelezionato.value = id
        _esito.value = null
    }

    fun inviaNotifica() {
        val titolo = _titolo.value.trim()
        val messaggio = _messaggio.value.trim()
        val selezione = _idPazienteSelezionato.value

        if (titolo.isBlank()) {
            _esito.value = "Inserisci il titolo della notifica."
            return
        }

        if (messaggio.isBlank()) {
            _esito.value = "Inserisci il testo della notifica."
            return
        }

        val destinatari = if (selezione == "TUTTI") {
            _pazienti.value.map { it.uid }
        } else {
            listOf(selezione)
        }

        if (destinatari.isEmpty()) {
            _esito.value = "Non sono presenti pazienti registrati."
            return
        }

        _caricamento.value = true
        _esito.value = null

        val data = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        notificaRepository.inviaNotifica(
            idPazienti = destinatari,
            titolo = titolo,
            messaggio = messaggio,
            data = data
        ) { successo, errore ->
            _caricamento.value = false

            if (successo) {
                _titolo.value = ""
                _messaggio.value = ""
                _esito.value = "Notifica inviata correttamente."
            } else {
                _esito.value =
                    errore ?: "Impossibile inviare la notifica."
            }
        }
    }
}
