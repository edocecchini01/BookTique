package com.example.booktique

data class Utenti(
    val email: String,
    val password: String,
    val username: String,
    val libriDaLeggere: List<LibriDaL>,
    val libriInCorso: List<LibriInC>,
    val libriLetti: List<LibriL>
)
data class LibriDaL(
    val titolo: String,
    val copertina: String,
    val autori: String
)
data class LibriL(
    val titolo: String,
    val copertina: String,
    val autori: String,
    val recensione: String,
    val valutazione: Int
)
data class LibriInC(
    val titolo: String,
    val copertina: String,
    val autori: String,
    val pagineTot: Int,
    val paginaAtt: Int
)

