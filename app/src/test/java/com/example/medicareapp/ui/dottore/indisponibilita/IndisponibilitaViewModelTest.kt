
package com.example.medicareapp.ui.dottore.indisponibilita

import com.example.medicareapp.data.model.Indisponibilita
import com.example.medicareapp.data.repository.IndisponibilitaDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class IndisponibilitaViewModelTest {

    private class FakeIndisponibilitaRepository : IndisponibilitaDataSource {

        var chiamateRepository = 0

        override suspend fun getIndisponibilitaDottore(
            idDottore: String
        ): List<Indisponibilita> {
            chiamateRepository++
            return emptyList()
        }

        override suspend fun getIndisponibilitaGiorno(
            idDottore: String,
            data: String
        ): List<Indisponibilita> {
            chiamateRepository++
            return emptyList()
        }

        override suspend fun aggiungiIndisponibilitaOraria(
            idDottore: String,
            data: String,
            ora: String
        ): Boolean {
            chiamateRepository++
            return true
        }

        override suspend fun rimuoviIndisponibilitaOraria(
            idDottore: String,
            data: String,
            ora: String
        ): Boolean {
            chiamateRepository++
            return true
        }

        override suspend fun aggiungiIndisponibilitaGiornata(
            idDottore: String,
            data: String
        ): Boolean {
            chiamateRepository++
            return true
        }

        override suspend fun rimuoviIndisponibilitaGiornata(
            idDottore: String,
            data: String
        ): Boolean {
            chiamateRepository++
            return true
        }
    }

    @Test
    fun bloccaOrario_conDataPassata_rifiutaOperazione() {
        val repository = FakeIndisponibilitaRepository()
        val viewModel = IndisponibilitaViewModel(repository)

        viewModel.bloccaOrario(
            idDottore = "dottoreTest",
            data = "2020-01-01",
            ora = "09:00"
        )

        assertEquals(
            "Non puoi modificare un giorno passato",
            viewModel.errore.value
        )
        assertEquals(0, repository.chiamateRepository)
    }

    @Test
    fun bloccaOrario_conOrarioNonValido_rifiutaOperazione() {
        val repository = FakeIndisponibilitaRepository()
        val viewModel = IndisponibilitaViewModel(repository)

        viewModel.bloccaOrario(
            idDottore = "dottoreTest",
            data = LocalDate.now().plusDays(1).toString(),
            ora = "14:00"
        )

        assertEquals("Orario non valido", viewModel.errore.value)
        assertEquals(0, repository.chiamateRepository)
    }

    @Test
    fun bloccaGiornata_conDataPassata_rifiutaOperazione() {
        val repository = FakeIndisponibilitaRepository()
        val viewModel = IndisponibilitaViewModel(repository)

        viewModel.bloccaGiornata(
            idDottore = "dottoreTest",
            data = "2020-01-01"
        )

        assertEquals(
            "Non puoi modificare un giorno passato",
            viewModel.errore.value
        )
        assertEquals(0, repository.chiamateRepository)
    }

    @Test
    fun sbloccaOrario_conOrarioNonValido_rifiutaOperazione() {
        val repository = FakeIndisponibilitaRepository()
        val viewModel = IndisponibilitaViewModel(repository)

        viewModel.sbloccaOrario(
            idDottore = "dottoreTest",
            data = LocalDate.now().plusDays(1).toString(),
            ora = "14:00"
        )

        assertEquals("Orario non valido", viewModel.errore.value)
        assertEquals(0, repository.chiamateRepository)
    }
}
