package com.example.booktique

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoDaLeggereBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CatalogoDaLeggere : Fragment() {

    private lateinit var binding: FragmentCatalogoDaLeggereBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterDL
    private lateinit var listaLibri: ArrayList<LibriDaL>
    private lateinit var select: Spinner
    private val sezioni = arrayListOf("In corso","Letti")
    private var isRecyclerViewPopulated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoDaLeggereBinding>(inflater,
            R.layout.fragment_catalogo_da_leggere,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza la lista dei libri
        listaLibri = ArrayList()

        recyclerView = binding.listaLibriLeggere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        adapter = MyAdapterDL(listaLibri)
        recyclerView.adapter = adapter

        checkBookCatalogo()

        if(isRecyclerViewPopulated) {
            adapter.setOnCLickItemListener(object : MyAdapterDL.onItemClickListener {
                override fun onItemClick(position: Int) {

                }

                override fun moveBook(spinner: Spinner, send: ImageButton) {
                    select = spinner
                    val btn = send
                    val arrayAdapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item
                    )
                    select.adapter = arrayAdapter
                    arrayAdapter.addAll(sezioni)
                    select.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedItem = parent?.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            Toast.makeText(
                                requireContext(),
                                "Seleziona una sezione!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                    }
                    btn.setOnClickListener {
                        if (select.visibility == View.INVISIBLE) {
                            select.visibility = View.VISIBLE
                        } else {
                            select.visibility = View.INVISIBLE
                        }
                    }
                }

            })

        }
    }
    //va fattorizzato
    private fun checkBookCatalogo() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            /*
            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")

             */

            daLeggereRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val daLeggereBooks = arrayListOf<LibriDaL>()
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libriDaL = bookSnapshot.getValue(LibriDaL::class.java)
                            Log.d("TAG", "VolumeDet : $libriDaL")
                            daLeggereBooks.add(libriDaL!!)

                        }
                    }

                    // Richiama la funzione per i libri "DaLeggere"

                    loadBooks(daLeggereBooks)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }

    private fun loadBooks(books: List<LibriDaL>?){
        if (books != null) {
            listaLibri.addAll(books)
            Log.d("TAG","LIBRI: $listaLibri" )
            adapter = MyAdapterDL(listaLibri)
            Log.d("TAG","LIBRI:11: $listaLibri" )
            recyclerView.adapter = adapter
            isRecyclerViewPopulated = true
        }
    }

}