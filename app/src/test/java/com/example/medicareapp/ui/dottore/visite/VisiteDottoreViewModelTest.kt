
package com.example.medicareapp.ui.dottore.visite

import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthDataSource
import com.example.medicareapp.data.repository.NotificaDataSource
import com.example.medicareapp.data.repository.UtenteDataSource
import com.example.medicareapp.data.repository.VisitaDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class VisiteDottoreViewModelTest {

    private lateinit var viewModel: VisiteDottoreViewModel

    @Before
    fun setup() {
        viewModel = VisiteDottoreViewModel(
            visitaRepository = FakeVisitaRepository(),
            notificaRepository = FakeNotificaRepository(),
            utenteRepository = FakeUtenteRepository(),
            authRepository = FakeAuthRepository()
        )
    }

    @Test
    fun utenteNonAutenticatoMostraMessaggio() {
        assertEquals(
            "Utente non autenticato",
            viewModel.messaggio.value
        )
        assertFalse(viewModel.caricamento.value)
    }

    @Test
    fun cancellazioneVisitaNonPrenotataVieneRifiutata() {
        viewModel.cancellaVisita(creaVisita(stato = "effettuato"))

        assertEquals(
            "È possibile cancellare solo le visite prenotate.",
            viewModel.messaggio.value
        )
    }

    @Test
    fun cancellazioneVisitaSenzaAutorizzazioneVieneRifiutata() {
        viewModel.cancellaVisita(creaVisita(stato = "prenotato"))

        assertEquals(
            "Non sei autorizzato a cancellare questa visita.",
            viewModel.messaggio.value
        )
    }

    @Test
    fun completamentoVisitaNonPrenotataVieneRifiutato() {
        viewModel.segnaVisitaEffettuata(creaVisita(stato = "effettuato"))

        assertEquals(
            "È possibile completare solo le visite prenotate.",
            viewModel.messaggio.value
        )
    }

    @Test
    fun completamentoVisitaSenzaAutorizzazioneVieneRifiutato() {
        viewModel.segnaVisitaEffettuata(creaVisita(stato = "prenotato"))

        assertEquals(
            "Non sei autorizzato a modificare questa visita.",
            viewModel.messaggio.value
        )
    }

    @Test
    fun pulisciMessaggioRimuoveIlMessaggio() {
        viewModel.cancellaVisita(creaVisita(stato = "effettuato"))

        viewModel.pulisciMessaggio()

        assertEquals(null, viewModel.messaggio.value)
    }

    private fun creaVisita(stato: String) = Visita(
        idVisita = "visita1",
        idPaziente = "paziente1",
        idDottore = "dottore1",
        data = "20/10/2026",
        ora = "10:00",
        stato = stato
    )

    private class FakeVisitaRepository : VisitaDataSource {

        override fun prenotaVisita(
            visita: Visita,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(true, null)
        }

        override fun getVisitePaziente(
            idPaziente: String,
            onResult: (List<Visita>) -> Unit
        ) {
            onResult(emptyList())
        }

        override fun getVisiteDottore(
            idDottore: String,
            onResult: (List<Visita>) -> Unit
        ) {
            onResult(emptyList())
        }

        override fun verificaAltreVisitePrenotate(
            visita: Visita,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(false, null)
        }

        override fun cancellaVisita(
            idVisita: String,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(true, null)
        }

        override fun segnaVisitaEffettuata(
            idVisita: String,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(true, null)
        }
    }

    private class FakeNotificaRepository : NotificaDataSource {

        override fun getNotificheUtente(
            idUtente: String,
            onResult: (List<Notifica>) -> Unit
        ) {
            onResult(emptyList())
        }

        override fun creaNotifica(
            notifica: Notifica,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(true, null)
        }

        override fun inviaNotifica(
            idPazienti: List<String>,
            titolo: String,
            messaggio: String,
            data: String,
            onResult: (Boolean, String?) -> Unit
        ) {
            onResult(true, null)
        }
    }

    private class FakeUtenteRepository : UtenteDataSource {

        override fun getDottori(onResult: (List<Utente>) -> Unit) {
            onResult(emptyList())
        }

        override fun getUtente(
            uid: String,
            onResult: (Utente?) -> Unit
        ) {
            onResult(null)
        }
    }

    private class FakeAuthRepository : AuthDataSource {

        override fun utenteCorrente(): String? = null

        override fun logout() = Unit
    }
}
