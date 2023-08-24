package com.example.booktique

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.booktique.R
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.databinding.FragmentCatalogoHomeBlockBinding

class CatalogoHomeBlock : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCatalogoHomeBlockBinding>(
            inflater,
            R.layout.fragment_catalogo_home_block, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
    }

}