
package com.example.medicareapp

import com.example.medicareapp.data.model.Utente
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UtenteTest {

    @Test
    fun utenteVieneCreatoConIValoriCorretti() {
        val utente = Utente(
            uid = "uid01",
            username = "mario.rossi",
            codiceFiscale = "RSSMRA80A01F205X",
            nome = "Mario",
            cognome = "Rossi",
            dataNascita = "01/01/1980",
            email = "mario@example.com",
            telefono = "3331234567",
            ruolo = "paziente"
        )

        assertEquals("uid01", utente.uid)
        assertEquals("mario.rossi", utente.username)
        assertEquals("RSSMRA80A01F205X", utente.codiceFiscale)
        assertEquals("Mario", utente.nome)
        assertEquals("Rossi", utente.cognome)
        assertEquals("01/01/1980", utente.dataNascita)
        assertEquals("mario@example.com", utente.email)
        assertEquals("3331234567", utente.telefono)
        assertEquals("paziente", utente.ruolo)
    }

    @Test
    fun utenteCreatoSenzaParametriHaValoriVuoti() {
        val utente = Utente()

        assertEquals("", utente.uid)
        assertEquals("", utente.username)
        assertEquals("", utente.codiceFiscale)
        assertEquals("", utente.nome)
        assertEquals("", utente.cognome)
        assertEquals("", utente.dataNascita)
        assertEquals("", utente.email)
        assertEquals("", utente.telefono)
        assertEquals("", utente.ruolo)
    }

    @Test
    fun utentiConUsernameDiversoNonSonoUguali() {
        val utente1 = Utente(username = "mario")
        val utente2 = Utente(username = "luigi")

        assertNotEquals(utente1, utente2)
    }
}
