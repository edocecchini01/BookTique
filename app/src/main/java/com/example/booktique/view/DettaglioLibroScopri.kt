package com.example.booktique.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.view.DettaglioLibroScopriArgs
import com.example.booktique.R
import com.example.booktique.databinding.FragmentDettaglioLibroScopriBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class DettaglioLibroScopri : Fragment() {

    private lateinit var binding: FragmentDettaglioLibroScopriBinding
    private lateinit var cUser : FirebaseUser
    private val args by navArgs<DettaglioLibroScopriArgs>()
    private var allBookUser = mutableListOf<String?>()
    private lateinit var viewModel: CatalogoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentDettaglioLibroScopriBinding>(
            inflater,
            R.layout.fragment_dettaglio_libro_scopri, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)


        binding.titolo.text = args.LibroLeggere.titolo
        binding.autore.text = args.LibroLeggere.autori
        binding.descrizione.text = args.LibroLeggere.descrizione


        val imageView = binding.copertina
        Glide.with(this)
            .load(args.LibroLeggere.copertina)
            .into(imageView)

        // Gestione del click sul pulsante "Aggiungi"

        binding.buttonAggiungi.setOnClickListener {
            lifecycleScope.launch {
                val check = viewModel.addBook(args.LibroLeggere)
                if (check == true) {
                    val grayColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    ) // Ottieni il colore grigio dal tuo file di risorse colors.xml
                    val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

                    binding.buttonAggiungi.isEnabled = false
                    binding.buttonAggiungi.text = "Aggiunto"
                    binding.buttonAggiungi.setBackgroundColor(grayColor)
                    binding.buttonAggiungi.setTextColor(whiteColor)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Errore durante l'aggiunta del libro",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.imageButton2.setOnClickListener {
                val navController = findNavController()
                navController.popBackStack()
            }

        userBook()

    }


    // Verifica se il libro è già stato aggiunto al catalogo dell'utente
    private fun checkBookAdded() {

        val bookId = args.LibroLeggere.id
        if (allBookUser.contains(bookId)) {
            val grayColor = ContextCompat.getColor(
                requireContext(),
                R.color.gray
            )
            val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

            binding.buttonAggiungi.isEnabled = false
            binding.buttonAggiungi.text = "Aggiunto"
            binding.buttonAggiungi.setBackgroundColor(grayColor)
            binding.buttonAggiungi.setTextColor(whiteColor)
        }
    }

    // Carica i libri dell'utente e aggiunge i loro ID a allBookUser
    private fun userBook(){
        viewModel.checkBookCatalogo()
        if (!viewModel.libriDaLeggere.hasObservers()) {
            viewModel.libriDaLeggere.observe(viewLifecycleOwner, Observer { DaLeggereBooksList ->
                allBookUser.addAll(DaLeggereBooksList.map { it.id })
                checkBookAdded()
            })
        }

        if (!viewModel.libriInCorso.hasObservers()) {
            viewModel.libriInCorso.observe(viewLifecycleOwner, Observer { InCorsoBooksList ->
                allBookUser.addAll(InCorsoBooksList.map { it.id })
                checkBookAdded()
            })
        }

        if (!viewModel.libriLetti.hasObservers()) {
            viewModel.libriLetti.observe(viewLifecycleOwner, Observer { LettiBooksList ->
                allBookUser.addAll(LettiBooksList.map { it.id })
                checkBookAdded()
            })
        }


    }


}