package com.example.medicareapp.ui.paziente.visite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.medicareapp.data.model.Visita

//Aggiunta import
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
import com.example.medicareapp.ui.theme.MediCareError

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme

@Composable
fun VisitePazienteScreen(
    viewModel: VisitePazienteViewModel = viewModel()
) {

    val visite by
    viewModel.visite.collectAsState()

    val caricamento by
    viewModel.caricamento.collectAsState()

    val messaggio by
    viewModel.messaggio.collectAsState()

    val dottori by
    viewModel.dottori.collectAsState()


    var visitaDaCancellare by
    remember {
        mutableStateOf<Visita?>(null)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        PatientScreenTitle(
            title = "Le mie visite",
            subtitle = "Controlla i tuoi appuntamenti e lo stato delle visite."
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        if (caricamento) {

            CircularProgressIndicator()

        } else if (visite.isEmpty()) {

            PatientStatusMessage(
                message = "Non hai visite disponibili.",
                false
            )

        } else {

            visite.forEach { visita ->

                VisitaCard(
                    visita = visita,
                    nomeDottore = dottori[visita.idDottore]?.let {
                        "${it.nome} ${it.cognome}"
                    } ?: "Dottore",

                    onCancella = {

                        visitaDaCancellare = visita
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }


        messaggio?.let {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = it
            )
        }
    }


    // -------------------------------------------------
    // CONFERMA CANCELLAZIONE
    // -------------------------------------------------

    visitaDaCancellare?.let { visita ->

        AlertDialog(

            onDismissRequest = {

                visitaDaCancellare = null
            },

            title = {

                Text(
                    text = "Cancella visita"
                )
            },

            text = {

                Text(
                    text =
                        "Sei sicuro di voler cancellare questa visita?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.cancellaVisita(
                            visita
                        )

                        visitaDaCancellare = null
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

                        visitaDaCancellare = null
                    }
                ) {

                    Text(
                        text = "Annulla"
                    )
                }
            }
        )
    }
}


@Composable
private fun VisitaCard(
    visita: Visita,
    nomeDottore: String,
    onCancella: () -> Unit
) {
    PatientCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Data e ora sono le informazioni più importanti:
            // vengono mostrate subito nella parte alta della card.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = visita.data,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = visita.ora,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MediCareBlue
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (visita.stato == "prenotato") {
                        MediCareBlueLight
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = visita.stato.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (visita.stato == "prenotato") {
                            MediCareBlue
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dottore",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = nomeDottore,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (visita.stato == "prenotato") {
                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = onCancella,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancella visita",
                        color = MediCareError,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}