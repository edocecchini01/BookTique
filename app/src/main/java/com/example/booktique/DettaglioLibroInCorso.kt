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
    private val book = LibriInc


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentDettaglioLibroInCorsoBinding>(
            inflater,
            R.layout.fragment_catalogo_home, container, false
        )

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}