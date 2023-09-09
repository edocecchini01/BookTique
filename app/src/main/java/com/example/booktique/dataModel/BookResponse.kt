package com.example.booktique.dataModel
import com.squareup.moshi.JsonClass

//rappresentazione dei dati resituiti dalle chiamate API
@JsonClass(generateAdapter = true)
data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeDet,

    )

data class VolumeDet(
    val imageLinks: ImageLinks = ImageLinks(),
    val title: String?="",
    val authors: List<String> = emptyList(),
    val language: String?="",
    val pageCount: Int?=0,
    val id: String?="",
    val description: String?="",
    val categories: List<String> = emptyList()

)

data class ImageLinks(

    val thumbnail: String?=""
)
