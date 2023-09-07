package com.example.booktique.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.dataModel.LibriDaL
import com.example.booktique.adapter.MyAdapterDL
import com.example.booktique.R
import com.example.booktique.databinding.FragmentCatalogoDaLeggereBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CatalogoDaLeggere : Fragment() {

    private lateinit var binding: FragmentCatalogoDaLeggereBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterDL
    private lateinit var listaLibri: ArrayList<LibriDaL>
    private lateinit var select: Spinner
    private lateinit var viewModel: CatalogoViewModel
    private val sezioni = arrayListOf("In corso","Letti")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoDaLeggereBinding>(
                inflater,
                R.layout.fragment_catalogo_da_leggere, container, false
            )

        binding.backbuttonDl.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_catalogoDaLeggere_to_catalogoHome)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)

        // Inizializza la lista dei libri
        listaLibri = ArrayList()

        recyclerView = binding.listaLibriLeggere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        //passa l'arrayList listaLibri all'adapter
        adapter = MyAdapterDL(listaLibri)
        recyclerView.adapter = adapter

        Log.d("TAG", "ADAPTER")


        adapter.setOnCLickItemListener(object : MyAdapterDL.onItemClickListener {
            //implementiamo i metodi definiti nell'interfaccia dell'adapter
                override fun onItemClick(position: Int) {}

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
                                    viewModel.moveBooks(bookId, where, "da leggere")

                                    if(!where) {
                                        Toast.makeText(requireContext(), "Libro spostato nella sezione In Corso!", Toast.LENGTH_SHORT).show()
                                    } else{
                                        Toast.makeText(requireContext(), "Libro spostato nella sezione Letti!", Toast.LENGTH_SHORT).show()
                                    }
                                    val navController = findNavController()
                                    navController.navigate(R.id.action_catalogoDaLeggere_to_catalogoHome)

                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Seleziona un elemento!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            // Chiudi il dialog
                            dialog?.dismiss()
                        }

                        btnCancel.setOnClickListener {
                            dialog?.dismiss()
                        }

                        dialog = builder.create()
                        dialog?.show()
                    }
                }

            override fun dettaglioBook(cover: ImageButton, position: Int) {
                lifecycleScope.launch {
                    val libro = getLibro(position)

                    val navController = findNavController()
                    if (libro != null) {
                        val action =
                            CatalogoDaLeggereDirections.actionCatalogoDaLeggereToLibroDaLeggere(
                                libro,
                                "catalogoDaLeggere"
                            )
                        findNavController().navigate(action)
                    }
                }
            }

            })
            viewModel.checkBookCatalogo()
            if(!viewModel.libriDaLeggere.hasObservers()) {
                viewModel.libriDaLeggere.observe(viewLifecycleOwner, Observer { DaLeggereBooksList ->
                    loadBooks(DaLeggereBooksList)
                    Log.d("ciao", "ciao")
                })
        }

        }

    private fun loadBooks(books: List<LibriDaL>?){
        listaLibri.clear()
        if (books != null) {
            listaLibri.addAll(books)
            Log.d("lista", listaLibri.toString())
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

    private suspend fun getLibro(position: Int): LibriDaL? {
        val bookId = listaLibri[position].id
        var bookD: LibriDaL? = null

        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            Log.d("bookID", "$bookId")
            if (bookId != null) {
                val dataSnapshot = daLeggereRef.child(bookId).get().await()
                bookD = dataSnapshot.getValue(LibriDaL::class.java)
                Log.d("bookPrimaD", "$dataSnapshot")
            }
        }
        Log.d("bookD", "$bookD")
        return bookD
    }

}




/*private fun checkBookCatalogo() {
    if (FirebaseAuth.getInstance().currentUser != null) {
        val cUser = FirebaseAuth.getInstance().currentUser!!
        Log.d("TAG", "Sono qui:")
        val database =
            FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
        val usersRef = database.reference.child("Utenti")
        val childRef = usersRef.child(cUser.uid)
        val catalogoRef = childRef.child("Catalogo")
        val daLeggereRef = catalogoRef.child("DaLeggere")

        daLeggereRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
}*/