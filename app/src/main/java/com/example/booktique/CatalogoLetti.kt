package com.example.booktique

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktique.databinding.FragmentCatalogoInCorsoBinding
import com.example.booktique.databinding.FragmentCatalogoLettiBinding

class CatalogoLetti : Fragment() {
    private lateinit var binding: FragmentCatalogoLettiBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MyAdapterL
    private lateinit var listaLibri: ArrayList<Libro>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoLettiBinding>(inflater,
            R.layout.fragment_catalogo_letti,container,false)

        recyclerView = binding.listaLibriLeggere
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaLibri = ArrayList()

        val libro1 = Libro(
            cover = R.drawable.cover1,
            titolo = "LETTOOO",
            genere = "Fantasy",
            autore = "J.R.R. Tolkien",
            nPagine = 1178,
            Isbn = 9788804361235,
            dettagli = "Una grande epopea fantasy ambientata nella Terra di Mezzo."
        )

        listaLibri.add(libro1)

        recyclerView.setHasFixedSize(true)

        adapter = MyAdapterL(listaLibri)
        recyclerView.adapter = adapter
        adapter.setOnCLickItemListener(object : MyAdapterL.onItemClickListener{
            override fun onItemClick(position: Int) {

            }

            override fun hideShow(element: LinearLayout, comment: ImageButton) {
                val linearL = element
                val btn = comment
                if(linearL.visibility == View.GONE) {
                    linearL.visibility = View.VISIBLE
                    btn.setBackgroundResource(R.drawable.comment_filled_icon)
                }
                else {
                    linearL.visibility = View.GONE
                    btn.setBackgroundResource(R.drawable.comment_icon)
                }

            }

        })

    }

}