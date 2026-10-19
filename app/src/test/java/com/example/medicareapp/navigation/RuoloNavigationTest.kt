
package com.example.medicareapp.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RuoloNavigationTest {

    @Test
    fun ruoloPazienteApreAreaPaziente() {
        assertEquals(
            Routes.MAIN_PAZIENTE,
            destinazionePerRuolo("paziente")
        )
    }

    @Test
    fun ruoloDottoreApreAreaDottore() {
        assertEquals(
            Routes.MAIN_DOTTORE,
            destinazionePerRuolo("dottore")
        )
    }

    @Test
    fun ruoloConSpaziEMaiuscoleVieneRiconosciuto() {
        assertEquals(
            Routes.MAIN_PAZIENTE,
            destinazionePerRuolo("  PAZIENTE  ")
        )
    }

    @Test
    fun ruoloNulloNonHaDestinazione() {
        assertNull(destinazionePerRuolo(null))
    }

    @Test
    fun ruoloSconosciutoNonHaDestinazione() {
        assertNull(destinazionePerRuolo("amministratore"))
    }

    @Test
    fun ruoloVuotoNonHaDestinazione() {
        assertNull(destinazionePerRuolo(""))
    }
}
