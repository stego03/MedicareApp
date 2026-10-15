//```kotlin
package com.example.medicareapp.ui.registrazione

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RegistrazioneScreen(
    viewModel: RegistrazioneViewModel = viewModel(),
    onRegistrazioneSuccess: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var codiceFiscale by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var dataNascita by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var errore by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Registrazione Paziente"
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errore = null
            },
            label = {
                Text("Username")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errore = null
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = codiceFiscale,
            onValueChange = {
                codiceFiscale = it
                errore = null
            },
            label = {
                Text("Codice fiscale")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = nome,
            onValueChange = {
                nome = it
                errore = null
            },
            label = {
                Text("Nome")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = cognome,
            onValueChange = {
                cognome = it
                errore = null
            },
            label = {
                Text("Cognome")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = dataNascita,
            onValueChange = {
                dataNascita = it
                errore = null
            },
            label = {
                Text("Data di nascita")
            },
            placeholder = {
                Text("GG/MM/AAAA")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errore = null
            },
            label = {
                Text("Email personale")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errore = null
            },
            label = {
                Text("Telefono")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                if (
                    username.isBlank() ||
                    password.isBlank() ||
                    codiceFiscale.isBlank() ||
                    nome.isBlank() ||
                    cognome.isBlank() ||
                    dataNascita.isBlank() ||
                    email.isBlank() ||
                    telefono.isBlank()
                ) {
                    errore = "Compila tutti i campi"
                    return@Button
                }

                viewModel.registraPaziente(
                    username = username,
                    password = password,
                    codiceFiscale = codiceFiscale,
                    nome = nome,
                    cognome = cognome,
                    dataNascita = dataNascita,
                    email = email,
                    telefono = telefono
                ) { successo, messaggioErrore ->

                    if (successo) {
                        onRegistrazioneSuccess()
                    } else {
                        errore = messaggioErrore
                            ?: "Errore durante la registrazione"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRATI")
        }


        errore?.let { messaggio ->
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = "Registrazione non riuscita",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = messaggio,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
//```
