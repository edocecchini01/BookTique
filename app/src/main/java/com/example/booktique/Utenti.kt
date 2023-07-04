package com.example.booktique

data class Utenti(
    val email: String,
    val password: String,
    val username: String,
    val catalogo: Catalogo
)
data class Catalogo(
    val libriDaLeggere: List<LibriDaL>,
    val libriInCorso: List<LibriInC>,
    val libriLetti: List<LibriL>
)
data class LibriDaL(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val pagineTot: Int?=0,
    val id: String?=""
)
data class LibriL(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val recensione: String?="",
    val valutazione: Int?=0,
    val id: String?=""
)
data class LibriInC(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val pagineTot: Int?=0,
    val paginaAtt: Int?=0,
    val id: String?=""
)

