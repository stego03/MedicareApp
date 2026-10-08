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


    var visitaDaCancellare by
    remember {
        mutableStateOf<Visita?>(null)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Le mie visite"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        if (caricamento) {

            CircularProgressIndicator()

        } else if (visite.isEmpty()) {

            Text(
                text = "Non hai visite."
            )

        } else {

            visite.forEach { visita ->

                VisitaCard(
                    visita = visita,

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
    onCancella: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Data: ${visita.data}"
            )

            Text(
                text = "Ora: ${visita.ora}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Dottore: ${visita.idDottore}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Stato: ${visita.stato}"
            )


            if (visita.stato == "prenotato") {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.End
                ) {

                    Button(
                        onClick = onCancella
                    ) {

                        Text(
                            text = "Cancella"
                        )
                    }
                }
            }
        }
    }
}