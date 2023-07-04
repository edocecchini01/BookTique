package com.example.booktique

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoInCorsoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CatalogoInCorso : Fragment() {
    private lateinit var binding: FragmentCatalogoInCorsoBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterIC
    private lateinit var listaLibri: ArrayList<LibriInC>
    private lateinit var select: Spinner
    private val sezioni = arrayListOf("Letti","Da Leggere")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoInCorsoBinding>(inflater,
            R.layout.fragment_catalogo_in_corso,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()

        recyclerView = binding.listaLibriCorso
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterIC(listaLibri)
        recyclerView.adapter = adapter


        adapter.setOnCLickItemListener(object : MyAdapterIC.onItemClickListener{
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

                            if(selectedItem == "Letti")
                                where = false
                            if(selectedItem == "Da Leggere")
                                where = true

                            if (bookId != null) {
                                Log.d("TAG", "idLibro: $bookId")
                                moveBooks(bookId,where)
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

            override fun reading(seekBar: SeekBar, pagAtt: TextView, pagTot : TextView, element: LinearLayout, position: Int) {
                val seek = seekBar
                val pagA = pagAtt
                val pagT = pagTot
                seek.max = pagT.text.toString().toInt()
                val linearL = element
                val bookPos = position
                val bookId = getIdPos(bookPos)

                if(linearL.visibility == View.VISIBLE) {
                    seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            pagA.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            if (FirebaseAuth.getInstance().currentUser != null) {
                                val cUser = FirebaseAuth.getInstance().currentUser!!
                                Log.d("TAG", "Sono :")
                                val database =
                                    FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
                                val usersRef = database.reference.child("Utenti")
                                val childRef = usersRef.child(cUser.uid)
                                val catalogoRef = childRef.child("Catalogo")
                                val inCorsoRef = catalogoRef.child("InCorso")

                                if (bookId != null) {
                                    val pagAconv = pagA.text.toString()
                                    inCorsoRef.child(bookId).child("paginaAtt").setValue(pagAconv.toInt())
                                }
                                //metodo da rivedere
                                val navController = findNavController()
                                navController.popBackStack()
                                navController.navigate(R.id.catalogoInCorso)
                            }

                            if(pagA.text == pagT.text && bookId != null){
                                moveBooks(bookId,false)
                                Toast.makeText(
                                    requireContext(),
                                    "Complimenti hai terminato la tua lettura!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    })
                }
            }

            override fun hideShow(element: LinearLayout, arrow : ImageButton) {
                val linearL = element
                val btn = arrow
                if(linearL.visibility == View.GONE) {
                    val rotateAnimation = ObjectAnimator.ofFloat(btn, "rotation", 0f, 180f)
                    rotateAnimation.duration = 100
                    rotateAnimation.start()
                    linearL.visibility = View.VISIBLE
                }
                else {
                    val rotateAnimation = ObjectAnimator.ofFloat(btn, "rotation", 180f, 0f)
                    rotateAnimation.duration = 100
                    rotateAnimation.start()
                    linearL.visibility = View.GONE
                }


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
            val inCorsoRef = catalogoRef.child("InCorso")
            /*
            val lettiRef = catalogoRef.child("Letti")
            val inCorsoRef = catalogoRef.child("InCorso")

             */

            inCorsoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val inCorsoBooks = arrayListOf<LibriInC>()
                    if (snapshot.exists()) {
                        Log.d("TAG", "VolumeDet : $inCorsoBooks")
                        for (bookSnapshot in snapshot.children) {
                            val LibriInC = bookSnapshot.getValue(LibriInC::class.java)
                            Log.d("TAG", "VolumeDet : $LibriInC")
                            inCorsoBooks.add(LibriInC!!)

                        }
                    }

                    // Richiama la funzione per i libri "DaLeggere"

                    loadBooks(inCorsoBooks)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori nella lettura dei dati
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            })
        }
    }

    private fun loadBooks(books: List<LibriInC>?){
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
                inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                lettiRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
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
                        TODO("Not yet implemented")
                    }

                })
        } else{
                inCorsoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val libro = childSnapshot.getValue(LibriDaL::class.java)

                            if (libro != null && libro.id == bookId) {

                                // Hai individuato il libro desiderato
                                Log.d("Libro", "Libro trovato: $libro")
                                daLeggereRef.child(bookId).setValue(libro)
                                val libroRef = childSnapshot.ref
                                Log.d("Libro", "Libro da eliminare: $libro")
                                libroRef.removeValue()
                                Toast.makeText(
                                    requireContext(),
                                    "${libro.titolo?.take(50)}, spostato in \"Da leggere\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = findNavController()
                                navController.navigate(R.id.action_catalogoInCorso_to_catalogoHome)


                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

        }
        }

    }


}