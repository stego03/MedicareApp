package com.example.medicareapp.ui.paziente.notifiche

import androidx.lifecycle.ViewModel
import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.data.repository.AuthRepository
import com.example.medicareapp.data.repository.NotificaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotifichePazienteViewModel : ViewModel() {

    private val notificaRepository = NotificaRepository()
    private val authRepository = AuthRepository()

    private val _notifiche =
        MutableStateFlow<List<Notifica>>(emptyList())

    val notifiche: StateFlow<List<Notifica>> =
        _notifiche.asStateFlow()

    private val _caricamento =
        MutableStateFlow(true)

    val caricamento: StateFlow<Boolean> =
        _caricamento.asStateFlow()

    private val _messaggio =
        MutableStateFlow<String?>(null)

    val messaggio: StateFlow<String?> =
        _messaggio.asStateFlow()

    init {
        caricaNotifiche()
    }

    fun caricaNotifiche() {
        val idUtente = authRepository.utenteCorrente()

        if (idUtente == null) {
            _caricamento.value = false
            _messaggio.value = "Utente non autenticato"
            return
        }

        _caricamento.value = true
        _messaggio.value = null

        notificaRepository.getNotificheUtente(
            idUtente = idUtente
        ) { listaNotifiche ->
            _notifiche.value = listaNotifiche
            _caricamento.value = false
        }
    }
}
