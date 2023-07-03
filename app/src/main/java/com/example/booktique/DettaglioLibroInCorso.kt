package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentCatalogoHomeBinding

import com.example.booktique.databinding.FragmentDettaglioLibroInCorsoBinding
import com.google.firebase.auth.FirebaseUser


class DettaglioLibroInCorso : Fragment() {
    private lateinit var binding: FragmentDettaglioLibroInCorsoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroIncorso: LibriInC


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_dettaglio_libro_in_corso, container, false)
    }


    companion object {
        fun newInstance(libroIncorso: LibriInC): DettaglioLibroInCorso {
            val fragment = DettaglioLibroInCorso()
            fragment.libroIncorso = libroIncorso
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (::libroIncorso.isInitialized) {
            view.findViewById<TextView>(R.id.textView22).text = libroIncorso.titolo
            view.findViewById<TextView>(R.id.textView23).text = libroIncorso.autori
        }

    }

}