package com.example.booktique

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.booktique.databinding.ActivityDettaglioLibroBinding
import com.example.booktique.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DettaglioLibro : AppCompatActivity() {
    private lateinit var binding: ActivityDettaglioLibroBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDettaglioLibroBinding.inflate(layoutInflater)
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
        aggiungiLibro()

    }

    private fun aggiungiLibro(){
        Log.d("TAG", "Messaggio di debug")
        binding.buttonAggiungi.setOnClickListener {
            Log.d("TAG", "Messaggio di debug")
            database = FirebaseDatabase.getInstance().getReference("Utenti").child("user1")

            val libroLeg = LibriDaL(
                binding.textView11.toString(),
                binding.imageView3.toString(),
                binding.textView9.toString()
            )

            val libriRef = database.child("DaLeggere")
            val nuovoLibroRef = libriRef.push()
            nuovoLibroRef.setValue(libroLeg)
        }
    }
}