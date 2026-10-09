
package com.example.medicareapp.ui.paziente.areaPersonale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

//Aggiunta di import per abbellimento Area personale paziente
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientSectionTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight

@Composable
fun AreaPersonaleScreen(
    viewModel: AreaPersonaleViewModel = viewModel(),
    onLogout: () -> Unit
) {

    val utente by viewModel.utente.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val salvataggio by viewModel.salvataggio.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    var nome by rememberSaveable {
        mutableStateOf("")
    }

    var cognome by rememberSaveable {
        mutableStateOf("")
    }

    var dataNascita by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var telefono by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * Quando il ViewModel riceve i dati da Firebase,
     * aggiorniamo i campi della schermata.
     */
    LaunchedEffect(utente) {

        utente?.let {

            nome = it.nome
            cognome = it.cognome
            dataNascita = it.dataNascita
            email = it.email
            telefono = it.telefono
        }
    }


    // =================================================
    // CARICAMENTO
    // =================================================

    if (caricamento) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            CircularProgressIndicator()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Caricamento dati personali..."
            )
        }

        return
    }


    // =================================================
    // ERRORE CARICAMENTO
    // =================================================

    if (utente == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "Impossibile visualizzare i dati personali."
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    viewModel.caricaUtente()
                }
            ) {

                Text(
                    text = "Riprova"
                )
            }
        }

        return
    }


    // =================================================
    // AREA PERSONALE
    // =================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        // ---------------------------------------------
        // TITOLO
        // ---------------------------------------------

        Text(
            text = "Area personale",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Visualizza e modifica i tuoi dati personali."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =================================================
        // DATI NON MODIFICABILI
        // =================================================

        Text(
            text = "Dati account",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // CODICE FISCALE
        // ---------------------------------------------

        OutlinedTextField(
            value = utente!!.codiceFiscale,

            onValueChange = {},

            label = {
                Text("Codice fiscale")
            },

            modifier = Modifier.fillMaxWidth(),

            readOnly = true,

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // USERNAME
        // ---------------------------------------------

        OutlinedTextField(
            value = utente!!.username,

            onValueChange = {},

            label = {
                Text("Username")
            },

            modifier = Modifier.fillMaxWidth(),

            readOnly = true,

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =================================================
        // DATI PERSONALI
        // =================================================

        Text(
            text = "Dati personali",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // NOME
        // ---------------------------------------------

        OutlinedTextField(
            value = nome,

            onValueChange = {

                nome = it

                viewModel.pulisciMessaggio()
            },

            label = {
                Text("Nome")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // COGNOME
        // ---------------------------------------------

        OutlinedTextField(
            value = cognome,

            onValueChange = {

                cognome = it

                viewModel.pulisciMessaggio()
            },

            label = {
                Text("Cognome")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // DATA DI NASCITA
        // ---------------------------------------------

        OutlinedTextField(
            value = dataNascita,

            onValueChange = {

                dataNascita = it

                viewModel.pulisciMessaggio()
            },

            label = {
                Text("Data di nascita")
            },

            placeholder = {
                Text("gg/mm/aaaa")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // EMAIL
        // ---------------------------------------------

        OutlinedTextField(
            value = email,

            onValueChange = {

                email = it

                viewModel.pulisciMessaggio()
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ---------------------------------------------
        // TELEFONO
        // ---------------------------------------------

        OutlinedTextField(
            value = telefono,

            onValueChange = {

                telefono = it

                viewModel.pulisciMessaggio()
            },

            label = {
                Text("Telefono")
            },

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =================================================
        // SALVA
        // =================================================

        Button(
            onClick = {

                viewModel.salvaModifiche(
                    nome = nome,
                    cognome = cognome,
                    dataNascita = dataNascita,
                    email = email,
                    telefono = telefono
                )
            },

            enabled = !salvataggio,

            modifier = Modifier.fillMaxWidth()
        ) {

            if (salvataggio) {

                CircularProgressIndicator()

            } else {

                Text(
                    text = "Salva modifiche"
                )
            }
        }


        // =================================================
        // MESSAGGIO
        // =================================================

        messaggio?.let {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = it
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Esci"
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

