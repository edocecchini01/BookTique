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
import com.example.booktique.databinding.FragmentCatalogoDaLeggereBinding

class CatalogoDaLeggere : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterDL
    private lateinit var listaLibri: ArrayList<Libro>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCatalogoDaLeggereBinding>(inflater,
            R.layout.fragment_catalogo_da_leggere,container,false)

        recyclerView = binding.listaLibriLeggere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza la lista dei libri
        listaLibri = ArrayList()

        // Crea un oggetto Libro di prova
        val libro1 = Libro(
            cover = R.drawable.cover1,
            titolo = "Il signore degli anelli",
            genere = "Fantasy",
            autore = "J.R.R. Tolkien",
            nPagine = 1178,
            Isbn = 9788804668235,
            dettagli = "Una grande epopea fantasy ambientata nella Terra di Mezzo."
        )

        // Aggiungi il libro alla lista dei libri
        listaLibri.add(libro1)

        recyclerView = view.findViewById(R.id.lista_libri_leggere)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        adapter = MyAdapterDL(listaLibri)
        recyclerView.adapter = adapter
    }

}