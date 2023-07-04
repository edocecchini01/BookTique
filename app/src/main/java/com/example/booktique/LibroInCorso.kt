package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.google.firebase.auth.FirebaseUser



class LibroInCorso : Fragment() {
    private lateinit var binding: FragmentLibroInCorsoBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroIncorso: LibriInC

    private val args by navArgs<LibroInCorsoArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate<FragmentLibroInCorsoBinding>(inflater,
            R.layout.fragment_libro_in_corso,container,false)

        return binding.root
    }


    companion object {
        fun newInstance(libroIncorso: LibriInC): LibroInCorso {
            val fragment = LibroInCorso()
            fragment.libroIncorso = libroIncorso
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            val imageView = binding.imageView3
            Glide.with(this)
                .load(args.LibroInC.copertina)
                .into(imageView)

            binding.textView22.text = args.LibroInC.titolo
            binding.textView23.text = args.LibroInC.autori


    }
}