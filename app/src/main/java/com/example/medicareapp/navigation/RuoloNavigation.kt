
package com.example.medicareapp.navigation

/**
 * Determina la destinazione in base al ruolo dell'utente.
 * Restituisce null se il ruolo non è riconosciuto.
 */
fun destinazionePerRuolo(ruolo: String?): String? {
    return when (ruolo?.trim()?.lowercase()) {
        "paziente" -> Routes.MAIN_PAZIENTE
        "dottore" -> Routes.MAIN_DOTTORE
        else -> null
    }
}
