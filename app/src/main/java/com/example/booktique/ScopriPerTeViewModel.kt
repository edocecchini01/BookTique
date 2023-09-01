package com.example.booktique

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booktique.ApiServiceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


    fun likeBook() : ArrayList<LibriL>{
        val likeBook = arrayListOf<LibriL>()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = arrayListOf<LibriL>()

                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libroL = bookSnapshot.getValue(LibriL::class.java)

                            if (libroL != null) {
                                lettiBooks.add(libroL)
                            }

                        }
                    }

                    for(book in lettiBooks){
                        if(book.valutazione == 1)
                            likeBook.add(book)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            }

            lettiRef.addValueEventListener(eventListener)

        }
        return likeBook
    }

    fun userTaste() : ArrayList<String>{
        //uguale a checkbook letti
        val taste = ArrayList<String>()
        val likeBook = likeBook()

        if(likeBook.isNotEmpty()) {
            val countAutori = likeBook.groupingBy { it.autori }.eachCount()
            val mostAutore = countAutori.maxByOrNull { it.value }?.key
            val countGeneri = likeBook.groupingBy { it.categorie }.eachCount()
            val mostGenere = countGeneri.maxByOrNull { it.value }?.key
            Log.d("GENERE", "Valore genere: $mostGenere")
            Log.d("GENERE", "Valore autore: $mostAutore")
            val query1 = "inauthor:\"$mostAutore\""
            val query2 = "subject:\"$mostGenere\""
            taste.add(query1)
            taste.add(query2)
        }

        return taste
    }

    fun allUBook(): ArrayList<String?>{
        val allBookUser = ArrayList<String?>()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            Log.d("TAG", "Sono :")
            val database =
                FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val lettiRef = catalogoRef.child("Letti")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            val inCorsoRef = catalogoRef.child("InCorso")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lettiBooks = arrayListOf<LibriL>()
                    val daLeggereBooks = arrayListOf<LibriDaL>()
                    val inCorsoBooks = arrayListOf<LibriInC>()

                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val libroL = bookSnapshot.getValue(LibriL::class.java)
                            val libroDaL = bookSnapshot.getValue(LibriDaL::class.java)
                            val libroInC = bookSnapshot.getValue(LibriInC::class.java)

                            if (libroL != null) {
                                lettiBooks.add(libroL)
                            } else if (libroDaL != null) {
                                daLeggereBooks.add(libroDaL)
                            } else if (libroInC != null) {
                                inCorsoBooks.add(libroInC)
                            }

                        }
                    }

                    if(lettiBooks.isNotEmpty()) {
                        val ids = lettiBooks.map { libro -> libro.id }
                        allBookUser.addAll(ids)
                    }
                    if(daLeggereBooks.isNotEmpty()) {
                        val ids = daLeggereBooks.map { libro -> libro.id }
                        allBookUser.addAll(ids)
                    }
                    if(inCorsoBooks.isNotEmpty()) {
                        val ids = inCorsoBooks.map { libro -> libro.id }
                        allBookUser.addAll(ids)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "Errore nel recupero dei dati", error.toException())
                }
            }

            lettiRef.addValueEventListener(eventListener)
            daLeggereRef.addValueEventListener(eventListener)
            inCorsoRef.addValueEventListener(eventListener)

        }
        return allBookUser
    }

    fun authorCall(order: String, maxResults: Int){
        val taste = userTaste()
        val query1 = taste[0]
        val allBookUser = allUBook()
        val yourBooks = mutableListOf<VolumeDet>()
        val perTeCall1 = ApiServiceManager.apiService.getPerTe(query1,order,maxResults)
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
                                yourBooks.add(newBook)
                            }

                            yourBooks.removeAll{ libro -> allBookUser.contains(libro.id)}

                            _perTeBooksList.value = yourBooks

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

    fun genCall(order: String, maxResults: Int){
        val taste = userTaste()
        val query2 = taste[1]
        val allBookUser = allUBook()
        val yourBooks = mutableListOf<VolumeDet>()
        val perTeCall2 = ApiServiceManager.apiService.getPerTe(query2,order,maxResults)
        perTeCall2.enqueue(object : Callback<ResponseBody> {
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
                                yourBooks.add(newBook)
                            }

                            yourBooks.removeAll{ libro -> allBookUser.contains(libro.id)}

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