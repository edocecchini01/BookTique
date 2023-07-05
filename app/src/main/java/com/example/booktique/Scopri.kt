package com.example.booktique

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
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
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import org.json.JSONException

import org.json.JSONObject


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
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - 1
        val endYear = currentYear
        orderedBooks("published:$startYear..$endYear", "newest")
        relevantBooks("published:$startYear..$endYear", "relevance")

        searchBook()

        return binding.root
    }

    private fun searchBook(){
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Effettua la chiamata all'API
                val newReleasesCall = ApiServiceManager.apiService.searchBooks(query,"relevance")
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
                                                    "https://thenounproject.com/api/private/icons/2637513/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0"
                                                ImageLinks(smallThumbnail)
                                            }
                                        val id = book.optString("id")

                                        var categorieList = mutableListOf<String>()
                                        if (volumeInfo.has("categories")) {
                                            val categorieArray = volumeInfo.optJSONArray("categories")
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
                                            VolumeDet(imageLinks, title, authors, language, pag, id, descrizione, categoria)
                                        newBooksList.add(newBook)
                                    }

                                    if (!newBooksList.isNullOrEmpty()) {
                                        BooksHolder.books = newBooksList
                                        val fragmentManager = requireActivity().supportFragmentManager
                                        val scopriFragment = ScopriGenere.newInstanceS(query)
                                        FragmentUtils.replaceFragment(
                                            fragmentManager,
                                            R.id.fragmentContainerView,
                                            scopriFragment
                                        )
                                    }
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
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Gestisci gli eventi di modifica del testo della SearchView, se necessario
                return true
            }
        })


    }

    private fun orderedBooks(query:String, tipologia: String){
        // Chiamata per ottenere i nuovi libri
                val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query,tipologia)   //forse meglio usare getnewreleases con relevant come parametro oltre la query
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
                                                    "https://thenounproject.com/api/private/icons/2637513/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0"
                                                ImageLinks(smallThumbnail)
                                            }
                                        val id = book.optString("id")

                                        var categorieList = mutableListOf<String>()
                                        if (volumeInfo.has("categories")) {
                                            val categorieArray = volumeInfo.optJSONArray("categories")
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
                                            VolumeDet(imageLinks, title, authors, language, pag, id, descrizione, categoria)
                                        newBooksList.add(newBook)
                                    }

                                    loadImagesIntoImageButtons(newBooksList, tipologia)
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

    private fun relevantBooks(query:String, tipologia: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query,tipologia)   //forse meglio usare getnewreleases con relevant come parametro oltre la query
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
                                            "https://thenounproject.com/api/private/icons/2637513/edit/?backgroundShape=SQUARE&backgroundShapeColor=%23000000&backgroundShapeOpacity=0&exportSize=752&flipX=false&flipY=false&foregroundColor=%23000000&foregroundOpacity=1&imageFormat=png&rotation=0"
                                        ImageLinks(smallThumbnail)
                                    }
                                val id = book.optString("id")

                                var categorieList = mutableListOf<String>()
                                if (volumeInfo.has("categories")) {

                                    Log.d("cat", "ccc:")
                                    val categorieArray = volumeInfo.optJSONArray("categories")
                                    if (categorieArray != null) {
                                        for (j in 0 until categorieArray.length()) {
                                            val categoria = categorieArray.getString(j)
                                            Log.d("cat", "ccc: $categoria")
                                            categorieList.add(categoria)
                                        }
                                    }
                                }
                                val categoria = categorieList.toList()
                                Log.d("cat", "c: $categoria")

                                var descrizione = "Descrizione non presente"
                                if (volumeInfo.has("description")) {
                                    descrizione = volumeInfo.optString("description")
                                }

                                val newBook =
                                    VolumeDet(imageLinks, title, authors, language, pag, id, descrizione, categoria)
                                Log.d("cat", "ccc: $newBook")
                                newBooksList.add(newBook)
                            }

                            loadImagesIntoImageButtons(newBooksList, tipologia)
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

                setupImageButtonClickListener(book, targetButtons[i])

            }
        }
    }



    private fun setupImageButtonClickListener(book: VolumeDet, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            BookHolder.book = book
            val intent = Intent(requireContext(), DettaglioLibro::class.java)
            startActivity(intent)
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        perTeButton()
        genereButtons()
/*
        binding.clickListenerPerTe = object: ClickListenerPerTe {
            override fun onButtonClick() {
                Navigation.findNavController(view).navigate(R.id.action_scopri_to_scopriPerTe)
            }
        }*/

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

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("fantasia")
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }
        binding.button4.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("romanzi")
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }
        binding.button5.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("fiction")
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }
        binding.button6.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("biografia" )
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }
        binding.button7.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("storia")
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }
        binding.button8.setOnClickListener {

            val fragmentManager = requireActivity().supportFragmentManager
            val scopriFragment = ScopriGenere.newInstance("horror")
            FragmentUtils.replaceFragment(
                fragmentManager,
                R.id.fragmentContainerView,
                scopriFragment
            )
        }

    }

    inline fun <T> T?.orDefault(defaultValue: T): T {
        return this ?: defaultValue
    }
}