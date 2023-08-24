package com.example.booktique
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

//scopri e scopri genere sono collegata a questa
class ScopriViewModel : ViewModel() {

    private val _newBooks = MutableLiveData<List<VolumeDet>>()
    val newBooks: LiveData<List<VolumeDet>>
        get() = _newBooks
    //relevantBooks si può cancellare e lasciare solo genrebooks sostituendo in scopri la lista che viene osservata
    private val _relevantBooks = MutableLiveData<List<VolumeDet>>()
    val relevantBooks: LiveData<List<VolumeDet>>
        get() = _genreBooks //questo è sbagliato ma potrebbe essere il motivo per cui funziona

    private val _genreBooks = MutableLiveData<List<VolumeDet>>()
    val genreBooks: LiveData<List<VolumeDet>>
        get() = _genreBooks

    private var booksLoaded = false
    // Chiamata API per ottenere i nuovi libri
    fun loadNewBooks() {
        if (!booksLoaded) {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val startYear = currentYear - 1
            val endYear = currentYear
            orderedBooks("published:$startYear..$endYear", "newest")
            orderedBooks("published:$startYear..$endYear", "relevance")
            booksLoaded= true
        }

    }

    fun searchBooks(query: String) {
        orderedBooks(query, "relevance") // Cambia "relevance" con l'ordine desiderato
    }


    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun orderedBooks(query: String, tipologia: String) {
        coroutineScope.launch {
            try {
                val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query, tipologia)
                val response = newReleasesCall.execute()

                handleApiResponse(response) { jsonString ->
                    parseAndLoadBooks(jsonString, tipologia)
                }
            } catch (e: Exception) {
                handleApiError(e, tipologia)
            }
        }
    }

    private suspend fun parseAndLoadBooks(jsonString: String, tipologia: String) {
        try {
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
            newBooksList.shuffle()

            withContext(Dispatchers.Main) {
                if (tipologia == "newest") {
                    _newBooks.value = newBooksList
                } else if (tipologia == "relevance") {
                    _genreBooks.value = newBooksList
                }
            }
        } catch (e: JSONException) {
            Log.e("JSON Parsing Error", "Errore nel parsing del JSON: ${e.message}")
        }
    }

    private suspend fun handleApiError(error: Throwable, tipologia: String) {
        Log.e("API Error", "Errore nella chiamata API: ${error.message}", error)
        withContext(Dispatchers.Main) {
            if (tipologia == "newest") {
                _newBooks.value = emptyList()
            } else if (tipologia == "relevance") {
                _genreBooks.value = emptyList()
            }
        }
    }

    private inline fun handleApiResponse(
        response: Response<ResponseBody>,
        onSuccess: (jsonString: String) -> Unit
    ) {
        if (response.isSuccessful) {
            val bookResponse = response.body()
            if (bookResponse != null) {
                val jsonString = bookResponse.string()
                onSuccess(jsonString)
            }
        } else {
            val statusCode = response.code()
            val errorMessage = response.message()
            Log.d("API Error", "Status Code: $statusCode")
            Log.d("API Error", "Error Message: $errorMessage")
        }
    }


}
