package com.example.medicareapp.ui.dottore.indisponibilita

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun IndisponibilitaScreen(
    viewModel: IndisponibilitaViewModel = viewModel()
) {

    val indisponibilita by
    viewModel.indisponibilita.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val errore by
    viewModel.errore.collectAsState()

    val idDottore =
        FirebaseAuth.getInstance()
            .currentUser
            ?.uid

    // =================================================
    // CALENDARIO
    // =================================================

    var meseVisualizzato by remember {
        mutableStateOf(
            Calendar.getInstance()
        )
    }

    var giornoSelezionato by remember {
        mutableStateOf(
            Calendar.getInstance()
        )
    }

    // =================================================
    // DIALOG ORARIO
    // =================================================

    var mostraDialogOrario by remember {
        mutableStateOf(false)
    }

    var oraSelezionata by remember {
        mutableStateOf<String?>(null)
    }

    var azioneOrario by remember {
        mutableStateOf<String?>(null)
    }

    // =================================================
    // DIALOG GIORNATA
    // =================================================

    var mostraDialogGiornata by remember {
        mutableStateOf(false)
    }

    var azioneGiornata by remember {
        mutableStateOf<String?>(null)
    }

    // =================================================
    // FORMATTAZIONE
    // =================================================

    val formatoData =
        remember {
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )
        }

    val formatoMese =
        remember {
            SimpleDateFormat(
                "MMMM yyyy",
                Locale.ITALIAN
            )
        }

    val dataSelezionata =
        formatoData.format(
            giornoSelezionato.time
        )

    // =================================================
    // CARICAMENTO AUTOMATICO
    // =================================================

    LaunchedEffect(
        idDottore,
        dataSelezionata
    ) {

        if (idDottore != null) {

            viewModel.caricaIndisponibilitaGiorno(
                idDottore = idDottore,
                data = dataSelezionata
            )
        }
    }

    // =================================================
    // GIORNI DEL MESE
    // =================================================

    val giorniMese =
        remember(meseVisualizzato) {

            val calendario =
                meseVisualizzato.clone()
                        as Calendar

            calendario.set(
                Calendar.DAY_OF_MONTH,
                1
            )

            val primoGiorno =
                calendario.get(
                    Calendar.DAY_OF_WEEK
                )

            val offset =
                (
                        primoGiorno -
                                Calendar.MONDAY +
                                7
                        ) % 7

            val numeroGiorni =
                calendario.getActualMaximum(
                    Calendar.DAY_OF_MONTH
                )

            val lista =
                mutableListOf<Int?>()

            repeat(offset) {
                lista.add(null)
            }

            for (
            giorno in 1..numeroGiorni
            ) {
                lista.add(giorno)
            }

            lista
        }

    // =================================================
    // GIORNATA BLOCCATA
    // =================================================

    val giornataBloccata =
        indisponibilita.any {

            it.data ==
                    dataSelezionata &&
                    it.tuttoGiorno
        }

    // =================================================
    // ORARI STANDARD
    // =================================================

    val orari =
        listOf(
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "15:00",
            "16:00",
            "17:00"
        )

    // =================================================
    // DIALOG ORARIO
    // =================================================

    if (
        mostraDialogOrario &&
        oraSelezionata != null &&
        azioneOrario != null
    ) {

        val ora =
            oraSelezionata!!

        val bloccare =
            azioneOrario == "blocca"

        AlertDialog(

            onDismissRequest = {

                mostraDialogOrario = false
                oraSelezionata = null
                azioneOrario = null
            },

            title = {

                Text(
                    text =
                        if (bloccare)
                            "Blocca orario"
                        else
                            "Rendi disponibile l'orario"
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            if (bloccare) {

                                "Vuoi rendere non disponibile " +
                                        "l'orario " +
                                        "$ora - ${calcolaOraFine(ora)}?"

                            } else {

                                "Vuoi rendere nuovamente " +
                                        "disponibile l'orario " +
                                        "$ora - ${calcolaOraFine(ora)}?"
                            }
                    )

                    if (bloccare) {

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Attenzione: se è presente " +
                                        "una visita prenotata in " +
                                        "questo orario, la visita " +
                                        "verrà cancellata."
                        )
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (idDottore != null) {

                            if (bloccare) {

                                viewModel.bloccaOrario(
                                    idDottore =
                                        idDottore,
                                    data =
                                        dataSelezionata,
                                    ora =
                                        ora
                                )

                            } else {

                                viewModel.sbloccaOrario(
                                    idDottore =
                                        idDottore,
                                    data =
                                        dataSelezionata,
                                    ora =
                                        ora
                                )
                            }
                        }

                        mostraDialogOrario = false
                        oraSelezionata = null
                        azioneOrario = null
                    }
                ) {

                    Text(
                        text = "Conferma"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostraDialogOrario = false
                        oraSelezionata = null
                        azioneOrario = null
                    }
                ) {

                    Text(
                        text = "Annulla"
                    )
                }
            }
        )
    }

    // =================================================
    // DIALOG GIORNATA
    // =================================================

    if (
        mostraDialogGiornata &&
        azioneGiornata != null
    ) {

        val bloccare =
            azioneGiornata == "blocca"

        AlertDialog(

            onDismissRequest = {

                mostraDialogGiornata = false
                azioneGiornata = null
            },

            title = {

                Text(
                    text =
                        if (bloccare)
                            "Blocca giornata"
                        else
                            "Rendi disponibile la giornata"
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            if (bloccare) {

                                "Vuoi rendere non disponibile " +
                                        "l'intera giornata?"

                            } else {

                                "Vuoi rendere nuovamente " +
                                        "disponibile l'intera giornata?"
                            }
                    )

                    if (bloccare) {

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Attenzione: tutte le visite " +
                                        "prenotate per questa giornata " +
                                        "verranno cancellate."
                        )
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (idDottore != null) {

                            if (bloccare) {

                                viewModel.bloccaGiornata(
                                    idDottore =
                                        idDottore,
                                    data =
                                        dataSelezionata
                                )

                            } else {

                                viewModel.sbloccaGiornata(
                                    idDottore =
                                        idDottore,
                                    data =
                                        dataSelezionata
                                )
                            }
                        }

                        mostraDialogGiornata = false
                        azioneGiornata = null
                    }
                ) {

                    Text(
                        text = "Conferma"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostraDialogGiornata = false
                        azioneGiornata = null
                    }
                ) {

                    Text(
                        text = "Annulla"
                    )
                }
            }
        )
    }

    // =================================================
    // SCHERMATA
    // =================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        Text(
            text = "Disponibilità",
            style =
                MaterialTheme
                    .typography
                    .headlineMedium,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        // =================================================
        // MESE
        // =================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Button(
                onClick = {

                    val nuovoMese =
                        meseVisualizzato
                            .clone() as Calendar

                    nuovoMese.add(
                        Calendar.MONTH,
                        -1
                    )

                    meseVisualizzato =
                        nuovoMese
                }
            ) {

                Text("‹")
            }

            Text(
                text =
                    formatoMese
                        .format(
                            meseVisualizzato.time
                        )
                        .replaceFirstChar {
                            it.uppercase()
                        },
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Button(
                onClick = {

                    val nuovoMese =
                        meseVisualizzato
                            .clone() as Calendar

                    nuovoMese.add(
                        Calendar.MONTH,
                        1
                    )

                    meseVisualizzato =
                        nuovoMese
                }
            ) {

                Text("›")
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // GIORNI SETTIMANA
        // =================================================

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            listOf(
                "L",
                "M",
                "M",
                "G",
                "V",
                "S",
                "D"
            ).forEach { giorno ->

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(2.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = giorno,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        // =================================================
        // CALENDARIO
        // =================================================

        LazyVerticalGrid(
            columns =
                GridCells.Fixed(7),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(230.dp)
        ) {

            items(giorniMese) { giorno ->

                if (giorno == null) {

                    Box(
                        modifier =
                            Modifier.size(48.dp)
                    )

                } else {

                    val calendarioGiorno =
                        meseVisualizzato
                            .clone() as Calendar

                    calendarioGiorno.set(
                        Calendar.DAY_OF_MONTH,
                        giorno
                    )

                    val dataGiorno =
                        formatoData.format(
                            calendarioGiorno.time
                        )

                    val selezionato =
                        dataGiorno ==
                                dataSelezionata

                    val oggi =
                        formatoData.format(
                            Calendar.getInstance()
                                .time
                        ) == dataGiorno

                    Box(
                        modifier =
                            Modifier
                                .padding(2.dp)
                                .size(44.dp)
                                .clip(
                                    RoundedCornerShape(
                                        12.dp
                                    )
                                )
                                .background(
                                    if (selezionato)
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                    else
                                        MaterialTheme
                                            .colorScheme
                                            .surfaceVariant
                                )
                                .clickable {

                                    giornoSelezionato =
                                        calendarioGiorno
                                },
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                giorno.toString(),
                            color =
                                if (selezionato)
                                    MaterialTheme
                                        .colorScheme
                                        .onPrimary
                                else
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                            fontWeight =
                                if (oggi)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // GIORNO SELEZIONATO
        // =================================================

        Text(
            text = "Giorno selezionato",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text = dataSelezionata,
            style =
                MaterialTheme
                    .typography
                    .bodyLarge
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // PULSANTE GIORNATA
        // =================================================

        Button(
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                idDottore != null &&
                        !isLoading,
            onClick = {

                if (idDottore == null) {
                    return@Button
                }

                azioneGiornata =
                    if (giornataBloccata)
                        "sblocca"
                    else
                        "blocca"

                mostraDialogGiornata = true
            }
        ) {

            Text(
                text =
                    if (giornataBloccata)
                        "Rendi disponibile tutta la giornata"
                    else
                        "Blocca tutta la giornata"
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // TITOLO ORARI
        // =================================================

        Text(
            text = "Orari",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        // =================================================
        // LISTA ORARI SCORREVOLE
        // =================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
        ) {

            if (isLoading) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator()
                }

            } else {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    items(
                        count = orari.size
                    ) { indice ->

                        val ora =
                            orari[indice]

                        val orarioBloccato =
                            giornataBloccata ||
                                    indisponibilita.any {

                                        it.data ==
                                                dataSelezionata &&
                                                it.ora == ora &&
                                                !it.tuttoGiorno
                                    }

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 4.dp
                                    )
                                    .clickable(
                                        enabled =
                                            idDottore != null &&
                                                    !giornataBloccata
                                    ) {

                                        oraSelezionata =
                                            ora

                                        azioneOrario =
                                            if (
                                                orarioBloccato
                                            ) {
                                                "sblocca"
                                            } else {
                                                "blocca"
                                            }

                                        mostraDialogOrario =
                                            true
                                    }
                        ) {

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            16.dp
                                        ),
                                horizontalArrangement =
                                    Arrangement
                                        .SpaceBetween,
                                verticalAlignment =
                                    Alignment
                                        .CenterVertically
                            ) {

                                Text(
                                    text =
                                        "$ora - ${
                                            calcolaOraFine(
                                                ora
                                            )
                                        }",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyLarge
                                )

                                Text(
                                    text =
                                        if (
                                            orarioBloccato
                                        )
                                            "NON DISPONIBILE"
                                        else
                                            "DISPONIBILE",
                                    color =
                                        if (
                                            orarioBloccato
                                        )
                                            MaterialTheme
                                                .colorScheme
                                                .error
                                        else
                                            MaterialTheme
                                                .colorScheme
                                                .primary,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // =================================================
        // ERRORE
        // =================================================

        if (errore != null) {

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    errore ?: "",
                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }
    }
}

// =================================================
// CALCOLO ORA FINE
// =================================================

private fun calcolaOraFine(
    ora: String
): String {

    val parti =
        ora.split(":")

    val ore =
        parti[0].toInt()

    val minuti =
        parti[1].toInt()

    val calendario =
        Calendar.getInstance()

    calendario.set(
        Calendar.HOUR_OF_DAY,
        ore
    )

    calendario.set(
        Calendar.MINUTE,
        minuti
    )

    calendario.add(
        Calendar.HOUR_OF_DAY,
        1
    )

    return String.format(
        Locale.getDefault(),
        "%02d:%02d",
        calendario.get(
            Calendar.HOUR_OF_DAY
        ),
        calendario.get(
            Calendar.MINUTE
        )
    )
}