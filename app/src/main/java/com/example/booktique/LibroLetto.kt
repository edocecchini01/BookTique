package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.example.booktique.databinding.FragmentLibroLettoBinding
import com.google.firebase.auth.FirebaseUser

class LibroLetto : Fragment() {
    private lateinit var binding: FragmentLibroLettoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroLet: LibriL

    private val args by navArgs<LibroLettoArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroLettoBinding>(inflater,
            R.layout.fragment_libro_letto,container,false)

        return binding.root
    }


    companion object {
        fun newInstance(libroLetto: LibriL): LibroLetto {
            val fragment = LibroLetto()
            fragment.libroLet = libroLetto
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val imageView = binding.imageView3
            Glide.with(this)
                .load(args.LibroLett.copertina)
                .into(imageView)

            binding.textView22.text = args.LibroLett.titolo
            binding.textView23.text = args.LibroLett.autori
            binding.textView13.text = args.LibroLett.descrizione

    }
}