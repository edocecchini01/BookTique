package com.example.booktique

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.booktique.databinding.FragmentScopriBinding
import com.google.firebase.auth.FirebaseAuth

class Scopri : Fragment() {

    private lateinit var viewModel: ScopriViewModel
    private lateinit var binding: FragmentScopriBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate<FragmentScopriBinding>(
             inflater,
             R.layout.fragment_scopri, container, false
         )
        searchBook()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ScopriViewModel::class.java)

        // Osserva il LiveData newestBooks per ottenere gli aggiornamenti dei dati per i libri più recenti
        if (!viewModel.newBooks.hasObservers()) {
            viewModel.newBooks.observe(viewLifecycleOwner, Observer { newestBooksList ->
                loadImagesIntoImageButtons(newestBooksList, "newest")
            })
        }

        if (!viewModel.relevantBooks.hasObservers()) {
            viewModel.relevantBooks.observe(viewLifecycleOwner, Observer { relevantBooksList ->
                loadImagesIntoImageButtons(relevantBooksList, "relevance")
            })
        }

        viewModel.loadNewBooks()
        perTeButton()
        genereButtons()
    }



    private fun searchBook(){
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                val navController = findNavController()
                val bundle = bundleOf("genere" to query, "ricerca" to true)
                navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Gestisci gli eventi di modifica del testo della SearchView, se necessario
                return true
            }
        })
    }

    private fun loadImagesIntoImageButtons(books: List<VolumeDet>?, tipologia: String) {
        val imageButtons = listOf(
            binding.bookInC1,
            binding.bookInC2,
            binding.bookInC3,
            binding.bookInC4,
            binding.bookInC5,
            binding.bookInC6

        )

        val targetButtons = if (tipologia == "newest") {
            imageButtons
        } else {
            listOf(
                binding.bookLeggere1,
                binding.bookLeggere2,
                binding.bookLeggere3,
                binding.bookLeggere4,
                binding.bookLeggere5,
                binding.bookLeggere6
            )
        }

        // Verifica che la lista dei libri non sia nulla e contenga almeno 6 elementi
        if (books != null && books.size >= 6) {
            for (i in 0 until 6) {
                val book = books[i]
                val imageUrl = book.imageLinks.thumbnail
                Log.d("Image", "imageUrl: $imageUrl")

                Glide.with(requireContext())
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

                    .into(targetButtons[i])

                var link = ""
                var pag = 0
                var idL = ""
                var genere = ""
                link = book.imageLinks.thumbnail ?: ""
                pag = book.pageCount?: 0
                idL = book.id ?: ""
                genere = book.categories.toString()

                Log.d("TAG", "Sono qui: $link")

                val libroLeg = LibriDaL(
                    book.title,
                    link,
                    book.authors.toString(),
                    pag,
                    idL,
                    book.description,
                    genere

                )

                setupImageButtonClickListener(libroLeg, targetButtons[i])

            }
        }
    }



    private fun setupImageButtonClickListener(book: LibriDaL, imageButton: ImageButton) {
        imageButton.setOnClickListener {

            val navController = findNavController()
            val action = ScopriDirections.actionScopriToDettaglioLibroScopri(book, "scopri")
            findNavController().navigate(action)
        }
    }



    override fun onResume() {
        super.onResume()
        binding.button1.setBackgroundColor(Color.parseColor("#B46060"))
        binding.button1.setTextColor(Color.parseColor("#FFF4E0"))
    }

    private fun perTeButton(){
        if (FirebaseAuth.getInstance().currentUser != null) {
            binding.buttonPerTe.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_scopri_to_scopriPerTe)
            }
        }else{
            binding.buttonPerTe.setBackgroundColor(Color.parseColor("#4D4D4D"))
            binding.buttonPerTe.setTextColor(Color.parseColor("#FFF4E0"))
            binding.buttonPerTe.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Effettua il login!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun genereButtons() {

        binding.button3.setOnClickListener {

            val navController = findNavController()
            val bundle = bundleOf("genere" to "Poetry", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }
        binding.button4.setOnClickListener {
            val navController = findNavController()
            val bundle = bundleOf("genere" to "Drama", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }
        binding.button5.setOnClickListener {

            val navController = findNavController()
            val bundle = bundleOf("genere" to "Fiction", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }
        binding.button6.setOnClickListener {

            val navController = findNavController()
            val bundle = bundleOf("genere" to "Biography", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }
        binding.button7.setOnClickListener {

            val navController = findNavController()
            val bundle = bundleOf("genere" to "History", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }
        binding.button8.setOnClickListener {

            val navController = findNavController()
            val bundle = bundleOf("genere" to "Computers", "ricerca" to false)
            navController.navigate(R.id.action_scopri_to_scopriGenere, bundle)
        }

    }

    inline fun <T> T?.orDefault(defaultValue: T): T {
        return this ?: defaultValue
    }
}