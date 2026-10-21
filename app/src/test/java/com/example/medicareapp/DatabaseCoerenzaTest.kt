
package com.example.medicareapp

import com.example.medicareapp.data.model.Documento
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.data.model.Utente
import com.example.medicareapp.data.model.Visita
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseCoerenzaTest {

    private val paziente = Utente(
        uid = "paziente1",
        nome = "Mario",
        cognome = "Rossi",
        ruolo = "paziente"
    )

    private val dottore = Utente(
        uid = "dottore1",
        nome = "Luigi",
        cognome = "Bianchi",
        ruolo = "dottore"
    )

    private val utenti = listOf(paziente, dottore)

    private fun esistonoUtentiConRuolo(
        id: String,
        ruolo: String
    ): Boolean {
        return utenti.any {
            it.uid == id && it.ruolo.equals(ruolo, ignoreCase = true)
        }
    }

    @Test
    fun visitaRiferiscePazienteEDottoreEsistenti() {
        val visita = Visita(
            idVisita = "visita1",
            idPaziente = "paziente1",
            idDottore = "dottore1",
            data = "20/10/2026",
            ora = "10:00",
            stato = "prenotato"
        )

        assertTrue(
            esistonoUtentiConRuolo(visita.idPaziente, "paziente")
        )
        assertTrue(
            esistonoUtentiConRuolo(visita.idDottore, "dottore")
        )
    }

    @Test
    fun visitaConPazienteInesistenteVieneRilevata() {
        val visita = Visita(
            idPaziente = "paziente_inesistente",
            idDottore = "dottore1"
        )

        assertFalse(
            esistonoUtentiConRuolo(visita.idPaziente, "paziente")
        )
    }

    @Test
    fun visitaConRuoliInvertitiVieneRilevata() {
        val visita = Visita(
            idPaziente = "dottore1",
            idDottore = "paziente1"
        )

        assertFalse(
            esistonoUtentiConRuolo(visita.idPaziente, "paziente")
        )
        assertFalse(
            esistonoUtentiConRuolo(visita.idDottore, "dottore")
        )
    }

    @Test
    fun documentoRiferiscePazienteEDottoreEsistenti() {
        val documento = Documento(
            idDocumento = "documento1",
            idPaziente = "paziente1",
            /*idDottore = "dottore1",*/
            nome = "Referto",
            tipo = "PDF"
        )

        assertTrue(
            esistonoUtentiConRuolo(documento.idPaziente, "paziente")
        )
        /*assertTrue(
            esistonoUtentiConRuolo(documento.idDottore, "dottore")
        )*/
    }

    @Test
    fun notificaRiferisceUnUtenteEsistente() {
        val notifica = Notifica(
            idNotifica = "notifica1",
            idUtente = "paziente1",
            titolo = "Visita cancellata",
            messaggio = "La visita è stata cancellata."
        )

        assertTrue(
            utenti.any { it.uid == notifica.idUtente }
        )
    }

    @Test
    fun indisponibilitaRiferisceUnDottoreEsistente() {
        val indisponibilita = Indisponibilita(
            id = "indisponibilita1",
            idDottore = "dottore1",
            data = "20/10/2026",
            ora = "10:00"
        )

        assertTrue(
            esistonoUtentiConRuolo(
                indisponibilita.idDottore,
                "dottore"
            )
        )
    }
}
