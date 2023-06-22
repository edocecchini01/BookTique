package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoInCorsoBinding

class CatalogoInCorso : Fragment() {
    private lateinit var binding: FragmentCatalogoInCorsoBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterIC
    private lateinit var listaLibri: ArrayList<Libro>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoInCorsoBinding>(inflater,
            R.layout.fragment_catalogo_in_corso,container,false)

        recyclerView = binding.listaLibriCorso
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()

        val libro1 = Libro(
            cover = R.drawable.cover1,
            titolo = "Le mirabolanti avventure",
            genere = "Fantasy",
            autore = "J.R.R. Tolkien",
            nPagine = 1178,
            Isbn = 9788804368235,
            dettagli = "Una grande epopea fantasy ambientata nella Terra di Mezzo."
        )

        listaLibri.add(libro1)

        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterIC(listaLibri)
        recyclerView.adapter = adapter

    }

}