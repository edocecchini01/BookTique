package com.example.booktique

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.booktique.databinding.FragmentScopriPerTeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var binding:FragmentScopriPerTeBinding
private val perTeBooksList = mutableListOf<VolumeDet>()
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
        userBook()
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

    private fun perTeBook(query1: String, query2: String, order: String, maxResults: Int, allBookUser: ArrayList<String?>) {
        val perTeCall1 = ApiServiceManager.apiService.getPerTe(query1,order,maxResults)
        val perTeCall2 = ApiServiceManager.apiService.getPerTe(query2,order,maxResults)
        var completedCalls = 0
        //autore
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
                                    VolumeDet(imageLinks, title, authors, language, pag, id, descrizione, categoria)
                                perTeBooksList.add(newBook)
                            }

                            val allBookU = allBookUser
                            perTeBooksList.removeAll{ libro -> allBookU.contains(libro.id)}

                            completedCalls++

                            if (completedCalls == 2) {
                                perTeBooksList.shuffle()
                                slideBook(perTeBooksList)
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

        //genere
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
                                    VolumeDet(imageLinks, title, authors, language, pag, id, descrizione, categoria)
                                perTeBooksList.add(newBook)
                            }

                            val allBookU = allBookUser
                            perTeBooksList.removeAll{ libro -> allBookU.contains(libro.id)}

                            completedCalls++
                            if (completedCalls == 2) {
                                perTeBooksList.shuffle()
                                slideBook(perTeBooksList)
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

    private fun userBook(){
        //uguale a checkbook letti
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

            var allBookUser = ArrayList<String?>()
            val likeBook = arrayListOf<LibriL>()

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

                    for(book in lettiBooks){
                        if(book.valutazione == 1)
                            likeBook.add(book)
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

                    if(likeBook.isNotEmpty()) {
                        val countAutori = likeBook.groupingBy { it.autori }.eachCount()
                        val mostAutore = countAutori.maxByOrNull { it.value }?.key
                        val countGeneri = likeBook.groupingBy { it.categorie }.eachCount()
                        val mostGenere = countGeneri.maxByOrNull { it.value }?.key
                        Log.d("GENERE","Valore genere: $mostGenere")
                        Log.d("GENERE","Valore autore: $mostAutore")
                        val query1 = "inauthor:\"$mostAutore\""
                        val query2 = "subject:\"$mostGenere\""
                        perTeBook(query1, query2,"relevance",30,allBookUser)
                    }else{
                        binding.imageButton2.visibility = View.GONE
                        binding.textView7.text = "Non ci sono abbastanza informazioni, torna quando avrai letto altri libri!"
                        binding.linearL.visibility = View.GONE
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
    }

    private var currentIndex = 0
    private fun slideBook(bookss: List<VolumeDet>?) {
        var books = bookss
        books = books?.distinctBy { it.id }
        if (books != null) {
            if (books.isNotEmpty()){
                binding.imageButton2.visibility = View.VISIBLE
                binding.linearL.visibility = View.VISIBLE
                val book = books[currentIndex]
                binding.textView7.text = abbreviaInfo(book.title.toString(),25)
                val imageUrl = book?.imageLinks?.thumbnail
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
                setupImageButtonClickListener(book, binding.imageButton2)


                binding.no.setOnClickListener {
                    if (currentIndex < (books.size - 1)){
                        currentIndex++

                        val book = books[currentIndex]
                        binding.textView7.text = abbreviaInfo(book.title.toString(), 20)

                        val imageUrl = book?.imageLinks?.thumbnail
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
                    }else{
                        binding.imageButton2.visibility = View.GONE
                        binding.textView7.text = "Libri terminati! Torna più tardi"
                        binding.linearL.visibility = View.GONE
                        currentIndex = 0
                    }
                }

                binding.si.setOnClickListener {
                    var dialog: AlertDialog? = null
                    val builder = AlertDialog.Builder(requireContext())
                    val dialogView = layoutInflater.inflate(R.layout.dialog_per_te, null)
                    val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
                    val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
                    builder.setView(dialogView)

                    btnConfirm.setOnClickListener {
                        aggiungiLibro(books)
                        dialog?.dismiss()
                    }

                    dialog = builder.create()
                    dialog?.show()
                }
            }else{
                binding.imageButton2.visibility = View.GONE
                binding.textView7.text = "Caricamento..."
                binding.linearL.visibility = View.GONE
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

    private fun aggiungiLibro(books: List<VolumeDet>?){
        if(FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!
            val database = FirebaseDatabase.getInstance("https://booktique-87881-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.reference.child("Utenti")
            val childRef = usersRef.child(cUser.uid)
            val catalogoRef = childRef.child("Catalogo")
            val daLeggereRef = catalogoRef.child("DaLeggere")
            val book = books?.get(currentIndex)
            var title = ""
            var link = ""
            var authors = ""
            var pag = 0
            var id = ""
            if (book!=null){
                title = book.title?: ""
                link = book.imageLinks?.thumbnail ?: ""
                authors = book.authors[0]
                pag = book.pageCount?: 0
                id = book.id ?: ""
            }
            Log.d("TAG", "Sono qui: $link")

            val libroLeg = LibriDaL(
                title,
                link,
                authors,
                pag,
                id
            )

            val nuovoLibroRef = daLeggereRef.push()
            nuovoLibroRef.setValue(libroLeg)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Libro aggiunto con successo",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (books != null) {
                        if (currentIndex < (books.size -1)) {
                            currentIndex++
                            slideBook(books)
                        } else{
                            binding.imageButton2.visibility = View.GONE
                            binding.textView7.text = "Libri terminati! Torna più tardi"
                            binding.linearL.visibility = View.GONE
                            currentIndex = 0
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Errore durante l'aggiunta del libro",
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }else{
            Toast.makeText(
                requireContext(),
                "Errore",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun setupImageButtonClickListener(book: VolumeDet, imageButton: ImageButton) {
        imageButton.setOnClickListener {
            BookHolder.book = book
            val parametro = "ScopriPerTe"
            val bundle = Bundle()
            bundle.putString("origin", parametro)

            val intent = Intent(requireContext(), DettaglioLibro::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }



}
