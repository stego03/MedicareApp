
package com.example.medicareapp

import com.example.medicareapp.data.validation.IndisponibilitaValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import org.junit.Assert.assertEquals

class IndisponibilitaValidatorTest {

    private val oggi = LocalDate.of(2026, 10, 19)

    @Test
    fun dataFuturaEValida() {
        assertTrue(
            IndisponibilitaValidator.dataValida(
                "2026-10-20", oggi
            )
        )
    }

    @Test
    fun dataOdiernaEValida() {
        assertTrue(
            IndisponibilitaValidator.dataValida(
                "2026-10-19", oggi
            )
        )
    }

    @Test
    fun dataPassataNonEValida() {
        assertFalse(
            IndisponibilitaValidator.dataValida(
                "2026-10-18", oggi
            )
        )
    }

    @Test
    fun dataConFormatoErratoNonEValida() {
        assertFalse(
            IndisponibilitaValidator.dataValida(
                "19/10/2026", oggi
            )
        )
    }

    @Test
    fun orarioLavorativoEValido() {
        assertTrue(
            IndisponibilitaValidator.orarioValido("10:00")
        )
    }

    @Test
    fun orarioNonLavorativoNonEValido() {
        assertFalse(
            IndisponibilitaValidator.orarioValido("14:00")
        )
    }


    @Test
    fun tuttiGliOrariLavorativiSonoValidi() {
        val orariAttesi = listOf(
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "15:00",
            "16:00",
            "17:00"
        )

        assertEquals(
            orariAttesi,
            IndisponibilitaValidator.ORARI_LAVORATIVI
        )

        orariAttesi.forEach { ora ->
            assertTrue(
                "L'orario $ora dovrebbe essere valido",
                IndisponibilitaValidator.orarioValido(ora)
            )
        }
    }

    @Test
    fun orarioVuotoNonEValido() {
        assertFalse(
            IndisponibilitaValidator.orarioValido("")
        )
    }

}
