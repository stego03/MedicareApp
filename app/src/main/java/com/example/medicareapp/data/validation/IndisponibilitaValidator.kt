
package com.example.medicareapp.data.validation

import java.time.LocalDate

object IndisponibilitaValidator {

    val ORARI_LAVORATIVI = listOf(
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "15:00",
        "16:00",
        "17:00"
    )

    fun dataValida(
        data: String,
        oggi: LocalDate = LocalDate.now()
    ): Boolean {
        return try {
            !LocalDate.parse(data).isBefore(oggi)
        } catch (e: Exception) {
            false
        }
    }

    fun orarioValido(ora: String): Boolean {
        return ora in ORARI_LAVORATIVI
    }
}
