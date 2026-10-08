package com.example.medicareapp.ui.paziente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicareapp.ui.theme.MediCareBlue
import com.example.medicareapp.ui.theme.MediCareBlueLight
import com.example.medicareapp.ui.theme.MediCareErrorLight
import com.example.medicareapp.ui.theme.MediCareSuccessLight
import com.example.medicareapp.ui.theme.MediCareTextSecondary

/*
 * Componenti comuni alla sezione Paziente.
 * Loro utilità è quella di mantenere la stessa struttura grafica
 * in tutte le schermate del paziente.
 */

@Composable
fun PatientScreenTitle(
    title: String,
    subtitle: String? = null
) {
    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (subtitle != null) {

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MediCareTextSecondary
            )
        }
    }
}


@Composable
fun PatientSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}


@Composable
fun PatientCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        content()
    }
}


@Composable
fun PatientStatusMessage(
    message: String,
    success: Boolean = true
) {

    val background =
        if (success) {
            MediCareSuccessLight
        } else {
            MediCareErrorLight
        }

    Surface(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

        color = background
    ) {

        Text(
            text = message,

            modifier = Modifier.padding(14.dp),

            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun PatientLoadingState(
    message: String
) {

    Column(
        modifier = Modifier.fillMaxSize(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        CircularProgressIndicator(
            color = MediCareBlue
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun PatientEmptyState(
    message: String
) {

    Box(
        modifier = Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = message,

            style =
                MaterialTheme.typography.bodyMedium,

            color =
                MediCareTextSecondary
        )
    }
}


//Piccolo badge utilizzato per gli stati delle visite.
@Composable
fun PatientStatusBadge(
    text: String,
    positive: Boolean
) {

    Surface(
        shape = RoundedCornerShape(50),

        color =
            if (positive) {
                MediCareSuccessLight
            } else {
                MediCareBlueLight
            }
    ) {

        Text(
            text = text,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),

            style = MaterialTheme.typography.labelLarge,

            fontWeight = FontWeight.Medium
        )
    }
}