package com.example.medicareapp.ui.paziente.notifiche

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.data.model.Notifica
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue

@Composable
fun NotifichePazienteScreen(
    viewModel: NotifichePazienteViewModel = viewModel()
) {
    val notifiche by viewModel.notifiche.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    // =================================================
    // SCHERMATA NOTIFICHE
    // La UI legge i dati dal ViewModel e non gestisce
    // direttamente la logica delle notifiche.
    // =================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        PatientScreenTitle(
            title = "Notifiche",
            subtitle = "Rimani aggiornato sulle informazioni importanti."
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { viewModel.caricaNotifiche() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Aggiorna",
                color = MediCareBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // =================================================
        // CARICAMENTO
        // =================================================
        if (caricamento) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Caricamento notifiche...")
            }

            return
        }

        // =================================================
        // MESSAGGIO
        // =================================================
        messaggio?.let {
            PatientStatusMessage(message = it)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // =================================================
        // ELENCO NOTIFICHE
        // =================================================
        if (notifiche.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Non hai notifiche.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = notifiche,
                    key = { it.idNotifica }
                ) { notifica ->
                    NotificaCard(notifica)
                }
            }
        }
    }
}

// =================================================
// CARD DELLA NOTIFICA
// =================================================
@Composable
private fun NotificaCard(
    notifica: Notifica
) {
    PatientCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notifica.titolo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MediCareBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notifica.messaggio,
                style = MaterialTheme.typography.bodyMedium
            )

            if (notifica.data.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notifica.data,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}