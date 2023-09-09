package com.example.booktique.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//modello dati degli utenti salvati nel DB, all'interno c'è anche il catalogo personale
data class Utenti(
    val email: String,
    val password: String,
    val username: String,
    val catalogo: Catalogo
)

//modello dati del catalogo personale al cui interno sono presenti le liste dei libri suddivisi nelle varie tipologie
data class Catalogo(
    val libriDaLeggere: List<LibriDaL>,
    val libriInCorso: List<LibriInC>,
    val libriLetti: List<LibriL>
)

//modello dati dei libri nella sezione "da leggere"
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

//modello dati dei libri nella sezione "letti"
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

//modello dati dei libri nella sezione "in corso"
@Parcelize
data class LibriInC(
    val titolo: String?="",
    val copertina: String?="",
    val autori: String?="",
    val pagineTot: Int?=0,
    var paginaAtt: Int?=0,
    val id: String?="",
    val descrizione: String?="",
    val categorie: String?=""
):Parcelable

