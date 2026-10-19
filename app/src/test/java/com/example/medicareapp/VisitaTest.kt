
package com.example.medicareapp

import com.example.medicareapp.data.model.Visita
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class VisitaTest {

    @Test
    fun visitaVieneCreataConIValoriCorretti() {
        val visita = Visita(
            idVisita = "visita01",
            idPaziente = "paziente01",
            idDottore = "dottore01",
            data = "20/10/2026",
            ora = "10:30",
            stato = "prenotato"
        )

        assertEquals("visita01", visita.idVisita)
        assertEquals("paziente01", visita.idPaziente)
        assertEquals("dottore01", visita.idDottore)
        assertEquals("20/10/2026", visita.data)
        assertEquals("10:30", visita.ora)
        assertEquals("prenotato", visita.stato)
    }

    @Test
    fun visiteConIdentificativiDiversiNonSonoUguali() {
        val visita1 = Visita(idVisita = "visita01")
        val visita2 = Visita(idVisita = "visita02")

        assertNotEquals(visita1, visita2)
    }

    @Test
    fun visitaNuovaHaValoriPredefiniti() {
        val visita = Visita()

        assertEquals("", visita.idVisita)
        assertEquals("", visita.idPaziente)
        assertEquals("", visita.idDottore)
        assertEquals("", visita.data)
        assertEquals("", visita.ora)
        assertEquals("", visita.stato)
    }
}
