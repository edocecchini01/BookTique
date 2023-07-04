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
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
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

        Log.d("TAG", "ADAPTER")
        adapter.setOnCLickItemListener(object : MyAdapterDL.onItemClickListener {
                override fun onItemClick(position: Int) {

                }

                override fun moveBook(send: ImageButton, position: Int) {
                    val btn = send

                    btn.setOnClickListener {
                        val bookPos = position
                        val bookId = getIdPos(bookPos)
                        var dialog: AlertDialog? = null
                        val builder = AlertDialog.Builder(requireContext())
                        val dialogView = layoutInflater.inflate(R.layout.move_spinner, null)
                        select = dialogView.findViewById<Spinner>(R.id.spinner)
                        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
                        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
                        builder.setView(dialogView)

                        val arrayAdapter = ArrayAdapter<String>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item
                        )
                        select.adapter = arrayAdapter
                        arrayAdapter.addAll(sezioni)

                        btnConfirm.setOnClickListener {
                            // Verifica se è stato selezionato un elemento
                            if (select.selectedItem != null) {
                                val selectedItem = select.selectedItem.toString()
                                var where = false

                                if(selectedItem == "In corso")
                                    where = false
                                if(selectedItem == "Letti")
                                    where = true

                                if (bookId != null) {
                                    Log.d("TAG", "idLibro: $bookId")
                                    moveBooks(bookId, where)
                                    adapter.notifyDataSetChanged()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Seleziona un elemento!", Toast.LENGTH_SHORT).show()
                            }
                            // Chiudi il dialog
                            dialog?.dismiss()
                        }

                        dialog = builder.create()
                        dialog?.show()
                    }
                }

            })

        checkBookCatalogo()

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
            adapter.notifyDataSetChanged()
        }
    }

    private fun moveBooks(bookId : String, where : Boolean) {
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

            Log.d("TAG", "bookId: $bookId")
            if (!where){
                daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                inCorsoRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
                                libroRef.removeValue()
                                Toast.makeText(
                                    requireContext(),
                                    "${libro.titolo?.take(50)}, spostato in \"In corso\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = findNavController()
                                navController.navigate(R.id.action_catalogoDaLeggere_to_catalogoHome)

                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            requireContext(),
                            "Errore nello spostamento!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }else{
                daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro","Libro trovato: $libro")
                                lettiRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro","Libro da eliminare: $libro")
                                libroRef.removeValue()
                                Toast.makeText(
                                    requireContext(),
                                    "${libro.titolo?.take(50)}, spostato in \"Letti\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = findNavController()
                                navController.navigate(R.id.action_catalogoInCorso_to_catalogoHome)

                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            requireContext(),
                            "Errore nello spostamento!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
        }

    }

    private fun getIdPos(position : Int): String? {
        if(listaLibri.isNotEmpty()){
            val bookId = listaLibri[position].id
            return bookId
        }
        return null
    }

}