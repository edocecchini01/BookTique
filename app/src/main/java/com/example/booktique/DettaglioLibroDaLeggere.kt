package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.FrameMetricsAggregator
import androidx.databinding.DataBindingUtil
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.databinding.FragmentDettaglioLibroDaLeggereBinding
import com.example.booktique.databinding.FragmentDettaglioLibroInCorsoBinding
import com.google.firebase.auth.FirebaseUser


class DettaglioLibroDaLeggere : Fragment() {
    private lateinit var binding: FragmentDettaglioLibroDaLeggereBinding
    private lateinit var cUser : FirebaseUser
    private val book = BookHolder.book

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentDettaglioLibroDaLeggereBinding>(
            inflater,
            R.layout.fragment_catalogo_home, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (book != null) {
            binding.textView11.text = book.title
            binding.textView9.text = book.authors[0]
        }

    }



}