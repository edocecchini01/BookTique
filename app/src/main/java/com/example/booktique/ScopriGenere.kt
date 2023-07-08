package com.example.booktique

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentScopriGenereBinding>(inflater,
            R.layout.fragment_scopri_genere,container,false)

        arguments?.let { args ->
            ricerca = requireArguments().getBoolean("ricerca")
            param = requireArguments().getString("genere")
            Log.d("param", param.toString())
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

        binding.backbuttonGen.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_scopriGenere_to_scopri)
        }

        listaLibri = ArrayList()
        recyclerView = binding.listaLibriScopriGenere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterGenere(listaLibri)
        recyclerView.adapter = adapter

        if(!ricerca) {

            val queryparameter = "subject:" + param

            Log.d("TAG", queryparameter)
            getSubjectBooks(queryparameter, "relevance")
        }else{
                getSearchBooks(param)

        }

        adapter.setOnCLickItemListener(object: MyAdapterGenere.onItemClickListener{

            override fun dettaglioBook(cover: ImageButton, position: Int) {
                val libro = getLibro(position)

                val navController = findNavController()
                val action = ScopriGenereDirections.actionScopriGenereToDettaglioLibroScopri(libro, "scopriGenere")
                findNavController().navigate(action)
            }


        })


    }

    private fun getSearchBooks(query: String?){
                // Effettua la chiamata all'API
        if (query!=null) {
            val newReleasesCall = ApiServiceManager.apiService.searchBooks(query, "relevance")
            newReleasesCall.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
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

                                    var pag = 0
                                    if (volumeInfo.has("pageCount")) {
                                        val pageCountString = volumeInfo.optString("pageCount")
                                        pag = pageCountString.toIntOrNull() ?: 0
                                    }

                                    val imageLinks: ImageLinks =
                                        if (volumeInfo.has("imageLinks")) {
                                            val imageLinksObject =
                                                volumeInfo.getJSONObject("imageLinks")
                                            val thumbnail =
                                                imageLinksObject.optString("thumbnail")
                                            ImageLinks(thumbnail)
                                        } else {
                                            val thumbnail =
                                                "https://thenounproject.com/api/private/icons/2637513/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0"
                                            ImageLinks(thumbnail)
                                        }
                                    val id = book.optString("id")

                                    var categorieList = mutableListOf<String>()
                                    if (volumeInfo.has("categories")) {
                                        val categorieArray =
                                            volumeInfo.optJSONArray("categories")
                                        if (categorieArray != null) {
                                            for (j in 0 until categorieArray.length()) {
                                                val categoria = categorieArray.getString(j)
                                                categorieList.add(categoria)
                                            }
                                        }
                                    }
                                    val categoria = categorieList.toList()

                                    var descrizione = "Descrizione non presente"
                                    if (volumeInfo.has("description")) {
                                        descrizione = volumeInfo.optString("description")
                                    }


                                    val newBook =
                                        VolumeDet(
                                            imageLinks,
                                            title,
                                            authors,
                                            language,
                                            pag,
                                            id,
                                            descrizione,
                                            categoria
                                        )
                                    newBooksList.add(newBook)
                                    Log.d("Tag", "ciao: $newBook.toString()")
                                }

                                loadBooks(newBooksList)
                            }
                        } catch (e: JSONException) {
                            // Il parsing del JSON non è valido
                            // Gestisci l'errore
                            Log.e(
                                "JSON Parsing Error",
                                "Errore nel parsing del JSON: ${e.message}"
                            )
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
            }




    private fun getSubjectBooks(query:String, ordine: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.searchBooks(query,"relevance")
        val requestUrl = newReleasesCall.request().url
        Log.d("API URL", "URL: $requestUrl")
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

                            Log.d("JSON", "jsonString $jsonString")
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

                                var pag = 0
                                if (volumeInfo.has("pageCount")) {
                                    val pageCountString = volumeInfo.optString("pageCount")
                                    pag = pageCountString.toIntOrNull() ?: 0
                                }

                                val imageLinks: ImageLinks =
                                    if (volumeInfo.has("imageLinks")) {
                                        val imageLinksObject =
                                            volumeInfo.getJSONObject("imageLinks")
                                        val thumbnail =
                                            imageLinksObject.optString("thumbnail")
                                        ImageLinks(thumbnail)
                                    } else {
                                        val thumbnail =
                                            "https://thenounproject.com/api/private/icons/2637513/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0"
                                        ImageLinks(thumbnail)
                                    }

                                val id = book.optString("id")

                                var categorieList = mutableListOf<String>()
                                if (volumeInfo.has("categories")) {
                                    val categorieArray =
                                        volumeInfo.optJSONArray("categories")
                                    if (categorieArray != null) {
                                        for (j in 0 until categorieArray.length()) {
                                            val categoria = categorieArray.getString(j)
                                            categorieList.add(categoria)
                                        }
                                    }
                                }
                                val categoria = categorieList.toList()

                                var descrizione = "Descrizione non presente"
                                if (volumeInfo.has("description")) {
                                    descrizione = volumeInfo.optString("description")
                                }


                                val newBook =
                                    VolumeDet(
                                        imageLinks,
                                        title,
                                        authors,
                                        language,
                                        pag,
                                        id,
                                        descrizione,
                                        categoria
                                    )
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
            adapter.notifyDataSetChanged()
        }
    }

    private fun getLibro(position: Int): LibriDaL {


        val libro = LibriDaL(
            listaLibri[position].title,
            listaLibri[position].imageLinks.thumbnail ?: "",
            listaLibri[position].authors.toString(),
            listaLibri[position].pageCount?: 0,
            listaLibri[position].id?:"",
            listaLibri[position].description,
            listaLibri[position].categories.toString()

        )

        return libro

    }

}