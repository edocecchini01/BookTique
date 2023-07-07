package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.booktique.databinding.FragmentLibroDaLeggereBinding
import com.example.booktique.databinding.FragmentLibroInCorsoBinding
import com.google.firebase.auth.FirebaseUser


class LibroDaLeggere : Fragment() {
    private lateinit var binding: FragmentLibroDaLeggereBinding
    private lateinit var cUser : FirebaseUser
    private lateinit var libroDaL: LibriDaL
    private val args by navArgs<LibroDaLeggereArgs>()
    private lateinit var origin: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        binding = DataBindingUtil.inflate<FragmentLibroDaLeggereBinding>(inflater,
            R.layout.fragment_libro_da_leggere,container,false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = binding.imageView5
        Glide.with(this)
            .load(args.LibroDaLeg.copertina)
            .into(imageView)

        binding.textView26.text = args.LibroDaLeg.titolo
        binding.textView27.text = args.LibroDaLeg.autori
        binding.textView14.text = args.LibroDaLeg.descrizione

        origin = args.origin

        if (origin == "catalogoDaLeggere") {
            binding.imageButton2.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroDaLeggere_to_catalogoDaLeggere)
            }
        }
        else if (origin == "catalogoHome") {
            binding.imageButton2.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.action_libroDaLeggere_to_catalogoHome)
            }
        }


    }



}