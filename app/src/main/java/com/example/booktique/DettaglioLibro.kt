package com.example.booktique

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DettaglioLibro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettaglio_libro)

        val book = BookHolder.book

        if (book != null) {
            // Utilizza l'oggetto 'book' per impostare i valori nelle TextView e nell'ImageView
            findViewById<TextView>(R.id.textView11).text = book.title
            findViewById<TextView>(R.id.textView9).text = book.authors[0]
            // Imposta gli altri valori nelle TextView

            val imageView = findViewById<ImageView>(R.id.imageView3)
            Glide.with(this)
                .load(book.imageLinks.smallThumbnail)
                .into(imageView)
        }
    }
}