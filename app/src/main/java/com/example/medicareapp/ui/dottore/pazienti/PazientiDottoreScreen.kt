
package com.example.medicareapp.ui.dottore.pazienti

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.medicareapp.data.model.Documento
import com.example.medicareapp.data.model.Utente
import java.io.File
import java.io.IOException

@Composable
fun PazientiDottoreScreen(viewModel: PazientiDottoreViewModel) {
    val pazienti by viewModel.pazienti.collectAsState()
    val documenti by viewModel.documenti.collectAsState()
    val pazienteSelezionato by viewModel.pazienteSelezionato.collectAsState()
    val caricamento by viewModel.caricamento.collectAsState()
    val messaggio by viewModel.messaggio.collectAsState()

    val context = LocalContext.current

    var documentoImmagine by remember { mutableStateOf<Documento?>(null) }
    var documentoDaSalvare by remember { mutableStateOf<Documento?>(null) }
    var messaggioOperazione by remember { mutableStateOf<String?>(null) }

    val salvaPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            documentoDaSalvare?.let { documento ->
                messaggioOperazione = salvaDocumento(context, documento, uri)
            }
        }
        documentoDaSalvare = null
    }

    val salvaJpegLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        if (uri != null) {
            documentoDaSalvare?.let { documento ->
                messaggioOperazione = salvaDocumento(context, documento, uri)
            }
        }
        documentoDaSalvare = null
    }

    val salvaPngLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri ->
        if (uri != null) {
            documentoDaSalvare?.let { documento ->
                messaggioOperazione = salvaDocumento(context, documento, uri)
            }
        }
        documentoDaSalvare = null
    }

    val salvaAltroLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            documentoDaSalvare?.let { documento ->
                messaggioOperazione = salvaDocumento(context, documento, uri)
            }
        }
        documentoDaSalvare = null
    }

    fun scaricaDocumento(documento: Documento) {
        documentoDaSalvare = documento

        val nomeFile = nomeFileDocumento(documento)

        when (documento.tipo.lowercase()) {
            "application/pdf" -> salvaPdfLauncher.launch(nomeFile)
            "image/jpeg", "image/jpg" -> salvaJpegLauncher.launch(nomeFile)
            "image/png" -> salvaPngLauncher.launch(nomeFile)
            else -> salvaAltroLauncher.launch(nomeFile)
        }
    }

    fun apriDocumento(documento: Documento) {
        messaggioOperazione = null

        when {
            documento.contenuto.isBlank() -> {
                messaggioOperazione = "Il documento non contiene dati da aprire."
            }

            documento.tipo.lowercase().startsWith("image/") -> {
                documentoImmagine = documento
            }

            documento.tipo.equals("application/pdf", ignoreCase = true) -> {
                messaggioOperazione = apriPdf(context, documento)
            }

            else -> {
                messaggioOperazione =
                    "Formato non supportato per l'apertura. Puoi provare a salvare il documento."
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            pazienteSelezionato?.let { paziente ->
                TextButton(
                    onClick = {
                        viewModel.tornaAllaLista()
                        messaggioOperazione = null
                    }
                ) {
                    Text("← Torna ai pazienti")
                }

                Text(
                    text = "${paziente.nome} ${paziente.cognome}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (paziente.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Email: ${paziente.email}")
                }

                if (paziente.telefono.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Telefono: ${paziente.telefono}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Documenti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                messaggioOperazione?.let { testo ->
                    Text(
                        text = testo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (caricamento) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (documenti.isEmpty()) {
                    Text(
                        text = messaggio ?: "Nessun documento disponibile.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = documenti,
                            key = { it.idDocumento }
                        ) { documento ->
                            SchedaDocumento(
                                documento = documento,
                                onApri = { apriDocumento(documento) },
                                onScarica = { scaricaDocumento(documento) }
                            )
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "Pazienti",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Seleziona un paziente per visualizzare i suoi documenti.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (caricamento) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (pazienti.isEmpty()) {
                    Text(
                        text = messaggio ?: "Nessun paziente disponibile.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = pazienti,
                            key = { it.uid }
                        ) { paziente ->
                            SchedaPaziente(
                                paziente = paziente,
                                onClick = {
                                    messaggioOperazione = null
                                    viewModel.selezionaPaziente(paziente)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    documentoImmagine?.let { documento ->
        DialogImmagineDottore(
            documento = documento,
            onChiudi = { documentoImmagine = null }
        )
    }
}

@Composable
private fun SchedaPaziente(
    paziente: Utente,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${paziente.cognome} ${paziente.nome}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (paziente.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Email: ${paziente.email}")
            }

            if (paziente.telefono.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Telefono: ${paziente.telefono}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Visualizza documenti →",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SchedaDocumento(
    documento: Documento,
    onApri: () -> Unit,
    onScarica: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = documento.nome.ifBlank { "Documento senza nome" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (documento.tipo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tipo: ${documento.tipo}")
            }

            if (documento.data.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Data: ${documento.data}")
            }

            if (documento.descrizione.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Descrizione: ${documento.descrizione}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApri,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apri")
                }

                OutlinedButton(
                    onClick = onScarica,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Scarica")
                }
            }
        }
    }
}

@Composable
private fun DialogImmagineDottore(
    documento: Documento,
    onChiudi: () -> Unit
) {
    val bitmap = remember(documento.contenuto) {
        try {
            val bytes = Base64.decode(documento.contenuto, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    AlertDialog(
        onDismissRequest = onChiudi,
        title = {
            Text(documento.nome.ifBlank { "Immagine" })
        },
        text = {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = documento.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text("Impossibile visualizzare questa immagine.")
            }
        },
        confirmButton = {
            TextButton(onClick = onChiudi) {
                Text("Chiudi")
            }
        }
    )
}

private fun apriPdf(
    context: Context,
    documento: Documento
): String {
    return try {
        if (documento.contenuto.isBlank()) {
            return "Il documento non contiene dati da aprire."
        }

        val bytes = Base64.decode(documento.contenuto, Base64.DEFAULT)
        val nomeFile = nomeFileDocumento(documento)
        val file = File(context.cacheDir, nomeFile)

        file.writeBytes(bytes)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(context.packageManager) == null) {
            "Non è installata un'app compatibile per aprire i PDF."
        } else {
            context.startActivity(intent)
            ""
        }
    } catch (_: Exception) {
        "Impossibile aprire il PDF. Verifica che sia valido e riprova."
    }
}

private fun salvaDocumento(
    context: Context,
    documento: Documento,
    uri: Uri
): String {
    return try {
        if (documento.contenuto.isBlank()) {
            return "Il documento non contiene dati da salvare."
        }

        val bytes = Base64.decode(documento.contenuto, Base64.DEFAULT)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()
        } ?: throw IOException("Impossibile aprire la destinazione di salvataggio.")

        "Documento salvato correttamente."
    } catch (_: Exception) {
        "Errore durante il salvataggio del documento. Riprova."
    }
}

private fun nomeFileDocumento(documento: Documento): String {
    val nomeOriginale = documento.nome
        .trim()
        .ifBlank { "documento" }
        .replace("/", "_")
        .replace("\\", "_")

    val estensioneRichiesta = when (documento.tipo.lowercase()) {
        "application/pdf" -> ".pdf"
        "image/jpeg", "image/jpg" -> ".jpg"
        "image/png" -> ".png"
        else -> ""
    }

    return if (
        estensioneRichiesta.isNotEmpty() &&
        !nomeOriginale.endsWith(estensioneRichiesta, ignoreCase = true)
    ) {
        nomeOriginale + estensioneRichiesta
    } else {
        nomeOriginale
    }
}
