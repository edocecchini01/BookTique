package com.example.booktique
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ScopriViewModel : ViewModel() {

    private val _newBooks = MutableLiveData<List<VolumeDet>>()
    val newBooks: LiveData<List<VolumeDet>>
        get() = _newBooks

    private val _relevantBooks = MutableLiveData<List<VolumeDet>>()
    val relevantBooks: LiveData<List<VolumeDet>>
        get() = _relevantBooks
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

    private fun orderedBooks(query: String, tipologia: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                GlobalScope.launch(Dispatchers.Main) {
                    if (tipologia == "newest") {
                        _newBooks.value = newBooksList
                    } else if (tipologia == "relevance") {
                        _relevantBooks.value = newBooksList
                    }
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
            } catch (e: Exception) {
                // Gestisci l'errore in modo appropriato
                GlobalScope.launch(Dispatchers.Main) {
                    if (tipologia == "newest") {
                        _newBooks.value = emptyList()
                    } else if (tipologia == "relevance") {
                        _relevantBooks.value = emptyList()
                    }
                }
            }
        }
    }
}
