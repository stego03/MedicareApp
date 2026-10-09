
package com.example.medicareapp.ui.dottore.visite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun VisiteDottoreScreen(
    viewModel: VisiteDottoreViewModel = viewModel()
) {
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
        Text(
            text = "Le mie visite",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Consulta gli appuntamenti dei tuoi pazienti.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        Text("Riprova")
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
    onCancella: () -> Unit
) {
    val stato = visita.stato.trim().lowercase()

    val statoVisualizzato = when (stato) {
        "prenotato" -> "Prenotata"
        "effettuato" -> "Effettuata"
        "cancellato" -> "Cancellata"
        else -> visita.stato.ifBlank { "Non specificato" }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = nomePaziente,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Data")
                Text(visita.data.ifBlank { "Non disponibile" })
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ora")
                Text(visita.ora.ifBlank { "Non disponibile" })
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Stato: $statoVisualizzato",
                style = MaterialTheme.typography.titleSmall,
                color = when (stato) {
                    "cancellato" -> MaterialTheme.colorScheme.error
                    "effettuato" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            if (stato == "prenotato") {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCancella,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancella visita")
                }
            }
        }
    }
}
