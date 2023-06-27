package com.example.booktique
import android.icu.text.CaseMap.Title
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeDet,
    val id: String
)

data class VolumeDet(
    val imageLinks: ImageLinks,
    val title: String,
    val authors: List<String>,
    val description: String,
    val publisher: String,
    val categories: List<String>,
    val language: String
)

data class ImageLinks(

    val smallThumbnail: String
)