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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScopriGenere : Fragment() {
    private lateinit var binding: FragmentScopriGenereBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterGenere
    private lateinit var listaLibri: ArrayList<VolumeDet>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentScopriGenereBinding>(inflater,
            R.layout.fragment_scopri_genere,container,false)

        recyclerView = binding.listaLibriScopriGenere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()
        recyclerView.setHasFixedSize(true)
        getSubjectBooks("subject:history", "relevance")

    }

    private fun getSubjectBooks(query:String, ordine: String){
        // Chiamata per ottenere i nuovi libri
        val newReleasesCall = ApiServiceManager.apiService.getNewReleases(query, ordine)
        Log.d("Image", "imageUrl: $newReleasesCall")

        newReleasesCall.enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    Log.d("TAG", "Messaggio di debug")

                    val bookResponse = response.body()
                    Log.d("TAG", "bookResponse: $bookResponse")
                    val subBook =bookResponse?.items?.map { bookItem ->
                        VolumeDet(
                            imageLinks = bookItem.volumeInfo.imageLinks,
                            title = bookItem.volumeInfo.title,
                            authors = bookItem.volumeInfo.authors,
                            language = bookItem.volumeInfo.language
                        )
                    }
                    loadBooks(subBook)

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