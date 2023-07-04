package com.example.booktique

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.booktique.databinding.FragmentScopriPerTeBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var binding:FragmentScopriPerTeBinding

class ScopriPerTe : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentScopriPerTeBinding>(inflater,
            R.layout.fragment_scopri_per_te,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scopriButton()
        perTeBook("a", "relevance",20)
    }

    override fun onResume() {
        super.onResume()
        binding.buttonPerte.setBackgroundColor(Color.parseColor("#B46060"))
        binding.buttonPerte.setTextColor(Color.parseColor("#FFF4E0"))
    }

    private fun scopriButton(){
        binding.buttonScopri.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_scopriPerTe_to_scopri)
        }
    }

    private fun perTeBook(query:String, order: String, maxResults: Int){
        val titolo = binding.textView7.toString()
        val perTeCall = ApiServiceManager.apiService.getPerTe(query,order,maxResults)
        perTeCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("TAG", "Messaggio di debug")

                    val bookResponse = response.body()
                    Log.d("TAG", "bookResponse: $bookResponse")

                    try {
                        if (bookResponse != null) {
                            val jsonString = bookResponse.string()

                            val jsonObject = JSONObject(jsonString)
                            val itemsArray = jsonObject.getJSONArray("items")

                            val perTeBooksList = mutableListOf<VolumeDet>()

                            for (i in 0 until itemsArray.length()) {
                                val book = itemsArray.getJSONObject(i)
                                val volumeInfo = book.getJSONObject("volumeInfo")

                                var title = "Titolo non disponibile"
                                if (volumeInfo.has("title")) {
                                    title = volumeInfo.optString("title")
                                }
                                val authorsList = mutableListOf<String>()
                                if (volumeInfo.has("authors")) {
                                    val authorsArray = volumeInfo.optJSONArray("authors")
                                    if (authorsArray != null) {
                                        for (j in 0 until authorsArray.length()) {
                                            val author = authorsArray.getString(j)
                                            authorsList.add(author)
                                        }
                                    }
                                }
                                val authors = authorsList.toList()

                                var language = "Lingua non specificata"
                                if (volumeInfo.has("language")) {
                                    language = volumeInfo.optString("language")
                                }

                                var pag = 0
                                if (volumeInfo.has("pageCount")) {
                                    val pageCountString = volumeInfo.optString("pageCount")
                                    pag = pageCountString.toIntOrNull() ?: 0
                                }

                                val imageLinks: ImageLinks =
                                    if (volumeInfo.has("imageLinks")) {
                                        val imageLinksObject =
                                            volumeInfo.getJSONObject("imageLinks")
                                        val smallThumbnail =
                                            imageLinksObject.optString("smallThumbnail")
                                        ImageLinks(smallThumbnail)
                                    } else {
                                        val smallThumbnail =
                                            "android.resource://com.example.booktique/drawable/no_book_icon"
                                        ImageLinks(smallThumbnail)
                                    }
                                val id = book.optString("id")

                                val newBook =
                                    VolumeDet(imageLinks, title, authors, language, pag, id)
                                perTeBooksList.add(newBook)
                            }

                            slideBook(perTeBooksList)
                        }
                    } catch (e: JSONException) {
                        // Il parsing del JSON non è valido
                        // Gestisci l'errore
                        Log.e("JSON Parsing Error", "Errore nel parsing del JSON: ${e.message}")
                    }

                } else {
                    val statusCode = response.code()
                    val errorMessage = response.message()
                    Log.d("API Error", "Status Code: $statusCode")
                    Log.d("API Error", "Error Message: $errorMessage")

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "Messaggio di debug11111")
                Log.e("TAG", "Errore nella chiamata API: ${t.message}", t)

            }
        })


    }

    private fun slideBook(books: List<VolumeDet>?) {
        var i = 0
        if (books != null){
            val book = books?.get(i)
            if (book != null) {
                binding.textView7.text = abbreviaInfo(book.title.toString(),25)
            }
            val imageUrl = book?.imageLinks?.smallThumbnail
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

                .into(binding.imageButton2)

            binding.no.setOnClickListener {
                if (i <= 19) {
                    Log.d("Per Te", "$i")
                    val book = books?.get(i)
                    if (book != null) {
                        binding.textView7.text = abbreviaInfo(book.title.toString(), 20)
                    }
                    val imageUrl = book?.imageLinks?.smallThumbnail
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

                        .into(binding.imageButton2)
                    i++
                }else{
                    binding.imageButton2.visibility = View.GONE
                    binding.textView7.text = "Libri terminati! Torna più tardi"
                    binding.linearL.visibility = View.GONE
                }
            }

        }
    }

    fun abbreviaInfo(stringa: String, lunghezzaMassima: Int): String {
        return if (stringa.length <= lunghezzaMassima) {
            stringa
        } else {
            val sottostringa = stringa.take(lunghezzaMassima)
            "$sottostringa..."
        }
    }


}
