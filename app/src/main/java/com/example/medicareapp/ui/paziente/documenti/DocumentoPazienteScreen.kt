
package com.example.medicareapp.ui.paziente.documenti

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicareapp.data.model.Documento
import java.io.File

@Composable
fun DocumentoPazienteScreen(
    viewModel: DocumentoPazienteViewModel = viewModel()
) {
    val context = LocalContext.current

    val documenti by viewModel.documenti.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val caricamentoFile by viewModel.caricamentoFile.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    var mostraDialog by remember { mutableStateOf(false) }
    var mostraImmagine by remember { mutableStateOf<Documento?>(null) }

    var uriSelezionato by remember { mutableStateOf<Uri?>(null) }
    var nomeFile by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }

    val filePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                uriSelezionato = uri

                nomeFile =
                    uri.lastPathSegment
                        ?.substringAfterLast("/")
                        ?: "documento"

                mostraDialog = true
            }
        }

    if (caricamento) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Caricamento documenti...")
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "I miei documenti",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
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
                CircularProgressIndicator()
            } else {
                Text(text = "+ Carica documento")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        messaggio?.let {
            Text(text = it)

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (documenti.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Non hai ancora caricato documenti.")
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

                                "image/jpeg",
                                "image/png" -> {
                                    mostraImmagine = documento
                                }

                                "application/pdf" -> {
                                    apriPdf(
                                        context = context,
                                        documento = documento
                                    )
                                }
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

    // Dialog caricamento
    if (mostraDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!caricamentoFile) {
                    mostraDialog = false
                    uriSelezionato = null
                }
            },

            title = {
                Text(text = "Carica documento")
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = nomeFile,
                        onValueChange = {
                            nomeFile = it
                        },
                        label = {
                            Text("Nome documento")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = descrizione,
                        onValueChange = {
                            descrizione = it
                        },
                        label = {
                            Text("Descrizione")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },

            confirmButton = {

                TextButton(
                    enabled = uriSelezionato != null && !caricamentoFile,

                    onClick = {

                        val uri =
                            uriSelezionato ?: return@TextButton

                        viewModel.caricaNuovoDocumento(
                            context = context,
                            uriFile = uri,
                            nomeFile = nomeFile,
                            tipo =
                                context.contentResolver
                                    .getType(uri)
                                    ?: "file",
                            data =
                                System.currentTimeMillis().toString(),
                            descrizione = descrizione
                        )

                        mostraDialog = false
                        uriSelezionato = null
                        descrizione = ""
                    }
                ) {
                    Text(text = "Carica")
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
                    Text(text = "Annulla")
                }
            }
        )
    }

    // Dialog immagine
    mostraImmagine?.let { documento ->

        DialogImmagine(
            documento = documento,
            onChiudi = {
                mostraImmagine = null
            }
        )
    }
}


@Composable
private fun DocumentoCard(
    documento: Documento,
    onApri: () -> Unit,
    onElimina: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = documento.nome,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tipo: ${documento.tipo}"
            )

            if (documento.descrizione.isNotBlank()) {

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = documento.descrizione
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = onApri
                ) {
                    Text(text = "Apri")
                }

                TextButton(
                    onClick = onElimina
                ) {
                    Text(text = "Elimina")
                }
            }
        }
    }
}


@Composable
private fun DialogImmagine(
    documento: Documento,
    onChiudi: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onChiudi,

        title = {
            Text(text = documento.nome)
        },

        text = {

            val bytes = remember(documento.idDocumento) {

                try {
                    Base64.decode(
                        documento.contenuto,
                        Base64.DEFAULT
                    )
                } catch (e: Exception) {
                    null
                }
            }

            val bitmap = remember(bytes) {

                bytes?.let {
                    BitmapFactory.decodeByteArray(
                        it,
                        0,
                        it.size
                    )
                }
            }

            if (bitmap != null) {

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = documento.nome,
                    modifier = Modifier.fillMaxWidth()
                )

            } else {

                Text(
                    text = "Impossibile visualizzare il documento."
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onChiudi
            ) {
                Text(text = "Chiudi")
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

        val bytes =
            Base64.decode(
                documento.contenuto,
                Base64.DEFAULT
            )

        val file =
            File(
                context.cacheDir,
                documento.nome.ifBlank {
                    "documento.pdf"
                }
            )

        file.writeBytes(bytes)

        val uri =
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

        val intent =
            Intent(
                Intent.ACTION_VIEW
            ).apply {

                setDataAndType(
                    uri,
                    "application/pdf"
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        context.startActivity(intent)

    } catch (e: Exception) {

        e.printStackTrace()
    }
}

