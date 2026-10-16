
package com.example.medicareapp.ui.registrazione

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.ui.theme.MediCareBlueLight
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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

    var mostraPassword by remember { mutableStateOf(false) }
    var mostraCalendario by remember { mutableStateOf(false) }
    var errore by remember { mutableStateOf<String?>(null) }

    val azzurro = Color(0xFF2196F3)
    val scrollState = rememberScrollState()

    // Formato e fuso orario usati per la selezione della data.
    val oggi = remember { Calendar.getInstance() }
    val annoCorrente = oggi.get(Calendar.YEAR)
    val annoMassimo = annoCorrente - 18

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = azzurro,
            modifier = Modifier.size(40.dp)
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Registrazione Paziente",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF17324D),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Inserisci i tuoi dati personali per registrarti.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF607D8B),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))

        // Username usato per accedere all'app.
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errore = null
            },
            label = { Text("Username") },
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Password con possibilità di mostrarla o nasconderla.
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errore = null
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(
                    onClick = { mostraPassword = !mostraPassword }
                ) {
                    Icon(
                        imageVector = if (mostraPassword)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                        contentDescription = if (mostraPassword)
                            "Nascondi password"
                        else
                            "Mostra password"
                    )
                }
            },
            visualTransformation = if (mostraPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Codice fiscale: massimo 16 caratteri, in maiuscolo.
        OutlinedTextField(
            value = codiceFiscale,
            onValueChange = {
                codiceFiscale = it.uppercase(Locale.ITALY).take(16)
                errore = null
            },
            label = { Text("Codice fiscale") },
            leadingIcon = {
                Icon(Icons.Outlined.Badge, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = {
                nome = it
                errore = null
            },
            label = { Text("Nome") },
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = cognome,
            onValueChange = {
                cognome = it
                errore = null
            },
            label = { Text("Cognome") },
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Campo in sola lettura: la data viene scelta dal calendario.
        OutlinedTextField(
            value = dataNascita,
            onValueChange = {},
            readOnly = true,
            label = { Text("Data di nascita") },
            placeholder = { Text("GG/MM/AAAA") },
            leadingIcon = {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { mostraCalendario = true }) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "Seleziona data"
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errore = null
            },
            label = { Text("Email personale") },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                errore = null
            },
            label = { Text("Telefono") },
            leadingIcon = {
                Icon(Icons.Outlined.Phone, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = azzurro,
                focusedLabelColor = azzurro,
                focusedLeadingIconColor = azzurro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )

        // La card compare solo quando è presente un errore.
        errore?.let { messaggio ->
            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(26.dp)
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
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val usernamePulito = username.trim()
                val cf = codiceFiscale.trim()
                val emailPulita = email.trim()
                val telefonoPulito = telefono.trim()

                when {
                    usernamePulito.isEmpty() ||
                            password.isEmpty() ||
                            cf.isEmpty() ||
                            nome.isBlank() ||
                            cognome.isBlank() ||
                            dataNascita.isBlank() ||
                            emailPulita.isEmpty() ||
                            telefonoPulito.isEmpty() -> {
                        errore = "Compila tutti i campi."
                    }

                    usernamePulito.contains("@") -> {
                        errore = "Lo username non deve contenere @."
                    }

                    usernamePulito.length < 3 -> {
                        errore = "Usa almeno 3 caratteri per lo username."
                    }

                    password.length < 6 -> {
                        errore = "La password deve contenere almeno 6 caratteri."
                    }

                    !Regex("^[A-Za-z0-9]{16}$").matches(cf) -> {
                        errore = "Inserisci un codice fiscale di 16 caratteri."
                    }

                    !Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                        .matches(emailPulita) -> {
                        errore = "Inserisci un indirizzo email valido."
                    }

                    telefonoPulito.length < 6 -> {
                        errore = "Inserisci un numero di telefono valido."
                    }

                    else -> {
                        viewModel.registraPaziente(
                            username = usernamePulito,
                            password = password,
                            codiceFiscale = cf,
                            nome = nome.trim(),
                            cognome = cognome.trim(),
                            dataNascita = dataNascita,
                            email = emailPulita,
                            telefono = telefonoPulito
                        ) { successo, messaggioErrore ->
                            if (successo) {
                                onRegistrazioneSuccess()
                            } else {
                                errore = messaggioErrore
                                    ?: "Errore durante la registrazione."
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = azzurro,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "REGISTRATI",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(10.dp))
    }

    // Dialog del calendario: limita la selezione al giorno corrente.
    if (mostraCalendario) {
        val calendarioIniziale = remember {
            Calendar.getInstance().apply {
                set(Calendar.YEAR, annoMassimo)
                set(Calendar.MONTH, oggi.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, oggi.get(Calendar.DAY_OF_MONTH))
            }
        }

        val statoData = rememberDatePickerState(
            initialSelectedDateMillis = calendarioIniziale.timeInMillis,
            yearRange = 1900..annoCorrente
        )

        DatePickerDialog(
            onDismissRequest = { mostraCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        statoData.selectedDateMillis?.let { millis ->
                            val calendario = Calendar.getInstance(
                                TimeZone.getTimeZone("UTC")
                            ).apply {
                                timeInMillis = millis
                            }

                            val giorno = calendario.get(Calendar.DAY_OF_MONTH)
                            val mese = calendario.get(Calendar.MONTH) + 1
                            val anno = calendario.get(Calendar.YEAR)

                            dataNascita = String.format(
                                Locale.ITALY,
                                "%02d/%02d/%04d",
                                giorno,
                                mese,
                                anno
                            )
                            errore = null
                        }
                        mostraCalendario = false
                    }
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostraCalendario = false }) {
                    Text("Annulla")
                }
            }
        ) {
            DatePicker(
                state = statoData,
                title = {
                    Text(
                        text = "Seleziona la data di nascita",
                        modifier = Modifier.padding(
                            start = 24.dp,
                            top = 16.dp
                        )
                    )
                },
                showModeToggle = false
            )
        }
    }
}
