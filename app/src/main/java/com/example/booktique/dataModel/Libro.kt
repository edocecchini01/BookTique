package com.example.booktique.dataModel

data class Libro(
    var cover: Int,
    var titolo: String,
    var genere: String,
    var autore: String,
    var nPagine: Int,
    var Isbn: Long,
    var dettagli: String)