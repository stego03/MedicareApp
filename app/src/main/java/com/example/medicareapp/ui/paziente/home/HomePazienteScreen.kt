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
//import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientSectionTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
import com.example.medicareapp.ui.theme.MediCareSuccess
import com.example.medicareapp.ui.theme.MediCareSuccessLight
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomePazienteScreen(
    viewModel: HomePazienteViewModel
) {
    val dottori by viewModel.dottori.collectAsState()
    val dottoreSelezionato by viewModel.dottoreSelezionato.collectAsState()
    val indisponibilita by viewModel.indisponibilita.collectAsState()
    val giornoSelezionato by viewModel.giornoSelezionato.collectAsState()
    val orariDisponibili by viewModel.orariDisponibili.collectAsState()
    val messaggioPrenotazione by viewModel.messaggioPrenotazione.collectAsState()
    val prenotazioneInCorso by viewModel.prenotazioneInCorso.collectAsState()

    var menuAperto by remember { mutableStateOf(false) }
    var meseVisualizzato by remember { mutableStateOf(YearMonth.now()) }
    var oraDaConfermare by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        PatientScreenTitle(
            title = "Prenota una visita",
            subtitle = "Scegli il medico, il giorno e l'orario che preferisci."
        )

        Spacer(modifier = Modifier.height(24.dp))

        PatientCard {
            Column(modifier = Modifier.padding(16.dp)) {
                PatientSectionTitle(title = "Medico")
                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { menuAperto = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dottoreSelezionato?.let {
                                "${it.nome} ${it.cognome}"
                            } ?: "Seleziona un dottore"
                        )
                    }

                    DropdownMenu(
                        expanded = menuAperto,
                        onDismissRequest = { menuAperto = false }
                    ) {
                        if (dottori.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text("Nessun dottore disponibile")
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
                                            "${dottore.nome} ${dottore.cognome}"
                                        )
                                    },
                                    onClick = {
                                        viewModel.selezionaDottore(dottore)
                                        meseVisualizzato = YearMonth.now()
                                        oraDaConfermare = null
                                        menuAperto = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (dottoreSelezionato != null) {
            PatientCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    PatientSectionTitle(title = "Scegli il giorno")
                    Spacer(modifier = Modifier.height(12.dp))

                    CalendarioPaziente(
                        mese = meseVisualizzato,
                        indisponibilita = indisponibilita,
                        giornoSelezionato = giornoSelezionato,
                        onCambioMese = { nuovoMese ->
                            meseVisualizzato = nuovoMese
                        },
                        onSelezionaGiorno = { data ->
                            oraDaConfermare = null
                            viewModel.selezionaGiorno(data)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (giornoSelezionato != null) {
                PatientSectionTitle(title = "Orari disponibili")
                Spacer(modifier = Modifier.height(10.dp))

                if (orariDisponibili.isEmpty()) {
                    PatientStatusMessage(
                        message = "Nessun orario disponibile per questa data.",
                        success = false
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        orariDisponibili.forEach { ora ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(
                                        enabled = !prenotazioneInCorso
                                    ) {
                                        oraDaConfermare = ora
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (prenotazioneInCorso) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    MediCareSuccessLight
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "$ora - ${calcolaOraFinePaziente(ora)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MediCareSuccess
                                        )

                                        Text(
                                            text = "Durata: 1 ora",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = if (prenotazioneInCorso) {
                                            "Attendere..."
                                        } else {
                                            "Prenota"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MediCareSuccess,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                messaggioPrenotazione?.let { messaggio ->
                    Spacer(modifier = Modifier.height(16.dp))
                    PatientStatusMessage(message = messaggio)
                }
            }
        }
    }

    oraDaConfermare?.let { ora ->
        val dottore = dottoreSelezionato
        val data = giornoSelezionato

        if (dottore != null && data != null) {
            AlertDialog(
                onDismissRequest = {
                    if (!prenotazioneInCorso) {
                        oraDaConfermare = null
                    }
                },
                title = {
                    Text("Conferma prenotazione")
                },
                text = {
                    Column {
                        Text(
                            "Vuoi prenotare una visita con " +
                                    "${dottore.nome} ${dottore.cognome}?"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Data: $data")
                        Text(
                            "Orario: $ora - ${calcolaOraFinePaziente(ora)}"
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = !prenotazioneInCorso,
                        onClick = {
                            viewModel.prenotaVisita(ora = ora) { _, _ ->
                                oraDaConfermare = null
                            }
                        }
                    ) {
                        Text("Conferma prenotazione")
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !prenotazioneInCorso,
                        onClick = {
                            oraDaConfermare = null
                        }
                    ) {
                        Text("Annulla")
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarioPaziente(
    mese: YearMonth,
    indisponibilita: List<Indisponibilita>,
    giornoSelezionato: String?,
    onCambioMese: (YearMonth) -> Unit,
    onSelezionaGiorno: (String) -> Unit
) {
    val primoGiorno = mese.atDay(1)
    val numeroGiorni = mese.lengthOfMonth()
    val offset = primoGiorno.dayOfWeek.value - 1
    val oggi = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onCambioMese(mese.minusMonths(1)) }
            ) {
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.titleLarge,
                    color = MediCareBlue
                )
            }

            Text(
                text = nomeMese(mese),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            TextButton(
                onClick = { onCambioMese(mese.plusMonths(1)) }
            ) {
                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleLarge,
                    color = MediCareBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
                .forEach { giorno ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = giorno,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totaleCelle = offset + numeroGiorni
        val numeroRighe = (totaleCelle + 6) / 7

        for (riga in 0 until numeroRighe) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (colonna in 0..6) {
                    val indice = riga * 7 + colonna

                    if (indice < offset || indice >= totaleCelle) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                    } else {
                        val giorno = indice - offset + 1
                        val data = mese.atDay(giorno)
                        val dataString = data.toString()

                        val giornoBloccato = indisponibilita.any {
                            it.data == dataString && it.tuttoGiorno
                        }

                        val passato = data.isBefore(oggi)
                        val disponibile = !passato && !giornoBloccato
                        val selezionato = giornoSelezionato == dataString

                        GiornoCalendario(
                            modifier = Modifier.weight(1f),
                            giorno = giorno,
                            disponibile = disponibile,
                            selezionato = selezionato,
                            onClick = {
                                if (disponibile) {
                                    onSelezionaGiorno(dataString)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GiornoCalendario(
    modifier: Modifier = Modifier,
    giorno: Int,
    disponibile: Boolean,
    selezionato: Boolean,
    onClick: () -> Unit
) {
    val coloreSfondo = when {
        selezionato -> MediCareBlue
        disponibile -> MediCareBlueLight
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val coloreTesto = when {
        selezionato -> MaterialTheme.colorScheme.onPrimary
        disponibile -> MediCareBlue
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(50.dp)
            .padding(4.dp)
            .background(
                color = coloreSfondo,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                enabled = disponibile,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = giorno.toString(),
            color = coloreTesto,
            fontWeight = if (selezionato) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}

private fun calcolaOraFinePaziente(ora: String): String {
    val parti = ora.split(":")
    val ore = parti[0].toInt()
    val minuti = parti[1].toInt()

    val oraFine = (ore + 1) % 24

    return String.format(
        java.util.Locale.ITALIAN,
        "%02d:%02d",
        oraFine,
        minuti
    )
}

fun nomeMese(mese: YearMonth): String {
    val nome = when (mese.monthValue) {
        1 -> "Gennaio"
        2 -> "Febbraio"
        3 -> "Marzo"
        4 -> "Aprile"
        5 -> "Maggio"
        6 -> "Giugno"
        7 -> "Luglio"
        8 -> "Agosto"
        9 -> "Settembre"
        10 -> "Ottobre"
        11 -> "Novembre"
        12 -> "Dicembre"
        else -> mese.month.toString()
    }

    return "$nome ${mese.year}"
}