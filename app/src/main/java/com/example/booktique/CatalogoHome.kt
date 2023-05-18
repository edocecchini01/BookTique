package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentCatalogoHomeBinding

class CatalogoHome : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate<FragmentCatalogoHomeBinding>(inflater,
            R.layout.fragment_catalogo_home,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.daLeggereLabel.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_catalogoHome_to_catalogoDaLeggere)
        }

    }
}
