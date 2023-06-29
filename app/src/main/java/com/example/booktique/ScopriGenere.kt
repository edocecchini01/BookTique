package com.example.booktique

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentScopriGenereBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScopriGenere : Fragment() {
    private lateinit var binding: FragmentScopriGenereBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterGenere
    private lateinit var listaLibri: ArrayList<VolumeDet>
    private var param: String? = null
    private var ricerca: Boolean = false

    companion object {

        fun newInstance(genre: String): ScopriGenere {
            val fragment = ScopriGenere()
            val args = Bundle()
            args.putString("param", genre)
            args.putBoolean("ricerca", false)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceS(searchQuery: String): ScopriGenere {
            val fragment = ScopriGenere()
            val args = Bundle()
            args.putString("param", searchQuery)
            args.putBoolean("ricerca", true)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentScopriGenereBinding>(inflater,
            R.layout.fragment_scopri_genere,container,false)

        recyclerView = binding.listaLibriScopriGenere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        arguments?.let { args ->
            ricerca = args.getBoolean("ricerca")
            param = args.getString("param")
            if(param != null){
                if(ricerca)
                    binding.genere.text = "Ricerca: "+ param
                else
                    binding.genere.text = param
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()
        recyclerView.setHasFixedSize(true)
        if(!ricerca) {
            val queryparameter = "subject:" + param
            Log.d("TAG", queryparameter)
            getSubjectBooks(queryparameter, "relevance")
        }else{
            loadBooks(BooksHolder.books)
        }

    }

    private fun getSubjectBooks(query:String, ordine: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.searchBooks(query)   //forse meglio usare getnewreleases con relevant come parametro oltre la query
        newReleasesCall.enqueue(object : Callback<ResponseBody> {
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

                            val newBooksList = mutableListOf<VolumeDet>()

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


                                val newBook =
                                    VolumeDet(imageLinks, title, authors, language)
                                newBooksList.add(newBook)
                            }

                            loadBooks(newBooksList)

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

    private fun loadBooks(books: List<VolumeDet>?){
        if (books != null) {
            listaLibri.addAll(books)
            Log.d("TAG","LIBRI: $listaLibri" )
            adapter = MyAdapterGenere(listaLibri)
            Log.d("TAG","LIBRI:11: $listaLibri" )
            recyclerView.adapter = adapter
        }
    }

}