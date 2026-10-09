package com.example.medicareapp.ui.paziente.documenti

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.data.model.Documento
import com.example.medicareapp.ui.paziente.PatientCard
import com.example.medicareapp.ui.paziente.PatientScreenTitle
import com.example.medicareapp.ui.paziente.PatientStatusMessage
import java.io.File

@Composable
fun DocumentoPazienteScreen(
    viewModel: DocumentoPazienteViewModel = viewModel()
) {
    val context = LocalContext.current

    // =================================================
    // STATO DEL VIEWMODEL
    // La UI legge gli StateFlow e comunica solo con il ViewModel.
    // La logica di caricamento/eliminazione resta quindi nell'MVVM.
    // =================================================
    val documenti by viewModel.documenti.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val caricamentoFile by viewModel.caricamentoFile.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    var mostraDialog by remember { mutableStateOf(false) }
    var mostraImmagine by remember { mutableStateOf<Documento?>(null) }
    var uriSelezionato by remember { mutableStateOf<Uri?>(null) }
    var nomeFile by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }

    // Selettore dei documenti supportati dall'app.
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            uriSelezionato = it
            nomeFile = it.lastPathSegment?.substringAfterLast("/") ?: "documento"
            mostraDialog = true
        }
    }

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
            Spacer(modifier = Modifier.height(16.dp))
            Text("Caricamento documenti...")
        }
        return
    }

    // =================================================
    // SCHERMATA DOCUMENTI
    // =================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        PatientScreenTitle(
            title = "I miei documenti",
            subtitle = "Visualizza e gestisci i tuoi documenti personali."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                filePicker.launch(
                    arrayOf(
                        "application/pdf",
                        "image/jpeg",
                        "image/png"
                    )
                )
            },
            enabled = !caricamentoFile,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (caricamentoFile) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("+ Carica documento")
            }
        }

        messaggio?.let {
            Spacer(modifier = Modifier.height(12.dp))
            PatientStatusMessage(message = it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (documenti.isEmpty()) {
            PatientCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Non hai ancora caricato documenti.")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = documenti,
                    key = { it.idDocumento }
                ) { documento ->
                    DocumentoCard(
                        documento = documento,
                        onApri = {
                            when (documento.tipo) {
                                "image/jpeg", "image/png" ->
                                    mostraImmagine = documento

                                "application/pdf" ->
                                    apriPdf(context, documento)
                            }
                        },
                        onElimina = {
                            viewModel.eliminaDocumento(documento)
                        }
                    )
                }
            }
        }
    }

    // =================================================
    // DIALOG CARICAMENTO
    // =================================================
    if (mostraDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!caricamentoFile) {
                    mostraDialog = false
                    uriSelezionato = null
                }
            },
            title = { Text("Carica documento") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nomeFile,
                        onValueChange = { nomeFile = it },
                        label = { Text("Nome documento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = descrizione,
                        onValueChange = { descrizione = it },
                        label = { Text("Descrizione") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = uriSelezionato != null && !caricamentoFile,
                    onClick = {
                        val uri = uriSelezionato ?: return@TextButton

                        // La schermata passa i dati al ViewModel:
                        // il repository resta fuori dalla UI.
                        viewModel.caricaNuovoDocumento(
                            context = context,
                            uriFile = uri,
                            nomeFile = nomeFile,
                            tipo = context.contentResolver.getType(uri) ?: "file",
                            data = System.currentTimeMillis().toString(),
                            descrizione = descrizione
                        )

                        mostraDialog = false
                        uriSelezionato = null
                        descrizione = ""
                    }
                ) {
                    Text("Carica")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !caricamentoFile,
                    onClick = {
                        mostraDialog = false
                        uriSelezionato = null
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }

    // Dialog dedicato alla visualizzazione delle immagini.
    mostraImmagine?.let { documento ->
        DialogImmagine(
            documento = documento,
            onChiudi = { mostraImmagine = null }
        )
    }
}

// =================================================
// CARD DOCUMENTO
// =================================================
@Composable
private fun DocumentoCard(
    documento: Documento,
    onApri: () -> Unit,
    onElimina: () -> Unit
) {
    PatientCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = documento.nome,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tipo: ${documento.tipo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (documento.descrizione.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(documento.descrizione)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onApri) {
                    Text("Apri")
                }

                TextButton(onClick = onElimina) {
                    Text("Elimina")
                }
            }
        }
    }
}

// =================================================
// VISUALIZZAZIONE IMMAGINE
// =================================================
@Composable
private fun DialogImmagine(
    documento: Documento,
    onChiudi: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onChiudi,
        title = { Text(documento.nome) },
        text = {
            val bytes = remember(documento.idDocumento) {
                try {
                    Base64.decode(documento.contenuto, Base64.DEFAULT)
                } catch (_: Exception) {
                    null
                }
            }

            val bitmap = remember(bytes) {
                bytes?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = documento.nome,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Impossibile visualizzare il documento.")
            }
        },
        confirmButton = {
            TextButton(onClick = onChiudi) {
                Text("Chiudi")
            }
        }
    )
}

/**
 * Trasforma il Base64 del documento in un PDF temporaneo
 * e lo apre tramite un'applicazione PDF installata.
 */
private fun apriPdf(
    context: Context,
    documento: Documento
) {
    try {
        val bytes = Base64.decode(documento.contenuto, Base64.DEFAULT)

        val file = File(
            context.cacheDir,
            documento.nome.ifBlank { "documento.pdf" }
        ).apply {
            writeBytes(bytes)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}