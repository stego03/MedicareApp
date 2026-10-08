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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.data.model.Notifica

@Composable
fun NotifichePazienteScreen(
    viewModel: NotifichePazienteViewModel = viewModel()
) {
    val notifiche by viewModel.notifiche.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Notifiche",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { viewModel.caricaNotifiche() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Aggiorna")
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        messaggio?.let {
            Text(text = it)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (notifiche.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Non hai notifiche.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun NotificaCard(
    notifica: Notifica
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = notifica.titolo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = notifica.messaggio)

            if (notifica.data.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notifica.data,
                    fontSize = 12.sp
                )
            }
        }
    }
}
