package com.example.booktique

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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

@Parcelize
data class LibriDaL(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val pagineTot: Int?=0,
    val id: String?="",
    val descrizione: String?="",
    val categorie: String?=""
): Parcelable

@Parcelize
data class LibriL(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val recensione: String?="",
    var valutazione: Int?=0,
    val id: String?="",
    val descrizione: String?="",
    val categorie: String?=""
):Parcelable
@Parcelize
data class LibriInC(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val pagineTot: Int?=0,
    val paginaAtt: Int?=0,
    val id: String?="",
    val descrizione: String?="",
    val categorie: String?=""
):Parcelable

