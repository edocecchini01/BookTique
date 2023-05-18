package com.example.booktique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentRegistrazioneBinding

private lateinit var binding: FragmentRegistrazioneBinding
class registrazione : Fragment() {

    interface ClickListener {
        fun onScrittaClicked()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentRegistrazioneBinding>(inflater, R.layout.fragment_registrazione,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clickListener = object : ClickListener {
            override fun onScrittaClicked() {
                Navigation.findNavController(view).navigate(R.id.action_registrazione_to_login)
            }
        }
}
}