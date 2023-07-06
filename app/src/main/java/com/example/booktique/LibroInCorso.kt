package com.example.booktique

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LibroInCorso : Fragment() {
    private lateinit var binding: FragmentLibroInCorsoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroIncorso: LibriInC
    private lateinit var activity : FragmentActivity

    private val args by navArgs<LibroInCorsoArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroInCorsoBinding>(inflater,
            R.layout.fragment_libro_in_corso,container,false)

        return binding.root
    }


    companion object {
        fun newInstance(libroIncorso: LibriInC): LibroInCorso {
            val fragment = LibroInCorso()
            fragment.libroIncorso = libroIncorso
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity()
        val bookId = args.LibroInC.id
        val imageView = binding.imageView3
        Glide.with(this)
            .load(args.LibroInC.copertina)
            .into(imageView)

        binding.textView22.text = args.LibroInC.titolo
        binding.textView23.text = args.LibroInC.autori
        binding.textView13.text = args.LibroInC.descrizione

        if (args.LibroInC.pagineTot != 0){
            binding.textView26.text = args.LibroInC.pagineTot.toString()
        binding.seekBar4.max = args.LibroInC.pagineTot!!
        binding.seekBar4.progress = args.LibroInC.paginaAtt!!
        binding.textView25.text = args.LibroInC.paginaAtt.toString()


        binding.seekBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.textView25.text = progress.toString()
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
                        val pagAconv = binding.textView25.text.toString()
                        inCorsoRef.child(bookId).child("paginaAtt").setValue(pagAconv.toInt())
                    }
                }

                if (binding.textView25.text == binding.textView26.text && bookId != null) {
                    moveBooks(bookId)
                    Toast.makeText(
                        activity,
                        "Complimenti hai terminato la tua lettura!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }else{
            binding.seekBar4.visibility = View.GONE
            binding.textView26.visibility = View.GONE
            binding.textView25.text = "Pagine totali non disponibili"
            binding.textView25.gravity = Gravity.CENTER
    }
        val origin = args.origin

        if (origin == "catalogoInCorso") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroInCorso_to_catalogoInCorso)
            }
        }
        else if (origin == "catalogoHome") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroInCorso_to_catalogoHome)
            }
        }


    }

    private fun moveBooks(bookId : String) {
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
                                    activity,
                                    "${libro.titolo?.take(50)}, spostato in \"Letti\"",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val navController = Navigation.findNavController(activity, R.id.fragmentContainerView)
                                navController.navigate(R.id.action_libroInCorso_to_catalogoLetti)

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