package com.example.medicareapp.ui.dottore.home

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

//Import usati per abbellire card
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import com.example.medicareapp.ui.theme.MediCareBlueLight

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import com.example.medicareapp.ui.theme.MediCareBlue

@Composable
fun HomeDottoreScreen(
    viewModel: HomeDottoreViewModel = viewModel()
) {

    val utente by viewModel.utente.collectAsState()
    val visiteOggi by viewModel.visiteOggi.collectAsState()
    val numeroPazienti by viewModel.numeroPazienti.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errore by viewModel.errore.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top
    ) {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "MediCare",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Area Dottore",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        when {

            isLoading -> {

                CircularProgressIndicator()
            }

            errore != null -> {

                Text(
                    text = errore ?: "Errore",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            utente != null -> {

                //Prima card: messaggio personalizzato di benvenuto a dottore loggato
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MediCareBlueLight
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Benvenuto Dott.",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            //Nome e cognome dottore non possono essere null
                            text = "${utente!!.nome} ${utente!!.cognome}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Area professionale",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(25.dp)
                )

                Text(
                    text = "Gestisci la tua attività",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(25.dp)
                )

                //Seconda card: numero di visite giornaliere del dott. che fa login
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MediCareBlueLight
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Visite di oggi",
                            tint = MediCareBlue
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column() {
                            Text(
                                text = "Visite oggi",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = visiteOggi.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                //Terza card: numero di pazienti associati a dott. loggato
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MediCareBlueLight
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            "Pazienti",
                            tint = MediCareBlue
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column() {
                            Text(
                                text = "Pazienti",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = numeroPazienti.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }


            } //Fine caso dottore non nullo

            else -> {

                Text(
                    text = "Nessun dato disponibile",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}