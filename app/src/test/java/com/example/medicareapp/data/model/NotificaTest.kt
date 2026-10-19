
package com.example.medicareapp.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificaTest {

    @Test
    fun creaNotifica_conValoriPredefiniti() {
        val notifica = Notifica()

        assertEquals("", notifica.idNotifica)
        assertEquals("", notifica.idUtente)
        assertEquals("", notifica.titolo)
        assertEquals("", notifica.messaggio)
        assertEquals("", notifica.data)
        assertEquals("", notifica.tipo)
    }

    @Test
    fun creaNotifica_conTuttiICampi() {
        val notifica = Notifica(
            idNotifica = "notifica001",
            idUtente = "paziente001",
            titolo = "Visita cancellata",
            messaggio = "La tua visita è stata cancellata.",
            data = "19/10/2026 10:00",
            tipo = "cancellazione_visita"
        )

        assertEquals("notifica001", notifica.idNotifica)
        assertEquals("paziente001", notifica.idUtente)
        assertEquals("Visita cancellata", notifica.titolo)
        assertEquals(
            "La tua visita è stata cancellata.",
            notifica.messaggio
        )
        assertEquals("19/10/2026 10:00", notifica.data)
        assertEquals("cancellazione_visita", notifica.tipo)
    }

    @Test
    fun notificheConGliStessiDati_sonoUguali() {
        val notifica1 = Notifica(
            idNotifica = "notifica001",
            titolo = "Promemoria",
            messaggio = "Ricordati della visita"
        )

        val notifica2 = Notifica(
            idNotifica = "notifica001",
            titolo = "Promemoria",
            messaggio = "Ricordati della visita"
        )

        assertEquals(notifica1, notifica2)
    }
}
