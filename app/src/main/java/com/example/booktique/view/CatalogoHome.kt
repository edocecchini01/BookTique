package com.example.booktique.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.booktique.dataModel.LibriDaL
import com.example.booktique.dataModel.LibriInC
import com.example.booktique.dataModel.LibriL
import com.example.booktique.R
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.viewModel.CatalogoViewModel
import com.google.firebase.auth.FirebaseAuth

class CatalogoHome : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBinding
    private lateinit var activity : FragmentActivity
    private lateinit var viewModel: CatalogoViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCatalogoHomeBinding>(
            inflater,
            R.layout.fragment_catalogo_home, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogoViewModel::class.java)

        activity = binding.root.context as FragmentActivity
        val fragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

        if (fragment is NavHostFragment) {
            val navController = fragment.navController
            binding.daLeggereLabel.setOnClickListener {
                navController.navigate(R.id.action_catalogoHome_to_catalogoDaLeggere)
            }
            binding.inCorsoLabel.setOnClickListener {
                navController.navigate(R.id.action_catalogoHome_to_catalogoInCorso)
            }
            binding.lettiLabel.setOnClickListener {
                navController.navigate(R.id.action_catalogoHome_to_catalogoLetti)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Errore nella navigazione!",
                Toast.LENGTH_SHORT,
            ).show()
        }

        //val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        //val navController = navHostFragment.navController

        viewModel.checkBookCatalogo()

        viewModel.libriLetti.observe(viewLifecycleOwner, Observer { LettiBooksList ->
            loadImagesIntoImageButtonsLetti(LettiBooksList)
        })


        viewModel.libriDaLeggere.observe(viewLifecycleOwner, Observer { DaLeggereBooksList ->
                Log.d("da leggere", "$DaLeggereBooksList")
                loadImagesIntoImageButtonsDaLeggere(DaLeggereBooksList)
            })


            viewModel.libriInCorso.observe(viewLifecycleOwner, Observer { InCorsoBooksList ->
                Log.d("incorso", "$InCorsoBooksList")
                loadImagesIntoImageButtonsInCorso(InCorsoBooksList)
            })




        if (FirebaseAuth.getInstance().currentUser != null) {
            binding.myButton.setOnClickListener {
                if (fragment is NavHostFragment) {
                    val navController = fragment.navController
                    navController.navigate(R.id.action_catalogoHome_to_impostazioni)
                }else {
                    Toast.makeText(
                        requireContext(),
                        "Errore nella navigazione!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        } else {
            if (fragment is NavHostFragment) {
                val navController = fragment.navController
                navController.navigate(R.id.catalogoHomeBlock)
                binding.myButton.setOnClickListener {
                    navController.navigate(R.id.action_catalogoHomeBlock_to_impostazioni)
                }
            }else {
                Toast.makeText(
                    requireContext(),
                    "Errore nella navigazione!",
                    Toast.LENGTH_SHORT,
                ).show()
            }

        }
    }


    private fun loadImagesIntoImageButtonsDaLeggere(books: List<LibriDaL>?) {
        val imageButtons = listOf(
            binding.bookLetti1,
            binding.bookLetti2,
            binding.bookLetti3,
            binding.bookLetti4,
            binding.bookLetti5,
            binding.bookLetti6
        )

        // Verifica che la lista dei libri non sia nulla
        if (books != null) {
            for (i in 0 until minOf(books.size, 6)) {
                val book = books[i]
                val imageUrl = book.copertina
                Log.d("Image", "imageUrl: $imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(activity)
                        .load(imageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                e?.let {
                                    // Ottieni la lista delle cause radice dell'eccezione
                                    val rootCauses = e.rootCauses
                                    for (cause in rootCauses) {
                                        // Stampa le informazioni sulla causa dell'errore
                                        Log.e("Glide1", "Root cause: ${cause.message}")
                                    }
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // L'immagine è stata caricata con successo
                                return false
                            }
                        })
                        .into(imageButtons[i])

                    setupImageButtonClickListenerDaLeggere(book, imageButtons[i])
                }
            }
        }else{

        }
    }


    private fun loadImagesIntoImageButtonsInCorso(books: List<LibriInC>?) {
        val imageButtons = listOf(
            binding.bookInC1,
            binding.bookInC2,
            binding.bookInC3,
            binding.bookInC4,
            binding.bookInC5,
            binding.bookInC6
        )

        // Verifica che la lista dei libri non sia nulla e contenga almeno 6 elementi
        if (books != null) {
            for (i in 0 until minOf(books.size, 6)) {
                val book = books[i]
                val imageUrl = book.copertina
                Log.d("Image", "imageUrl: $imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(activity)
                        .load(imageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                e?.let {
                                    // Ottieni la lista delle cause radice dell'eccezione
                                    val rootCauses = e.rootCauses
                                    for (cause in rootCauses) {
                                        // Stampa le informazioni sulla causa dell'errore
                                        Log.e("Glide1", "Root cause: ${cause.message}")
                                    }
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // L'immagine è stata caricata con successo
                                return false
                            }
                        })
                        .into(imageButtons[i])

                    setupImageButtonClickListener(book, imageButtons[i])
                }
            }
        }
    }

    private fun loadImagesIntoImageButtonsLetti (books: List<LibriL>?) {
        val imageButtons =
            listOf(
                binding.bookLeggere1,
                binding.bookLeggere2,
                binding.bookLeggere3,
                binding.bookLeggere4,
                binding.bookLeggere5,
                binding.bookLeggere6
            )

        // Verifica che la lista dei libri non sia nulla e contenga almeno 6 elementi
        if (books != null ) {
            for (i in 0 until minOf(books.size, 6)) {
                val book = books[i]
                val imageUrl = book.copertina
                Log.d("Image", "imageUrl: $imageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(activity)
                        .load(imageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                e?.let {
                                    // Ottieni la lista delle cause radice dell'eccezione
                                    val rootCauses = e.rootCauses
                                    for (cause in rootCauses) {
                                        // Stampa le informazioni sulla causa dell'errore
                                        Log.e("Glide1", "Root cause: ${cause.message}")
                                    }
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // L'immagine è stata caricata con successo
                                return false
                            }
                        })

                        .into(imageButtons[i])

                    setupImageButtonClickListenerLetto(book, imageButtons[i])
                }
            }
        }
    }



    private fun setupImageButtonClickListener(book: LibriInC, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            val navController = findNavController()
            val action =
                CatalogoHomeDirections.actionCatalogoHomeToLibroInCorso(book, "catalogoHome")
            findNavController().navigate(action)
        }
    }

    private fun setupImageButtonClickListenerDaLeggere(book: LibriDaL, imageButton: ImageButton) {
        imageButton.setOnClickListener {

            val navController = findNavController()
            val action =
                CatalogoHomeDirections.actionCatalogoHomeToLibroDaLeggere(book, "catalogoHome")
            findNavController().navigate(action)
        }
    }

    private fun setupImageButtonClickListenerLetto(book: LibriL, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            val navController = findNavController()
            val action = CatalogoHomeDirections.actionCatalogoHomeToLibroLetto(book, "catalogoHome")
            findNavController().navigate(action)
        }
    }


}