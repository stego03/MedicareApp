package com.example.medicareapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PatientHomeScreen(
    onPrenotaVisitaClick: () -> Unit,
    onVisiteClick: () -> Unit,
    onProfiloClick: () -> Unit,
    onDocumentiClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Medi Care"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Area Paziente"
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = onPrenotaVisitaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("PRENOTA VISITA")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onVisiteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LE MIE VISITE")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onProfiloClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("AREA PERSONALE")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onDocumentiClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DOCUMENTI")
        }
    }
}