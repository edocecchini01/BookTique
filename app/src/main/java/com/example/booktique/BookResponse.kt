package com.example.booktique
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeDet
)

data class VolumeDet(
    val imageLinks: ImageLinks,
)

data class ImageLinks(

    val smallThumbnail: String
)