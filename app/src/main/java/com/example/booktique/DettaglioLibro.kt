package com.example.booktique

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.booktique.databinding.ActivityDettaglioLibroBinding
import com.example.booktique.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DettaglioLibro : AppCompatActivity() {
    private lateinit var binding: ActivityDettaglioLibroBinding
    private lateinit var cUser : FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDettaglioLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.buttonAggiungi.setOnClickListener {
            aggiungiLibro()
        }
    }

    private fun aggiungiLibro(){
            if(FirebaseAuth.getInstance().currentUser != null) {
                cUser = FirebaseAuth.getInstance().currentUser!!
                Log.d("TAG", "Sono qui")
                val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
                val usersRef = database.reference.child("Utenti")
                val childRef = usersRef.child(cUser.uid)
                val catalogoRef = childRef.child("Catalogo")
                val daLeggereRef = catalogoRef.child("DaLeggere")

                val libroLeg = LibriDaL(
                    binding.textView11.text.toString(),
                    binding.imageView3.toString(), //va fixato per prendere l'url
                    binding.textView9.text.toString()
                )

                val nuovoLibroRef = daLeggereRef.push()
                nuovoLibroRef.setValue(libroLeg)
            }else{
                Toast.makeText(
                    this,
                    "Errore",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}