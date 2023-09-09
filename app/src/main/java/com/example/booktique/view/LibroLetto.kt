package com.example.booktique.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.dataModel.LibriL
import com.example.booktique.R
import com.example.booktique.databinding.FragmentLibroLettoBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseUser

class LibroLetto : Fragment() {
    private lateinit var binding: FragmentLibroLettoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroLet: LibriL
    private lateinit var activity : FragmentActivity
    private lateinit var viewModel: CatalogoViewModel

    private val args by navArgs<LibroLettoArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroLettoBinding>(
            inflater,
            R.layout.fragment_libro_letto, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)
        activity = requireActivity()
        var valutazione = args.LibroLett.valutazione
        var review = args.LibroLett.recensione

        val imageView = binding.imageView3
        Glide.with(this)
            .load(args.LibroLett.copertina)
            .into(imageView)

        binding.textView22.text = args.LibroLett.titolo
        binding.textView23.text = args.LibroLett.autori

        binding.textInputLayout.editText?.setText(review)

        graphicLike(valutazione)

        val bookId = args.LibroLett.id

        //gestione dei pulsanti mi piace/non mi piace
        binding.likeL.setOnClickListener {
            if (valutazione == 1) {
                if (bookId != null) {
                    viewModel.removelike(bookId)
                    valutazione=0
                    graphicLike(valutazione)
                }
            }else{
                if (bookId != null) {
                    viewModel.like(bookId)
                    valutazione=1
                    graphicLike(valutazione)
                }
            }
        }

        binding.dislikeL.setOnClickListener {
            if (valutazione == 2) {
                if (bookId != null) {
                    valutazione = 0
                    viewModel.removelike(bookId)
                    graphicLike(valutazione)
                }
            }else{
                if (bookId != null) {
                    valutazione = 2
                    viewModel.dislike(bookId)
                    graphicLike(valutazione)
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
                        val review = s.toString()
                        if (bookId != null) {
                            viewModel.comment(review, bookId)
                        }
                    }

                }
            })
        }


        val origin = args.origin
        //gestione del pulsante indietro in base alla pagina di provenienza
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
        //gestione del pulsante elimina
        binding.elimina.setOnClickListener {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialog_elimina, null)
            val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
            builder.setView(dialogView)

            btnConfirm.setOnClickListener {
                if (bookId != null) {
                    viewModel.removeBook(bookId, "letti")
                    val navController = findNavController()
                    navController.navigate(R.id.action_libroLetto_to_catalogoHome)
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Errore nell'eliminazione!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog?.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }

            dialog = builder.create()
            dialog?.show()
        }
    }
    //gestisce la grafica dei pulsanti raffigurante un ollice in su
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