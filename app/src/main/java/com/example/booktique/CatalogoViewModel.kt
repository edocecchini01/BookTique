package com.example.booktique

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CatalogoViewModel: ViewModel() {

    private val _libriLetti = MutableLiveData<List<LibriL>>()
    val libriLetti: LiveData<List<LibriL>>
        get() = _libriLetti

    private val _libriDaLeggere = MutableLiveData<List<LibriDaL>>()
    val libriDaLeggere: LiveData<List<LibriDaL>>
        get() = _libriDaLeggere

    private val _libriInCorso = MutableLiveData<List<LibriInC>>()
    val libriInCorso: LiveData<List<LibriInC>>
        get() = _libriInCorso

    fun checkBookCatalogo(){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")



            daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val daLeggereBooks = mutableListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriDaL = bookSnapshot.getValue(LibriDaL::class.java)
                            Log.d("TAG", "VolumeDet : $libriDaL")
                            daLeggereBooks.add(libriDaL!!)

                        }
                    }
                    _libriDaLeggere.value = daLeggereBooks
                }
                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
            lettiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = mutableListOf<LibriL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val LibriL = bookSnapshot.getValue(LibriL::class.java)
                            lettiBooks.add(LibriL!!)
                        }
                    }
                    _libriLetti.value = lettiBooks
                }
                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
            inCorsoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val inCorsoBooks = mutableListOf<LibriInC>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriInC = bookSnapshot.getValue(LibriInC::class.java)
                            Log.d("TAG", "VolumeDet : $libriInC")
                            inCorsoBooks.add(libriInC!!)

                        }
                    }
                    _libriInCorso.value = inCorsoBooks
                    Log.d("LIbri", inCorsoBooks.toString())
                }
                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }

     fun removeBook(bookId : String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            lettiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val updatedLettiBooks = mutableListOf<LibriL>() // Lista aggiornata
                    for (childSnapshot in dataSnapshot.children) {
                        val libro = childSnapshot.getValue(LibriDaL::class.java)

                        if (libro != null && libro.id == bookId) {
                            val libroRef = childSnapshot.ref

                            libroRef.removeValue()

                            // Ignora il libro rimosso nella lista aggiornata
                        } else {
                            val libriL = childSnapshot.getValue(LibriL::class.java)
                            if (libriL != null) {
                                updatedLettiBooks.add(libriL)
                            }
                        }
                    }

                    // Aggiorna la MutableList nel ViewModel
                    _libriLetti.value = updatedLettiBooks
                }

                override fun onCancelled(error: DatabaseError) {
                    /*Toast.makeText(
                        requireContext(),
                        "Errore nello spostamento!",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }

            })
        }
    }

    fun comment(review : String, bookId: String){

        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            if (bookId != null) {
                lettiRef.child(bookId).child("recensione").setValue(review)
            }

        }
    }

    fun removelike(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            if (bookId != null) {
                val valutazione = 0
                lettiRef.child(bookId).child("valutazione").setValue(valutazione)
            }
        }
    }

    fun like(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            if (bookId != null) {
                val valutazione = 1
                lettiRef.child(bookId).child("valutazione").setValue(valutazione)
            }
        }
    }

    fun dislike(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            if (bookId != null) {
                val valutazione = 2
                lettiRef.child(bookId).child("valutazione").setValue(valutazione)
            }
        }
    }

}