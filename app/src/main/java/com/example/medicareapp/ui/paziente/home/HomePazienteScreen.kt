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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicareapp.data.model.Disponibilita
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientSectionTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
import com.example.medicareapp.ui.theme.MediCareSuccess
import com.example.medicareapp.ui.theme.MediCareSuccessLight
import java.time.YearMonth


@Composable
fun HomePazienteScreen(
    viewModel: HomePazienteViewModel
) {

    /*
     * Il ViewModel continua è l'unica fonte dei dati
     * relativi a medici, disponibilità, giorno e prenotazione.
     */

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


    /*
     * menuAperto: controlla solamente la visualizzazione del menu.
     *
     * meseVisualizzato: controlla solamente quale mese mostrare nel calendario.
     */

    var menuAperto by remember {
        mutableStateOf(false)
    }

    var meseVisualizzato by remember {
        mutableStateOf(YearMonth.now())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
    ) {

        //Titolo comune della sezione Paziente.
        PatientScreenTitle(
            title = "Prenota una visita",
            subtitle = "Scegli il medico, il giorno e l'orario che preferisci."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        //DropDown menu che permette selezione del dottore
        PatientCard {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                PatientSectionTitle(
                    title = "Medico"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            menuAperto = true
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                dottoreSelezionato?.let {
                                    "${it.nome} ${it.cognome}"
                                } ?: "Seleziona un dottore"
                        )
                    }

                    DropdownMenu(
                        expanded = menuAperto,

                        onDismissRequest = {
                            menuAperto = false
                        }
                    ) {
                        //Caso in cui non ci sono dottori salvati
                        if (dottori.isEmpty()) {

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text =
                                            "Nessun dottore disponibile"
                                    )
                                },

                                onClick = {
                                    menuAperto = false
                                }
                            )

                        } else {
                            //Nel caso ci siano si inseriscono come item del DropDownMenu con nome e cognome
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


                                         // Quando cambia medico torniamo al mese corrente.
                                        meseVisualizzato =
                                            YearMonth.now()

                                        menuAperto = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        /*
         * Mostriamo il calendario solamente quando è stato
         * selezionato un medico.
         */

        if (dottoreSelezionato != null) {

            PatientCard {

                Column(
                    modifier =
                        Modifier.padding(16.dp)
                ) {

                    PatientSectionTitle(
                        title = "Scegli il giorno"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    CalendarioPaziente(
                        mese = meseVisualizzato,

                        disponibilita =
                            disponibilita,

                        giornoSelezionato =
                            giornoSelezionato,

                        onCambioMese = { nuovoMese ->
                            meseVisualizzato = nuovoMese
                        },

                        onSelezionaGiorno = { data ->
                            viewModel.selezionaGiorno(
                                data
                            )
                        }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ORARI DISPONIBILI
            if (giornoSelezionato != null) {

                PatientSectionTitle(
                    title = "Orari disponibili"
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                if (orariDisponibili.isEmpty()) {

                    /*
                     * Nessun orario non viene considerato un errore.
                     * È semplicemente uno stato vuoto della schermata.
                     */
                    PatientStatusMessage(
                        message =
                            "Nessun orario disponibile.",
                        success = false
                    )

                } else {

                    Column {

                        orariDisponibili.forEach { ora ->
                             //disponibilità selezionabile.

                            Surface(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 4.dp
                                    )
                                    .clickable {
                                        viewModel.prenotaVisita(
                                            ora = ora
                                        ) { _, _ ->
                                        }
                                    },

                                shape =
                                    RoundedCornerShape(12.dp),

                                color =
                                    MediCareSuccessLight
                            ) {

                                Row(
                                    modifier =
                                        Modifier.padding(
                                            16.dp
                                        ),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = ora,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium,

                                        color =
                                            MediCareSuccess
                                    )
                                }
                            }
                        }
                    }
                }

                messaggioPrenotazione?.let { messaggio ->

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    PatientStatusMessage(
                        message = messaggio
                    )
                }
            }
        }
    }
}


//Calendario paziente
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
     * Lunedì = 1
     * Martedì = 2
     * ...
     * Domenica = 7
     *
     * Sottraendo 1 otteniamo l'offset necessario.
     */
    val offset =
        primoGiorno.dayOfWeek.value - 1


    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        //Header mese
        Row(
            modifier =
                Modifier.fillMaxWidth(),

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
                    text = "‹",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    color =
                        MediCareBlue
                )
            }


            Text(
                text = nomeMese(mese),

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.SemiBold
            )


            TextButton(
                onClick = {

                    onCambioMese(
                        mese.plusMonths(1)
                    )
                }
            ) {

                Text(
                    text = "›",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    color =
                        MediCareBlue
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


      //Giorni della settimana
        Row(
            modifier =
                Modifier.fillMaxWidth()
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
                        text = giorno,

                        style =
                            MaterialTheme
                                .typography
                                .labelLarge,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        //Giorni del mese
        val totaleCelle =
            offset + numeroGiorni

        val numeroRighe =
            (totaleCelle + 6) / 7


        for (riga in 0 until numeroRighe) {

            Row(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                for (colonna in 0..6) {

                    val indice =
                        riga * 7 + colonna


                    /*
                     * Celle vuote prima del primo giorno
                     * e dopo l'ultimo giorno.
                     */
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

                        val giorno =
                            indice - offset + 1

                        val data =
                            mese.atDay(giorno)

                        /*
                         * LocalDate.toString() restituisce:
                         *
                         * yyyy-MM-dd
                         *
                         * Manteniamo esattamente questo formato
                         * perché viene utilizzato dalla logica
                         * delle disponibilità.
                         */
                        val dataString =
                            data.toString()


                        //Controllo disponibilità
                        val disponibile =
                            disponibilita.any {
                                it.data == dataString
                            }


                        //Controllo selezione
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

                                /*
                                 * La data viene ancora passata
                                 * alla funzione originale.
                                 */
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


/*
 * SINGOLO GIORNO DEL CALENDARIO
 *
 * - blu = selezionato
 * - celeste = disponibile
 * - grigio = non disponibile
 */
@Composable
fun GiornoCalendario(
    modifier: Modifier = Modifier,
    giorno: Int,
    disponibile: Boolean,
    selezionato: Boolean,
    onClick: () -> Unit
) {

    val coloreSfondo = when {

        selezionato ->
            MediCareBlue

        disponibile ->
            MediCareBlueLight

        else ->
            MaterialTheme
                .colorScheme
                .surfaceVariant
    }


    val coloreTesto = when {

        selezionato ->
            MaterialTheme
                .colorScheme
                .onPrimary

        disponibile ->
            MediCareBlue

        else ->
            MaterialTheme
                .colorScheme
                .onSurfaceVariant
    }


    Box(

        modifier = modifier
            .height(50.dp)
            .padding(4.dp)

            .background(
                color = coloreSfondo,
                //Angoli arrotondati
                shape =
                    RoundedCornerShape(10.dp)
            )

            // Un giorno non disponibile non è cliccabile
            .clickable(
                enabled = disponibile,
                onClick = onClick
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = giorno.toString(),

            color = coloreTesto,

            fontWeight =
                if (selezionato) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}


//Nome del mese
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