
package com.example.medicareapp

import com.example.medicareapp.data.model.Indisponibilita
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IndisponibilitaTest {

    @Test
    fun indisponibilitaVieneCreataConIValoriCorretti() {
        val indisponibilita = Indisponibilita(
            id = "ind01",
            idDottore = "dottore01",
            data = "2026-10-20",
            ora = "10:30",
            tuttoGiorno = false
        )

        assertEquals("ind01", indisponibilita.id)
        assertEquals("dottore01", indisponibilita.idDottore)
        assertEquals("2026-10-20", indisponibilita.data)
        assertEquals("10:30", indisponibilita.ora)
        assertFalse(indisponibilita.tuttoGiorno)
    }

    @Test
    fun indisponibilitaDiUnInteroGiornoVieneRiconosciuta() {
        val indisponibilita = Indisponibilita(
            idDottore = "dottore01",
            data = "2026-10-20",
            tuttoGiorno = true
        )

        assertTrue(indisponibilita.tuttoGiorno)
    }

    @Test
    fun indisponibilitaSenzaParametriHaValoriPredefiniti() {
        val indisponibilita = Indisponibilita()

        assertEquals("", indisponibilita.id)
        assertEquals("", indisponibilita.idDottore)
        assertEquals("", indisponibilita.data)
        assertEquals("", indisponibilita.ora)
        assertFalse(indisponibilita.tuttoGiorno)
    }
}
