package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentScopriBinding
import com.example.booktique.FragmentUtils

private lateinit var binding: FragmentScopriBinding
class Scopri : Fragment() {

    interface ClickListenerPerTe {
        fun onButtonClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate<FragmentScopriBinding>(
            inflater,
            R.layout.fragment_scopri, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener {
            FragmentUtils.replaceFragment(requireFragmentManager(), R.id.fragmentContainerView, ScopriPerTe())
        }
/*
        binding.clickListenerPerTe = object: ClickListenerPerTe {
            override fun onButtonClick() {
                Navigation.findNavController(view).navigate(R.id.action_scopri_to_scopriPerTe)
            }
        }*/

    }
}