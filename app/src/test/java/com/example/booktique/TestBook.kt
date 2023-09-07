package com.example.booktique


import com.example.booktique.dataModel.LibriDaL
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.dataModel.LibriL
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class TestBook {

    @Test
    fun BookTest(){

        val libroDaLeg = LibriDaL(titolo = "Titolo",autori = "autori prova", id = "1234")

        assertEquals(libroDaLeg.titolo , "Titolo")
        assertEquals(libroDaLeg.autori , "autori prova")
        assertEquals(libroDaLeg.id , "1234")

        val libroInC = LibriInC(titolo = "Titolo",autori = "autori prova", id = "123", pagineTot = 200, paginaAtt = 50)

        assertEquals(libroInC.titolo , "Titolo")
        assertEquals(libroInC.autori , "autori prova")
        assertEquals(libroInC.id , "123")
        assertEquals(libroInC.pagineTot , 200)
        assertEquals(libroInC.paginaAtt , 50)

        val libroLetto = LibriL(titolo = "Titolo",autori = "autori prova", id = "12", recensione = "libro molto bello")

        assertEquals(libroLetto.titolo , "Titolo")
        assertEquals(libroLetto.autori , "autori prova")
        assertEquals(libroLetto.id , "12")
        assertEquals(libroLetto.recensione , "libro molto bello")

    }

}