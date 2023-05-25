package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentScopriPerTeBinding

private lateinit var binding:FragmentScopriPerTeBinding

class ScopriPerTe : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentScopriPerTeBinding>(inflater,
            R.layout.fragment_scopri_per_te,container,false)
        return binding.root
    }


    }
