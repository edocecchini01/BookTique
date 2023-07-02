package com.example.booktique

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
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
        checkBookCatalogo()

        adapter.setOnCLickItemListener(object : MyAdapterIC.onItemClickListener{
            override fun onItemClick(position: Int) {

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
            Log.d("TAG","LIBRI: $listaLibri" )
            adapter = MyAdapterIC(listaLibri)
            Log.d("TAG","LIBRI:11: $listaLibri" )
            recyclerView.adapter = adapter
        }
    }


}