package com.example.booktique

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoInCorsoBinding
import com.example.booktique.databinding.FragmentCatalogoLettiBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CatalogoLetti : Fragment() {
    private lateinit var binding: FragmentCatalogoLettiBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterL
    private lateinit var listaLibri: ArrayList<LibriL>
    private lateinit var select: Spinner
    private val sezioni = arrayListOf("Da Leggere","Letti")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoLettiBinding>(inflater,
            R.layout.fragment_catalogo_letti,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()

        recyclerView = binding.listaLibriLeggere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterL(listaLibri)
        recyclerView.adapter = adapter


        adapter.setOnCLickItemListener(object : MyAdapterL.onItemClickListener{
            override fun onItemClick(position: Int) {

            }

            override fun hideShow(element: LinearLayout, comment: ImageButton) {
                val linearL = element
                val btn = comment
                if(linearL.visibility == View.GONE) {
                    linearL.visibility = View.VISIBLE
                    btn.setBackgroundResource(R.drawable.comment_filled_icon)
                }
                else {
                    linearL.visibility = View.GONE
                    btn.setBackgroundResource(R.drawable.comment_icon)
                }

            }

            override fun likeDislike(like: ImageButton, dislike: ImageButton,position: Int) {
                val btnLike = like
                val btnDislike = dislike
                val bookPos = position
                val bookId = getIdPos(bookPos)

                btnLike.setOnClickListener {
                    if (listaLibri[position].valutazione == 1) {
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
                            val navController = findNavController()
                            navController.popBackStack()
                            navController.navigate(R.id.catalogoLetti)
                        }
                    }else{
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
                            val navController = findNavController()
                            navController.popBackStack()
                            navController.navigate(R.id.catalogoLetti)
                        }
                    }
                }

                btnDislike.setOnClickListener {
                    if (listaLibri[position].valutazione == 2) {
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
                            val navController = findNavController()
                            navController.popBackStack()
                            navController.navigate(R.id.catalogoLetti)
                        }
                    }else{
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
                            val navController = findNavController()
                            navController.popBackStack()
                            navController.navigate(R.id.catalogoLetti)
                        }
                    }

                }
            }

            override fun comment(recensione: TextInputLayout, position: Int) {
                val commento = recensione.editText
                val bookPos = position
                val bookId = getIdPos(bookPos)

                if (commento != null) {
                    commento.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {

                        }

                        override fun afterTextChanged(s: Editable?) {
                            if(s != null && s.isNotEmpty() && s.last() == '\n'){
                                s.replace(s.length - 1, s.length, "")
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
                                        val review = s.toString()
                                        lettiRef.child(bookId).child("recensione").setValue(review)
                                    }
                                    val navController = findNavController()
                                    navController.popBackStack()
                                    navController.navigate(R.id.catalogoLetti)
                                }
                            }

                        }


                    })
                }
            }
            override fun dettaglioBook(cover: ImageButton, position: Int) {
                val libro = getLibro(position)

                val navController = findNavController()
                val action = CatalogoLettiDirections.actionCatalogoLettiToLibroLetto(libro)
                findNavController().navigate(action)
            }

        })

        checkBookCatalogo()

    }

    private fun checkBookCatalogo() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")
            /*
            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")

             */

            lettiRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = arrayListOf<LibriL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val LibriL = bookSnapshot.getValue(LibriL::class.java)
                            Log.d("TAG", "VolumeDet : ${LibriL}")
                            lettiBooks.add(LibriL!!)

                        }
                    }

                    // Richiama la funzione per i libri "DaLeggere"

                    loadBooks(lettiBooks)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }

    private fun loadBooks(books: List<LibriL>?){
        if (books != null) {
            listaLibri.addAll(books)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getIdPos(position : Int): String? {
        if(listaLibri.isNotEmpty()){
            val bookId = listaLibri[position].id
            return bookId
        }
        return null
    }

    private fun getLibro(position: Int): LibriL {

        return listaLibri[position]

    }

}