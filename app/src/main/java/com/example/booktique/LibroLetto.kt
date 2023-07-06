package com.example.booktique

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.example.booktique.databinding.FragmentLibroLettoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class LibroLetto : Fragment() {
    private lateinit var binding: FragmentLibroLettoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroLet: LibriL
    private lateinit var activity : FragmentActivity

    private val args by navArgs<LibroLettoArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroLettoBinding>(inflater,
            R.layout.fragment_libro_letto,container,false)

        return binding.root
    }


    companion object {
        fun newInstance(libroLetto: LibriL): LibroLetto {
            val fragment = LibroLetto()
            fragment.libroLet = libroLetto
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = requireActivity()
        var valutazione = args.LibroLett.valutazione
        var review = args.LibroLett.recensione

        val imageView = binding.imageView3
        Glide.with(this)
            .load(args.LibroLett.copertina)
            .into(imageView)

        binding.textView22.text = args.LibroLett.titolo
        binding.textView23.text = args.LibroLett.autori
        binding.textView13.text = args.LibroLett.descrizione

        binding.textInputLayout.editText?.setText(review)

        graphicLike(valutazione)

        val bookId = args.LibroLett.id


        binding.likeL.setOnClickListener {
            if (valutazione == 1) {
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
                            valutazione = 0
                        graphicLike(valutazione)
                        lettiRef.child(bookId).child("valutazione").setValue(valutazione)
                    }
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
                            valutazione = 1
                        graphicLike(valutazione)
                        lettiRef.child(bookId).child("valutazione").setValue(valutazione)
                    }
                }
            }
        }

        binding.dislikeL.setOnClickListener {
            if (valutazione == 2) {
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
                            valutazione = 0
                        graphicLike(valutazione)
                        lettiRef.child(bookId).child("valutazione").setValue(valutazione)
                    }

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
                            valutazione = 2
                        graphicLike(valutazione)
                        lettiRef.child(bookId).child("valutazione").setValue(valutazione)
                    }
                }
            }

        }

        if (binding.textInputLayout.editText != null) {
            binding.textInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
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
                                    review = s.toString()
                                lettiRef.child(bookId).child("recensione").setValue(review)
                            }

                        }
                    }

                }

            })
        }


        val origin = args.origin

        if (origin == "catalogoLetti") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroLetto_to_catalogoLetti)
            }
        }
        else if (origin == "catalogoHome") {
            binding.imageButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroLetto_to_catalogoHome)
            }
        }

    }

    fun graphicLike(valAtt : Int?){
        if(valAtt == 0){
            binding.likeL.setImageResource(R.drawable.pollice_icon)
            binding.dislikeL.setImageResource(R.drawable.pollice_icon)
        }else if(valAtt == 1){
            binding.likeL.setImageResource(R.drawable.like_click_icon)
            binding.dislikeL.setImageResource(R.drawable.pollice_icon)
        }else{
            binding.dislikeL.setImageResource(R.drawable.dislike_click)
            binding.likeL.setImageResource(R.drawable.pollice_icon)
        }
    }
}