package com.example.booktique

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.booktique.R
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.databinding.FragmentCatalogoHomeBlockBinding
import com.google.firebase.auth.FirebaseAuth

class CatalogoHomeBlock : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBlockBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentCatalogoHomeBlockBinding>(
            inflater,
            R.layout.fragment_catalogo_home_block, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        binding.imageView4.setOnClickListener {
            val intent = Intent(requireContext(), AutenticazioneActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}