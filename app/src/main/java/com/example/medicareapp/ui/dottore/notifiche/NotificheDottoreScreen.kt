
package com.example.medicareapp.ui.dottore.notifiche

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NotificheDottoreScreen(
    viewModel: NotificheDottoreViewModel = viewModel()
) {
    val pazienti by viewModel.pazienti.collectAsState()
    val selezione by viewModel.idPazienteSelezionato.collectAsState()
    val titolo by viewModel.titolo.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val esito by viewModel.esito.collectAsState()

    var menuAperto by remember { mutableStateOf(false) }

    val pazienteSelezionato = pazienti.find {
        it.uid == selezione
    }

    val testoDestinatario = if (selezione == "TUTTI") {
        "Tutti i pazienti"
    } else {
        pazienteSelezionato?.let {
            "${it.nome} ${it.cognome}"
        } ?: "Seleziona un paziente"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Invia una notifica"
        )

        Text(
            text = "Scegli a chi inviare il messaggio."
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Destinatario")

                TextButton(
                    onClick = { menuAperto = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(testoDestinatario)
                }

                DropdownMenu(
                    expanded = menuAperto,
                    onDismissRequest = { menuAperto = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Tutti i pazienti")
                        },
                        onClick = {
                            viewModel.selezionaPaziente("TUTTI")
                            menuAperto = false
                        }
                    )

                    pazienti.forEach { paziente ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${paziente.nome} ${paziente.cognome}"
                                )
                            },
                            onClick = {
                                viewModel.selezionaPaziente(paziente.uid)
                                menuAperto = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = titolo,
            onValueChange = viewModel::aggiornaTitolo,
            label = { Text("Titolo della notifica") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = messaggio,
            onValueChange = viewModel::aggiornaMessaggio,
            label = { Text("Corpo del messaggio") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = { viewModel.inviaNotifica() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !caricamento
        ) {
            Text(
                if (caricamento) {
                    "Invio in corso..."
                } else {
                    "Invia notifica"
                }
            )
        }

        esito?.let {
            Text(text = it)
        }
    }
}
