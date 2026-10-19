
package com.example.medicareapp.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentoTest {

    @Test
    fun creaDocumento_conValoriPredefiniti() {
        val documento = Documento()

        assertEquals("", documento.idDocumento)
        assertEquals("", documento.idPaziente)
        assertEquals("", documento.idDottore)
        assertEquals("", documento.nome)
        assertEquals("", documento.tipo)
        assertEquals("", documento.data)
        assertEquals("", documento.descrizione)
        assertEquals("", documento.urlFile)
        assertEquals("", documento.contenuto)
    }

    @Test
    fun creaDocumento_conTuttiICampi() {
        val documento = Documento(
            idDocumento = "doc001",
            idPaziente = "paziente001",
            idDottore = "dottore001",
            nome = "Analisi del sangue",
            tipo = "PDF",
            data = "2026-10-19",
            descrizione = "Esame ematico",
            urlFile = "https://example.com/analisi.pdf",
            contenuto = "Contenuto di prova"
        )

        assertEquals("doc001", documento.idDocumento)
        assertEquals("paziente001", documento.idPaziente)
        assertEquals("dottore001", documento.idDottore)
        assertEquals("Analisi del sangue", documento.nome)
        assertEquals("PDF", documento.tipo)
        assertEquals("2026-10-19", documento.data)
        assertEquals("Esame ematico", documento.descrizione)
        assertEquals("https://example.com/analisi.pdf", documento.urlFile)
        assertEquals("Contenuto di prova", documento.contenuto)
    }

    @Test
    fun documentiConGliStessiDati_sonoUguali() {
        val documento1 = Documento(
            idDocumento = "doc001",
            nome = "Ricetta",
            tipo = "PDF"
        )

        val documento2 = Documento(
            idDocumento = "doc001",
            nome = "Ricetta",
            tipo = "PDF"
        )

        assertEquals(documento1, documento2)
    }
}
