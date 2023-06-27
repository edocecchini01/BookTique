package com.example.booktique

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.booktique.databinding.FragmentScopriBinding
import com.example.booktique.BookResponse
import com.squareup.picasso.Picasso

import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.bumptech.glide.request.target.Target


private lateinit var binding: FragmentScopriBinding
class Scopri : Fragment() {

    interface ClickListenerPerTe {
        fun onButtonClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate<FragmentScopriBinding>(
            inflater,
            R.layout.fragment_scopri, container, false
        )
        orderedBooks("harrypotter", "newest")
        relevantBooks("harrypotter", "relevance")

        return binding.root
    }

    private fun orderedBooks(query:String, tipologia: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query, tipologia)
        Log.d("Image", "imageUrl: $newReleasesCall")

        newReleasesCall.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    Log.d("TAG", "Messaggio di debug")

                    val bookResponse = response.body()
                    Log.d("TAG", "bookResponse: $bookResponse")
                    val newReleases =bookResponse?.items?.map { bookItem ->
                        VolumeDet(
                            imageLinks = bookItem.volumeInfo.imageLinks,
                            title = bookItem.volumeInfo.title,
                            authors = bookItem.volumeInfo.authors,
                            description = bookItem.volumeInfo.description,
                            id = bookItem.volumeInfo.id,
                            categories = bookItem.volumeInfo.categories,
                            publisher = bookItem.volumeInfo.publisher,
                            language = bookItem.volumeInfo.language
                        )
                    }
                    loadImagesIntoImageButtons(newReleases, "newest")

                } else {
                    val statusCode = response.code()
                    val errorMessage = response.message()
                    Log.d("API Error", "Status Code: $statusCode")
                    Log.d("API Error", "Error Message: $errorMessage")

                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Log.d("TAG", "Messaggio di debug11111")
                Log.e("TAG", "Errore nella chiamata API: ${t.message}", t)

            }
        })

    }

    private fun relevantBooks(query:String, tipologia: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query, tipologia)
        Log.d("Image", "imageUrl: $newReleasesCall")

        newReleasesCall.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    Log.d("TAG", "Messaggio di debug")

                    val bookResponse = response.body()
                    Log.d("TAG", "bookResponse: $bookResponse")
                    val newReleases =bookResponse?.items?.map { bookItem ->
                        VolumeDet(
                            imageLinks = bookItem.volumeInfo.imageLinks,
                            title = bookItem.volumeInfo.title,
                            authors = bookItem.volumeInfo.authors,
                            description = bookItem.volumeInfo.description,
                            id = bookItem.volumeInfo.id,
                            categories = bookItem.volumeInfo.categories,
                            publisher = bookItem.volumeInfo.publisher,
                            language = bookItem.volumeInfo.language
                        )
                    }
                    loadImagesIntoImageButtons(newReleases, "relevance")

                } else {
                    val statusCode = response.code()
                    val errorMessage = response.message()
                    Log.d("API Error", "Status Code: $statusCode")
                    Log.d("API Error", "Error Message: $errorMessage")

                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Log.d("TAG", "Messaggio di debug11111")
                Log.e("TAG", "Errore nella chiamata API: ${t.message}", t)

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
                val imageUrl = book.imageLinks.smallThumbnail
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

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener {
            FragmentUtils.replaceFragment(requireFragmentManager(), R.id.fragmentContainerView, ScopriPerTe())
        }
/*
        binding.clickListenerPerTe = object: ClickListenerPerTe {
            override fun onButtonClick() {
                Navigation.findNavController(view).navigate(R.id.action_scopri_to_scopriPerTe)
            }
        }*/

    }
}