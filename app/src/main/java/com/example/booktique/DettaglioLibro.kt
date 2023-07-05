package com.example.booktique

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.booktique.databinding.ActivityDettaglioLibroBinding
import com.example.booktique.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.databinding.DataBindingUtil


private lateinit var database: DatabaseReference
class DettaglioLibro : AppCompatActivity() {

    private lateinit var binding: ActivityDettaglioLibroBinding
    private lateinit var cUser : FirebaseUser
    private val book = BookHolder.book
    private val libroIncorso = BookHolder.libroInc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDettaglioLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (book != null) {
            Log.d("TAG", "Sono $libroIncorso")


            // Utilizza l'oggetto 'book' per impostare i valori nelle TextView e nell'ImageView
            findViewById<TextView>(R.id.textView11).text = book.title
            findViewById<TextView>(R.id.textView9).text = book.authors[0]
            // Imposta gli altri valori nelle TextView

            val imageView = findViewById<ImageView>(R.id.imageView3)
            Glide.with(this)
                .load(book.imageLinks.smallThumbnail)
                .into(imageView)

            checkBookAdded()

            binding.buttonAggiungi.setOnClickListener {
                aggiungiLibro()
            }

        }else if (libroIncorso!=null){
            val imageView = findViewById<ImageView>(R.id.imageView3)
            Glide.with(this)
                .load(libroIncorso.copertina)
                .into(imageView)

            Log.d("TAG", "Sono qui $libroIncorso")

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
                var link = ""
                var pag = 0
                var id = ""
                if (book!=null){
                    link = book.imageLinks?.smallThumbnail ?: ""
                    pag = book.pageCount?: 0
                    id = book.id ?: ""
                }
                Log.d("TAG", "Sono qui: $link")

                val libroLeg = LibriDaL(
                    binding.textView11.text.toString(),
                    link,
                    binding.textView9.text.toString(),
                    pag,
                    id
                )

                val nuovoLibroRef = daLeggereRef.push()
                nuovoLibroRef.setValue(libroLeg)
                    .addOnSuccessListener {
                        val grayColor = ContextCompat.getColor(this, R.color.gray) // Ottieni il colore grigio dal tuo file di risorse colors.xml
                        val whiteColor = ContextCompat.getColor(this, R.color.white)

                        binding.buttonAggiungi.isEnabled = false
                        binding.buttonAggiungi.text = "Aggiunto"
                        binding.buttonAggiungi.setBackgroundColor(grayColor)
                        binding.buttonAggiungi.setTextColor(whiteColor)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Errore durante l'aggiunta del libro",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


            }else{
                Toast.makeText(
                    this,
                    "Errore",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

    private fun checkBookAdded() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            // Recupera l'ID del libro corrente
            val bookId = book?.id ?: ""

            Log.d("TAG", "Sono : $bookId")
            // Verifica se il libro è stato aggiunto dall'utente
            daLeggereRef.orderByChild("id").equalTo(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookExists = snapshot.exists()

                    // Imposta lo stato del pulsante "Aggiungi" in base al libro aggiunto
                    binding.buttonAggiungi.isEnabled = !bookExists

                    if (bookExists) {
                        val grayColor = ContextCompat.getColor(this@DettaglioLibro, R.color.gray) // Ottieni il colore grigio dal tuo file di risorse colors.xml
                        val whiteColor = ContextCompat.getColor(this@DettaglioLibro, R.color.white)

                        binding.buttonAggiungi.isEnabled = false
                        binding.buttonAggiungi.text = "Aggiunto"
                        binding.buttonAggiungi.setBackgroundColor(grayColor)
                        binding.buttonAggiungi.setTextColor(whiteColor)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }
}