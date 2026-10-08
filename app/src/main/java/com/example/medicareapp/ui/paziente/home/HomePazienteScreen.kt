package com.example.medicareapp.ui.paziente.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.medicareapp.data.model.Disponibilita
import java.time.YearMonth


@Composable
fun HomePazienteScreen(
    viewModel: HomePazienteViewModel
) {

    // -------------------------------------------------
    // DATI DAL VIEWMODEL
    // -------------------------------------------------

    val dottori by
    viewModel.dottori.collectAsState()

    val dottoreSelezionato by
    viewModel.dottoreSelezionato.collectAsState()

    val disponibilita by
    viewModel.disponibilita.collectAsState()

    val giornoSelezionato by
    viewModel.giornoSelezionato.collectAsState()

    val orariDisponibili by
    viewModel.orariDisponibili.collectAsState()

    val messaggioPrenotazione by
    viewModel.messaggioPrenotazione.collectAsState()


    // -------------------------------------------------
    // STATO LOCALE
    // -------------------------------------------------

    var menuAperto by remember {
        mutableStateOf(false)
    }

    var meseVisualizzato by remember {
        mutableStateOf(YearMonth.now())
    }


    // -------------------------------------------------
    // SCHERMATA
    // -------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // =================================================
        // SELEZIONE DOTTORE
        // =================================================

        Text(
            text = "Seleziona dottore"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                onClick = {
                    menuAperto = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = dottoreSelezionato?.let {
                        "${it.nome} ${it.cognome}"
                    } ?: "Seleziona un dottore"
                )
            }


            // ---------------------------------------------
            // MENU DOTTORI
            // ---------------------------------------------

            DropdownMenu(
                expanded = menuAperto,
                onDismissRequest = {
                    menuAperto = false
                }
            ) {

                if (dottori.isEmpty()) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Nessun dottore disponibile"
                            )
                        },
                        onClick = {
                            menuAperto = false
                        }
                    )

                } else {

                    dottori.forEach { dottore ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text =
                                        "${dottore.nome} ${dottore.cognome}"
                                )
                            },
                            onClick = {

                                viewModel.selezionaDottore(
                                    dottore
                                )

                                // Torniamo al mese corrente
                                meseVisualizzato =
                                    YearMonth.now()

                                menuAperto = false
                            }
                        )
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =================================================
        // CALENDARIO
        // =================================================

        if (dottoreSelezionato != null) {

            CalendarioPaziente(
                mese = meseVisualizzato,
                disponibilita = disponibilita,
                giornoSelezionato = giornoSelezionato,

                onCambioMese = { nuovoMese ->

                    meseVisualizzato = nuovoMese
                },

                onSelezionaGiorno = { data ->

                    viewModel.selezionaGiorno(
                        data
                    )
                }
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // ORARI DISPONIBILI
            // =================================================

            if (giornoSelezionato != null) {

                Text(
                    text = "Orari disponibili"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                if (orariDisponibili.isEmpty()) {

                    Text(
                        text = "Nessun orario disponibile."
                    )

                } else {

                    orariDisponibili.forEach { ora ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 4.dp
                                )
                                .background(
                                    color =
                                        Color(0xFFE8F5E9),
                                    shape =
                                        RoundedCornerShape(8.dp)
                                )
                                .clickable {

                                    viewModel.prenotaVisita(
                                        ora = ora
                                    ) { _, _ ->
                                        // Il risultato viene
                                        // gestito dal ViewModel
                                    }
                                }
                                .padding(16.dp)
                        ) {

                            Text(
                                text = ora
                            )
                        }
                    }
                }


                // =================================================
                // MESSAGGIO PRENOTAZIONE
                // =================================================

                messaggioPrenotazione?.let { messaggio ->

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = messaggio
                    )
                }
            }
        }
    }
}


// =========================================================
// CALENDARIO PAZIENTE
// =========================================================

@Composable
fun CalendarioPaziente(
    mese: YearMonth,
    disponibilita: List<Disponibilita>,
    giornoSelezionato: String?,
    onCambioMese: (YearMonth) -> Unit,
    onSelezionaGiorno: (String) -> Unit
) {

    val primoGiorno =
        mese.atDay(1)

    val numeroGiorni =
        mese.lengthOfMonth()

    /*
     * dayOfWeek.value:
     *
     * Lunedì = 1
     * Martedì = 2
     * ...
     * Domenica = 7
     *
     * Sottraendo 1 otteniamo l'offset
     * necessario per il calendario.
     */

    val offset =
        primoGiorno.dayOfWeek.value - 1


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // =================================================
        // HEADER DEL MESE
        // =================================================

        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            TextButton(
                onClick = {

                    onCambioMese(
                        mese.minusMonths(1)
                    )
                }
            ) {

                Text(
                    text = "‹"
                )
            }


            Text(
                text = nomeMese(mese)
            )


            TextButton(
                onClick = {

                    onCambioMese(
                        mese.plusMonths(1)
                    )
                }
            ) {

                Text(
                    text = "›"
                )
            }
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        // =================================================
        // GIORNI DELLA SETTIMANA
        // =================================================

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            val giorniSettimana =
                listOf(
                    "Lun",
                    "Mar",
                    "Mer",
                    "Gio",
                    "Ven",
                    "Sab",
                    "Dom"
                )


            giorniSettimana.forEach { giorno ->

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = giorno
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        // =================================================
        // GIORNI DEL MESE
        // =================================================

        val totaleCelle =
            offset + numeroGiorni

        val numeroRighe =
            (totaleCelle + 6) / 7


        for (riga in 0 until numeroRighe) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                for (colonna in 0..6) {

                    val indice =
                        riga * 7 + colonna


                    // -----------------------------------------
                    // CELLA VUOTA
                    // -----------------------------------------

                    if (
                        indice < offset ||
                        indice >= totaleCelle
                    ) {

                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )

                    } else {

                        // -----------------------------------------
                        // GIORNO
                        // -----------------------------------------

                        val giorno =
                            indice - offset + 1


                        val data =
                            mese.atDay(giorno)


                        /*
                         * LocalDate.toString()
                         * restituisce:
                         *
                         * yyyy-MM-dd
                         *
                         * Esempio:
                         * 2026-10-15
                         */

                        val dataString =
                            data.toString()


                        // -----------------------------------------
                        // CONTROLLO DISPONIBILITÀ
                        // -----------------------------------------

                        val disponibile =
                            disponibilita.any {
                                it.data == dataString
                            }


                        // -----------------------------------------
                        // CONTROLLO SELEZIONE
                        // -----------------------------------------

                        val selezionato =
                            giornoSelezionato ==
                                    dataString


                        GiornoCalendario(
                            modifier =
                                Modifier.weight(1f),

                            giorno =
                                giorno,

                            disponibile =
                                disponibile,

                            selezionato =
                                selezionato,

                            onClick = {

                                onSelezionaGiorno(
                                    dataString
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


// =========================================================
// SINGOLO GIORNO DEL CALENDARIO
// =========================================================

@Composable
fun GiornoCalendario(
    modifier: Modifier = Modifier,
    giorno: Int,
    disponibile: Boolean,
    selezionato: Boolean,
    onClick: () -> Unit
) {

    /*
     * Colore dello sfondo:
     *
     * Blu   = giorno selezionato
     * Verde = giorno disponibile
     * Rosso = giorno non disponibile
     */

    val coloreSfondo = when {

        selezionato ->
            Color(0xFF1976D2)

        disponibile ->
            Color(0xFFE8F5E9)

        else ->
            Color(0xFFFFEBEE)
    }


    /*
     * Colore del numero:
     */

    val coloreTesto = when {

        selezionato ->
            Color.White

        disponibile ->
            Color(0xFF2E7D32)

        else ->
            Color.Red
    }


    Box(
        modifier = modifier
            .height(50.dp)
            .padding(4.dp)
            .background(
                color = coloreSfondo,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = disponibile,
                onClick = onClick
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = giorno.toString(),
            color = coloreTesto
        )
    }
}


// =========================================================
// NOME DEL MESE
// =========================================================

fun nomeMese(
    mese: YearMonth
): String {

    return when (mese.monthValue) {

        1 ->
            "Gennaio ${mese.year}"

        2 ->
            "Febbraio ${mese.year}"

        3 ->
            "Marzo ${mese.year}"

        4 ->
            "Aprile ${mese.year}"

        5 ->
            "Maggio ${mese.year}"

        6 ->
            "Giugno ${mese.year}"

        7 ->
            "Luglio ${mese.year}"

        8 ->
            "Agosto ${mese.year}"

        9 ->
            "Settembre ${mese.year}"

        10 ->
            "Ottobre ${mese.year}"

        11 ->
            "Novembre ${mese.year}"

        12 ->
            "Dicembre ${mese.year}"

        else ->
            "${mese.month} ${mese.year}"
    }
}

