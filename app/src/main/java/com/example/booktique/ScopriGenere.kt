package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoLettiBinding
import com.example.booktique.databinding.FragmentScopriGenereBinding

class ScopriGenere : Fragment() {
    private lateinit var binding: FragmentScopriGenereBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterGenere
    private lateinit var listaLibri: ArrayList<Libro>

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

        val libro1 = Libro(
            cover = R.drawable.cover1,
            titolo = "SCOPRI",
            genere = "Fantasy",
            autore = "Edoardo",
            nPagine = 1178,
            Isbn = 9788804351235,
            dettagli = "Una grande epopea fantasy ambientata nella Terra di Mezzo."
        )

        listaLibri.add(libro1)

        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterGenere(listaLibri)
        recyclerView.adapter = adapter

    }

}