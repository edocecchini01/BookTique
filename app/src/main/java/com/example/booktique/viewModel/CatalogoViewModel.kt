package com.example.booktique.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booktique.dataModel.LibriDaL
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.dataModel.LibriL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

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

    // estrazione dal DB di tutti i libri presenti nel catalogo personale dell'utente autenticato,
    // inserimento degli stessi nelle liste MutableLiveData suddivisi per categorie
    fun checkBookCatalogo(){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")

            lettiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = mutableListOf<LibriL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val LibriL = bookSnapshot.getValue(LibriL::class.java)
                            if(LibriL!=null) {
                                lettiBooks.add(LibriL)
                            }
                        }
                    }
                    _libriLetti.value = lettiBooks
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
            inCorsoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val inCorsoBooks = mutableListOf<LibriInC>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriInC = bookSnapshot.getValue(LibriInC::class.java)
                            if (libriInC!=null) {
                                inCorsoBooks.add(libriInC)
                            }
                        }
                    }
                    _libriInCorso.value = inCorsoBooks
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
            daLeggereRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val daLeggereBooks = mutableListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriDaL = bookSnapshot.getValue(LibriDaL::class.java)
                            if (libriDaL != null) {
                                daLeggereBooks.add(libriDaL)
                            }

                        }
                    }
                    _libriDaLeggere.value = daLeggereBooks
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }

    //aggiunta nel DB di un libro all'interno del catalogo personale nel nodo "da leggere" dell'utente autenticato
    suspend fun addBook(libro: LibriDaL): Boolean{
        var check = false
        if(FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            if (libro.id!=null) {
                val nuovoLibroRef = daLeggereRef.child(libro.id)
                try {
                    nuovoLibroRef.setValue(libro).await()
                    check=true
                } catch (e: Exception) {
                    check = false
                }
            }
        }
        return check
    }

    // rimozione dal DB di un libro specifico dal catalogo personale dell'utente autenticato
     fun removeBook(bookId : String, tipologia: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")
            val daLeggereRef = catalogoRef.child("DaLeggere")

            if (tipologia == "letti") {
                lettiRef.addValueEventListener(object : ValueEventListener {
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
            }else if (tipologia == "In Corso"){
                inCorsoRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val uptatedInCorsoBooks = mutableListOf<LibriInC>()
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriInC::class.java)

                            if (libro != null && libro.id == bookId) {
                                val libroRefInC = childSnapshot.ref

                                libroRefInC.removeValue()

                            } else {
                                val libriInC = childSnapshot.getValue(LibriInC::class.java)
                                if (libriInC != null) {
                                    uptatedInCorsoBooks.add(libriInC)
                                }
                            }

                        }
                        _libriInCorso.value = uptatedInCorsoBooks
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            } else if (tipologia == "da leggere"){
                daLeggereRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val updatedDaLeggereBooks = mutableListOf<LibriDaL>()
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {
                                val libroRef = childSnapshot.ref

                                libroRef.removeValue()

                            }else {
                                val libriDaL = childSnapshot.getValue(LibriDaL::class.java)
                                if (libriDaL != null) {
                                    updatedDaLeggereBooks.add(libriDaL)
                                }
                            }
                        }
                        _libriDaLeggere.value = updatedDaLeggereBooks
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }
        }
    }

    //spostamento di un libro specifico tra le varie sezioni del catalogo
    fun moveBooks(bookId : String, where : Boolean, lista: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            val inCorsoRef = catalogoRef.child("InCorso")
            val lettiRef = catalogoRef.child("Letti")


            if (lista == "in corso") {
                    inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (childSnapshot in dataSnapshot.children) {
                                val libro = childSnapshot.getValue(LibriInC::class.java)

                                if (libro != null && libro.id == bookId) {
                                    // Hai individuato il libro desiderato
                                    if(!where) {
                                        lettiRef.child(bookId).setValue(libro)
                                    }else if (where){
                                        daLeggereRef.child(bookId).setValue(libro)
                                    }

                                    val libroRef = childSnapshot.ref
                                    libroRef.removeValue()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

            }else if (lista == "da leggere"){
                    daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            for (childSnapshot in dataSnapshot.children) {
                                val libro = childSnapshot.getValue(LibriDaL::class.java)

                                if (libro != null && libro.id == bookId) {

                                    // Hai individuato il libro desiderato
                                    if(!where) {
                                        inCorsoRef.child(bookId).setValue(libro)
                                    }else if (where){
                                        lettiRef.child(bookId).setValue(libro)
                                    }
                                    val libroRef = childSnapshot.ref
                                    libroRef.removeValue()
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                }

            }
        }

    //aggiornamento del campo "recensione" di un libro della sezione "letti"
    fun comment(review : String, bookId: String){

        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
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

    //aggiornamento del campo "valutazione" di un libro della sezione "letti" con valore 0 (valore neutro)
    fun removelike(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
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

    //aggiornamento del campo "valutazione" di un libro della sezione "letti" con valore 1 (valore positivo/like)
    fun like(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
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

    //aggiornamento del campo "valutazione" di un libro della sezione "letti" con valore 2 (valore negativo/dislike)
    fun dislike(bookId: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
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

    //aggiornamento del campo "paginaAtt" di un libro della sezione "in corso"
    fun numPage(bookId: String, pagAconv: String){
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val inCorsoRef = catalogoRef.child("InCorso")

            if (bookId != null) {
                inCorsoRef.child(bookId).child("paginaAtt").setValue(pagAconv.toInt())
            }
        }
    }


}