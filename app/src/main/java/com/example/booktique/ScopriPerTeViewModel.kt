package com.example.booktique

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booktique.ApiServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger


//non penso funzioni questa, non l'ho collegata alla view perchè mi turbava
class ScopriPerTeViewModel : ViewModel() {
    // Variabile LiveData per i libri "Per Te"
    private val _perTeBooksList = MutableLiveData<List<VolumeDet>>()
    val perTeBooksList: LiveData<List<VolumeDet>>
        get() = _perTeBooksList


    fun loadPerTeBooks(
        query1: String,
        query2: String,
        order: String,
        maxResults: Int,
        allBookUser: ArrayList<String?>
    ) {
        val perTeBooksList = mutableListOf<VolumeDet>()
        val perTeCall1 = ApiServiceManager.apiService.getPerTe(query1, order, maxResults)
        val perTeCall2 = ApiServiceManager.apiService.getPerTe(query2, order, maxResults)
        var completedCalls = 0
        val newPerTeBooksList = mutableListOf<VolumeDet>()

        perTeCall1.enqueue(object : Callback<ResponseBody> {
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
                                            "android.resource://com.example.booktique/drawable/no_book_icon"
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
                                perTeBooksList.add(newBook)
                            }

                            val allBookU = allBookUser
                            perTeBooksList.removeAll { libro -> allBookU.contains(libro.id) }

                            completedCalls++

                            if (completedCalls == 2) {
                                perTeBooksList.shuffle()

                                    _perTeBooksList.value=perTeBooksList

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

        perTeCall2.enqueue(object : Callback<ResponseBody> {
            override  fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("TAG", "Messaggio di debug")

                    val bookResponse = response.body()
                    Log.d("TAG", "bookResponse: $bookResponse")

                    try {
                        if (bookResponse != null) {
                            val jsonString = bookResponse.string()

                            val jsonObject = JSONObject(jsonString)
                            val itemsArray = jsonObject.getJSONArray("items")

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
                                            "android.resource://com.example.booktique/drawable/no_book_icon"
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
                                perTeBooksList.add(newBook)
                            }

                            val allBookU = allBookUser
                            perTeBooksList.removeAll { libro -> allBookU.contains(libro.id) }

                            completedCalls++
                            if (completedCalls == 2) {
                                perTeBooksList.shuffle()

                                    _perTeBooksList.value=perTeBooksList

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
    }

}