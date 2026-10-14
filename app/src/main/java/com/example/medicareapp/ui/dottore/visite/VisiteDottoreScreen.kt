
package com.example.medicareapp.ui.dottore.visite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.data.model.Visita

//Import per abbellire card
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
import com.example.medicareapp.ui.theme.MediCareError
import androidx.compose.ui.text.font.FontWeight

@Composable
fun VisiteDottoreScreen(
    viewModel: VisiteDottoreViewModel = viewModel()
) {
    //Sono visite già filtrate (solo quelle prenotate)
    val visite by viewModel.visite.collectAsState()
    val pazienti by viewModel.pazienti.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    var visitaDaCancellare by remember {
        mutableStateOf<Visita?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PatientScreenTitle(
            title = "Le mie visite",
            subtitle = "Consulta e gestisci gli appuntamenti dei tuoi pazienti",
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            caricamento -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Caricamento visite...")
                }
            }

            messaggio != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = messaggio ?: "Errore",
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.pulisciMessaggio()
                            viewModel.caricaVisite()
                        }
                    ) {
                        Text("Ritorna alle visite")
                    }
                }
            }

            visite.isEmpty() -> {
                Text(
                    text = "Non ci sono visite da visualizzare.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.caricaVisite()
                    }
                ) {
                    Text("Aggiorna")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = visite,
                        key = { it.idVisita }
                    ) { visita ->
                        VisitaDottoreCard(
                            visita = visita,
                            nomePaziente = pazienti[visita.idPaziente]
                                ?.let {
                                    "${it.nome} ${it.cognome}".trim()
                                }
                                ?.takeIf { it.isNotBlank() }
                                ?: "Paziente non disponibile",
                            onCancella = {
                                visitaDaCancellare = visita
                            },
                            onEffettuata = {
                                viewModel.segnaVisitaEffettuata(visita)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.caricaVisite()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aggiorna visite")
                        }
                    }
                }
            }
        }
    }

    visitaDaCancellare?.let { visita ->
        AlertDialog(
            onDismissRequest = {
                visitaDaCancellare = null
            },
            title = {
                Text("Conferma cancellazione")
            },
            text = {
                Text(
                    "Vuoi cancellare la visita del " +
                            "${visita.data} alle ${visita.ora}?\n\n" +
                            "Il paziente dovrà essere informato " +
                            "della cancellazione."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        visitaDaCancellare = null
                        viewModel.cancellaVisita(visita)
                    }
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        visitaDaCancellare = null
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
private fun VisitaDottoreCard(
    visita: Visita,
    nomePaziente: String,
    onCancella: () -> Unit,
    onEffettuata: () -> Unit
) {
    val stato = visita.stato.trim().lowercase()
    //Stato effettivamente visualizzato su schermata visite
    val statoVisualizzato = when (stato) {
        "prenotato" -> "Prenotata"
        "effettuato" -> "Effettuata"
        "cancellato" -> "Cancellata"
        else -> visita.stato.ifBlank { "Non specificato" }
    }

    PatientCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Data e ora sono le informazioni principali -> mostrate nella parte alta della card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = visita.data.ifBlank {
                            "Data non disponibile"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = visita.ora.ifBlank {
                            "Ora non disponibile"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MediCareBlue
                    )
                }
                //Serve a creare un badge dello stato della visita (prenotato/cancellato)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (stato){
                        "prenotato" -> MediCareBlueLight
                        "cancellato" -> MediCareError.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {

                    Text(
                        text = statoVisualizzato,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (stato){
                            "prenotato" -> MediCareBlue
                            "cancellato" -> MediCareError
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Paziente",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = nomePaziente,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(15.dp) )

            /*Se visita è prenotata ci sono due possibili azioni che può effettuare il dottore:
              1. Segnare visita come effettuata
              2. Cancellare visita */
            if (stato == "prenotato") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onEffettuata,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFE6F5EC),
                            contentColor = androidx.compose.ui.graphics.Color(0xFF16865B)
                        )
                    ) {
                        Text(
                            text = "Segna come fatta",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onCancella,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFFDECEC),
                            contentColor = androidx.compose.ui.graphics.Color(0xFFC62828)
                        )
                    ) {
                        Text(
                            text = "Cancella visita",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}