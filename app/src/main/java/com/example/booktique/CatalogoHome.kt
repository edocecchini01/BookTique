package com.example.booktique

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.booktique.databinding.FragmentCatalogoHomeBinding
import com.example.booktique.AutenticazioneActivity
import com.example.booktique.FragmentUtils.replaceFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CatalogoHome : Fragment() {
    private lateinit var binding: FragmentCatalogoHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCatalogoHomeBinding>(
            inflater,
            R.layout.fragment_catalogo_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.daLeggereLabel.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_catalogoHome_to_catalogoDaLeggere)
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            binding.myButton.setOnClickListener {
                replaceFragment(Impostazioni())
            }
        } else {
            binding.myButton.setOnClickListener {
                val intent = Intent(requireActivity(), AutenticazioneActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.impostazioniPulsante
    }
}
