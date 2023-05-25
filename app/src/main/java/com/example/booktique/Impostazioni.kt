package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.booktique.databinding.FragmentImpostazioniBinding

private lateinit var binding: FragmentImpostazioniBinding

class Impostazioni : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentImpostazioniBinding>(
            inflater,
            R.layout.fragment_impostazioni, container, false
        )
        return binding.root
    }

}