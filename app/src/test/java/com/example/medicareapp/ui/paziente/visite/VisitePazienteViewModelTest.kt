
package com.example.medicareapp.ui.paziente.visite

import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import com.example.medicareapp.data.repository.AuthDataSource
import com.example.medicareapp.data.repository.UtenteDataSource
import com.example.medicareapp.data.repository.VisitaDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VisitePazienteViewModelTest {

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

    private class FakeUtenteRepository : UtenteDataSource {
        override fun getDottori(
            onResult: (List<Utente>) -> Unit
        ) {
            onResult(emptyList())
        }

        override fun getUtente(
            uid: String,
            onResult: (Utente?) -> Unit
        ) {
            onResult(null)
        }
    }

    private class FakeAuthRepository(
        private val uid: String? = null
    ) : AuthDataSource {
        override fun utenteCorrente(): String? = uid

        override fun logout() = Unit
    }

    private fun creaViewModel(): VisitePazienteViewModel {
        return VisitePazienteViewModel(
            visitaRepository = FakeVisitaRepository(),
            utenteRepository = FakeUtenteRepository(),
            authRepository = FakeAuthRepository()
        )
    }

    @Test
    fun statoIniziale_haListeVuoteEMessaggioUtenteNonAutenticato() {
        val viewModel = creaViewModel()

        assertEquals(emptyList<Visita>(), viewModel.visite.value)
        assertEquals(emptyMap<String, Utente>(), viewModel.dottori.value)
        assertEquals("Utente non autenticato", viewModel.messaggio.value)
    }

    @Test
    fun cancellaVisita_nonPrenotata_mostraErrore() {
        val viewModel = creaViewModel()

        val visita = Visita(
            idVisita = "visita001",
            stato = "effettuato"
        )

        viewModel.cancellaVisita(visita)

        assertEquals(
            "Questa visita non può essere cancellata",
            viewModel.messaggio.value
        )
    }

    @Test
    fun pulisciMessaggio_rimuoveMessaggio() {
        val viewModel = creaViewModel()

        viewModel.cancellaVisita(
            Visita(
                idVisita = "visita001",
                stato = "effettuato"
            )
        )

        viewModel.pulisciMessaggio()

        assertNull(viewModel.messaggio.value)
    }
}
